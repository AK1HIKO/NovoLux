package com.akihiko.novolux.engine.utils;

import com.akihiko.novolux.ecs.ECSRuntimeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Advanced NovoLux logger. Unless explicitly stated, will automatically derive the caller class and set it as "owner" during the logging.
 * @author AK1HIKO
 * @project NovoLux
 * @created 17/11/22
 */
public class Logger {

    private static StringBuilder logList = new StringBuilder();

    public static synchronized void log(String owner, String type, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        String fmsg = String.format("%s | %s | %s :: %s%n", timestamp, type, owner, message);
        Logger.logList.append(fmsg);
        System.out.printf(fmsg);
    }

    public static synchronized void warn(String message) {
        Logger.log(Logger.getCallerClassName(), "WARN", message);
    }

    public static synchronized void info(String message) {
        Logger.log(Logger.getCallerClassName(), "INFO", message);
    }

    public static synchronized void error(String message) {
        Logger.log(Logger.getCallerClassName(), "ERROR", message);
    }

    public static synchronized void dumpCore() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss_SSS"));
            Files.writeString(Paths.get(String.format("LOG_%s.log", timestamp)), logList.toString());
        } catch (IOException e) {
            throw new ECSRuntimeException(e.getMessage());
        }
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
