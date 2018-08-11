package com.example.administrator.mochenmusic.appliction;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.example.administrator.mochenmusic.Model.Music;
import com.example.administrator.mochenmusic.service.QuitTimer;
import com.example.administrator.mochenmusic.storage.Preferences;
import com.example.administrator.mochenmusic.utils.binding.CoverLoader;
import com.example.administrator.mochenmusic.utils.binding.ScreenUtils;
import com.example.administrator.mochenmusic.utils.binding.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class AppCache {
    private Context mContext;
    private final List<Music> mLocalMusicList = new ArrayList<>();
    //    private final List<SheetInfo> mSheetList = new ArrayList<>();
    private List<Activity> activityLifeList = new ArrayList<>();
    private AppCache() {
    }
    private static class SingletonHolder {
        private static AppCache instance = new AppCache();
    }

    public static AppCache get() {
        return SingletonHolder.instance;
    }

    public void init(Application application) {
        mContext = application.getApplicationContext();
        ToastUtils.init(mContext);
        Preferences.init(mContext);
        ScreenUtils.init(mContext);
        QuitTimer.get().init(mContext);

//        CrashHandler.getInstance().init();
        CoverLoader.get().init(mContext);
        application.registerActivityLifecycleCallbacks(new ActivityLife());

    }
    public Context getContext() {
        return mContext;
    }

    public List<Music> getLocalMusicList() {
        return mLocalMusicList;
    }

    public void clearActivity() {
        List<Activity>activityList=activityLifeList;
        for (int i=activityList.size()-1;i>=0;i--) {
            Activity activity = activityList.get(i);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activityList.clear();
    }


    public class ActivityLife implements Application.ActivityLifecycleCallbacks{

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            activityLifeList.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
