package com.akihiko.novolux.engine.utils;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 17/11/22
 */
public class Logger {

    //TODO: perform core dump on crash
    //TODO: store all log messages in list

    public static synchronized void log(String message){
        System.out.println(message);
    }

    public static synchronized void dumpCore(){
        // TODO: Implement
        System.out.println("DUMPED");
    }

}
