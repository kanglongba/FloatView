package com.applovin.me.afloat;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 参考文章：
 * 1.https://developer.android.com/training/data-storage/shared-preferences?hl=zh-cn
 *
 * @Author: edison qian
 * @Email: edison.qian@applovin.com
 * @CreateDate: 2022/9/24 14:45
 * @Description:
 */
public class SpManager {

    private static volatile SpManager INSTANCE;

    public static SpManager get() {
        if (INSTANCE == null) {
            synchronized (SpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SpManager();
                }
            }
        }
        return INSTANCE;
    }

    private SharedPreferences sp;

    private SpManager() {
        sp = DebugPanel.getApplication().getSharedPreferences(DebugPanelConstant.ConfigConstant.SHARE_PREFERENCE_FILE, Context.MODE_PRIVATE);
    }

    public void putString(String name, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public String getString(String name, String defaultValue) {
        return sp.getString(name, defaultValue);
    }

    public void putInt(String name, int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    public int getInt(String name, int defaultValue) {
        return sp.getInt(name, defaultValue);
    }

    public void putLong(String name, long value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(name, value);
        editor.apply();
    }

    public long getLong(String name, long defaultValue) {
        return sp.getLong(name, defaultValue);
    }

    public void putFloat(String name, float value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(name, value);
        editor.apply();
    }

    public float getLong(String name, float defaultValue) {
        return sp.getFloat(name, defaultValue);
    }

    public void putBoolean(String name, boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        return sp.getBoolean(name, defaultValue);
    }

}
