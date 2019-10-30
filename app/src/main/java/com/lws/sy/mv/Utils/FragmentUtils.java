package com.lws.sy.mv.Utils;


import com.lws.sy.mv.fragment.BaseFragment;
import com.lws.sy.mv.fragment.UserCenterFragment;
import com.lws.sy.mv.fragment.MusicHomeFragment;
import com.lws.sy.mv.fragment.VideoHomeFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class FragmentUtils {
    public static Map<Integer,BaseFragment> fragments=new HashMap<>();
    public static  BaseFragment getFragment(int position){
        BaseFragment baseFragment=fragments.get(position);
            if(baseFragment==null){
                if(position==0){
                    baseFragment= new MusicHomeFragment();
                }else if(position==1){
                    baseFragment= new VideoHomeFragment();
                }else if(position==2){
                    baseFragment= new UserCenterFragment();
                }
            }
        return baseFragment;
    }
}
