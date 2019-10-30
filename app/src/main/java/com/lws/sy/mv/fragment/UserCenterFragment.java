package com.lws.sy.mv.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lws.sy.mv.NetInfo.postInfo;
import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.Utils.DisplayUtil;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.activity.CenterPostActivity;
import com.lws.sy.mv.activity.LoginActivity;
import com.lws.sy.mv.activity.PostDetailActivity;
import com.lws.sy.mv.activity.UserActivity;
import com.lws.sy.mv.dialog.CommonDialog;
import com.lws.sy.mv.musicUtils.TagUtils;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.postRequest;
import com.lws.sy.mv.view.CircleImageView;
import com.lws.sy.mv.view.LoadMoreListView;
import com.lws.sy.mv.view.Player;

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

public class UserCenterFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView iv_add;
    private PopupWindow pw;
    private RadioGroup rg_select;
    private LoadMoreListView lv_center_list;
    private RelativeLayout rl_all;
    private ImageView post_cancel;
    private RadioButton center_photo;
    private RadioButton center_video;
    private RadioButton center_music;
    private CommonDialog dialog;
    private postRequest request;
    private List<postInfo> infos;
    private MyPostAdapter adapter;
    private Player mPlayer;
    private int total = 0;
    private boolean isOver = false;
    private int currentPager = 1;
    private SwipeRefreshLayout sf_fresh;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0){
                lv_center_list.setLoadCompleted();
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void initView() {
        view = View.inflate(ContextUtils.getContext(), R.layout.activity_main_center,null);
        iv_add = (ImageView) view.findViewById(R.id.center_add);
        lv_center_list = (LoadMoreListView) view.findViewById(R.id.lv_center_list);
        sf_fresh = (SwipeRefreshLayout) view.findViewById(R.id.sf_fresh);
        sf_fresh.setColorSchemeColors(Color.RED,Color.GREEN,Color.BLUE);
        lv_center_list.setOnItemClickListener(this);
        rl_all = (RelativeLayout) view.findViewById(R.id.rl_all);
        infos = new ArrayList<>();
        adapter = new MyPostAdapter();
        //mPlayer = new Player();
        dialog=new CommonDialog(getContext());
        dialog.setInter(new CommonDialog.onDialogClickListener() {
            @Override
            public void OnOkClick() {
                startActivity(new Intent(ContextUtils.getContext(), LoginActivity.class));
            }

            @Override
            public void OnCancelClick() {

            }
        });
        iv_add.setOnClickListener(this);
        //initData();
        lv_center_list.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onloadMore() {
                if(infos.size()<10){
                    lv_center_list.setLoadCompleted();
                    Toast.makeText(ContextUtils.getContext(), "没有更多帖子了", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isOver){
                    lv_center_list.setLoadCompleted();
                    Toast.makeText(ContextUtils.getContext(), "没有更多帖子了", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentPager = currentPager+1;
                loadMore();
            }
        });
        sf_fresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPager = 1;
                initData();
                sf_fresh.setRefreshing(false);
            }
        });

    }

    private void loadMore() {
        postRequest request1=new postRequest();
        request1.getData(NET.getPost+"&currentPager="+currentPager);
        request1.setInter(new BaseRequest.BaseInter<List<postInfo>>() {

            @Override
            public void handleData(List<postInfo> data, Boolean isSuccess) {
                lv_center_list.setLoadCompleted();
                if(!isSuccess){
                    Toast.makeText(ContextUtils.getContext(), "网络连接出错，请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(data==null){
                    currentPager = currentPager-1;
                    return;
                }
                if(data.size()==total){
                    isOver = true;
                    return;
                }
                total = data.size();
                infos=data;
                mHandler.sendEmptyMessage(0);
            }
        });
    }

    @Override
    protected void closeOther() {
        if(pw!=null&&pw.isShowing()){
            pw.dismiss();
        }
    }

    private void initData() {
        request=new postRequest();
        request.getData(NET.getPost+"&currentPager="+currentPager);
        request.setInter(new BaseRequest.BaseInter<List<postInfo>>() {

            @Override
            public void handleData(List<postInfo> data, Boolean isSuccess) {
                if(!isSuccess){
                    Toast.makeText(ContextUtils.getContext(), "网络连接出错，请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                infos=data;
                total = infos.size();
                if(data.size()==0){
                    return;
                }
                lv_center_list.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void lazyLoad() {
        if(pw!=null&&pw.isShowing()){
            pw.dismiss();
        }
        currentPager=1;
        initData();
        //mPlayer=new Player();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.center_add:
                Log.e("UserCenter","点击加号");
                showTypePw();
                break;
            case R.id.post_cancel:
                if(pw!=null&&pw.isShowing()){
                    pw.dismiss();
                }
                break;
        }
    }

    private void showTypePw() {
        if(pw==null){
            View pwView=View.inflate(ContextUtils.getContext(),R.layout.center_pw,null);
            pw=new PopupWindow(pwView, DisplayUtil.getSreenWidth(ContextUtils.getContext()),DisplayUtil.getSreenHeight(ContextUtils.getContext()),true);
            rg_select = (RadioGroup) pwView.findViewById(R.id.rg_select);
            post_cancel = (ImageView) pwView.findViewById(R.id.post_cancel);
            center_photo = (RadioButton) pwView.findViewById(R.id.center_photo);
            center_music = (RadioButton) pwView.findViewById(R.id.center_music);
            center_video = (RadioButton) pwView.findViewById(R.id.center_video);
        }
        pw.setFocusable(false);
        if(pw.isShowing()){
            pw.dismiss();
            return;
        }
        pw.setAnimationStyle(R.style.popwin_anim_style);
        pw.showAsDropDown(rl_all, 0, -DisplayUtil.getSreenHeight(ContextUtils.getContext()),0);
        rg_select.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(TagUtils.id==-1){
                    dialog.show("您尚未登录，是否前往登录页？",false);
                    center_photo.setChecked(false);
                    center_music.setChecked(false);
                    center_video.setChecked(false);
                    return;
                }
                switch (checkedId){
                    case R.id.center_photo:
                        center_photo.setChecked(false);
                        pw.dismiss();
                        Intent photo_intent = new Intent(ContextUtils.getContext(),CenterPostActivity.class);
                        photo_intent.putExtra("type",1);
                        startActivity(photo_intent);
                        break;
                    case R.id.center_music:
                        center_music.setChecked(false);
                        pw.dismiss();
                        Intent music_intent = new Intent(ContextUtils.getContext(),CenterPostActivity.class);
                        music_intent.putExtra("type",2);
                        music_intent.putExtra("path","");
                        startActivity(music_intent);
                        break;
                    case R.id.center_video:
                        center_video.setChecked(false);
                        pw.dismiss();
                        Intent video_intent = new Intent(ContextUtils.getContext(),CenterPostActivity.class);
                        video_intent.putExtra("type",3);
                        startActivity(video_intent);
                        break;
                }
            }
        });
        post_cancel.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(ContextUtils.getContext(), PostDetailActivity.class);
        intent.putExtra("postid",infos.get(position).getPostid());
        startActivity(intent);
    }

    private class MyPostAdapter extends BaseAdapter{
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView==null){
                holder=new ViewHolder();
                convertView = View.inflate(getContext(),R.layout.center_post_item,null);
                holder.post_avatar = (CircleImageView) convertView.findViewById(R.id.post_avatar);
                holder.post_nickname = (TextView) convertView.findViewById(R.id.post_nickname);
                holder.post_title = (TextView) convertView.findViewById(R.id.post_title);
                holder.post_time = (TextView) convertView.findViewById(R.id.post_time);
                holder.post_content = (TextView) convertView.findViewById(R.id.post_content);
                holder.post_photo = (ImageView) convertView.findViewById(R.id.post_photo);
                holder.post_jz = (JZVideoPlayerStandard) convertView.findViewById(R.id.post_jz);
                holder.post_music_item = (LinearLayout) convertView.findViewById(R.id.post_music_item);
                holder.post_play_music = (ImageView) convertView.findViewById(R.id.post_play_music);
                holder.post_music_name = (TextView) convertView.findViewById(R.id.post_music_name);
                holder.post_loveSum = (TextView) convertView.findViewById(R.id.post_loveSum);
                holder.post_collectSum = (TextView) convertView.findViewById(R.id.post_collectSum);
                holder.post_commentSum = (TextView) convertView.findViewById(R.id.post_commentSum);
                convertView.setTag(holder);
            }else{
                holder=(ViewHolder)convertView.getTag();
            }
            Log.e("TAG",infos.get(position).getAvatar());
            holder.post_avatar.setVisibility(View.VISIBLE);
            if(infos.get(position).getAvatar().equals("")){
                holder.post_avatar.setImageResource(R.mipmap.icon);
            }else{
                Glide.with(getContext()).load(NET.BaseUrl+"/"+infos.get(position).getAvatar()).into(holder.post_avatar);
            }
            holder.post_loveSum.setText(infos.get(position).getLoveSum()+"");
            holder.post_collectSum.setText(infos.get(position).getCollectSum()+"");
            holder.post_commentSum.setText(infos.get(position).getCommentSum()+"");
            holder.post_nickname.setText(infos.get(position).getNickname());
            holder.post_title.setText(infos.get(position).getTitle());
            holder.post_content.setText(infos.get(position).getContent());
            holder.post_time.setText(infos.get(position).getTime().substring(0,infos.get(position).getTime().indexOf(".")));
            holder.post_photo.setVisibility(View.GONE);
            holder.post_music_item.setVisibility(View.GONE);
            holder.post_jz.setVisibility(View.GONE);
            holder.post_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TagUtils.userid = infos.get(position).getId();
                    Intent intent =new Intent(ContextUtils.getContext(), UserActivity.class);
                    intent.putExtra("isAvatar",true);
                    startActivity(intent);
                }
            });
            if(infos.get(position).getType()==1){
                if(infos.get(position).getImgPath().equals("")){

                }else{
                    holder.post_photo.setVisibility(View.VISIBLE);
                    String path = infos.get(position).getImgPath().substring(0,infos.get(position).getImgPath().indexOf("#"));
                    Glide.with(ContextUtils.getContext()).load(NET.BaseUrl+"/upload/"+path).into(holder.post_photo);
                }
            }else if(infos.get(position).getType()==2){
                holder.post_music_item.setVisibility(View.VISIBLE);
                final ViewHolder finalHolder = holder;
                if(lastPosition == position){
                    if(mPlayer.mediaPlayer.isPlaying()){
                        finalHolder.post_play_music.setImageResource(R.mipmap.post_play);
                    }else{
                        finalHolder.post_play_music.setImageResource(R.mipmap.post_pause);
                    }
                }
                mPlayer.setOnMusicOverListener(new Player.OnMusicOverListener() {
                    @Override
                    public void OnMusicOver() {
                        //Toast.makeText(ContextUtils.getContext(), "音乐完了", Toast.LENGTH_SHORT).show();
                        finalHolder.post_play_music.setImageResource(R.mipmap.post_pause);
                    }
                });
                holder.post_play_music.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(ContextUtils.getContext(), "播放音乐", Toast.LENGTH_SHORT).show();
                        if(lastPosition == position){
                            if(mPlayer.mediaPlayer.isPlaying()){
                                mPlayer.pause();
                                finalHolder.post_play_music.setImageResource(R.mipmap.post_pause);
                            }else{
                                mPlayer.play();
                                finalHolder.post_play_music.setImageResource(R.mipmap.post_play);
                            }
                        }else{
                            lastPosition=position;
                            mPlayer.playUrl(NET.BaseUrl+"/upload/"+infos.get(position).getMusicPath().substring(infos.get(position).getMusicPath().indexOf("#")+1));
                            finalHolder.post_play_music.setImageResource(R.mipmap.post_play);
                        }
                    }
                });
                holder.post_music_name.setText(infos.get(position).getMusicPath().substring(infos.get(position).getMusicPath().indexOf("/")+1,infos.get(position).getMusicPath().indexOf("#")));
            }else if(infos.get(position).getType()==3){
                holder.post_jz.setVisibility(View.VISIBLE);
//                if(mPlayer.mediaPlayer.isPlaying()){
//                    mPlayer.stop();
//                }
                holder.post_jz.setUp(NET.BaseUrl+"/upload/"+infos.get(position).getVideoPath().substring(infos.get(position).getVideoPath().indexOf("#")+1),JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
            }
            return convertView;
        }
        class ViewHolder {
            CircleImageView post_avatar;
            TextView post_nickname;
            TextView post_title;
            TextView post_content;
            ImageView post_photo;
            TextView post_time;
            JZVideoPlayerStandard post_jz;
            LinearLayout post_music_item;
            ImageView post_play_music;
            TextView post_music_name;
            TextView post_loveSum;
            TextView post_collectSum;
            TextView post_commentSum;
        }
    }
    private int lastPosition = -1;
    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
        mPlayer.stop();
        lastPosition=-1;
    }
    @Override
    public void onResume() {
        super.onResume();
        if(TagUtils.isPost){
            initData();
            TagUtils.isPost = false;
        }
        mPlayer=new Player();
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
}
