package com.applovin.me.afloat;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.applovin.me.afloat.databinding.ActivityDebugSettingBinding;

/**
 * 开源图标：https://fonts.google.com/icons?selected=Material+Icons
 * SwitchCompat：https://www.jianshu.com/p/85f9ac2303e7
 *
 */
public class DebugSettingActivity extends AppCompatActivity {
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 220923;

    private ActivityDebugSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //没有效果
        binding = ActivityDebugSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide(); //隐藏titleBar，有用
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