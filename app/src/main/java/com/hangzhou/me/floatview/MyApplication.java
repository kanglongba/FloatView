package com.hangzhou.me.floatview;

import android.app.Application;

import androidx.annotation.Nullable;

import com.hangzhou.me.afloat.DebugPanel;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogcatLogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * @Author: edison
 * @Email: kanglongba@gmail.com
 * @CreateDate: 2022/9/23 17:41
 * @Description:
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DebugPanel.init(this);
        DebugPanel.showNotify(getApplicationContext());
        UncaughtExceptionHandlerImpl.getInstance().init(this);
        initLogger();
    }

    /**
     * https://github.com/orhanobut/logger
     */
    public void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(3)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .logStrategy(new LogcatLogStrategy()) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("My custom tag")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return BuildConfig.DEBUG;
            }
        });

        FormatStrategy csvFormatStrategy = CsvFormatStrategy.newBuilder()
                .tag("custom disk")
                .build();

        Logger.addLogAdapter(new DiskLogAdapter(csvFormatStrategy));
    }
}
