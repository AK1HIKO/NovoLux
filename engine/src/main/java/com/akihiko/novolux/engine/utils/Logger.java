package com.akihiko.novolux.engine.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 17/11/22
 */
public class Logger {

    //TODO: perform core dump on crash
    //TODO: store all log messages in list

    public static synchronized void log(String owner, String type, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        System.out.printf("%s | %s | %s :: %s", timestamp, type, owner, message);
    }

    public static synchronized void warn(String message) {
        Logger.log(Logger.getCallerClassName(), "WARN", message);
    }

    public static synchronized void error(String message) {
        Logger.log(Logger.getCallerClassName(), "ERROR", message);
    }

    public static synchronized void dumpCore() {
        // TODO: Implement
        System.out.println("DUMPED");
    }


    private static synchronized String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Logger.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                return ste.getClassName();
            }
        }
        return "UNKNOWN";
    }

}
