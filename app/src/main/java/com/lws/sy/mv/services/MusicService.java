package com.lws.sy.mv.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.lws.sy.mv.broadcast.MusicReceiver;
import com.lws.sy.mv.musicUtils.musicInfo;
import com.lws.sy.mv.view.MusicNotify;

import java.util.List;


public class MusicService extends Service {

    private String TAG="MusicServices";
    private final String MUSIC_NOTIFICATION_ACTION_PLAY = "musicnotificaion.To.PLAY";
    private final String MUSIC_NOTIFICATION_ACTION_NEXT = "musicnotificaion.To.NEXT";
    private final String MUSIC_NOTIFICATION_ACTION_PRE = "musicnotificaion.To.PRE";
    private final String MUSIC_NOTIFICATION_ACTION_CLOSE = "musicnotificaion.To.CLOSE";
    private MediaPlayer mediaPlayer;
    private int preposition=-1;
    private MusicReceiver musicBroadCast = null;
    private boolean isBusing=false;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(this, "显示通知栏", Toast.LENGTH_SHORT).show();
        mediaPlayer = new MediaPlayer();
        musicBroadCast = new MusicReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MUSIC_NOTIFICATION_ACTION_PLAY);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_NEXT);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_PRE);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_CLOSE);
        registerReceiver(musicBroadCast, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        MusicNotify.showMotication();
        return new MyBinder();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(musicBroadCast!=null){
            unregisterReceiver(musicBroadCast);
        }
    }

    private class MyBinder extends Binder implements IMusicservices{

        @Override
        public void callPlay(List<musicInfo> palylist, int position) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });
            paly(palylist,position);
        }

        @Override
        public void callStop() {
            stop();
        }

        public int getMusicDuration(){
            if(isBusing){
                return 0;
            }
            int rtn=0;
            if(mediaPlayer!=null){
                rtn=mediaPlayer.getDuration();
                Log.e(TAG, Integer.toString(rtn));
            }
            return rtn;
        }
        public int getMusicCurrentPosition(){
            if(isBusing){
                return 0;
            }
            int rtn=0;
            if(mediaPlayer!=null){
                if (mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition()<=500) {
                    rtn=mediaPlayer.getDuration();
                } else {
                    rtn=mediaPlayer.getCurrentPosition();
                }
            }
            return rtn;
        }
        public void seek(int progress){
            if(mediaPlayer!=null){
                mediaPlayer.seekTo(progress);
            }
        }
        public int check(){
            int flag=0;
            if(mediaPlayer.isPlaying()){
                flag=1;
            }
            return flag;
        }
    }

    private void stop() {
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        MusicNotify.cancel();
    }

    private void paly(List<musicInfo> palylist, int position) {
        if(position==preposition) {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
            else{
                mediaPlayer.start();
            }
        }
        else{
            preposition=position;
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            isBusing = true;
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(palylist.get(position).getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                isBusing = false;
            /*mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
