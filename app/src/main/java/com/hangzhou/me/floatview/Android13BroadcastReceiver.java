package com.hangzhou.me.floatview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hangzhou.me.afloat.ToastUtil;

/**
 * Android13，在开发者选项中的App Compatibility Changes中，开启 DYNAMIC_RECEIVER_EXPLICIT_EXPORT_REQUIRED 后，在代码中为不受保护的广播注册接收
 * 器（动态注册广播接收器）时，需要应用显式设置 Context.RECEIVER_EXPORTED 或 Context.RECEIVER_NOT_EXPORTED，否则应用会Crash。
 *
 * 但是在正式环境中，不必要适配，因为 DYNAMIC_RECEIVER_EXPLICIT_EXPORT_REQUIRED 默认对所有应用都是关闭的。并且官方的适配文档也没有提及这个适配。
 *
 * 参考：
 * 1.https://developer.android.com/about/versions/13/reference/compat-framework-changes?authuser=0&hl=zh-cn#dynamic_receiver_explicit_export_required
 *
 * 动态注册广播接收器
 * @Author: edison
 * @Email: kanglongba@gmail.com
 * @CreateDate: 2022/10/15 20:38
 * @Description:
 */
public class Android13BroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION = "com.hangzhou.me.floatview.Android13BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("edison", "Android13BroadcastReceiver onReceive");
        ToastUtil.show("收到广播");
    }
}
