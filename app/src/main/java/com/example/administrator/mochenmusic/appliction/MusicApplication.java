package com.example.administrator.mochenmusic.appliction;

import android.app.Application;
import android.content.Intent;

import com.example.administrator.mochenmusic.service.PlayService;
import com.example.administrator.mochenmusic.storage.DBManager;

public class MusicApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCache.get().init(this);
        DBManager.get().init(this);

        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }
}
