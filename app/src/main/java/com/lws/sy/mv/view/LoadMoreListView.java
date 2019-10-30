package com.lws.sy.mv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.lws.sy.mv.R;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener {
    private View mFootView;
    private int mTotalItemCount;//item总数
    private OnLoadMoreListener mLoadMoreListener;
    private boolean mIsLoading=false;//是否正在加载

    public LoadMoreListView(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context){
        mFootView= LayoutInflater.from(context).inflate(R.layout.foot_view,null);
        setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 滑到底部后，判断listview已经停止滚动并且最后可视的条目等于adapter的条目
        int lastVisibleIndex=view.getLastVisiblePosition();
        if (!mIsLoading&&scrollState == OnScrollListener.SCROLL_STATE_IDLE//停止滚动
                && lastVisibleIndex ==mTotalItemCount-1) {//滑动到最后一项
            mIsLoading=true;
            addFooterView(mFootView);
            if (mLoadMoreListener!=null) {
                mLoadMoreListener.onloadMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mTotalItemCount=totalItemCount;
    }
    public void setOnLoadMoreListener(OnLoadMoreListener listener){
        mLoadMoreListener=listener;
    }

    public interface OnLoadMoreListener{
        void onloadMore();
    }
    public void setLoadCompleted(){
        mIsLoading=false;
        removeFooterView(mFootView);
    }
}

