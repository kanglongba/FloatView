package com.hangzhou.me.afloat;

/**
 * @Author: edison
 * @Email: kanglongba@gmail.com
 * @CreateDate: 2022/9/24 12:22
 * @Description:
 */
public interface DebugPanelConstant {

    interface ConfigConstant {
        String SHARE_PREFERENCE_FILE = "array_debug_panel_config";
        String SHOW_DRAGON_BALL = "show_dragon_ball";
        String ENABLE_PROXY_HTTP = "enable_proxy_http";
        String PROXY_HTTP_URL = "pro_http_url";
    }

    interface NotificationConfig {
        String CHANNEL_ID = "array_debug_panel";
        String CHANNEL_NAME = "dragon_ball";
        String CHANNEL_DESCRIPTION = "for array debug";
        String CONTENT_TITLE = "Dragon Ball";
        String CONTENT_TEXT = "Debug Tool for ";
    }

}
