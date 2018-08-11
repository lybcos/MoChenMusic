package com.example.administrator.mochenmusic.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.administrator.mochenmusic.Model.Music;
import com.example.administrator.mochenmusic.R;
import com.example.administrator.mochenmusic.adapter.PlayAdapter;
import com.example.administrator.mochenmusic.enums.PlayModeEnum;
import com.example.administrator.mochenmusic.service.AudioPlayer;
import com.example.administrator.mochenmusic.service.OnPlayerEventListener;
import com.example.administrator.mochenmusic.storage.Preferences;
import com.example.administrator.mochenmusic.utils.binding.ScreenUtils;
import com.example.administrator.mochenmusic.utils.binding.ToastUtils;
import com.example.administrator.mochenmusic.widget.IndicatorLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 正在播放页面
 */

public class PlayFragment extends BaseFragment implements OnPlayerEventListener,
        SeekBar.OnSeekBarChangeListener,ViewPager.OnPageChangeListener,View.OnClickListener{

    @Bind(R.id.iv_play_page_bg)
    ImageView ivPlayPageBg;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_artist)
    TextView tvArtist;
    @Bind(R.id.vp_play_page)
    ViewPager vpPlayPage;
    @Bind(R.id.il_indicator)
    IndicatorLayout ilIndicator;
    @Bind(R.id.tv_current_time)
    TextView tvCurrentTime;
    @Bind(R.id.sb_progress)
    SeekBar sbProgress;
    @Bind(R.id.tv_total_time)
    TextView tvTotalTime;
    @Bind(R.id.iv_mode)
    ImageView ivMode;
    @Bind(R.id.iv_prev)
    ImageView ivPrev;
    @Bind(R.id.iv_play)
    ImageView ivPlay;
    @Bind(R.id.iv_next)
    ImageView ivNext;
    @Bind(R.id.ll_content)
    LinearLayout llContent;

    private List<View>viewList;
    private PlayAdapter adapter;
    private AudioManager audioManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSystemBar();
        initViewPager();
        ilIndicator.create(viewList.size());
        AudioPlayer.get().addOnPlayEventListener(this);

    }

    private void initViewPager() {
        View coverView=LayoutInflater.from(getContext()).inflate(R.layout.fragment_play_page_cover, null, false);
        View lrcView=LayoutInflater.from(getContext()).inflate(R.layout.fragment_play_page_lrc, null, false);
        viewList = new ArrayList<>();
        viewList.add(coverView);
        viewList.add(lrcView);
        initVolums();
        adapter = new PlayAdapter(viewList);
        vpPlayPage.setAdapter(adapter);
    }

    private void initVolums() {
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);//获得AudioManager实例对象
        sbProgress.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sbProgress.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }

    @Override
    protected void setListener() {
        super.setListener();
        ivBack.setOnClickListener(this);
        ivMode.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        vpPlayPage.setOnPageChangeListener(this);
        sbProgress.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_mode:
                switchPlayMode();
                break;
            case R.id.iv_play:
                play();
                break;
            case R.id.iv_next:
                next();
                break;
            case R.id.iv_prev:
                prev();
                break;
        }

    }

    private void onBackPressed() {
    }

    /**
     * 沉浸式状态栏
     */
    private void initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int top = ScreenUtils.getStatusBarHeight();
            llContent.setPadding(0, top, 0, 0);
        }
    }
    private void switchPlayMode() {
        PlayModeEnum modeEnum = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (modeEnum) {
            case LOOP:
                modeEnum=PlayModeEnum.SHUFFLE;
                ToastUtils.show(R.string.mode_shuffle);
                break;
            case SINGLE:
                modeEnum=PlayModeEnum.LOOP;
                ToastUtils.show(R.string.mode_loop);
                break;
            case SHUFFLE:
                modeEnum=PlayModeEnum.SINGLE;
                ToastUtils.show(R.string.mode_one);
                break;
        }
        Preferences.savePlayMode(modeEnum.value());
        initPlayMode();
    }

    private void initPlayMode() {
        int modeLevel=Preferences.getPlayMode();
        ivMode.setImageLevel(modeLevel);
    }

    private void play() {
        AudioPlayer.get().playPause();
    }

    private void next() {
        AudioPlayer.get().next();
    }

    private void prev() {
        AudioPlayer.get().prev();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onChange(Music music) {

    }

    @Override
    public void onPlayerStart() {
        ivPlay.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        ivPlay.setSelected(false);
    }

    @Override
    public void onPublish(int progress) {

    }

    @Override
    public void onBufferingUpdate(int percent) {

    }
    //音量进度条监听

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    //ViewPager滚动监听

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ilIndicator.setCurrent(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
