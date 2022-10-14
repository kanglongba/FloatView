package com.applovin.me.afloat;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.security.NetworkSecurityPolicy;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.applovin.me.afloat.databinding.ActivityDebugSettingBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 开源图标：https://fonts.google.com/icons?selected=Material+Icons
 * SwitchCompat：https://www.jianshu.com/p/85f9ac2303e7
 */
public class DebugSettingActivity extends AppCompatActivity {
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 220923;
    private static final String TAG = "DebugSetting";
    public static final String ACTION = "com.applovin.me.afloat.DebugSettingActivity";

    private ActivityDebugSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //没有效果
        binding = ActivityDebugSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide(); //隐藏titleBar，有用，但是推荐通过Theme做
        initViews();
        initClick();
    }

    /**
     * 等同于 hideSystemBars() 方法，只不过使用了Compat包，自动兼容低版本
     */
    private void setFullScreen() {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        // 隐藏导航栏
        controller.hide(WindowInsetsCompat.Type.navigationBars());
        // 隐藏状态栏
        controller.hide(WindowInsetsCompat.Type.statusBars());
    }

    /**
     * 1.https://medium.com/swlh/modifying-system-ui-visibility-in-android-11-e66a4128898b
     * 2.https://itcn.blog/p/3656326300.html
     */
    private void hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 同时隐藏状态栏和导航栏
            getWindow().getInsetsController().hide(WindowInsets.Type.systemBars());
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private void initViews() {
        binding.ballSwitch.setChecked(ConfigManager.get().isShowDragonBall());
        binding.proxySwitch.setChecked(ConfigManager.get().isProxyEnable());
        showProxyLayout(ConfigManager.get().isProxyEnable());
    }

    private void initClick() {
        binding.backImage.setOnClickListener(v -> finish());
        binding.ballSwitch.setOnCheckedChangeListener((compoundButton, checked) -> {
            ConfigManager.get().setShowDragonBall(checked);
            if (checked) {
                startDragonBallService();
            } else {
                hideBall();
            }
        });
        binding.proxySwitch.setOnCheckedChangeListener((compoundButton, checked) -> {
            setCleartextTrafficPermitted(checked);
            ConfigManager.get().setEnableProxy(checked);
            showProxyLayout(checked);
        });
        binding.saveBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.proxyEdit.getText())) {
                ToastUtil.show("请输入ip地址");
            } else {
                ConfigManager.get().setProxyHttp(binding.proxyEdit.getText().toString());
                ToastUtil.show("ip地址已保存");
            }
        });
    }

    /**
     * 开启支持Http
     * 从API 28开始，系统默认情况下已停用明文支持。
     * 从API 23开始，系统添加了NetworkSecurityPolicy类。
     * 参考文章
     * 1.https://developer.android.google.cn/training/articles/security-config.html#CleartextTrafficPermitted
     * 2.https://www.jianshu.com/p/11992edd61e7
     * 3.https://www.cnblogs.com/renhui/p/14214996.html
     * 4.https://blog.csdn.net/firedancer0089/article/details/82969969
     *
     * Android P对反射调用隐藏api做了限制，无法反射得到setCleartextTrafficPermitted方法
     * 1.https://www.cnblogs.com/renhui/p/14214996.html
     * 2.现有的突破方案，实践都不行：https://github.com/tiann/FreeReflection
     *
     * 通过反射不能达到目的，只能在network_security_config.xml文件中配置规则
     *
     * NetworkSecurityPolicy
     *
     * @param permitted
     */
    private void setCleartextTrafficPermitted(boolean permitted) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Class<?> clazz = Class.forName("android.security.NetworkSecurityPolicy");
                Method method = clazz.getMethod("setCleartextTrafficPermitted", Boolean.class);
                method.setAccessible(true);
                method.invoke(NetworkSecurityPolicy.getInstance(), permitted);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                PLog.e(TAG, "ClassNotFoundException");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                PLog.e(TAG, "NoSuchMethodException");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                PLog.e(TAG, "IllegalAccessException");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                PLog.e(TAG, "InvocationTargetException");
            }
        }
    }

    private void showProxyLayout(boolean isShow) {
        if (isShow) {
            binding.proxyEdit.setVisibility(View.VISIBLE);
            binding.saveBtn.setVisibility(View.VISIBLE);
            binding.proxyEdit.setText(ConfigManager.get().getProxyHttp());
        } else {
            binding.proxyEdit.setVisibility(View.GONE);
            binding.saveBtn.setVisibility(View.GONE);
        }
    }

    /**
     * 动态申请权限
     */
    public void startDragonBallService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                ToastUtil.show("当前无权限，请授权");
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), OVERLAY_PERMISSION_REQUEST_CODE);
            } else {
                showBall();
            }
        } else {
            showBall();
        }
    }

    /**
     * 动态申请权限
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                showBall();
            }
        }
    }

    private void showBall() {
        Intent intent = new Intent();
        intent.setAction(DragonBallService.DRAGON_BALL_SERVICE_ACTION);
        intent.setPackage(getPackageName());
        intent.putExtra(DragonBallService.DATA_SHOW_BALL, DragonBallService.SHOW_BALL);
        startService(intent);
    }

    private void hideBall() {
        Intent intent = new Intent();
        intent.setAction(DragonBallService.DRAGON_BALL_SERVICE_ACTION);
        intent.setPackage(getPackageName());
        intent.putExtra(DragonBallService.DATA_SHOW_BALL, DragonBallService.HIDE_BALL);
        startService(intent);
    }
}