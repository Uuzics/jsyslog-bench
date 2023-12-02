/*
 * Copyright 2021 Uuzics
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uuzics.common;

import java.text.SimpleDateFormat;
import java.util.Date;

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
