package com.lws.sy.mv.services;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.db.dao.MusicInfoDao;
import com.lws.sy.mv.musicUtils.musicInfo;
import com.lws.sy.mv.view.MusicNotify;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.R.attr.max;
import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class ServicesUtils {
    private static IMusicservices iMusicservices=null;
    public static int lastposition = -1;
    private static int Max = 0;
    private static int currentProgress = 0;
    public static int music_play_type=0;
    public static String defaultMusic_name="手心的蔷薇";
    public static String defaultMusic_singer="邓紫棋";
    private static List<Integer> num=new ArrayList<>();
    private static MusicInfoDao dao=new MusicInfoDao(ContextUtils.getContext());
    public static List<musicInfo> data=new ArrayList<>();
    private static ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMusicservices = (IMusicservices) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    public static void addServices() {
        //Toast.makeText(ContextUtils.getContext(), "创建服务", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ContextUtils.getContext(), MusicService.class);
        ContextUtils.getContext().startService(intent);
        ContextUtils.getContext().bindService(intent, conn, BIND_AUTO_CREATE);
    }
    public static void playMusic(List<musicInfo> data, int position) {
        ServicesUtils.data =data;
        lastposition = position;
        if (iMusicservices != null) {
            iMusicservices.callPlay(data, position);
            Max=iMusicservices.getMusicDuration();
        }
        MusicNotify.updateNotify();
        java.util.Date date = new java.util.Date();
        long datetime = date.getTime();
        dao.delete(data.get(position).getMusic_name());
        dao.addLast(data.get(position).getPath(),data.get(position).getMusic_name(),data.get(position).getMusic_singer(),datetime,"lastMusic");
    }
    public static int getMax() {
        return Max;
    }
    public static void setProgress(int progress) {
        iMusicservices.seek(progress);
    }
    public static int getProgress() {
        currentProgress = iMusicservices.getMusicCurrentPosition();
        return currentProgress;
    }

    /**
     *
     * @param type 播放模式
     * @param person 用户手动，0代表上一首，1代表下一首
     * @param isUser 是否为用户手动
     */
    public static void playNext(int type,int person,Boolean isUser) {
        music_play_type=type;
        Log.e("播放模式",type+"");
        Log.e("播放未知",lastposition+"");
        switch (type){
            //顺序播放
            case 0:
                if(isUser){
                    if(person==0) {
                        if(lastposition==0) {
                            iMusicservices.callPlay(data, data.size()-1);
                            Max=iMusicservices.getMusicDuration();
                            currentProgress = iMusicservices.getMusicCurrentPosition();
                            lastposition = data.size()-1;
                        } else {
                            iMusicservices.callPlay(data, lastposition-1);
                            Max=iMusicservices.getMusicDuration();
                            currentProgress = iMusicservices.getMusicCurrentPosition();
                            lastposition = lastposition-1;
                        }
                    }else {
                        Log.e("TAG","播放下一首");
                        if(lastposition==data.size()-1) {
                            iMusicservices.callPlay(data, 0);
                            Max=iMusicservices.getMusicDuration();
                            currentProgress = iMusicservices.getMusicCurrentPosition();
                            lastposition = 0;
                        } else {
                            iMusicservices.callPlay(data, lastposition+1);
                            Max=iMusicservices.getMusicDuration();
                            currentProgress = iMusicservices.getMusicCurrentPosition();
                            lastposition = lastposition+1;
                        }
                    }
                }else {
                    if(num.size()==0){
                        if(lastposition==data.size()-1) {
                            iMusicservices.callPlay(data, 0);
                            Max=iMusicservices.getMusicDuration();
                            currentProgress = iMusicservices.getMusicCurrentPosition();
                            lastposition = 0;
                        } else {
                            iMusicservices.callPlay(data, lastposition+1);
                            Max=iMusicservices.getMusicDuration();
                            currentProgress = iMusicservices.getMusicCurrentPosition();
                            lastposition = lastposition+1;
                        }
                    }else {
                        iMusicservices.callPlay(data,num.get(0));
                        Max=iMusicservices.getMusicDuration();
                        currentProgress = iMusicservices.getMusicCurrentPosition();
                        lastposition=num.get(0);
                        num.remove(0);
                    }
                }
                break;
            //随机播放
            case 1:
                if(isUser){
                    Random random = new Random();
                    int num = random.nextInt(max)%(data.size());
                    while(num==lastposition) {
                        num = random.nextInt(max)%(data.size());
                    }
                    iMusicservices.callPlay(data, num);
                    Max=iMusicservices.getMusicDuration();
                    currentProgress = iMusicservices.getMusicCurrentPosition();
                    lastposition = num;
                }else {
                    if(num.size()==0){
                        Random random = new Random();
                        int num = random.nextInt(max)%(data.size());
                        while(num==lastposition) {
                            num = random.nextInt(max)%(data.size());
                        }
                        iMusicservices.callPlay(data, num);
                        Max=iMusicservices.getMusicDuration();
                        currentProgress = iMusicservices.getMusicCurrentPosition();
                        lastposition = num;
                    }else {
                        iMusicservices.callPlay(data,num.get(0));
                        Max=iMusicservices.getMusicDuration();
                        currentProgress = iMusicservices.getMusicCurrentPosition();
                        lastposition=num.get(0);
                        num.remove(0);
                    }
                }
                break;
            //单曲循环
            case 2:
                if(isUser){
                    if(person==0){
                        iMusicservices.callPlay(data, lastposition-1);
                        Max=iMusicservices.getMusicDuration();
                        currentProgress = iMusicservices.getMusicCurrentPosition();
                        lastposition = lastposition-1;
                    }else {
                        iMusicservices.callPlay(data, lastposition+1);
                        Max=iMusicservices.getMusicDuration();
                        currentProgress = iMusicservices.getMusicCurrentPosition();
                        lastposition = lastposition+1;
                    }
                }else {
                    iMusicservices.callPlay(data, lastposition);
                    Max=iMusicservices.getMusicDuration();
                    currentProgress = iMusicservices.getMusicCurrentPosition();
                }
                break;
        }
        MusicNotify.updateNotify();
        java.util.Date date = new java.util.Date();
        long datetime = date.getTime();
        Log.e("TAG",datetime+"");
        dao.delete(data.get(lastposition).getMusic_name());
        dao.addLast(data.get(lastposition).getPath(),data.get(lastposition).getMusic_name(),data.get(lastposition).getMusic_singer(), datetime,"lastMusic");
    }
    public static int checkIsPlaying() {
        return iMusicservices.check();
    }
    public static void play() {
        iMusicservices.callPlay(data, lastposition);
        Max=iMusicservices.getMusicDuration();
        currentProgress = iMusicservices.getMusicCurrentPosition();
        MusicNotify.updateNotify();
    }
    public static String getMusicName() {
        return data.get(lastposition).getMusic_name();
    }
    public static String getMusicSinger() {
        if(data.get(lastposition).getMusic_singer().equals("<unknown>")){
            return "未知";
        }
        return data.get(lastposition).getMusic_singer();
    }
    public static String getPath(){
        return data.get(lastposition).getPath();
    }
    public static void destory() {
        if (iMusicservices != null) {
            iMusicservices.callStop();
        }
        if (conn != null) {
            ContextUtils.getContext().unbindService(conn);
            conn = null;
        }
        ContextUtils.getContext().stopService(new Intent(ContextUtils.getContext(), MusicService.class));
    }
    public static void addLove(){
        if(lastposition==-1){
            return;
        }
        dao.add(data.get(lastposition).getPath(),data.get(lastposition).getMusic_name(),data.get(lastposition).getMusic_singer(),"loveMusic");
    }
    public static void deleteLove(){
        dao.deleteLove(data.get(lastposition).getMusic_name());
        data.remove(lastposition);
        if(data.size()==0){
            lastposition=-1;
        }
    }
    public static void addSum(int position) {
        num.add(position);
    }
    public static void addMyList(String table_name){
        if(lastposition==-1){
            return;
        }
        boolean b = dao.queryList(data.get(lastposition).getPath(), table_name);
        if(b){
            return;
        }
        dao.add(data.get(lastposition).getPath(),data.get(lastposition).getMusic_name(),data.get(lastposition).getMusic_singer(),table_name);
    }
}
