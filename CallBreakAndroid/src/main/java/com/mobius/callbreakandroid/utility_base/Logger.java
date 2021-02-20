package com.mobius.callbreakandroid.utility_base;

import android.util.Log;

//import com.crashlytics.android.Crashlytics;

//import io.fabric.sdk.android.Fabric;

public class Logger {

    public static void print(String msg) {

        try {
            if (msg.length() > 4000) {
                String tag = msg.substring(0, msg.indexOf(":"));

                msg = msg.replace(tag + ": ", "");
                int chunkCount = msg.length() / 4000;     // integer division

                for (int i = 0; i <= chunkCount; i++) {
                    int max = 4000 * (i + 1);

                    if (max >= msg.length()) {
                        printLog("_IR_C", tag + ": " + i + " = " + msg.substring(4000 * i) + "\n");

                    } else {
                        printLog("_IR_C", tag + ": " + i + " - " + msg.substring(4000 * i, max) + "\n");
                    }
                }

            } else {
                printLog("_IR_C", msg);
            }

        } catch (Exception e) {
            Log.e("_IR_C", "LOG_C : ", e);
        }
    }

    private static void printLog(String TAG, String msg) {
//        if (Fabric.isInitialized()) {
//            Crashlytics.log(Log.DEBUG, TAG, msg);
//        } else {
            Log.e(TAG, msg);
//        }
    }

    public static void print(String tag, String msg) {
        try {
            if (msg.length() > 4000) {

                msg = msg.replace(tag + ": ", "");
                int chunkCount = msg.length() / 4000;     // integer division

                for (int i = 0; i <= chunkCount; i++) {
                    int max = 4000 * (i + 1);

                    if (max >= msg.length()) {
                        printLog("_IR_", tag + ": " + i + " = " + msg.substring(4000 * i) + "\n");

                    } else {
                        printLog("_IR_", tag + ": " + i + " - " + msg.substring(4000 * i, max) + "\n");
                    }
                }

            } else {
                printLog("_IR_", msg);
            }
        } catch (Exception e) {
            Log.e("_IR_C", "LOG_ : ", e);
        }
    }

}
