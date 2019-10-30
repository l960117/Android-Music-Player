package com.lws.sy.mv.videoUtils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.db.dao.VideoInfoDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class Video {
    private static VideoInfoDao dao =new VideoInfoDao(ContextUtils.getContext());
    public static void getVideo() {
        dao.deleteTable();
        String []projection = { MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA};
        String orderBy = MediaStore.Video.Media.DISPLAY_NAME;
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        getContentProvider(uri,projection, orderBy);
    }
    public static void getContentProvider(Uri uri, String[] projection, String orderBy) {
        List<videoInfo> list = new ArrayList<>();
        Cursor cursor = ContextUtils.getContext().getContentResolver().query(uri, projection, null, null, orderBy);
        if (null == cursor) {
            return;
        }
        while (cursor.moveToNext()) {
            videoInfo info = new videoInfo();
            info.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
            info.setVideo_id(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID)));
            info.setVideo_name(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
            list.add(info);
            dao.add(info.getPath(),info.getVideo_name(),info.getVideo_id());
        }
    }
}
