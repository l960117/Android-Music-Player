package com.lws.sy.mv.Utils;

import android.os.Environment;

import java.io.File;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */
//找文件夹
public class FileUtils {
    private static  String TAG="TEST";
    private static  String CACHE="cache";
    private static  String ICON="icon";
    /*
    判断SD卡是否有用
     */
    public static boolean isSdAlive() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }
    public static String getDir(String str){
        String path="";
        if(isSdAlive()){
            //path=/sd/TEST/cache/
            String SDpath=Environment.getExternalStorageDirectory().getAbsolutePath();
            path+=SDpath;path+="/";path+=TAG;path+="/";path+=str;
        }else{
            //path=/data/data/项目安装包/cache
            File file=ContextUtils.getContext().getCacheDir();
            path=file.getAbsolutePath();
        }
        File file1=new File(path.toString());
        if(!file1.exists() || !file1.isDirectory()){
            file1.mkdirs();
        }

        return path;
    }
    public static String getCacheDir(){
        return getDir(CACHE);
    }
    public static String getIconDir(){
        return getDir(ICON);
    }
}
