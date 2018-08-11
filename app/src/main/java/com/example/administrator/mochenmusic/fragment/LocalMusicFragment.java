package com.example.administrator.mochenmusic.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.mochenmusic.Model.Music;
import com.example.administrator.mochenmusic.R;
import com.example.administrator.mochenmusic.adapter.PlaylistAdapter;
import com.example.administrator.mochenmusic.appliction.AppCache;
import com.example.administrator.mochenmusic.constants.OnMoreClickListener;
import com.example.administrator.mochenmusic.constants.RxBusTags;
import com.example.administrator.mochenmusic.service.AudioPlayer;
import com.example.administrator.mochenmusic.utils.binding.MusicUtils;
import com.example.administrator.mochenmusic.utils.binding.PermissionReq;
import com.example.administrator.mochenmusic.utils.binding.ToastUtils;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;

import java.util.List;

import butterknife.Bind;

/**
 * 本地音乐
 * Created by lyb on 2018/07/30.
 */
public class LocalMusicFragment extends BaseFragment implements AdapterView.OnItemClickListener,OnMoreClickListener{
    private ListView listView;
    private TextView v_searching;
    private PlaylistAdapter adapter;
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.fragment_local_music, container, false);
        listView = view.findViewById(R.id.lv_local_music);
        v_searching = view.findViewById(R.id.v_searching);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new PlaylistAdapter(AppCache.get().getLocalMusicList());
        adapter.setOnMoreClick(this);
        listView.setAdapter(adapter);
        if (AppCache.get().getLocalMusicList().isEmpty()) {
            scanMusic();
        }
    }
    @Subscribe(tags = {@Tag(RxBusTags.SCAN_MUSIC)})
    public void scanMusic() {
        listView.setVisibility(View.GONE);
        v_searching.setVisibility(View.VISIBLE);
        PermissionReq.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionReq.Result() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onGranted() {
                        new AsyncTask<Void, Void, List<Music>>() {
                            @Override
                            protected List<Music> doInBackground(Void... params) {
                                return MusicUtils.scanMusic(getContext());
                            }

                            @Override
                            protected void onPostExecute(List<Music> musicList) {
                                AppCache.get().getLocalMusicList().clear();
                                AppCache.get().getLocalMusicList().addAll(musicList);
                                listView.setVisibility(View.VISIBLE);
                                v_searching.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        }.execute();
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.show(R.string.no_permission_storage);
                        listView.setVisibility(View.VISIBLE);
                        v_searching.setVisibility(View.GONE);
                    }
                })
                .request();

    }

    @Override
    protected void setListener() {
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Music music = AppCache.get().getLocalMusicList().get(position);
        AudioPlayer.get().addAndPlay(music);
        ToastUtils.show("已添加到播放列表,噜啦啦");
    }

    @Override
    public void onMoreClick(int position) {
        Music music = AppCache.get().getLocalMusicList().get(position);
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext())
                .setTitle(music.getTitle())
                .setItems(R.array.local_music_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0://分享
                                break;
                            case 1://设为铃声
                                break;
                            case 2://歌曲信息
                                break;
                            case 3://删除
                                deleteMusic(music);
                                break;
                        }
                    }
                });
        dialog.show();
    }

    private void deleteMusic(Music music) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage("删除" + music.getTitle());
        dialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.show();
    }

}
