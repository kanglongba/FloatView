package com.hangzhou.me.floatview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.hangzhou.me.afloat.ToastUtil;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * https://blog.csdn.net/u011033906/article/details/88399782
 * https://codeantenna.com/a/UXYmOFnf5J
 *
 * @Author: edison qian
 * @CreateDate: 2022/11/15 11:27
 * @Description:
 */
public class AppUtils {

    /**
     * 判断是否安装
     * 经过测试，只有申请 QUERY_ALL_PACKAGES 和 queries 权限，才能查到应用是否已安装
     *
     * @param context
     * @param packageName 包名
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        Set<String> pName = new HashSet<>();
        if (pInfo != null && !pInfo.isEmpty()) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                pName.add(pn);
            }
        }
        boolean installed = pName.contains(packageName);
        Log.d("edison", packageName + (installed ? "已安装" : "未安装"));
        return installed;
    }

    /**
     * 判断是否安装
     * <p>
     * 抖音：com.zhiliaoapp.musically
     * 腾讯动漫：com.qq.ac.android
     * WhatsApp：com.whatsapp
     * 经过测试，只有申请 QUERY_ALL_PACKAGES 和 queries 权限，才能查到应用是否已安装
     *
     * @param context
     * @param packageName 包名
     * @return
     */
    public static boolean isAppInstalled2(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        boolean installed = packageInfo != null;
        Log.d("edison", packageName + (installed ? "已安装" : "未安装"));
        return installed;
    }

    /**
     * 安装apk
     * <p>
     * 腾讯动漫：file:///storage/emulated/0/Download/qqcomic_android_10.7.8_dm2017_arm32.apk
     *
     * @param context
     * @param filePath apk文件的路径
     * @return
     */
    public static void install(Context context, String filePath) {
        File apk = new File(filePath);
        if (!apk.exists()) {
            ToastUtil.show("安装包不存在");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", apk);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    /**
     * 安装下载目录的apk：https://blog.csdn.net/qq_37858386/article/details/123075426
     * 遇到异常：android.os.FileUriExposedException
     * 原因：Android N及以上，暴露文件Uri（文件Uri离开应用进程）时会抛异常
     * 处理办法：
     * 1.https://blog.csdn.net/linxinfa/article/details/103975164
     * 2.https://cloud.tencent.com/developer/article/1743070
     * 3.https://juejin.cn/post/6939823618918449166
     * <p>
     * 提前内置apk文件：/storage/emulated/0/Download/qqcomic_android_10.7.8_dm2017_arm32.apk
     * 包名：com.qq.ac.android
     * 启动Activity：com.qq.ac.android.splash.SplashActivity
     * <p>
     * 结果：安装腾讯动漫，解析软件包错误(There was a problem parsing the package)
     * <p>
     * 内置抖音apk文件：/storage/emulated/0/Download/aweme_aweGW_v1015_230101_3fa1_1668172727.apk
     * 结果：安装抖音，解析软件包错误(There was a problem parsing the package)
     * <p>
     * 产生 There was a problem parsing the package 错误的原因：
     * 1.应用没有读写权限，读取不了APK文件
     * 2.Android R以上需要申请：android.permission.MANAGE_EXTERNAL_STORAGE 权限
     * <p>
     * 要想安装APK，以下权限一个都不能少
     * 1.android.permission.READ_EXTERNAL_STORAGE、android.permission.WRITE_EXTERNAL_STORAGE (Android 11以下)
     * 2.android.permission.MANAGE_EXTERNAL_STORAGE （Android 11及以上）
     * 3.android.permission.REQUEST_INSTALL_PACKAGES (Android 8.0及以上，这个权限可以不用在代码中申请，当执行安装代码时，系统会自动弹出授权弹窗）
     *
     * @param context
     * @param apkName apk文件的名字
     */
    public static void installDownloadApk(Context context, String apkName) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator + apkName;
        File apk = new File(path);
        if (!apk.exists()) {
            ToastUtil.show("安装包不存在");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", apk);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    /**
     * 打开App.
     * 从apk包中解析出packageName，然后利用packageName获取到启动Intent
     * <p>
     * 遇到的问题：腾讯动漫和抖音的软件包无法解析。原因最后找到了：因为没有获取到文件的访问权限。授权存储权限以后，就能正常打开了。
     *
     * @param context
     * @param apkPath apk文件的路径
     */
    public static void openApp(Context context, String apkPath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo == null) {
            ToastUtil.show("Apk无法解析");
            return;
        }
        Intent intent = packageManager.getLaunchIntentForPackage(packageInfo.applicationInfo.packageName);
        context.startActivity(intent);
    }

    /**
     * 打开app
     * 经过测试，只有申请 QUERY_ALL_PACKAGES 和 queries 权限，才可以打开App
     *
     * @param context
     * @param packageName
     */
    public static void openApp2(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // 没有权限，intent为null
            return;
        }
        context.startActivity(intent);
    }

    /**
     * https://www.jianshu.com/p/56f606da9f77
     * https://blog.csdn.net/ta_ab/article/details/77949348
     * @param context
     * @param packageName
     */
    public static void uninstallApp(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
