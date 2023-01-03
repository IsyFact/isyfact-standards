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

import java.util.UUID;
import java.util.function.Function;

import de.bund.bva.isyfact.task.exception.TaskDeactivatedException;
import de.bund.bva.isyfact.task.konstanten.Ereignisschluessel;
import de.bund.bva.isyfact.task.konstanten.FehlerSchluessel;
import de.bund.bva.isyfact.task.konstanten.HinweisSchluessel;
import de.bund.bva.isyfact.util.spring.MessageSourceHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.task.exception.HostNotApplicableException;
import de.bund.bva.isyfact.task.konfiguration.HostHandler;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfiguration;
import de.bund.bva.isyfact.task.konfiguration.TaskKonfigurationVerwalter;
import de.bund.bva.isyfact.task.util.TaskCounterBuilder;
import de.bund.bva.isyfact.task.util.TaskId;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TaskMonitoringAspect {

    private final IsyLogger logger = IsyLoggerFactory.getLogger(TaskMonitoringAspect.class);

    private final MeterRegistry registry;

    private final HostHandler hostHandler;

    private final Function<Throwable, String> throwableClass = (ex) -> ex.getClass().getSimpleName();

    private final TaskKonfigurationVerwalter konfigurationVerwalter;

    public TaskMonitoringAspect(MeterRegistry registry, HostHandler hostHandler, TaskKonfigurationVerwalter konfigurationVerwalter) {
        this.registry = registry;
        this.hostHandler = hostHandler;
        this.konfigurationVerwalter = konfigurationVerwalter;
    }

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled) || @annotation(de.bund.bva.isyfact.task.annotation.ManualTask)")
    public Object invokeAndMonitorTask(ProceedingJoinPoint pjp) throws Throwable {
        String taskId = TaskId.of(pjp);
        TaskKonfiguration taskKonfiguration = konfigurationVerwalter.getTaskKonfiguration(taskId);

        try {
            // Step 1: Set correlation ID.
            MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());

            // Step 2: Check for deactivated task config
            if (taskKonfiguration.isDeaktiviert()) {

                logger.debug(MessageSourceHolder.getMessage(Ereignisschluessel.TASK_DEAKTIVIERT, taskId));
                recordFailure(pjp, TaskDeactivatedException.class.getSimpleName());
                return null;
            }

            // Step 3: Check if we are on the right host.
            try {
                if (!hostHandler.isHostApplicable(taskKonfiguration.getHostname())) {
                    // Simply return and do not execute the task.
                    logger.info(LogKategorie.JOURNAL, "ISYTA14101", "Task {0} wird nicht ausgeführt: Hostname muss \"{1}\" entsprechen.", taskId, taskKonfiguration.getHostname());
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
                taskKonfiguration.getAuthenticator().login();
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
            taskKonfiguration.getAuthenticator().logout();
            MdcHelper.entferneKorrelationsId();
        }
    }

    private void recordSuccess(ProceedingJoinPoint pjp) {
        try {
            Counter successCounter = TaskCounterBuilder.successCounter(pjp, registry);
            successCounter.increment();
        } catch (Exception e) {
            // do not rethrow!
            // TODO: issue log statement!
        }
    }

    private void recordFailure(ProceedingJoinPoint pjp, String exceptionClass) {
        try {
            Counter failureCounter = TaskCounterBuilder.failureCounter(pjp, exceptionClass, registry);
            failureCounter.increment();
        } catch (Exception e) {
            // do not rethrow!
            // TODO: issue log statement!
        }
    }

}
