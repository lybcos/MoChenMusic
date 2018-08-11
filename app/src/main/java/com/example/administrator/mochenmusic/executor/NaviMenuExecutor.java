package com.example.administrator.mochenmusic.executor;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.example.administrator.mochenmusic.R;
import com.example.administrator.mochenmusic.activity.AboutActivity;
import com.example.administrator.mochenmusic.activity.MusicActivity;
import com.example.administrator.mochenmusic.activity.SettingActivity;
import com.example.administrator.mochenmusic.constants.Actions;
import com.example.administrator.mochenmusic.service.PlayService;
import com.example.administrator.mochenmusic.service.QuitTimer;
import com.example.administrator.mochenmusic.storage.Preferences;
import com.example.administrator.mochenmusic.utils.binding.ToastUtils;

/**
 * 导航菜单执行器
 * Created by lyb on 2018/07/30.
 */
public class NaviMenuExecutor {
    private MusicActivity activity;
    public NaviMenuExecutor(MusicActivity activity) {
        this.activity=activity;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                startActivity(SettingActivity.class);
                return true;
            case R.id.action_night://设置夜间模式
                nightMode();
                break;
            case R.id.action_timer://定时播放
                timerDialog();
                return true;
            case R.id.action_exit:
                activity.finish();
                PlayService.startCommand(activity, Actions.ACTION_STOP);
                return true;
            case R.id.action_about:
                startActivity(AboutActivity.class);
                return true;
        }
        return false;
    }

    private void nightMode() {
        Preferences.saveNightMode(!Preferences.isNightMode());
        activity.recreate();
    }

    private void timerDialog() {
        new AlertDialog.Builder(activity)
        .setTitle("定时停止播放")
        .setItems(activity.getResources().getStringArray(R.array.timer_text),new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int[]timeint = activity.getResources().getIntArray(R.array.timer_int);
                startTimer(timeint[which]);
            }
        }).show();
    }

    @SuppressLint("StringFormatMatches")
    private void startTimer(int time) {
        QuitTimer.get().Start(time*60*1000);
        if (time > 0) {
            ToastUtils.show(activity.getString(R.string.timer_set, time));
        }else{
            ToastUtils.show(R.string.timer_cancel);
        }
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }
}
