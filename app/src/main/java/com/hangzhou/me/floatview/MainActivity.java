package com.hangzhou.me.floatview;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.security.NetworkSecurityPolicy;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hangzhou.me.afloat.DebugPanel;
import com.hangzhou.me.afloat.ToastUtil;
import com.hangzhou.me.floatview.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onClick();
        isCleartextTrafficPermitted();
//        getStorageInfo();
//        testCompare();
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
        binding.countryCodeBtn.setOnClickListener(v -> {
            Log.d("edison", "countryCode=" + TelephonyUtils.getCountryCode(MainActivity.this));
        });
        binding.queryApp.setOnClickListener(v -> {
            Log.d("edison", "queryApp=" + getAppInfo("com.zhiliaoapp.musically"));
        });
        binding.testCrash.setOnClickListener(v -> {
            Log.d("edison", word.length() + "");
        });
        binding.installApk.setOnClickListener(v -> {
            installAndLaunchApp();
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
     * tiktok:com.zhiliaoapp.musically
     * 快手：com.kwai.video
     * 腾讯动漫：com.qq.ac.android
     * WhatsApp：com.whatsapp
     * 拿到 launchable-activity：
     * ./aapt dump badging /Users/edison/Downloads/qqcomic_android_10.7.8_dm2017_arm32.apk
     */
    private void installAndLaunchApp() {
//        AppUtils.installDownloadApk(MainActivity.this, "aweme_aweGW_v1015_230101_3fa1_1668172727.apk");
//        AppUtils.isAppInstalled2(MainActivity.this, "com.kwai.video");
//        AppUtils.openApp(MainActivity.this, "/storage/emulated/0/Download/qqcomic_android_10.7.8_dm2017_arm32.apk");
//        AppUtils.openApp2(MainActivity.this, "com.qq.ac.android");
        Intent intent = new Intent();
        intent.setClassName("com.qq.ac.android", "com.qq.ac.android.splash.SplashActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

    /**
     * 1.经过对比：android:usesCleartextTraffic="true" 比 android:networkSecurityConfig="@xml/network_security_config" 优先级低
     * 二者同时设置，android:usesCleartextTraffic="true" 会无效。
     * 2.使用其他方module的配置文件，如debug_network_security_config.xml，也不行，因为多个xml文件只有一个能生效。最终会被app的模块的xml文件替换
     */
    private void isCleartextTrafficPermitted() {
        Log.d("edison", "是否允许明文传输：" + NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted());
        Log.d("edison", "是否允许192.168.230.56明文传输：" + NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted("192.168.230.56"));
        Log.d("edison", "是否允许192.168.230.57明文传输：" + NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted("192.168.230.57"));
    }

    /**
     * com.applovin.array.apphub.samsung
     * com.zhiliaoapp.musically
     *
     * @param packageName
     * @return
     */
    private String getAppInfo(String packageName) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject appInfo = new JSONObject();
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            appInfo.put("package_name", packageInfo.packageName);
            appInfo.put("version_code", packageInfo.getLongVersionCode());
            jsonObject.put("app_info", appInfo);
            return jsonObject.toString();
        } catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * 存储空间：
     * Environment.getRootDirectory().getTotalSpace()=6358536192
     * Environment.getRootDirectory().getFreeSpace()=75624448
     * Environment.getRootDirectory().getAbsolutePath()=/system
     * Environment.getStorageDirectory().getTotalSpace()=3421097984
     * Environment.getStorageDirectory().getFreeSpace()=3421097984
     * Environment.getStorageDirectory().getAbsolutePath()=/storage
     * Environment.getExternalStorageDirectory().getTotalSpace()=115008827392
     * Environment.getExternalStorageDirectory().getFreeSpace()=98139074560
     * Environment.getExternalStorageDirectory().getAbsolutePath()=/storage/emulated/0
     * Environment.getDownloadCacheDirectory().getTotalSpace()=115008827392
     * Environment.getDownloadCacheDirectory().getFreeSpace()=98139074560
     * Environment.getDownloadCacheDirectory().getAbsolutePath()=/data/cache
     * Environment.getDataDirectory().getTotalSpace()=115008827392
     * Environment.getDataDirectory().getFreeSpace()=98139074560
     * Environment.getDataDirectory().getAbsolutePath()=/data
     * TotalSpace=115 GB
     * FreeSpace=98.14 GB
     * Environment.getExternalStorageState()=mounted
     */
    private void getStorageInfo() {
        Log.d("edison", "Environment.getRootDirectory().getTotalSpace()=" + Environment.getRootDirectory().getTotalSpace());
        Log.d("edison", "Environment.getRootDirectory().getFreeSpace()=" + Environment.getRootDirectory().getFreeSpace());
        Log.d("edison", "Environment.getRootDirectory().getAbsolutePath()=" + Environment.getRootDirectory().getAbsolutePath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.getStorageDirectory().getTotalSpace();
            Log.d("edison", "Environment.getStorageDirectory().getTotalSpace()=" + Environment.getStorageDirectory().getTotalSpace());
            Log.d("edison", "Environment.getStorageDirectory().getFreeSpace()=" + Environment.getStorageDirectory().getFreeSpace());
            Log.d("edison", "Environment.getStorageDirectory().getAbsolutePath()=" + Environment.getStorageDirectory().getAbsolutePath());
        }
        Log.d("edison", "Environment.getExternalStorageDirectory().getTotalSpace()=" + Environment.getExternalStorageDirectory().getTotalSpace());
        Log.d("edison", "Environment.getExternalStorageDirectory().getFreeSpace()=" + Environment.getExternalStorageDirectory().getFreeSpace());
        Log.d("edison", "Environment.getExternalStorageDirectory().getAbsolutePath()=" + Environment.getExternalStorageDirectory().getAbsolutePath());

        Log.d("edison", "Environment.getDownloadCacheDirectory().getTotalSpace()=" + Environment.getDownloadCacheDirectory().getTotalSpace());
        Log.d("edison", "Environment.getDownloadCacheDirectory().getFreeSpace()=" + Environment.getDownloadCacheDirectory().getFreeSpace());
        Log.d("edison", "Environment.getDownloadCacheDirectory().getAbsolutePath()=" + Environment.getDownloadCacheDirectory().getAbsolutePath());
        long ts = Environment.getDataDirectory().getTotalSpace();
        long fs = Environment.getDataDirectory().getFreeSpace();
        Log.d("edison", "Environment.getDataDirectory().getTotalSpace()=" + ts);
        Log.d("edison", "Environment.getDataDirectory().getFreeSpace()=" + fs);
        Log.d("edison", "Environment.getDataDirectory().getAbsolutePath()=" + Environment.getDataDirectory().getAbsolutePath());
        // 随数字大小变化，可能是KB、MB、GB
        Log.d("edison", "TotalSpace=" + Formatter.formatFileSize(this, ts));
        Log.d("edison", "FreeSpace=" + fs / (1024 * 1024));
        Log.d("edison", "Environment.getExternalStorageState()=" + Environment.getExternalStorageState());
    }

    /**
     * https://developer.android.com/training/basics/network-ops/reading-network-state?hl=zh-cn
     * https://www.jianshu.com/p/10ed9ae02775
     */
    private void getNetworkInfo() {

    }

    public class Person {
        int age;
        String name;

        public Person(int age, String name) {
            this.age = age;
            this.name = name;
        }

        @NonNull
        @Override
        public String toString() {
            return "[name="+name+",age="+age+"]";
        }
    }
    private void testCompare() {
        List<Person> list = new ArrayList<>();
        list.add(new Person(70,"拜登"));
        list.add(new Person(60,"川普"));
        list.add(new Person(80,"查尔斯"));
        list.add(new Person(50,"马克龙"));
        // o1-o2 从小到大，o2-o1 从大到小

        list.sort((o1, o2) -> Integer.compare(o2.age,o1.age));
        list.stream().forEach(person -> {
            Log.d("edison", person.toString());
        });
    }
}