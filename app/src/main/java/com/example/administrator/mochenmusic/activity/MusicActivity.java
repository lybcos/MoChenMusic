package com.example.administrator.mochenmusic.activity;


import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mochenmusic.R;
import com.example.administrator.mochenmusic.adapter.FragmentAdapter;
import com.example.administrator.mochenmusic.constants.Extras;
import com.example.administrator.mochenmusic.executor.ControlPanel;
import com.example.administrator.mochenmusic.executor.NaviMenuExecutor;
import com.example.administrator.mochenmusic.fragment.LocalMusicFragment;
import com.example.administrator.mochenmusic.fragment.PlayFragment;
import com.example.administrator.mochenmusic.fragment.SheetListFragment;
import com.example.administrator.mochenmusic.service.AudioPlayer;
import com.example.administrator.mochenmusic.service.QuitTimer;
import com.example.administrator.mochenmusic.utils.binding.SystemUtils;
import com.example.administrator.mochenmusic.utils.binding.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MusicActivity extends BaseActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener,QuitTimer.OnTimerListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_menu)
    ImageView ivMenu;
    @Bind(R.id.tv_local_music)
    TextView tvLocalMusic;
    @Bind(R.id.tv_online_music)
    TextView tvOnlineMusic;
    @Bind(R.id.iv_search)
    ImageView ivSearch;
    @Bind(R.id.appbar)
    AppBarLayout appbar;
    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.iv_play_bar_cover)
    ImageView ivPlayBarCover;
    @Bind(R.id.tv_play_bar_title)
    TextView tvPlayBarTitle;
    @Bind(R.id.tv_play_bar_artist)
    TextView tvPlayBarArtist;
    @Bind(R.id.iv_play_bar_play)
    ImageView ivPlayBarPlay;
    @Bind(R.id.iv_play_bar_next)
    ImageView ivPlayBarNext;
    @Bind(R.id.v_play_bar_playlist)
    ImageView vPlayBarPlaylist;
    @Bind(R.id.pb_play_bar)
    ProgressBar pbPlayBar;
    @Bind(R.id.fl_play_bar)
    FrameLayout flPlayBar;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    View navHeader;
    private FragmentAdapter adapter;
    private PlayFragment playFragment;
    private LocalMusicFragment mLocalMusicFragment;
    private SheetListFragment mSheetListFragment;
    private long exitTime=0;
    private NaviMenuExecutor naviMenuExecutor;
    private ControlPanel controlPanel;
    private boolean isPlayFragmentShow;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        ButterKnife.bind(this);

    }

    private void setUpView() {
        navHeader = LayoutInflater.from(this).inflate(R.layout.navigation_header, navigationView, false);
        navigationView.addHeaderView(navHeader);
        ivMenu.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        adapter = new FragmentAdapter(getSupportFragmentManager());
        mLocalMusicFragment = new LocalMusicFragment();
        mSheetListFragment = new SheetListFragment();
        adapter.addFragment(mLocalMusicFragment);
        adapter.addFragment(mSheetListFragment);
        viewpager.setAdapter(adapter);
        tvLocalMusic.setSelected(true);
        tvOnlineMusic.setOnClickListener(this);
        tvLocalMusic.setOnClickListener(this);
        viewpager.addOnPageChangeListener(this);
        flPlayBar.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    protected void onServiceBound() {
        setUpView();

        controlPanel=new ControlPanel(flPlayBar);
        naviMenuExecutor = new NaviMenuExecutor(this);
        AudioPlayer.get().addOnPlayEventListener(controlPanel);
        QuitTimer.get().setOnTimerListener(this);
        parseIntent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.tv_local_music:
                viewpager.setCurrentItem(0);
                break;
            case R.id.tv_online_music:
                viewpager.setCurrentItem(1);
                break;
            case R.id.fl_play_bar:
                showPlayingFragment();
                break;

        }
    }
    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Extras.EXTRA_NOTIFICATION)) {//从通知栏点击进入的playFragment
            showPlayingFragment();
            setIntent(new Intent());
        }
    }
    public void showPlayingFragment() {
//        ToastUtils.show("你已经点击了显示播放页面");
        if (isPlayFragmentShow) {
            return;
        }
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(0,R.anim.fragment_slide_up);
        if (playFragment == null) {
            playFragment = new PlayFragment();
            transaction.replace(android.R.id.content, playFragment);
        }else{
            transaction.show(playFragment);
        }
        transaction.commitAllowingStateLoss();
        isPlayFragmentShow = true;

    }

    public void hidePlayingFragment() {
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(0, R.anim.fragment_slide_down);
        transaction.hide(playFragment);
        transaction.commitAllowingStateLoss();
        isPlayFragmentShow = false;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawers();

        return naviMenuExecutor.onNavigationItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tvLocalMusic.setSelected(true);
            tvOnlineMusic.setSelected(false);
        } else {
            tvLocalMusic.setSelected(false);
            tvOnlineMusic.setSelected(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        if (playFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }
        if (System.currentTimeMillis() - exitTime > 2000) {
             Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
             exitTime=System.currentTimeMillis();
                return;
        }else{
            finish();
        }
        super.onBackPressed();
    }


    @Override
    public void onTimer(long time) {
        if (menuItem == null) {
            menuItem = navigationView.getMenu().findItem(R.id.action_timer);
        }else{
            String title=getString(R.string.menu_timer);
            menuItem.setTitle(time == 0 ? title : SystemUtils.formatTime(title + "(mm:ss)", time));
        }
    }
}
