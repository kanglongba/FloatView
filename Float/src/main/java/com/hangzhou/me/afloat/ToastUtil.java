package com.hangzhou.me.afloat;

import android.widget.Toast;

/**
 * @Author: edison
 * @Email: kanglongba@gmail.com
 * @CreateDate: 2022/9/24 16:15
 * @Description:
 */
public class ToastUtil {

    public static void show(String message) {
        Toast.makeText(DebugPanel.getApplication(), message, Toast.LENGTH_SHORT).show();
    }
}
