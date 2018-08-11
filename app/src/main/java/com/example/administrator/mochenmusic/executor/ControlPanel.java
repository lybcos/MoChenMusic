package com.example.administrator.mochenmusic.executor;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.mochenmusic.Model.Music;
import com.example.administrator.mochenmusic.R;
import com.example.administrator.mochenmusic.activity.PlaylistActivity;
import com.example.administrator.mochenmusic.appliction.MusicApplication;
import com.example.administrator.mochenmusic.service.AudioPlayer;
import com.example.administrator.mochenmusic.service.OnPlayerEventListener;
import com.example.administrator.mochenmusic.utils.binding.Bind;
import com.example.administrator.mochenmusic.utils.binding.ViewBinder;

/**
 *  最下方播放栏的控制类
 *  Created by lyb on 2018/07/30.
 */
public class ControlPanel implements View.OnClickListener,OnPlayerEventListener{
    @Bind(R.id.iv_play_bar_cover)
    private ImageView ivPlayBarCover;
    @Bind(R.id.tv_play_bar_title)
    private TextView tvPlayBarTitle;
    @Bind(R.id.tv_play_bar_artist)
    private TextView tvPlayBarArtist;
    @Bind(R.id.iv_play_bar_play)
    private ImageView ivPlayBarPlay;
    @Bind(R.id.iv_play_bar_next)
    private ImageView ivPlayBarNext;
    @Bind(R.id.v_play_bar_playlist)
    private ImageView vPlayBarPlaylist;
    @Bind(R.id.pb_play_bar)
    private ProgressBar mProgressBar;
    public ControlPanel(View view) {
        ViewBinder.bind(this,view);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
        vPlayBarPlaylist.setOnClickListener(this);
        onChange(AudioPlayer.get().getPlayMusic());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_bar_play://播放按钮
                AudioPlayer.get().playPause();
                break;
            case R.id.iv_play_bar_next://下一首按钮
                AudioPlayer.get().next();
                break;
            case R.id.v_play_bar_playlist://播放列表按钮
                Context context=vPlayBarPlaylist.getContext();
                Intent intent = new Intent(context, PlaylistActivity.class);
                context.startActivity(intent);
                break;
        }
    }

    @Override
    public void onChange(Music music) {
        if (music == null) {
            return;
        }
        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarArtist.setText(music.getArtist());
        ivPlayBarPlay.setSelected(AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing());

    }

    @Override
    public void onPlayerStart() {
        ivPlayBarPlay.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        ivPlayBarPlay.setSelected(false);
    }

    @Override
    public void onPublish(int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }
}
