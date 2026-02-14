package com.gucardev.eventsphere.infrastructure.exception;

import org.slf4j.Logger;

import java.util.Arrays;

public final class ExceptionLogger {

    private static final String BASE_PACKAGE = "com.gucardev.eventsphere";
    public static final int MAX_LENGTH = 200;

    private ExceptionLogger() {}

    /**
     * Logs exception with clickable origin location.
     * Use for non-HTTP contexts (async, scheduler, kafka, etc.)
     */
    public static void logError(Logger log, String context, Throwable ex) {
        String origin = findOrigin(ex);
        String rootCause = getRootCauseMessage(ex);

        log.error("[{}] Exception at {}: {}", context, origin, rootCause, ex);
    }

    /**
     * Logs exception with method parameters for debugging.
     */
    public static void logError(Logger log, String context, Throwable ex,
                                String methodName, Object... params) {
        String origin = findOrigin(ex);
        String rootCause = getRootCauseMessage(ex);
        String paramStr = formatParams(params);

        log.error("[{}] Exception at {} in method '{}' with params {}: {}",
                context, origin, methodName, paramStr, rootCause, ex);
    }

    /**
     * Finds first stack trace element in application code.
     * Returns clickable format: ClassName.method(File.java:123)
     */
    public static String findOrigin(Throwable ex) {
        return Arrays.stream(ex.getStackTrace())
                .filter(e -> e.getClassName().startsWith(BASE_PACKAGE))
                .filter(e -> !e.getClassName().contains(".exception."))
                .filter(e -> !e.getClassName().contains("$$")) // Skip proxies
                .findFirst()
                .map(e -> String.format("%s.%s(%s:%d)",
                        shortClassName(e.getClassName()),
                        e.getMethodName(),
                        e.getFileName(),
                        e.getLineNumber()))
                .orElse("unknown location");
    }

    private static String shortClassName(String fullName) {
        int lastDot = fullName.lastIndexOf('.');
        return lastDot > 0 ? fullName.substring(lastDot + 1) : fullName;
    }

    private static String getRootCauseMessage(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root.getClass().getSimpleName() + ": " + root.getMessage();
    }

    private static String formatParams(Object[] params) {
        if (params == null || params.length == 0) return "[]";

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < params.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(safeToString(params[i], MAX_LENGTH));
        }
        return sb.append("]").toString();
    }

    private static String safeToString(Object obj, int maxLength) {
        if (obj == null) return "null";
        try {
            String str = obj.toString();
            return str.length() > maxLength
                    ? str.substring(0, maxLength) + "..."
                    : str;
        } catch (Exception e) {
            return obj.getClass().getSimpleName() + "(toString failed)";
        }
    }
}