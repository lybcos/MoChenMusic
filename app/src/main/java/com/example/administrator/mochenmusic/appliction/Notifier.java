package com.example.administrator.mochenmusic.appliction;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.administrator.mochenmusic.Model.Music;
import com.example.administrator.mochenmusic.R;
import com.example.administrator.mochenmusic.activity.MusicActivity;
import com.example.administrator.mochenmusic.constants.Extras;
import com.example.administrator.mochenmusic.receiver.StatusBarReceiver;
import com.example.administrator.mochenmusic.service.PlayService;
import com.example.administrator.mochenmusic.utils.binding.CoverLoader;
import com.example.administrator.mochenmusic.utils.binding.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
    * PendingIntent
    PendingIntent表示一种处于pending状态的意图，而pending状态表示的是一种待定、等待、即将发生的意思。
    PendingIntent支持三种待定意图：启动Activity、启动Service和发送广播。
    1.PendingIntent的Flag介绍
        FLAG_ONE_SHOT
    PendingIntent只能被使用一次，然后会自动cancel；如果后续还有相同的PendingIntent，
    那么他们的send方法就会调用失败
       FLAG_NO_CREATE
    PendingIntent不会主动创建，如果当前PendingIntent不存在，那么三种意图方法调用会直接返回null，
    获取PendingIntent会失败，它无法单独使用
      FLAG_CANCEL_CURRENT
    PendingIntent如果已经存在，那么它们都会被cancle，然后系统会创建一个新的PendingIntent。
    对于通知栏而言，那些被cancel的消息单击后无法打开
      FLAG_UPDATE_CURRENT
    PendingIntent如果已经存在，那么它们都会被更新
 */
public class Notifier {
    private NotificationManager manager;
    private PlayService playService;
    private static final int NOTIFICATION_ID = 0x111;

    public static Notifier get() {
        return SingletonHolder.notifier;
    }
    private static class SingletonHolder{
        public static Notifier notifier = new Notifier();
    }

    public void init(PlayService playService1) {
        this.playService=playService1;
        manager = (NotificationManager) playService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showPlay(Music music) {
        if (music == null) {
            return;
        }
        playService.startForeground(NOTIFICATION_ID,buildNotifition(playService,music,true));//开启前台服务
    }

    public void showPause(Music music) {
        if (music == null) {
            return;
        }
        playService.stopForeground(false);
        manager.notify(NOTIFICATION_ID, buildNotifition(playService, music, false));
    }

    private Notification buildNotifition(Context context, Music music, boolean isPlaying) {
        Intent intent = new Intent(context, MusicActivity.class);
        intent.putExtra(Extras.EXTRA_NOTIFICATION, true);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setContent(getRemoteViews(context,music,isPlaying));
        return builder.build();
    }
    private RemoteViews getRemoteViews(Context context, Music music, boolean isPlaying) {
        String title = music.getTitle();
        String subtitle = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        Bitmap cover = CoverLoader.get().loadThumb(music);//不懂

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
        if (cover != null) {
            remoteViews.setImageViewBitmap(R.id.iv_icon, cover);
        } else {
            remoteViews.setImageViewResource(R.id.iv_icon, R.drawable.ic_launcher);
        }
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_subtitle, subtitle);

        boolean isLightNotificationTheme = isLightNotificationTheme(playService);

        Intent playIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);//通知栏播放按钮的点击
        playIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_PLAY_PAUSE);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.iv_play_pause, getPlayIconRes(isLightNotificationTheme, isPlaying));
        remoteViews.setOnClickPendingIntent(R.id.iv_play_pause, playPendingIntent);

        Intent nextIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);//下一首按钮的点击
        nextIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.iv_next, getNextIconRes(isLightNotificationTheme));
        remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent);

        return remoteViews;
    }

    private int getPlayIconRes(boolean isLightNotificationTheme, boolean isPlaying) {
        if (isPlaying) {
            return isLightNotificationTheme
                    ?R.drawable.ic_status_bar_pause_dark_selector
                    :R.drawable.ic_status_bar_pause_light_selector;
        }else{
            return isLightNotificationTheme
                    ?R.drawable.ic_status_bar_play_dark_selector
                    :R.drawable.ic_status_bar_pause_light_selector;
        }
    }

    public void cancelAll() {
        manager.cancelAll();
    }

    private int getNextIconRes(boolean isLightNotificationTheme) {
        return isLightNotificationTheme
                ?R.drawable.ic_status_bar_next_dark_selector
                :R.drawable.ic_status_bar_next_light_selector;
    }

    private boolean isLightNotificationTheme(Context context) {//判断是否为夜间模式
        int notificationTextColor = getNotificationTextColor(context);
        return isSimilarColor(Color.BLACK, notificationTextColor);
    }

    private int getNotificationTextColor(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.build();
        RemoteViews remoteViews = notification.contentView;
        if (remoteViews == null) {
            return Color.BLACK;
        }
        int layoutId = remoteViews.getLayoutId();
        ViewGroup notificationLayout = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
        TextView title = notificationLayout.findViewById(android.R.id.title);
        if (title != null) {
            return title.getCurrentTextColor();
        } else {
            return findTextColor(notificationLayout);
        }
    }

    /**
     * 如果通过 android.R.id.title 无法获得 title ，
     * 则通过遍历 notification 布局找到 textSize 最大的 TextView ，应该就是 title 了。
     */
    private int findTextColor(ViewGroup notificationLayout) {
        List<TextView> textViewList = new ArrayList<>();
        findTextView(notificationLayout, textViewList);

        float maxTextSize = -1;
        TextView maxTextView = null;
        for (TextView textView : textViewList) {
            if (textView.getTextSize() > maxTextSize) {
                maxTextView = textView;
            }
        }

        if (maxTextView != null) {
            return maxTextView.getCurrentTextColor();
        }

        return Color.BLACK;
    }

    private void findTextView(View view, List<TextView> textViewList) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                findTextView(viewGroup.getChildAt(i), textViewList);
            }
        } else if (view instanceof TextView) {
            textViewList.add((TextView) view);
        }
    }

    private boolean isSimilarColor(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        return value < 180.0;
    }
}
