package com.lws.sy.mv.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lws.sy.mv.NetInfo.lunboInfo;
import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.Utils.Utils;
import com.lws.sy.mv.activity.VideoActivity;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.LunBoRequest;
import com.lws.sy.mv.view.MyListView;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;


/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class VideoHomeFragment extends BaseFragment implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private TextView tv_local;
    private ViewPager viewpager;
    private LinearLayout ll_layout1;
    private int preposition = 0;
    private JZVideoPlayerStandard jz_pp;
    private ArrayList<ImageView> mImageViews;
    private List<lunboInfo> infos;
    private MyListView lv_video_home;
    private VideoAdapter adapter;
    private ImageView loading;
    private ImageView loadingFail;
    private TextView changeType;
    private int type = 0;
    private boolean isLoadingSuccess= false;

    private final int[] imageIds = {
            R.mipmap.lunbo1,
            R.mipmap.lunbo2,
            R.mipmap.lunbo3};
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0){
                int i=viewpager.getCurrentItem()+1;
                viewpager.setCurrentItem(i);
                mHandler.sendEmptyMessageDelayed(0,4000);
            }
        }
    };
    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        if (JZVideoPlayer.backPress()) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
    }
    @Override
    public void initView() {
        view= View.inflate(getContext(), R.layout.activity_main_videohome,null);
        tv_local= (TextView) view.findViewById(R.id.tv_local);
        lv_video_home= (MyListView) view.findViewById(R.id.lv_video_home);
        ll_layout1= (LinearLayout) view.findViewById(R.id.ll_layout1);
        loading = (ImageView) view.findViewById(R.id.loading);
        loadingFail = (ImageView) view.findViewById(R.id.loadingfail);
        changeType = (TextView) view.findViewById(R.id.changeType);
        changeType.setOnClickListener(this);
        infos=new ArrayList<>();
        adapter=new VideoAdapter();
        lv_video_home.setAdapter(adapter);
        initViewPager();
        tv_local.setOnClickListener(this);
        lv_video_home.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        return true;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void closeOther() {

    }

    private void initData() {
        lv_video_home.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        loadingFail.setVisibility(View.GONE);
        LunBoRequest request=new LunBoRequest();
        request.getData(NET.getVideo+"&type="+type);
        //Toast.makeText(ContextUtils.getContext(), type+"", Toast.LENGTH_SHORT).show();
        request.setInter(new BaseRequest.BaseInter<List<lunboInfo>>() {
            @Override
            public void handleData(List<lunboInfo> data,Boolean isSuccess) {
                loading.setVisibility(View.GONE);
                lv_video_home.setVisibility(View.VISIBLE);
                if(!isSuccess){
                    isLoadingSuccess=false;
                    loadingFail.setVisibility(View.VISIBLE);
                    return;
                }
                isLoadingSuccess=true;
                infos=data;
                adapter.notifyDataSetChanged();

            }
        });
    }
    private int item;
    private void initViewPager() {
        viewpager= (ViewPager) view.findViewById(R.id.viewpager);
        mImageViews=new ArrayList<>();
        for(int i=0;i<imageIds.length;i++) {
            ImageView view = new ImageView(getContext());
            view.setBackgroundResource(imageIds[i]);
            mImageViews.add(view);
            ImageView point = new ImageView(getContext());
            point.setBackgroundResource(R.drawable.point_selector);
            int width = Utils.dip2px(getContext(),8);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,width);
            if(i==0){
                point.setEnabled(true); //显示红色
            }else{
                point.setEnabled(false);//显示灰色
                params.leftMargin = width;
            }
            point.setLayoutParams(params);

            ll_layout1.addView(point);
        }
        viewpager.setAdapter(new MyPagerAdapter());
        item = Integer.MAX_VALUE/2 - Integer.MAX_VALUE/2%mImageViews.size();
        viewpager.setCurrentItem(item);
        mHandler.sendEmptyMessageDelayed(0,4000);
        viewpager.addOnPageChangeListener(this);
    }

    @Override
    protected void lazyLoad() {
        if(isLoadingSuccess){ return;}
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_local:
                startActivity(new Intent(ContextUtils.getContext(), VideoActivity.class));
                break;
            case R.id.changeType:
                if(type==2){
                    type=0;
                }else{
                    type = type+1;
                }
                initData();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        int realposition=position%mImageViews.size();
        ll_layout1.getChildAt(preposition).setEnabled(false);
        //当前的设置为高亮-红色
        ll_layout1.getChildAt(realposition).setEnabled(true);
        preposition=realposition;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state==ViewPager.SCROLL_STATE_DRAGGING){
            mHandler.removeCallbacksAndMessages(null);
        }else if(state==ViewPager.SCROLL_STATE_SETTLING){
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendEmptyMessageDelayed(0,4000);
        }else{}
    }
    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            //return mImageViews.size();
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //container.removeView(mImageViews.get(position%mImageViews.size()));
            //container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = mImageViews.get(position%mImageViews.size());
            if(imageView.getParent()!=null){
                ((ViewPager)imageView.getParent()).removeView(imageView);
            }
            container.addView(mImageViews.get(position%mImageViews.size()));
            return mImageViews.get(position%mImageViews.size());
        }
    }
    private class VideoAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int position) {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if(convertView==null){
                holder=new ViewHolder();
                convertView=View.inflate(getContext(),R.layout.video_home_item,null);
                holder.jz= (JZVideoPlayerStandard) convertView.findViewById(R.id.jz);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.jz.setUp(NET.netPath+"/"+infos.get(position).getPath(), JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, infos.get(position).getName());
            return convertView;
        }
        class ViewHolder{
            JZVideoPlayerStandard jz;
        }
    }
}
