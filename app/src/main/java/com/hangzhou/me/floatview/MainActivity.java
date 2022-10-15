package com.hangzhou.me.floatview;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hangzhou.me.afloat.DebugPanel;
import com.hangzhou.me.afloat.ToastUtil;
import com.hangzhou.me.floatview.databinding.ActivityMainBinding;

import java.util.Collections;
import java.util.List;

/**
 * Android 13 Notification适配：
 * 1.https://developer.android.com/guide/topics/ui/notifiers/notification-permission?hl=zh-CN
 * 2.https://www.jianshu.com/p/f0d390c2751e
 * 3.https://juejin.cn/post/7099762078977622053#heading-1
 * <p>
 * Android 13 迁移指南：
 * 1.https://developer.android.com/about/versions/13/migration?hl=zh-CN
 */
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onClick();
    }

    private void onClick() {
        binding.showBall.setOnClickListener(v -> {
            DebugPanel.showBall(MainActivity.this);
        });
        binding.sendNotification.setOnClickListener(v -> {
            DebugPanel.showNotify(MainActivity.this);
        });
        binding.startSettingBtn.setOnClickListener(v -> {
            startSettings();
        });
        binding.registerBtn.setOnClickListener(v -> {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Android13BroadcastReceiver.ACTION);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(new Android13BroadcastReceiver(), intentFilter);
        });
        binding.sendBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Android13BroadcastReceiver.ACTION);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setPackage(getPackageName());
            sendBroadcast(intent);
        });
        binding.openBtn.setOnClickListener(v -> {
            launchApp("com.tencent.edison.router");
        });
        binding.grantNotificationBtn.setOnClickListener(v -> {
            requestNotificationPermission(MainActivity.this);
        });
    }

    /**
     * 根据包名，打开目标app。需要注册QUERY_ALL_PACKAGES权限，或者通过<queries>声明目标软件，否则无效。
     * https://developer.android.com/training/package-visibility
     * https://medium.com/androiddevelopers/package-visibility-in-android-11-cc857f221cd9
     *
     * @param packageName
     */
    public void launchApp(String packageName) {
        Intent intent = new Intent();
        intent.setPackage(packageName);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));

        if (resolveInfos.size() > 0) {
            ResolveInfo launchable = resolveInfos.get(0);
            ActivityInfo activity = launchable.activityInfo;
            ComponentName name = new ComponentName(activity.applicationInfo.packageName,
                    activity.name);
            Intent intent1 = new Intent(Intent.ACTION_MAIN);

            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent1.setComponent(name);

            startActivity(intent1);
        }
    }

    /**
     * 打开软件设置页面
     */
    public void startSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    /**
     * 请求通知权限
     *
     * @param activity
     */
    public void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33) {
            // 检测是否已获取权限
            if (ActivityCompat.checkSelfPermission(activity, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                //是否需要展示中间屏幕（系统授权弹窗）
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, POST_NOTIFICATIONS)) {
                    //请求权限，会打开系统授权弹窗，通过onRequestPermissionsResult()方法接收授权结果
                    ActivityCompat.requestPermissions(activity, new String[]{POST_NOTIFICATIONS}, 20221015);
                } else {
                    //跳转到应用设置页面，引导用户开启通知权限
                    openNotificationSetting(activity);
                }
            }
        } else {
            //检测是否已打开通知开关
            boolean enabled = NotificationManagerCompat.from(activity).areNotificationsEnabled();
            if (!enabled) {
                openNotificationSetting(activity);
            }
        }
    }

    /**
     * 跳转到应用通知的设置页面
     *
     * @param context
     */
    public void openNotificationSetting(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
        startActivityForResult(intent, 2022101521);
    }

    /**
     * 接收设置页面的返回通知
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("edison", "onActivityResult");
        if (requestCode == 2022101521) {
            boolean enabled = NotificationManagerCompat.from(MainActivity.this).areNotificationsEnabled();
            Log.d("edison", "resultCode=" + resultCode + (enabled ? ", 通知开关打开" : "，通知开关关闭"));
            ToastUtil.show(enabled ? "通知开关打开" : "通知开关关闭");
        }
    }

    /**
     * 接收ActivityCompat.requestPermissions()方法的授权结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("edison", "onRequestPermissionsResult");
        if (requestCode == 20221015) {
            if (TextUtils.equals(permissions[0], POST_NOTIFICATIONS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("edison", "用户授权通知权限");
                ToastUtil.show("用户授权通知权限");
            } else {
                //除了明确拒绝，用户把授权弹窗滑走，也会收到拒绝
                Log.d("edison", "用户拒绝通知权限");
                ToastUtil.show("用户拒绝通知权限");
            }
        }
    }
}