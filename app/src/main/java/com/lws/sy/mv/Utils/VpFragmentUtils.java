package com.lws.sy.mv.Utils;


import com.lws.sy.mv.videoFragment.DetialFragment;
import com.lws.sy.mv.videoFragment.PlFragment;
import com.lws.sy.mv.videoFragment.VideoBaseFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class VpFragmentUtils {
    public static Map<Integer,VideoBaseFragment> fragments=new HashMap<>();
    public static  VideoBaseFragment getFragment(int position){
        VideoBaseFragment baseFragment=fragments.get(position);
        if(baseFragment==null){
            if(position==0){
                baseFragment= new PlFragment("评论");
            }else if(position==1){
                baseFragment= new DetialFragment("详情");
            }
        }
        return baseFragment;
    }
}
