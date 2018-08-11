package com.example.administrator.mochenmusic.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.administrator.mochenmusic.Model.Music;
import com.example.administrator.mochenmusic.R;
import com.example.administrator.mochenmusic.adapter.PlaylistAdapter;
import com.example.administrator.mochenmusic.appliction.AppCache;
import com.example.administrator.mochenmusic.constants.OnMoreClickListener;
import com.example.administrator.mochenmusic.service.AudioPlayer;

public class PlaylistActivity extends BaseActivity implements AdapterView.OnItemClickListener,OnMoreClickListener{
    private ListView playListView;
    private PlaylistAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        playListView = findViewById(R.id.lv_playlist);
        adapter = new PlaylistAdapter(AudioPlayer.get().getMusicList());
        adapter.setIsPlaylist(true);
        playListView.setOnItemClickListener(this);
        playListView.setAdapter(adapter);
        adapter.setOnMoreClick(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AudioPlayer.get().play(position);
    }

    @Override
    public void onMoreClick(int position) {
        String[]move=new String[]{"移除"};
        Music music = AudioPlayer.get().getMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("移除"+music.getTitle())
                .setItems(move, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AudioPlayer.get().delete(position);
                        adapter.notifyDataSetChanged();
                    }
                });
        dialog.show();

    }
}
