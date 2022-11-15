package com.hangzhou.me.floatview;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Android 全局异常捕获之CrashHandler：https://blog.csdn.net/yyh352091626/article/details/50599195
 * Android app 全局异常统一处理：https://github.com/jingboli/CrashHandler
 *
 * @Author: edison
 * @CreateDate: 2022/11/14 20:27
 * @Description:
 */
public class UncaughtExceptionHandlerImpl implements Thread.UncaughtExceptionHandler {

    private static volatile UncaughtExceptionHandlerImpl mCrashHandler;
    private Context mContext;
    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;
    /**
     * 用于存储设备信息
     */
    private Map<String, String> mInfo = new HashMap<>();
    /**
     * 格式化时间，作为Log文件名
     */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");

    public static UncaughtExceptionHandlerImpl getInstance() {
        if (mCrashHandler == null) {
            synchronized (UncaughtExceptionHandlerImpl.class) {
                if (mCrashHandler == null) {
                    mCrashHandler = new UncaughtExceptionHandlerImpl();
                }
            }
        }
        return mCrashHandler;
    }

    public void init(Context context) {
        this.mContext = context;
        // 保存原来的handler
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 注入自己的handler
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        if (isNeedCaught(ex)) {
            handlerException(ex);
        }
        // 交给默认的handler处理
        defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
    }

    private boolean isNeedCaught(Throwable ex) {
        if (ex == null) {
            return false;
        }
        return true;
    }

    private void handlerException(final Throwable ex) {
        //收集错误信息，保存到 sd 卡上
        errorInfo2SD(ex);
        //弹出自定义的错误提醒
        Toast.makeText(mContext, "UnCrashException", Toast.LENGTH_SHORT).show();
        try {
            // 休眠5s，证明当uncaughtException方法未执行完，app不会退出
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 杀掉进程，退出应用
     */
    private void killProcess() {
        Process.killProcess(Process.myPid());
        System.exit(1);
    }

    private void errorInfo2SD(Throwable e) {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            // 获取版本信息
            if (info != null) {
                String versionName = TextUtils.isEmpty(info.versionName) ? "未设置版本名称" : info.versionName;
                String versionCode = info.getLongVersionCode() + "";
                mInfo.put("versionName", versionName);
                mInfo.put("versionCode", versionCode);
            }
            // 获取设备信息
            Field[] fields = Build.class.getFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    mInfo.put(field.getName(), field.get(null).toString());
                }
            }
            // 存储信息到 sd 卡指定目录
            saveErrorInfo(e);
        } catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    private void saveErrorInfo(Throwable e) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : mInfo.entrySet()) {
            String keyName = entry.getKey();
            String value = entry.getValue();
            stringBuffer.append(keyName + "=" + value + "\n");
        }
        stringBuffer.append("\n-----Crash Log Begin-----\n");
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        e.printStackTrace(writer);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(writer);
            cause = e.getCause();
        }
        writer.close();
        String string = stringWriter.toString();
        stringBuffer.append(string);
        stringBuffer.append("\n-----Crash Log End-----");
        String format = dateFormat.format(new Date());
        String fileName = "crash-" + format + ".log";

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // /data/user/0/com.hangzhou.me.floatview/files/crash
            String path = mContext.getFilesDir() + File.separator + "crash";
            Log.d("edison", "crashPath=" + path);
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fou = null;
            try {
                fou = new FileOutputStream(new File(path, fileName));
                fou.write(stringBuffer.toString().getBytes());
                fou.flush();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    if (fou != null) {
                        fou.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
