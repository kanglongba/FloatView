package com.hangzhou.me.afloat;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class DragonBallService extends Service {

    public static final String DRAGON_BALL_SERVICE_ACTION = "com.hangzhou.me.afloat.DragonBallService";
    public static final String DATA_SHOW_BALL = "data_show_ball";
    public static final int SHOW_BALL = 220924;
    public static final int HIDE_BALL = 220925;


    private View dragonBall;
    private WindowManager.LayoutParams layoutParams;
    private boolean isShow;
    private WindowManager windowManager;

    public DragonBallService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isShow = false;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int operate = intent.getIntExtra(DATA_SHOW_BALL, SHOW_BALL);
        showDragonBall(operate);
        return super.onStartCommand(intent, flags, startId);
    }

    private void showDragonBall(int operate) {
        if (operate == HIDE_BALL) {
            if (dragonBall != null) {
                isShow = false;
                windowManager.removeView(dragonBall);
            }
        } else if (operate == SHOW_BALL) {
            if (isShow) {
                return;
            }
            if (!Settings.canDrawOverlays(this)) {
                ToastUtil.show("请开启悬浮穿权限");
                return;
            }
            if (dragonBall == null || layoutParams == null) {
                inflateBall();
            }
            isShow = true;
            windowManager.addView(dragonBall, layoutParams);
        }
    }

    private void inflateBall() {
        dragonBall = LayoutInflater.from(this).inflate(R.layout.layout_dragon_ball, null, false);

        // 设置LayoutParam
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        // FLAG_LAYOUT_IN_SCREEN 和 FLAG_LAYOUT_INSET_DECOR 可以使浮窗移动到状态栏
        // FLAG_NOT_FOCUSABLE：表示Window不需要获取焦点，也不需要接收各种输入事件，此标记会同时启用FLAG_NOT_TOUCH_MODAL，最终事件会直接传递给下层具有焦点的Window
        // FLAG_NOT_TOUCH_MODAL：在此模式下，系统会将当前Window区域以外的点击事件传递给底层的Window，当前Window区域以内的点击事件则自己处理
        // FLAG_SHOW_WHEN_LOCKED：开启此模式可以让window显示在锁屏界面
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x = 3;
        layoutParams.y = 300;

        dragonBall.setOnTouchListener(new FloatingOnTouchListener(windowManager, layoutParams));
        dragonBall.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(DebugSettingActivity.ACTION);
            intent.setPackage(getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }
}