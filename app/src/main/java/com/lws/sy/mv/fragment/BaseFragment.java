package com.lws.sy.mv.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.Utils.ViewUtils;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public abstract class BaseFragment extends Fragment {
    public FrameLayout fl_main;
    public View view;
    public Boolean isVisible;
    public Boolean isInit=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(fl_main==null){
            fl_main=new FrameLayout(ContextUtils.getContext());
            initView();
            fl_main.addView(view);
        }else{
            ViewUtils.remove(fl_main);
        }
        return fl_main;
    }
    public abstract void initView();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible=isVisibleToUser;
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }
    /**
     * 可见
     */
    protected void onVisible() {
        lazyLoad();
    }
    /**
     * 不可见
     */
    protected void onInvisible() {
        closeOther();
    }

    protected abstract void closeOther();

    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected abstract void lazyLoad();
}
