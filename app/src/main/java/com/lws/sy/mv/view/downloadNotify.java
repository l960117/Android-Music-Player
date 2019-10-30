package com.lws.sy.mv.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class downloadNotify {
    private NotificationManager notificationManager;
    private Notification notification;
    private NotificationCompat.Builder builder;
    private int lastProgress=0;
    public void sendDownloadNotify(String title,int k){
        notificationManager = (NotificationManager) ContextUtils.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(ContextUtils.getContext());
        builder.setContentTitle("正在下载"+title+"...") //设置通知标题
                .setSmallIcon(R.mipmap.icon) //设置通知的小图标
                .setLargeIcon(BitmapFactory
                        .decodeResource(ContextUtils.getContext().getResources(),
                                R.mipmap.icon)) //设置通知的大图标
                .setDefaults(Notification.DEFAULT_LIGHTS) //设置通知的提醒方式： 呼吸灯
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
                .setAutoCancel(false)//设置通知被点击一次是否自动取消
                .setContentText("下载进度:" + "0%")
                .setProgress(100, 0, false);
        notification = builder.build();//构建通知对象
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(k,notification);
    }
    public void changeProgress(int progress,String title,int k){
        if(lastProgress>=progress-3){
            return;
        }
        lastProgress=progress;
        if(progress==100){
            lastProgress=0;
            builder.setProgress(100,progress,false)
                    .setContentText("下载进度："+progress+"%")
                    .setContentTitle("下载完成");
            notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(k,notification);
            return;
        }
        builder.setProgress(100,progress,false)
                .setContentText("下载进度："+progress+"%")
        .setContentTitle("正在下载"+title+"...");
        notification = builder.build();
        notificationManager.notify(k,notification);
    }
    public void clear(int k){
        notificationManager.cancel(k);
    }
}
