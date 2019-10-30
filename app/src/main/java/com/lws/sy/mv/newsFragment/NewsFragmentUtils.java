package com.lws.sy.mv.newsFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class NewsFragmentUtils {
    public static Map<Integer,NewsBaseFragment> fragments=new HashMap<>();
    public static  NewsBaseFragment getFragment(int position){
        NewsBaseFragment baseFragment=fragments.get(position);
        if(baseFragment==null){
            if(position==0){
                baseFragment= new praiseFragment("赞");
            }else if(position==1){
                baseFragment= new commentFragment("评论");
            }else if(position==2){
                baseFragment= new systemFragment("系统");
            }
        }
        return baseFragment;
    }
}
