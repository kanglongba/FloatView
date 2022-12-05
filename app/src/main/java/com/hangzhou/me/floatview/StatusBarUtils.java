package com.hangzhou.me.floatview;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

/**
 * https://www.jianshu.com/p/76d55b6e02d1
 * 一个Android沉浸式状态栏上的黑科技：https://cloud.tencent.com/developer/article/2024130
 * https://juejin.cn/post/7099436641449672735
 * https://blog.51cto.com/jun5753/4937977
 * https://www.51cto.com/article/704113.html
 * https://stars-one.site/2022/08/28/android-immerse-statusbar
 *
 * @Author: edison
 * @CreateDate: 2022/12/5 20:45
 * @Description:
 */
public class StatusBarUtils {

    public static void transparentStatusBar(@NonNull final Window window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            int vis = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(option | vis);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
