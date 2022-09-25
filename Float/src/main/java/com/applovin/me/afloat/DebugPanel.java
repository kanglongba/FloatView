package com.applovin.me.afloat;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
        intent.setAction(DebugSettingActivity.ACTION);
        intent.setPackage(context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri uri = Uri.parse("https://zh.wikipedia.org/wiki/%E4%B8%83%E9%BE%99%E7%8F%A0");
        Intent openUrl = new Intent(Intent.ACTION_VIEW, uri);
        PendingIntent openIntent = PendingIntent.getActivity(context,
                0,
                openUrl,
                0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, DebugPanelConstant.NotificationConfig.CHANNEL_ID)
                .setSmallIcon(R.drawable.ball)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.goku))
                .setContentTitle(DebugPanelConstant.NotificationConfig.CONTENT_TITLE)
                .setContentText(DebugPanelConstant.NotificationConfig.CONTENT_TEXT)
                .setContentIntent(pendingIntent)
                // true，Notification点击后消失；false，点击后不消失
                .setAutoCancel(false)
                // true，用户不能滑动移除Notification，也不能使用通知栏的清除键移除通知，只能通过代码调用cancel方法移除
                .setOngoing(true)
                // 添加操作按钮
//                .addAction(R.drawable.ic_baseline_favorite_24, "make a wish", openIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(20220923, builder.build());

    }

    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = DebugPanelConstant.NotificationConfig.CHANNEL_NAME;
            String description = DebugPanelConstant.NotificationConfig.CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(DebugPanelConstant.NotificationConfig.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
