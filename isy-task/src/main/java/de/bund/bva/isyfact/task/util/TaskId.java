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
