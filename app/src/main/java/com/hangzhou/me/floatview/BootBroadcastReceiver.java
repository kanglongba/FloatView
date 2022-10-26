package com.hangzhou.me.floatview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.hangzhou.me.afloat.ToastUtil;

import java.util.Locale;

/**
 * @Author: edison qian
 * @Email: edison.qian@applovin.com
 * @CreateDate: 2022/10/25 20:31
 * @Description:
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        ToastUtil.show(action +" "+ TelephonyUtils.getCountryCode(context));
        if (TextUtils.equals(action, Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("edison", "BOOT_COMPLETED");
        } else if (TextUtils.equals(action, Intent.ACTION_REBOOT)) {
            Log.d("edison", "REBOOT");
        } else if (TextUtils.equals(action, Intent.ACTION_SHUTDOWN)) {
            Log.d("edison", "SHUTDOWN");
        }
        Log.d("edison", "BootBroadcastReceiver countryCode = " + TelephonyUtils.getCountryCode(context));
    }
}
