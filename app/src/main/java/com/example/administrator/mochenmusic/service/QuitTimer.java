package com.example.administrator.mochenmusic.service;

import android.content.Context;
import android.os.Handler;
import android.text.format.DateUtils;

import com.example.administrator.mochenmusic.appliction.AppCache;
import com.example.administrator.mochenmusic.constants.Actions;

/**
 * 时间轴
 */
public class QuitTimer {
    private Context mContext;
    private Handler handler;
    private long timeDown;
    private OnTimerListener onTimerListener;

    public interface OnTimerListener{
        void onTimer(long time);
    }

    public void setOnTimerListener(OnTimerListener onTimerListener) {
        this.onTimerListener = onTimerListener;
    }

    private static class SingletonHolder{
        private static QuitTimer quitTimer = new QuitTimer();
    }

    public static QuitTimer get() {
         return SingletonHolder.quitTimer;
    }

    private QuitTimer() {
    }

    public void init(Context context) {
        this.mContext=context;
        this.handler = new Handler(context.getMainLooper());
    }

    public void Start(int time) {
        stop();
        if (time > 0) {
            timeDown=time+DateUtils.SECOND_IN_MILLIS;
            handler.postDelayed(timeRunnable, DateUtils.SECOND_IN_MILLIS);
        }else{
            timeDown=0;
            if (onTimerListener != null) {
                onTimerListener.onTimer(timeDown);//更新倒计时时间
            }
        }
    }
        Runnable timeRunnable=new Runnable() {
            @Override
            public void run() {
                timeDown -= DateUtils.SECOND_IN_MILLIS;
                if (timeDown > 0) {
                    if (onTimerListener != null) {
                        onTimerListener.onTimer(timeDown);
                    }
                    handler.postDelayed(this, DateUtils.SECOND_IN_MILLIS);
                } else {
                    AppCache.get().clearActivity();
                    PlayService.startCommand(mContext, Actions.ACTION_STOP);
                }
            }
        };

    public void stop() {
        handler.removeCallbacks(timeRunnable);
    }

}
