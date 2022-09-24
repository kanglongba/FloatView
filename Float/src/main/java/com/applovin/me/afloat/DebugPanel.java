package com.applovin.me.afloat;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * 浮窗参考文章：
 * 1.https://zhuanlan.zhihu.com/p/394211126
 * 2.https://cloud.tencent.com/developer/article/1742125
 * 3.https://juejin.cn/post/6951608145537925128
 * 通知栏参考文章：
 * 1.https://developer.android.com/training/notify-user/build-notification?hl=zh-cn
 * 2.https://bbs.huaweicloud.com/blogs/362305
 *
 * @Author: edison qian
 * @Email: edison.qian@applovin.com
 * @CreateDate: 2022/9/23 11:05
 * @Description:
 */
public class DebugPanel {

    private static Application application;

    public static void init(@NonNull Application context) {
        application = context;
        createNotificationChannel(context);
        context.registerActivityLifecycleCallbacks(ActivityLifecycleCallbacksImpl.of());
    }

    public static Application getApplication() {
        return application;
    }

    public static void showBall(Activity activity) {
        if (!ConfigManager.get().isShowDragonBall()) {
            return;
        }
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && !Settings.canDrawOverlays(activity)) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(DragonBallService.DRAGON_BALL_SERVICE_ACTION);
        intent.setPackage(activity.getPackageName());
        intent.putExtra(DragonBallService.DATA_SHOW_BALL, DragonBallService.SHOW_BALL);
        activity.startService(intent);
    }

    /**
     * 可以直接在Application中打开Notification
     *
     * @param context
     */
    public static void showNotify(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.applovin.me.afloat.DebugSettingActivity");
        intent.setPackage(context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "DebugPanel")
                .setSmallIcon(R.drawable.ball)
                .setContentTitle("DebugPanel")
                .setContentText("debug")
                .setContentIntent(pendingIntent)
                // true，Notification点击后消失；false，点击后不消失
                .setAutoCancel(false)
                // true，用户不能滑动移除Notification，也不能使用通知栏的清除键移除通知，只能通过代码调用cancel方法移除
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(20220923, builder.build());

    }

    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "DragonBall";
            String description = "ball";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("DebugPanel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
