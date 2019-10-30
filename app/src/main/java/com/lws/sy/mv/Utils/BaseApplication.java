package com.lws.sy.mv.Utils;

import android.app.Application;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class BaseApplication extends Application{
    public static BaseApplication application;
    private static int Flag=0;
    private static String Loadname=null;
    private static String path="";
    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
    }

    public static BaseApplication getApplication() {
        return application;
    }
}
