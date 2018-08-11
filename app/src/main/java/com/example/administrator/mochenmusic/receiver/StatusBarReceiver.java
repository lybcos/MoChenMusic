package com.example.administrator.mochenmusic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.administrator.mochenmusic.service.AudioPlayer;

public class StatusBarReceiver extends BroadcastReceiver {
    public static final String ACTION_STATUS_BAR = "me.wcy.music.STATUS_BAR_ACTIONS";
    public static final String EXTRA = "extra";
    public static final String EXTRA_NEXT = "next";//下一首广播通知
    public static final String EXTRA_PLAY_PAUSE = "play_pause";//播放暂停广播通知
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        String extra = intent.getStringExtra(EXTRA);
        if (TextUtils.equals(extra, EXTRA_NEXT)) {
            AudioPlayer.get().next();
        } else if (TextUtils.equals(extra, EXTRA_PLAY_PAUSE)) {
            AudioPlayer.get().playPause();
        }
    }
}
