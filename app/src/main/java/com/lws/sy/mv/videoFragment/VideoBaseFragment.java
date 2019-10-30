package com.lws.sy.mv.videoFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lws.sy.mv.Utils.ViewUtils;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public abstract class VideoBaseFragment extends Fragment {
    private FrameLayout fl_main;
    public View view;
    public String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(fl_main==null){
            fl_main=new FrameLayout(getContext());
            initView();
            fl_main.addView(view);
        }else{
            ViewUtils.remove(fl_main);
        }
        return fl_main;
    }

    public abstract void initView();

}

