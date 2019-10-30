package com.lws.sy.mv.activity;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lws.sy.mv.NetInfo.PCInfo;
import com.lws.sy.mv.NetInfo.commentInfo;
import com.lws.sy.mv.NetInfo.commonResponse;
import com.lws.sy.mv.NetInfo.postInfo;
import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.musicUtils.TagUtils;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.CommentRequest;
import com.lws.sy.mv.request.CommonRequest;
import com.lws.sy.mv.request.PCRequest;
import com.lws.sy.mv.request.postDetailRequest;
import com.lws.sy.mv.view.CircleImageView;
import com.lws.sy.mv.view.Player;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class PostDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView lv_post_comment;
    private CircleImageView post_detail_avatar;
    private TextView post_detail_nickname;
    private TextView post_detail_time;
    private TextView post_detail_title;
    private TextView post_detail_content;
    private ImageView post_detail_photo1;
    private ImageView post_detail_photo2;
    private ImageView post_detail_photo3;
    private LinearLayout post_detail_music;
    private TextView post_detail_music_name;
    private JZVideoPlayerStandard post_detail_jz;
    private LinearLayout post_detail_img;
    private ImageView post_detail_collect;
    private ImageView post_detail_praise;
    private EditText post_detail_comment;
    private TextView post_detail_comment_send;
    private TextView post_detail_comment_empty;
    private int postid;
    private Player mPlayer;
    private postInfo info;
    private CommonRequest request1;
    private commonResponse response;
    private CommonRequest mRequest;
    private ScrollView scroll;
    private commonResponse commentResponse;
    private PCInfo mPCInfo;
    private CommentAdapter adapter;
    private List<commentInfo> mCommentInfo;
    private LinearLayout ll_bottom_post;
    private int currentPager = 1;
    private ImageView goBack;
    private boolean isOver = false;
    private int total = 0;
    private TextView post_detail_comment_over;
    private TextView post_detail_comment_load_more;
    private ImageView post_play_music;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    //放头像
                    if(info.getAvatar()==null || info.getAvatar().equals("")){

                    }else{
                        Glide.with(PostDetailActivity.this).load(NET.BaseUrl+"/"+ info.getAvatar()).into(post_detail_avatar);
                    }
                    //放昵称
                    post_detail_nickname.setText(info.getNickname());
                    //放时间
                    post_detail_time.setText(info.getTime().substring(0,info.getTime().indexOf(".")));
                    //放标题
                    post_detail_title.setText(info.getTitle());
                    //放内容
                    post_detail_content.setText(info.getContent());
                    //
                    if(info.getType()==1){
                        post_detail_img.setVisibility(View.VISIBLE);
                        if(!info.getImgPath().equals("")){
                            String name1 = info.getImgPath().substring(0,info.getImgPath().indexOf("#"));
                            Glide.with(PostDetailActivity.this).load(NET.BaseUrl+"/upload/"+name1).into(post_detail_photo1);
                            String otherPath = info.getImgPath().substring(info.getImgPath().indexOf("#")+1);
                            if(otherPath.indexOf("#")+1==otherPath.length()){
                                return;
                            }
                            Log.e("otherPath",otherPath);
                            String name2 = otherPath.substring(0,otherPath.indexOf("#"));
                            Log.e("name2",name2);
                            if(!name2.equals("")){
                                Glide.with(PostDetailActivity.this).load(NET.BaseUrl+"/upload/"+name2).into(post_detail_photo2);
                                if(otherPath.indexOf("#")+1==otherPath.length()){

                                }else{
                                    String name3 = otherPath.substring(otherPath.indexOf("#")+1,otherPath.lastIndexOf("#"));
                                    Log.e("name3",name3);
                                    if(!name3.equals("")){
                                        Glide.with(PostDetailActivity.this).load(NET.BaseUrl+"/upload/"+name3).into(post_detail_photo3);
                                    }
                                }
                            }
                        }
                    }else if(info.getType()==2){
                        post_detail_music.setVisibility(View.VISIBLE);
                        post_detail_music_name.setText(info.getMusicPath().substring(info.getMusicPath().indexOf("/")+1,info.getMusicPath().indexOf("#")));
                    }else if(info.getType()==3){
                        post_detail_jz.setVisibility(View.VISIBLE);
                        post_detail_jz.setUp(NET.BaseUrl+"/upload/"+info.getVideoPath().substring(info.getVideoPath().indexOf("#")+1),JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,"");
                    }
                    break;
                case 1:
                    if(mPCInfo.isPraise()){
                        post_detail_praise.setImageResource(R.mipmap.praise1);
                    }else{
                        post_detail_praise.setImageResource(R.mipmap.praise);
                    }
                    if(mPCInfo.isCollect()){
                        post_detail_collect.setImageResource(R.mipmap.collect1);
                    }else{
                        post_detail_collect.setImageResource(R.mipmap.collect);
                    }
                    break;
                case 2:
                    if(response.getState().equals("ok")){
                        if(response.getMsg().equals("1")){
                            mPCInfo.setPraise(true);
                            post_detail_praise.setImageResource(R.mipmap.praise1);
                        }else if(response.getMsg().equals("2")){
                            post_detail_collect.setImageResource(R.mipmap.collect1);
                            mPCInfo.setCollect(true);
                        }else{
                            post_detail_collect.setImageResource(R.mipmap.collect);
                            mPCInfo.setCollect(false);
                        }
                    }else{
                        if(response.getMsg().equals("1")){
                            mPCInfo.setPraise(false);
                            Toast.makeText(PostDetailActivity.this, "点赞失败", Toast.LENGTH_SHORT).show();
                        }else if(response.getMsg().equals("2")){
                            mPCInfo.setCollect(false);
                            Toast.makeText(PostDetailActivity.this, "收藏失败", Toast.LENGTH_SHORT).show();
                        }else{
                            mPCInfo.setCollect(true);
                            Toast.makeText(PostDetailActivity.this, "取消收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case 3:
                    if (commentResponse.getState().equals("ok")){
                        Toast.makeText(PostDetailActivity.this, commentResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        //刷新数据
                        initComment();
                    } else {
                        Toast.makeText(PostDetailActivity.this, commentResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 4:
                    lv_post_comment.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    lv_post_comment.setSelection(mCommentInfo.size()-1);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        request1 = new CommonRequest();
        mRequest = new CommonRequest();
        mCommentInfo =new ArrayList<>();
        adapter = new CommentAdapter();
        Intent intent = getIntent();
        postid = intent.getIntExtra("postid", 0);
        request1.setInter(new BaseRequest.BaseInter<commonResponse>() {

            @Override
            public void handleData(commonResponse data, Boolean isSuccess) {
                if(!isSuccess){
                    Toast.makeText(PostDetailActivity.this, "网络连接出错，请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                response = data;
                mHandler.sendEmptyMessage(2);
            }
        });
        mRequest.setInter(new BaseRequest.BaseInter<commonResponse>() {

            @Override
            public void handleData(commonResponse data, Boolean isSuccess) {
                if(!isSuccess){
                    Toast.makeText(PostDetailActivity.this, "网络连接出错，请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                commentResponse = data;
                mHandler.sendEmptyMessage(3);
            }
        });
        initView();
        initData();
    }

    private void initData() {
        postDetailRequest request = new postDetailRequest();
        request.getData(NET.getPostDetail+"&postid="+postid);
        request.setInter(new BaseRequest.BaseInter<postInfo>() {

            @Override
            public void handleData(postInfo data, Boolean isSuccess) {
                if(!isSuccess){
                    Toast.makeText(PostDetailActivity.this, "网络连接出错，请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                info = data;
                mHandler.sendEmptyMessage(0);
            }
        });
        initComment();
        if(TagUtils.id == -1){
            return;
        }
        PCRequest request2 = new PCRequest();
        request2.getData(NET.judgePraiseCollect+"&id="+TagUtils.id+"&postid="+postid);
        request2.setInter(new BaseRequest.BaseInter<PCInfo>() {

            @Override
            public void handleData(PCInfo data, Boolean isSuccess) {
                if(!isSuccess){
                    Toast.makeText(PostDetailActivity.this, "网络连接出错，请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPCInfo = data;
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    private void initComment() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.getData(NET.getComment+"&postid="+postid+"&currentPager="+currentPager);
        commentRequest.setInter(new BaseRequest.BaseInter<List<commentInfo>>() {

            @Override
            public void handleData(List<commentInfo> data, Boolean isSuccess) {
                if(!isSuccess){
                    post_detail_comment_load_more.setVisibility(View.GONE);
                    Toast.makeText(PostDetailActivity.this, "网络连接出错，请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e("TAG","暂评论");
                mCommentInfo = data;
                if(mCommentInfo==null){
                    //暂无评论
                    //Log.e("TAG","暂无评论");
                    post_detail_comment_empty.setVisibility(View.VISIBLE);
                    post_detail_comment_load_more.setVisibility(View.GONE);
                    return;
                }
                if(mCommentInfo.size()==total){
                    isOver = true;
                    return;
                }
                post_detail_comment_empty.setVisibility(View.GONE);
                total = mCommentInfo.size();
                mHandler.sendEmptyMessage(4);
            }
        });
    }

    private void initView() {
        post_play_music = (ImageView) findViewById(R.id.post_play_music);
        lv_post_comment = (ListView) findViewById(R.id.lv_post_comment);
        ll_bottom_post = (LinearLayout) findViewById(R.id.ll_bottom_post);
        post_detail_comment_load_more = (TextView) findViewById(R.id.post_detail_comment_load_more);
        post_detail_comment_over = (TextView) findViewById(R.id.post_detail_comment_over);
        post_detail_avatar = (CircleImageView) findViewById(R.id.post_detail_avatar);
        post_detail_title = (TextView) findViewById(R.id.post_detail_title);
        post_detail_content = (TextView) findViewById(R.id.post_detail_content);
        post_detail_music = (LinearLayout) findViewById(R.id.post_detail_music);
        post_detail_music_name = (TextView) findViewById(R.id.post_detail_music_name);
        post_detail_nickname = (TextView) findViewById(R.id.post_detail_nickname);
        post_detail_photo1 = (ImageView) findViewById(R.id.post_detail_photo1);
        post_detail_photo2 = (ImageView) findViewById(R.id.post_detail_photo2);
        post_detail_photo3 = (ImageView) findViewById(R.id.post_detail_photo3);
        post_detail_time = (TextView) findViewById(R.id.post_detail_time);
        post_detail_jz = (JZVideoPlayerStandard) findViewById(R.id.post_detail_jz);
        post_detail_img = (LinearLayout) findViewById(R.id.post_detail_img);
        post_detail_avatar.setOnClickListener(this);
        post_detail_collect = (ImageView) findViewById(R.id.post_detail_collect);
        post_detail_praise = (ImageView) findViewById(R.id.post_detail_praise);
        post_detail_praise.setOnClickListener(this);
        post_detail_collect.setOnClickListener(this);
        post_detail_comment = (EditText) findViewById(R.id.post_detail_comment);
        post_detail_comment_send = (TextView) findViewById(R.id.post_detail_comment_send);
        post_detail_comment_send.setOnClickListener(this);
        post_detail_comment_empty = (TextView) findViewById(R.id.post_detail_comment_empty);
        scroll = (ScrollView) findViewById(R.id.scroll);
        post_detail_comment_load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCommentInfo.size()<10){
                    post_detail_comment_over.setVisibility(View.VISIBLE);
                    post_detail_comment_load_more.setVisibility(View.GONE);
                    return;
                }
                if(isOver){
                    post_detail_comment_over.setVisibility(View.VISIBLE);
                    post_detail_comment_load_more.setVisibility(View.GONE);
                    return;
                }
                currentPager = currentPager +1;
                initComment();
            }
        });
        goBack = (ImageView) findViewById(R.id.user_post_back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            Instrumentation inst = new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                        } catch (Exception e) {
                        }
                    }
                }.start();
            }
        });
        mPlayer=new Player();
        post_play_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPlaying){
                    mPlayer.playUrl(NET.BaseUrl+"/upload/"+info.getMusicPath().substring(info.getMusicPath().indexOf("#")+1));
                    post_play_music.setImageResource(R.mipmap.post_play);
                    isPlaying = true;
                }else{
                    if(mPlayer.mediaPlayer.isPlaying()){
                        mPlayer.pause();
                        post_play_music.setImageResource(R.mipmap.post_pause);
                    }else{
                        mPlayer.play();
                        post_play_music.setImageResource(R.mipmap.post_play);
                    }
                }
            }
        });
    }
    private boolean isPlaying = false;
    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
        mPlayer.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.post_detail_avatar:
                TagUtils.userid = info.getId();
                Intent intent =new Intent(ContextUtils.getContext(), UserActivity.class);
                intent.putExtra("isAvatar",true);
                startActivity(intent);
                break;
            case R.id.post_detail_collect:
                if(TagUtils.id == -1){
                    Toast.makeText(this, "您尚未登录，请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TagUtils.id == info.getId()){
                    Toast.makeText(this, "无法收藏自己的", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mPCInfo.isCollect()){
                    //取消收藏
                    request1.getData(NET.praiseOrCollect+"&id="+TagUtils.id+"&postid="+postid+"&type=3");
                }else{
                    //收藏
                    request1.getData(NET.praiseOrCollect+"&id="+TagUtils.id+"&postid="+postid+"&type=2");
                }
                break;
            case R.id.post_detail_praise:
                if(TagUtils.id == -1){
                    Toast.makeText(this, "您尚未登录，请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TagUtils.id == info.getId()){
                    Toast.makeText(this, "不能赞自己哟~", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mPCInfo.isPraise()){
                    Toast.makeText(this, "您已经赞过了", Toast.LENGTH_SHORT).show();
                    return;
                }
                request1.getData(NET.praiseOrCollect+"&id="+TagUtils.id+"&postid="+postid+"&type=1"+"&tower="+info.getId());
                break;
            case R.id.post_detail_comment_send:
                String content = post_detail_comment.getText().toString();
                hideIM();
                if(TagUtils.id == -1){
                    Toast.makeText(this, "您尚未登录，请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(content.trim().equals("")){
                    Toast.makeText(this, "评论不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //发评论
                mRequest.getData(NET.sendComment+"&postid="+info.getPostid()+"&commentid="+TagUtils.id+"&content="+content+"&tower="+info.getId());
                break;
        }
    }

    private void hideIM() {
        InputMethodManager imm =(InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(post_detail_comment.getWindowToken(), 0);
        post_detail_comment.setText("");
    }

    private class CommentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCommentInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return mCommentInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(null==convertView){
                convertView = View.inflate(PostDetailActivity.this,R.layout.post_detail_comment,null);
                holder = new ViewHolder();
                holder.post_detail_comment_avatar = (CircleImageView) convertView.findViewById(R.id.post_detail_comment_avatar);
                holder.post_detail_comment_nickname = (TextView) convertView.findViewById(R.id.post_detail_comment_nickname);
                holder.post_detail_comment_tower = (TextView) convertView.findViewById(R.id.post_detail_comment_tower);
                holder.post_detail_comment_content = (TextView) convertView.findViewById(R.id.post_detail_comment_content);
                holder.post_detail_comment_time = (TextView) convertView.findViewById(R.id.post_detail_comment_time);
                holder.post_detail_comment_sign = (TextView) convertView.findViewById(R.id.post_detail_comment_sign);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            if(mCommentInfo.get(position).getAvatar().equals("")||mCommentInfo.get(position).getAvatar()==null){
                holder.post_detail_comment_avatar.setImageResource(R.mipmap.icon);
            }else{
                Log.e("touxiang",mCommentInfo.get(position).getAvatar());
                Glide.with(PostDetailActivity.this).load(NET.BaseUrl+"/"+mCommentInfo.get(position).getAvatar()).into(holder.post_detail_comment_avatar);
            }
            if(mCommentInfo.get(position).getCommentid()==info.getId()){
                holder.post_detail_comment_sign.setText("楼主");
            }else{
                holder.post_detail_comment_sign.setText("路人");
            }
            holder.post_detail_comment_tower.setText("第"+mCommentInfo.get(position).getTower()+"楼");
            holder.post_detail_comment_nickname.setText(mCommentInfo.get(position).getNickname());
            holder.post_detail_comment_content.setText(mCommentInfo.get(position).getContent());
            holder.post_detail_comment_time.setText(mCommentInfo.get(position).getTime().substring(0,mCommentInfo.get(position).getTime().indexOf(".")));
            return convertView;
        }
        class ViewHolder {
            CircleImageView post_detail_comment_avatar;
            TextView post_detail_comment_nickname;
            TextView post_detail_comment_tower;
            TextView post_detail_comment_time;
            TextView post_detail_comment_content;
            TextView post_detail_comment_sign;
        }
    }
}
