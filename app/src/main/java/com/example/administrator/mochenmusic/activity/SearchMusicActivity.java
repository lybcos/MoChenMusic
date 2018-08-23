package com.example.administrator.mochenmusic.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mochenmusic.Model.DownloadPlayInfo;
import com.example.administrator.mochenmusic.Model.Music;
import com.example.administrator.mochenmusic.Model.SearchMusic;
import com.example.administrator.mochenmusic.R;
import com.example.administrator.mochenmusic.adapter.SearchListAdapter;
import com.example.administrator.mochenmusic.constants.OnMoreClickListener;
import com.example.administrator.mochenmusic.enums.LoadStateEnum;
import com.example.administrator.mochenmusic.executor.PlaySearchMusic;
import com.example.administrator.mochenmusic.http.HttpCallback;
import com.example.administrator.mochenmusic.http.HttpClient;
import com.example.administrator.mochenmusic.service.AudioPlayer;
import com.example.administrator.mochenmusic.utils.binding.ToastUtils;
import com.example.administrator.mochenmusic.utils.binding.ViewUtils;
import com.example.administrator.mochenmusic.widget.AutoLoadListView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchMusicActivity extends BaseActivity implements SearchView.OnQueryTextListener,
        AdapterView.OnItemClickListener, OnMoreClickListener,AutoLoadListView.OnLoadListener {
    private ListView lvSearchMusicList;
    private LinearLayout tvLoadingText;
    private LinearLayout tvLoadFailText;
    private List<SearchMusic.Song>songList=new ArrayList<>();
    private SearchListAdapter adapter=new SearchListAdapter(songList);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);
        lvSearchMusicList = findViewById(R.id.lv_search_music_list);
        tvLoadingText = findViewById(R.id.ll_loading);
        tvLoadFailText = findViewById(R.id.ll_load_fail);

    }

    @Override
    protected void onServiceBound() {
        super.onServiceBound();
        lvSearchMusicList.setAdapter(adapter);
        TextView tvLoadFail = tvLoadFailText.findViewById(R.id.tv_load_fail_text);
        tvLoadFail.setText(R.string.search_empty);

        lvSearchMusicList.setOnItemClickListener(this);
        adapter.setOnMoreClick(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_music, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("歌曲名，歌手名");
        searchView.onActionViewExpanded();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        try {
            Field field = searchView.getClass().getDeclaredField("mGoButton");
            field.setAccessible(true);
            ImageView mGoButton = (ImageView) field.get(searchView);
            mGoButton.setImageResource(R.drawable.ic_menu_search);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {//提交查询文本时调用，在这里我们需要做的逻辑是：当点击搜索按钮时，
        // 显示正在加载中的页面的然后网络请求，成功就返回结果，通过ListView显示搜索结果，没有返回结果就显示加载失败
        if (TextUtils.isEmpty(query)) {
            Toast.makeText(this, "请输入你要查询的歌手名或者歌曲", Toast.LENGTH_SHORT).show();

        }
        ViewUtils.setVisibility(lvSearchMusicList,tvLoadingText,tvLoadFailText, LoadStateEnum.LOADING);
        SearchMusic(query);
        return false;
    }

    private void SearchMusic(String query) {
        HttpClient.searchMusicHttp(query, new HttpCallback<SearchMusic>() {
            @Override
            public void onSuccess(SearchMusic response) {
                if (response==null&&response.getSong() == null) {
                    ViewUtils.setVisibility(lvSearchMusicList,tvLoadingText,tvLoadFailText, LoadStateEnum.LOADFAIL);
                }
                ViewUtils.setVisibility(lvSearchMusicList,tvLoadingText,tvLoadFailText, LoadStateEnum.SUCCESS);
                songList.clear();
                songList.addAll(response.getSong());
                adapter.notifyDataSetChanged();
                lvSearchMusicList.requestFocus();
            }

            @Override
            public void onFail(Exception e) {
                ViewUtils.setVisibility(lvSearchMusicList,tvLoadingText,tvLoadFailText, LoadStateEnum.LOADFAIL);

            }
        });

    }

    @Override
    public boolean onQueryTextChange(String newText) {//查询文本变化调用
//         Toast.makeText(this, "onQueryTextChange", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       HttpClient.playSearchMusic(songList.get(position).getSongid(), new HttpCallback<DownloadPlayInfo>() {
           @Override
           public void onSuccess(DownloadPlayInfo response) {
               if (response == null || response.getBitrate() == null) {
                   return;
               }
               Music music = new Music();
               music.setType(Music.Type.LOCAL);
               music.setArtist(songList.get(position).getArtistname());
               music.setTitle(songList.get(position).getSongname());

               music.setPath(response.getBitrate().getFile_link());
               music.setDuration(response.getBitrate().getFile_duration()*1000);
               AudioPlayer.get().addAndPlay(music);
               ToastUtils.show("可以播放了");
           }

           @Override
           public void onFail(Exception e) {

           }
       });
    }

    @Override
    public void onMoreClick(int position) {
        SearchMusic.Song song=songList.get(position);
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle(song.getSongname());
        dialog.setItems(R.array.search_music_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    @Override
    public void onLoad() {

    }
}
