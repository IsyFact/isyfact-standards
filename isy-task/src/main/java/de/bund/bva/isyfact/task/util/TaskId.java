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

import org.aspectj.lang.ProceedingJoinPoint;

public final class TaskId {

    private TaskId() {
    }

    public static String of(ProceedingJoinPoint pjp) {
        String className = pjp.getStaticPart().getSignature().getDeclaringType().getSimpleName();
        String annotatedMethodName = pjp.getStaticPart().getSignature().getName();
        return of(className, annotatedMethodName);
    }

    public static String of(String className, String annotatedMethodName) {
        return String.join("-", firstCharacterToLowercase(className), annotatedMethodName);
    }

    private static String firstCharacterToLowercase(String input) {
        char[] c = input.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }
}
