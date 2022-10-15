package com.hangzhou.me.afloat;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * @Author: edison
 * @Email: kanglongba@gmail.com
 * @CreateDate: 2022/9/23 11:27
 * @Description:
 */
public class FloatingOnTouchListener implements View.OnTouchListener {
    private int x;
    private int y;
    private WindowManager windowManager;
    WindowManager.LayoutParams layoutParams;


    public FloatingOnTouchListener(WindowManager windowManager, WindowManager.LayoutParams layoutParams) {
        this.windowManager = windowManager;
        this.layoutParams = layoutParams;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getRawX();
                y = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int nowX = (int) event.getRawX();
                int nowY = (int) event.getRawY();
                int movedX = nowX - x;
                int movedY = nowY - y;
                x = nowX;
                y = nowY;
                layoutParams.x = layoutParams.x + movedX;
                layoutParams.y = layoutParams.y + movedY;

                // 更新悬浮窗控件布局
                windowManager.updateViewLayout(view, layoutParams);
                break;
            default:
                break;
        }
        return false;
    }
}
