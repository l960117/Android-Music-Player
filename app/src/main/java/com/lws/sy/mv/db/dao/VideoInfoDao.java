package com.lws.sy.mv.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lws.sy.mv.db.DBSQLite.MyDBOpenHelper;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class VideoInfoDao {
    private final MyDBOpenHelper helper;

    public VideoInfoDao(Context context){
        helper = new MyDBOpenHelper(context);
        helper.getReadableDatabase();
    }
    // 添加所有
    public long add(String path,String video_name,String id){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("path",path);
        values.put("video_name",video_name);
        values.put("id",id);
        long rowid=db.insert("video",null,values);
        db.close();
        return rowid;
    }
    // 查找所有
    public Cursor query(String tableName){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor=db.query(tableName,null,null,null,null,null,null);
        return cursor;
    }
    public  void deleteTable(){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from video");
    }
}
