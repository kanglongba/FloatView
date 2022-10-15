package com.hangzhou.me.afloat;

import android.text.TextUtils;

/**
 * @Author: edison
 * @Email: kanglongba@gmail.com
 * @CreateDate: 2022/9/24 14:45
 * @Description:
 */
public class ConfigManager {

    private static volatile ConfigManager configManager;

    public static ConfigManager get() {
        if (configManager == null) {
            synchronized (ConfigManager.class) {
                if (configManager == null) {
                    configManager = new ConfigManager();
                }
            }
        }
        return configManager;
    }

    private Boolean showDragonBall;
    private Boolean enableProxy;
    private String proxyHttp;

    private ConfigManager() {

    }

    public boolean isShowDragonBall() {
        if (showDragonBall == null) {
            showDragonBall = SpManager.get().getBoolean(DebugPanelConstant.ConfigConstant.SHOW_DRAGON_BALL, false);
        }
        return showDragonBall;
    }

    public void setShowDragonBall(boolean isEnable) {
        if (showDragonBall != null && showDragonBall == isEnable) {
            return;
        }
        showDragonBall = isEnable;
        SpManager.get().putBoolean(DebugPanelConstant.ConfigConstant.SHOW_DRAGON_BALL, isEnable);
    }

    public boolean isProxyEnable() {
        if (enableProxy == null) {
            enableProxy = SpManager.get().getBoolean(DebugPanelConstant.ConfigConstant.ENABLE_PROXY_HTTP, false);
        }
        return enableProxy;
    }

    public void setEnableProxy(boolean isEnable) {
        enableProxy = isEnable;
        SpManager.get().putBoolean(DebugPanelConstant.ConfigConstant.ENABLE_PROXY_HTTP, isEnable);
    }

    public String getProxyHttp() {
        if (proxyHttp == null) {
            proxyHttp = SpManager.get().getString(DebugPanelConstant.ConfigConstant.PROXY_HTTP_URL, "");
        }
        return proxyHttp;
    }

    public void setProxyHttp(String httpUrl) {
        if (TextUtils.isEmpty(httpUrl)) {
            return;
        }
        proxyHttp = httpUrl;
        SpManager.get().putString(DebugPanelConstant.ConfigConstant.PROXY_HTTP_URL, httpUrl);
    }


}
