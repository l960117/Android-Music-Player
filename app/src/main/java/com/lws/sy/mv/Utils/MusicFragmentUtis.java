package com.lws.sy.mv.Utils;

import com.lws.sy.mv.fragment.BaseFragment;
import com.lws.sy.mv.musicFragment.musicDetailFragment;
import com.lws.sy.mv.musicFragment.musicListFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class MusicFragmentUtis {
    public static Map<Integer,BaseFragment> fragments=new HashMap<>();
    public static  BaseFragment getFragment(int position,int type) {
        BaseFragment baseFragment = fragments.get(position);
        if (baseFragment == null) {
            if (position == 0) {
                baseFragment = new musicListFragment(type);
            } else if (position == 1) {
                baseFragment = new musicDetailFragment(type);
            }
        }
        return baseFragment;
    }
}
