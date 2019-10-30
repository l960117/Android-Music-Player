package com.lws.sy.mv.broadcast;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.services.ServicesUtils;
import com.lws.sy.mv.view.MusicNotify;

import static android.content.Context.ACTIVITY_SERVICE;

public class MusicReceiver extends BroadcastReceiver {

    private final String MUSIC_NOTIFICATION_ACTION_PLAY = "musicnotificaion.To.PLAY";
    private final String MUSIC_NOTIFICATION_ACTION_NEXT = "musicnotificaion.To.NEXT";
    private final String MUSIC_NOTIFICATION_ACTION_PRE = "musicnotificaion.To.PRE";
    private final String MUSIC_NOTIFICATION_ACTION_CLOSE = "musicnotificaion.To.CLOSE";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("BroadcastReceiver",intent.getAction());
        switch (intent.getAction()){
            case MUSIC_NOTIFICATION_ACTION_PLAY :
                if(ServicesUtils.data.size()==0){
                    return;
                }
                if(ServicesUtils.lastposition==-1){
                    ServicesUtils.playMusic(ServicesUtils.data,0);
                }else{
                    ServicesUtils.play();
                }
                break;
            case MUSIC_NOTIFICATION_ACTION_NEXT:
                if(ServicesUtils.data.size()==0){
                    return;
                }
                if(ServicesUtils.lastposition==-1){
                    ServicesUtils.playMusic(ServicesUtils.data,0);
                }else{
                    ServicesUtils.playNext(ServicesUtils.music_play_type,1,true);
                }
                break;
            case MUSIC_NOTIFICATION_ACTION_PRE:
                if(ServicesUtils.data.size()==0){
                    return;
                }
                if(ServicesUtils.lastposition==-1){
                    ServicesUtils.playMusic(ServicesUtils.data,0);
                }else{
                    ServicesUtils.playNext(ServicesUtils.music_play_type,0,true);
                }
                break;
            case MUSIC_NOTIFICATION_ACTION_CLOSE:
                if(ServicesUtils.lastposition!=-1){
                    ServicesUtils.destory();
                }
                MusicNotify.cancel();
                //Toast.makeText(context, "关闭。", Toast.LENGTH_SHORT).show();
                int currentVersion = android.os.Build.VERSION.SDK_INT;
                if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(startMain);
                    System.exit(0);
                } else {// android2.1
                    ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                    am.restartPackage(context.getPackageName());
                }
                return;
        }
        Intent intent1 = new Intent("com.update");
        LocalBroadcastManager.getInstance(ContextUtils.getContext()).sendBroadcast(intent1);
    }
}
