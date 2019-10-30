package com.lws.sy.mv.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

import com.lws.sy.mv.MainActivity;
import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.broadcast.MusicReceiver;
import com.lws.sy.mv.services.ServicesUtils;

import static android.app.Notification.FLAG_NO_CLEAR;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class MusicNotify {
    private static final String MUSIC_NOTIFICATION_ACTION_PLAY = "musicnotificaion.To.PLAY";
    private static final String MUSIC_NOTIFICATION_ACTION_NEXT = "musicnotificaion.To.NEXT";
    private static final String MUSIC_NOTIFICATION_ACTION_PRE = "musicnotificaion.To.PRE";
    private static final String MUSIC_NOTIFICATION_ACTION_CLOSE = "musicnotificaion.To.CLOSE";
    private static Notification.Builder mbuilder;
    private static RemoteViews remoteViews;
    private static RemoteViews bigViews;
    private static NotificationManager noon;
    public static void showMotication() {
        Intent intent = new Intent(ContextUtils.getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pendingIntent=PendingIntent.getActivity(ContextUtils.getContext(),R.string.app_name,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mbuilder = new Notification.Builder(ContextUtils.getContext());
        mbuilder.setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(BitmapFactory.decodeResource(ContextUtils.getContext().getResources(),R.mipmap.logo))
                .setContent(getRemoteView());

        Notification noti=mbuilder.build();
        if(android.os.Build.VERSION.SDK_INT >= 16) {
            noti.bigContentView = getBigRemoteView();
        }
        noti.flags  |=FLAG_NO_CLEAR;
        noon = (NotificationManager) ContextUtils.getContext().getSystemService(NOTIFICATION_SERVICE);
        noon.notify(0,noti);

    }

    private static RemoteViews getBigRemoteView() {
        bigViews = new RemoteViews(ContextUtils.getContext().getPackageName(), R.layout.notify_music);
        if(ServicesUtils.lastposition!=-1){
            bigViews.setTextViewText(R.id.name, ServicesUtils.getMusicName());
            if (ServicesUtils.checkIsPlaying()==1) {
                bigViews.setImageViewResource(R.id.notify_play,R.mipmap.notify_pause);
            } else {
                bigViews.setImageViewResource(R.id.notify_play,R.mipmap.notify_play);
            }
        }else{
            bigViews.setTextViewText(R.id.name, ServicesUtils.defaultMusic_name);
        }
        Intent intent = new Intent(ContextUtils.getContext(),MusicReceiver.class);
        intent.setAction(MUSIC_NOTIFICATION_ACTION_PRE);
        PendingIntent pendingintent=PendingIntent.getBroadcast(ContextUtils.getContext(),0,intent,0);
        //打开上一首
        bigViews.setOnClickPendingIntent(R.id.notify_pre, pendingintent);

        intent.setAction(MUSIC_NOTIFICATION_ACTION_NEXT);
        pendingintent=PendingIntent.getBroadcast(ContextUtils.getContext(),0,intent,0);

        //打开下一首
        bigViews.setOnClickPendingIntent(R.id.notify_next, pendingintent);

        intent.setAction(MUSIC_NOTIFICATION_ACTION_PLAY);
        pendingintent = PendingIntent.getBroadcast(ContextUtils.getContext(),0,intent,0);
        bigViews.setOnClickPendingIntent(R.id.notify_play, pendingintent);

        intent.setAction(MUSIC_NOTIFICATION_ACTION_CLOSE);
        pendingintent = PendingIntent.getBroadcast(ContextUtils.getContext(),0,intent,0);
        bigViews.setOnClickPendingIntent(R.id.notify_close, pendingintent);
        //点击整体布局时,打开播放器
        //bigViews.setOnClickPendingIntent(R.id.ll_root, getClickPendingIntent());
        return bigViews;
    }

    public static RemoteViews getRemoteView() {
        remoteViews = new RemoteViews(ContextUtils.getContext().getPackageName(), R.layout.notify_common_music);
        remoteViews.setTextViewText(R.id.name, "");
        if(ServicesUtils.lastposition!=-1){
            remoteViews.setTextViewText(R.id.name, ServicesUtils.getMusicName());
            if (ServicesUtils.checkIsPlaying()==1) {
                remoteViews.setImageViewResource(R.id.notify_play,R.mipmap.notify_pause);
            } else {
                remoteViews.setImageViewResource(R.id.notify_play,R.mipmap.notify_play);
            }
        }else{
            remoteViews.setTextViewText(R.id.name, ServicesUtils.defaultMusic_name);
        }
        Intent intent = new Intent(ContextUtils.getContext(),MusicReceiver.class);
        intent.setAction(MUSIC_NOTIFICATION_ACTION_NEXT);
        PendingIntent pendingintent=PendingIntent.getBroadcast(ContextUtils.getContext(),0,intent,0);
        //打开下一首
        remoteViews.setOnClickPendingIntent(R.id.notify_next, pendingintent);

        intent.setAction(MUSIC_NOTIFICATION_ACTION_PLAY);
        pendingintent = PendingIntent.getBroadcast(ContextUtils.getContext(),0,intent,0);
        remoteViews.setOnClickPendingIntent(R.id.notify_play, pendingintent);

        intent.setAction(MUSIC_NOTIFICATION_ACTION_CLOSE);
        pendingintent = PendingIntent.getBroadcast(ContextUtils.getContext(),0,intent,0);
        remoteViews.setOnClickPendingIntent(R.id.notify_close, pendingintent);
        //点击整体布局时,打开播放器
        //remoteViews.setOnClickPendingIntent(R.id.ll_root, getClickPendingIntent());
        return remoteViews;
    }
    public static PendingIntent getClickPendingIntent() {
        Intent intent = new Intent(ContextUtils.getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int flag = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent clickIntent = PendingIntent.getActivity(ContextUtils.getContext(),R.string.app_name,intent,flag);
        return clickIntent;
    }
    public static void updateNotify(){
        //Toast.makeText(ContextUtils.getContext(), "更新通知栏", Toast.LENGTH_SHORT).show();
        showMotication();
    }
    public static void cancel(){
        NotificationManager noon = (NotificationManager) ContextUtils.getContext().getSystemService(NOTIFICATION_SERVICE);
        noon.cancelAll();
    }
}
