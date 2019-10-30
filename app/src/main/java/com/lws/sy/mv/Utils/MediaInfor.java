package com.lws.sy.mv.Utils;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.lws.sy.mv.R;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class MediaInfor {
    public static Bitmap getAlbumArt(int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = ContextUtils.getContext().getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        } else {
            bm = BitmapFactory.decodeResource(ContextUtils.getContext().getResources(), R.mipmap.avatar);
        }
        return bm;
    }


    public static String durationToString(int duration) {

        String reVal = "";
        int i = duration / 1000;
        int min = (int) i / 60;
        int sec = i % 60;
        if (min > 9) {
            if (sec > 9) {
                reVal = min + ":" + sec;
            }
            if (sec <= 9) {
                reVal = min + ":0" + sec;
            }
        } else {
            if (sec > 9) {
                reVal = "0" + min + ":" + sec;
            }
            if (sec <= 9) {
                reVal = "0" + min + ":0" + sec;
            }
        }

        return reVal;
    }
}
