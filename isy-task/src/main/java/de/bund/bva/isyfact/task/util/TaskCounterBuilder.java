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

package de.bund.bva.isyfact.task.util;

import java.util.function.Function;

import org.aspectj.lang.ProceedingJoinPoint;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

public final class TaskCounterBuilder {

    /** Default tags. **/
    private static final Function<ProceedingJoinPoint, Iterable<Tag>> DEFAULT_TAGS;

    private TaskCounterBuilder() {
    }

    static {
        DEFAULT_TAGS = pjp -> Tags.of("class", pjp.getStaticPart().getSignature().getDeclaringType().getSimpleName(), "method", pjp.getStaticPart().getSignature().getName());
    }

    public static Counter successCounter(ProceedingJoinPoint pjp, MeterRegistry registry) {
        return Counter.builder(TaskId.of(pjp) + ".success").tags(DEFAULT_TAGS.apply(pjp)).register(registry);
    }

    public static Counter successCounter(String className, String methodName, MeterRegistry registry) {
        return Counter.builder(TaskId.of(className, methodName) + ".success").tags("class", className, "method", methodName).register(registry);
    }

    public static Counter failureCounter(ProceedingJoinPoint pjp, String exceptionClassName, MeterRegistry registry) {
        return Counter.builder(TaskId.of(pjp) + ".failure").tags(DEFAULT_TAGS.apply(pjp)).tag("exception", exceptionClassName).register(registry);
    }

    public static Counter failureCounter(String className, String methodName, String exceptionClassName, MeterRegistry registry) {
        return Counter.builder(TaskId.of(className, methodName) + ".failure").tags("class", className, "method", methodName, "exception", exceptionClassName).register(registry);
    }

}
