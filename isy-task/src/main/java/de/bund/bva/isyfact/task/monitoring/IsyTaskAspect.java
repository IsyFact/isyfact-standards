/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.bund.bva.isyfact.task.monitoring;

import static de.bund.bva.isyfact.task.konstanten.HinweisSchluessel.VERWENDE_STANDARD_KONFIGURATION;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties.TaskConfig;
import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.exception.TaskDeactivatedException;
import de.bund.bva.isyfact.task.exception.TaskKonfigurationInvalidException;
import de.bund.bva.isyfact.task.konfiguration.HostHandler;
import de.bund.bva.isyfact.task.konstanten.Ereignisschluessel;
import de.bund.bva.isyfact.task.security.Authenticator;
import de.bund.bva.isyfact.task.security.AuthenticatorFactory;
import de.bund.bva.isyfact.task.util.TaskCounterBuilder;
import de.bund.bva.isyfact.task.util.TaskId;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IsyTaskAspect {

    /** Isy Logger. **/
    private final IsyLogger logger = IsyLoggerFactory.getLogger(IsyTaskAspect.class);

    /** MeterRegistry. **/
    private final MeterRegistry registry;

    /** HostHandler. **/
    private final HostHandler hostHandler;

    /** IsyConfigurationProperties. **/
    private final IsyTaskConfigurationProperties isyTaskConfigurationProperties;

    /** AuthenticatorFactory. **/
    private final AuthenticatorFactory authenticatorFactory;

    /** Get simple class name function. **/
    private final Function<Throwable, String> throwableClass = (ex) -> ex.getClass().getSimpleName();

    /** MessageSource to determine the messages. **/
    private final MessageSource messageSource;

    public IsyTaskAspect(
            MeterRegistry registry,
            HostHandler hostHandler,
            IsyTaskConfigurationProperties isyTaskConfigurationProperties,
            AuthenticatorFactory authenticatorFactory,
            MessageSource messageSource
    ) {
        this.registry = registry;
        this.hostHandler = hostHandler;
        this.isyTaskConfigurationProperties = isyTaskConfigurationProperties;
        this.authenticatorFactory = authenticatorFactory;
        this.messageSource = messageSource;
    }

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled) || @annotation(de.bund.bva.isyfact.task.annotation.OnceTask)")
    public Object invokeAndMonitorTask(ProceedingJoinPoint pjp) throws Throwable {
        String taskId = TaskId.of(pjp);

        Authenticator authenticator = authenticatorFactory.getAuthenticator(taskId);
        if (authenticator == null) {
            throw new RuntimeException(String.format("Authenticator for task %s is null", taskId));
        }

        String host = null;
        boolean isDeactivated = false; // tasks are enabled unless configured otherwise

        synchronized (this) {
            TaskConfig taskConfig = isyTaskConfigurationProperties.getTasks().get(taskId);
            if (taskConfig != null) {
                isDeactivated = taskConfig.isDeaktiviert();
                host = taskConfig.getHost();
            } else {
                logger.debug("Keine Konfiguration für Task {} gefunden. Es wird auf die Standardwerte zurückgefallen.", taskId);
            }

            // set default values if not provided by task config
            if (host == null) {
                String nachricht = messageSource.getMessage(VERWENDE_STANDARD_KONFIGURATION, new String[] { taskId, "hostname" }, Locale.GERMANY);
                logger.info(LogKategorie.JOURNAL, VERWENDE_STANDARD_KONFIGURATION, nachricht);
                host = isyTaskConfigurationProperties.getDefault().getHost();
            }
            try {
                Pattern.compile(host);
            } catch (PatternSyntaxException pse) {
                throw new TaskKonfigurationInvalidException(taskId, "Hostname ist keine gültige Regex");
            }
        }

        try {
            // Step 1: Set correlation ID.
            MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());

            // Step 2: Check for deactivated task config
            if (isDeactivated) {
                logger.debug(messageSource.getMessage(Ereignisschluessel.TASK_DEAKTIVIERT, new String[] { taskId }, Locale.GERMANY));
                recordFailure(pjp, TaskDeactivatedException.class.getSimpleName());
                return null;
            }

            // Step 3: Check if we are on the right host.
            try {
                if (!hostHandler.isHostApplicable(host)) {
                    // Simply return and do not execute the task.
                    logger.info(LogKategorie.JOURNAL, "ISYTA14101", "Task {0} wird nicht ausgeführt: Hostname muss \"{1}\" entsprechen.", taskId, host);
                    recordFailure(pjp, HostNotApplicableException.class.getSimpleName());
                    return null;
                }
            } catch (HostNotApplicableException hnae) {
                logger.info(LogKategorie.JOURNAL, hnae.getAusnahmeId(), hnae.getMessage());
                recordFailure(pjp, HostNotApplicableException.class.getSimpleName());
                return null;
            }

            // Step 4: Get authenticated.
            try {
                authenticator.login();
            } catch (Exception e) {
                logger.error("ISYTA14100", "Authentifizierung des Tasks {0} fehlgeschlagen. Task wird nicht ausgeführt.", e, taskId);
                return null;
            }

            // Step 5: Execute the task and monitor the result.
            try {
                Object returnValue = pjp.proceed();
                recordSuccess(pjp);
                return returnValue;
            } catch (Exception ex) {
                recordFailure(pjp, throwableClass.apply(ex));
                throw ex;
            }
        } finally {
            authenticator.logout();
            MdcHelper.entferneKorrelationsId();
        }
    }

    private void recordSuccess(ProceedingJoinPoint pjp) {
        try {
            Counter successCounter = TaskCounterBuilder.successCounter(pjp, registry);
            successCounter.increment();
        } catch (Exception e) {
            // do not rethrow!
            logger.warn(Ereignisschluessel.METRIC_WARNUNG, "Could not increment successCounter");

        }
    }

    private void recordFailure(ProceedingJoinPoint pjp, String exceptionClass) {
        try {
            Counter failureCounter = TaskCounterBuilder.failureCounter(pjp, exceptionClass, registry);
            failureCounter.increment();
        } catch (Exception e) {
            // do not rethrow!
            logger.warn(Ereignisschluessel.METRIC_WARNUNG, "Could not increment failureCounter");
        }
    }

}
