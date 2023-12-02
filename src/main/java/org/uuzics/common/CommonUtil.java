package org.uuzics.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Shared common utilities
 *
 * @author Uuzics
 */
public class CommonUtil {

    public static void startupInfo() {
        // System.out.println("");
        System.out.println("   _               _                   _                     _     ");
        System.out.println("  (_)___ _   _ ___| | ___   __ _      | |__   ___ _ __   ___| |__  ");
        System.out.println("  | / __| | | / __| |/ _ \\ / _` |_____| '_ \\ / _ \\ '_ \\ / __| '_ \\ ");
        System.out.println("  | \\__ \\ |_| \\__ \\ | (_) | (_| |_____| |_) |  __/ | | | (__| | | |");
        System.out.println(" _/ |___/\\__, |___/_|\\___/ \\__, |     |_.__/ \\___|_| |_|\\___|_| |_|");
        System.out.println("|__/     |___/             |___/                          alpha0.7 ");
    }

    public static void commonPrintTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeString = dateFormat.format(date);
        System.out.println("[INFO]Current time is " + timeString);
    }

    public static void commonPrintLog(int level, String message) {
        String levelString = "[DEFA]";
        if (0 == level) {
            levelString = "[INFO]";
        } else if (1 == level) {
            levelString = "[WARN]";
        } else if (2 == level) {
            levelString = "[ERRO]";
        }
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeString = dateFormat.format(date);
        System.err.println(levelString + " " + timeString + " " + message);
    }
}
