package com.applovin.me.afloat;

import android.widget.Toast;

/**
 * @Author: edison qian
 * @Email: edison.qian@applovin.com
 * @CreateDate: 2022/9/24 16:15
 * @Description:
 */
public class ToastUtil {

    public static void show(String message) {
        Toast.makeText(DebugPanel.getApplication(), message, Toast.LENGTH_SHORT).show();
    }
}
