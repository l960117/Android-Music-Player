package com.lws.sy.mv.Utils;

import com.lws.sy.mv.userFragment.MyTFragment;
import com.lws.sy.mv.userFragment.collectFragment;
import com.lws.sy.mv.videoFragment.VideoBaseFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class UserFragmentUtils {
    public static Map<Integer,VideoBaseFragment> fragments=new HashMap<>();
    public static  VideoBaseFragment getFragment(int position){
        VideoBaseFragment baseFragment=fragments.get(position);
        if(baseFragment==null){
            if(position==0){
                baseFragment= new MyTFragment("发帖");
            }else if(position==1){
                baseFragment= new collectFragment("收藏");
            }
        }
        return baseFragment;
    }
}
