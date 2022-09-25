package com.applovin.me.afloat;

import android.util.Log;

/**
 * @Author: edison qian
 * @Email: edison.qian@applovin.com
 * @CreateDate: 2022/9/25 10:59
 * @Description:
 */
public class PLog {

    public static void d(String tag, String message) {
        if (isDebugMode()) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (isDebugMode()) {
            Log.e(tag, message);
        }
    }

    private static boolean isDebugMode() {
        return true;
    }
}
