package com.lws.sy.mv.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lws.sy.mv.db.DBSQLite.MyDBOpenHelper;


/**
 *
 *
 */

public class MusicInfoDao {

    private final MyDBOpenHelper helper;

    public MusicInfoDao(Context context){
        helper = new MyDBOpenHelper(context);
        helper.getReadableDatabase();
    }
    public void addMyList(String name,String tableName){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("table_name",tableName);
        db.insert("listItem",null,values);
        db.close();
    }
    public Cursor queryMyList(String name){
        SQLiteDatabase db = helper.getReadableDatabase();
        //Cursor cursor=db.rawQuery("select password from user where username=?",new String[]{username});
        Cursor cursor = db.rawQuery("select table_name from listItem where name=?", new String[]{name});
        return cursor;
    }
    public boolean queryList(String path,String table_name){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + table_name + " where path='" + path+"'", null);
        if(cursor.moveToNext()){
            return true;
        }
        return false;
    }
    public void deleteMyList(String music_name,int type){
        SQLiteDatabase db=helper.getWritableDatabase();
        if(type==0){
            db.execSQL("delete from flowers where music_name=?",new Object[]{music_name});
        }else{
            db.execSQL("delete from spring where music_name=?",new Object[]{music_name});
        }
        db.close();
    }
    // 添加所有
    public long add(String path,String music_name,String music_singer,String tableName){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("path",path);
        values.put("music_name",music_name);
        values.put("music_singer",music_singer);
        long rowid=db.insert(tableName,null,values);
        db.close();
        return rowid;
    }
    public long addLast(String path,String music_name,String music_singer,long time,String tableName){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time",time);
        values.put("path",path);
        values.put("music_name",music_name);
        values.put("music_singer",music_singer);
        long rowid=db.insert(tableName,null,values);
        db.close();
        return rowid;
    }
    // 查找所有
    public Cursor query(String tableName){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;
        if(tableName.equals("lastMusic")){
            cursor = db.rawQuery("select * from lastMusic order by time desc", new String[]{});
        }else{
            cursor=db.query(tableName,null,null,null,null,null,null);
        }
        return cursor;
    }
    public boolean queryLove(String music_name){
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from loveMusic where music_name=?", new String[]{music_name});
        while(cursor.moveToNext()){
            db.close();
            return true;
        }
        db.close();
        return false;
    }
    public  void deleteTable(){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from music");
    }
    public  void deleteLogin(){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from login");
    }
    public void delete(String music_name){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from lastMusic where music_name=?",new Object[]{music_name});
        db.close();
    }
    public void deleteLove(String music_name){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from loveMusic where music_name=?",new Object[]{music_name});
        db.close();
    }
    public void delete(String music_name,int type){
        SQLiteDatabase db = helper.getWritableDatabase();
        switch (type) {
            case 1:
                db.execSQL("delete from music where music_name=?",new Object[]{music_name});
                break;
            case 2:
                db.execSQL("delete from loveMusic where music_name=?",new Object[]{music_name});
                break;
            case 3:
                db.execSQL("delete from downMusic where music_name=?",new Object[]{music_name});
                break;
            case 4:
                db.execSQL("delete from lastMusic where music_name=?",new Object[]{music_name});
                break;
            default:
                break;
        }
    }
    public void addLogin(int id){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",id);
        long rowid=db.insert("login",null,values);
        db.close();
    }
    public Cursor queryLogin(){
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("login",null,null,null,null,null,null);
        return cursor;
    }
    public void addDownload(String path,String music_name,String music_singer){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("path",path);
        values.put("music_name",music_name);
        values.put("music_singer",music_singer);
        long rowid=db.insert("downMusic",null,values);
        db.close();
    }
}

