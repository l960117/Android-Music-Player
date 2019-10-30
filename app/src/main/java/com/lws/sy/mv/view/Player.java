package com.lws.sy.mv.view;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class Player implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener{
    public MediaPlayer mediaPlayer;
    public Player()
    {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }
    }
    public void play()
    {
        mediaPlayer.start();
    }
    public void playUrl(String videoUrl)
    {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepare();//prepare之后自动播放
            //mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void pause()
    {
        mediaPlayer.pause();
    }
    public void stop()
    {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    @Override
    /**
     * 通过onPrepared播放
     */
    public void onPrepared(MediaPlayer arg0) {
        arg0.start();
        Log.e("mediaPlayer", "onPrepared");
    }
    @Override
    public void onCompletion(MediaPlayer arg0) {
        Log.e("mediaPlayer", "onCompletion");
        onMusicOverListener.OnMusicOver();
    }
    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
    }
    private OnMusicOverListener onMusicOverListener;

    public void setOnMusicOverListener(OnMusicOverListener onMusicOverListener) {
        this.onMusicOverListener = onMusicOverListener;
    }

    public interface OnMusicOverListener{
        void OnMusicOver();
    }
}
