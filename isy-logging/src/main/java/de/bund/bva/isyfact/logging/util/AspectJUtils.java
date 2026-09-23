package de.bund.bva.isyfact.logging.util;

public final class AspectJUtils {

    public static <T> T aspectOf(Class<T> aspectClass) {
        try {
            return (T) aspectClass
                    .getMethod("aspectOf")
                    .invoke(null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private AspectJUtils() {
    }
}
