package com.lws.sy.mv.Utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class ViewUtils {
    public static void remove(View v){
        ViewParent parent=v.getParent();
        if(parent instanceof ViewGroup){
            ((ViewGroup)parent).removeView(v);
        }
    }
}
