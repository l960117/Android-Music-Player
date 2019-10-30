package com.lws.sy.mv.db.DBSQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 *
 */

public class MyDBOpenHelper extends SQLiteOpenHelper {

    public MyDBOpenHelper(Context context) {
        super(context, "mv", null, 1);

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table music(path varchar(100),music_name varchar(10),music_singer varchar(10));");
        db.execSQL("create table video(path varchar(100),video_name varchar(10),id varchar(10));");
        db.execSQL("create table loveMusic(path varchar(100),music_name varchar(10),music_singer varchar(10));");
        db.execSQL("create table downMusic(path varchar(100),music_name varchar(10),music_singer varchar(10));");
        db.execSQL("create table lastMusic(path varchar(100),music_name varchar(10),music_singer varchar(10),time integer);");
        db.execSQL("create table listItem(name varchar(10),table_name varchar(10));");
        db.execSQL("create table flowers(path varchar(100),music_name varchar(10),music_singer varchar(10));");
        db.execSQL("create table spring(path varchar(100),music_name varchar(10),music_singer varchar(10));");
        db.execSQL("create table login(id integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

