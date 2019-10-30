package com.lws.sy.mv.musicUtils;

import android.database.Cursor;
import android.provider.MediaStore;

import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.db.dao.MusicInfoDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class Music {
    private static List<musicInfo> musicData =null;
    private static MusicInfoDao dao=new MusicInfoDao(ContextUtils.getContext());;
    public static void getMusic() {
        musicData = new ArrayList<>();
        musicInfo info = null;
        dao.deleteTable();
        Cursor c= ContextUtils.getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,null);
        while(c.moveToNext()){
            info=new musicInfo();
            String music_name=c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String music_singer=c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String path = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
//            Bitmap bitmap = musicUtils.getArtwork(ContextUtils.getContext(), c.getLong(3), c.getLong(7), true);
            info.setMusic_name(music_name);
            info.setMusic_singer(music_singer);
            info.setPath(path);
            dao.add(path,music_name,music_singer,"music");
            musicData.add(info);
        }
    }
    public static void addLoveMusic(String path,String music_name,String music_singer){
//        dao.add(path,music_name,music_singer,"loveMusic");
    }
    public static void addLastMusic(String path,String music_name,String music_singer){
//        dao.add(path,music_name,music_singer,"lastMusic");
    }
}
