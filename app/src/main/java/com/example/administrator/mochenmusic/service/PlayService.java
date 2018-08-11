package com.example.administrator.mochenmusic.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.administrator.mochenmusic.appliction.Notifier;
import com.example.administrator.mochenmusic.constants.Actions;

/**
 * 音乐播放后台服务
 * Created by wcy on 2015/11/27.
 */
public class PlayService extends Service {
    private static final String TAG = "Service";

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: " + getClass().getSimpleName());
        AudioPlayer.get().init(this);
        MediaSessionManager.get().init(this);
        Notifier.get().init(this);
//        QuitTimer.get().init(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    public static void startCommand(Context context, String action) {
        Intent intent = new Intent(context, PlayService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//当服务被启动的时候，就会调用这个方法
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Actions.ACTION_STOP://当我们点击侧边栏的退出应用，应该把通知栏也去掉，退出，
                    stop();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private void stop() {
        AudioPlayer.get().stopPlayer();
        QuitTimer.get().stop();
        Notifier.get().cancelAll();
    }
}

