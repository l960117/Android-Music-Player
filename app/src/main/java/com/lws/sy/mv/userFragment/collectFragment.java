package com.lws.sy.mv.userFragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lws.sy.mv.NetInfo.postInfo;
import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.activity.PostDetailActivity;
import com.lws.sy.mv.activity.UserPostActivity;
import com.lws.sy.mv.musicUtils.TagUtils;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.postDetailRequest;
import com.lws.sy.mv.videoFragment.VideoBaseFragment;
import com.lws.sy.mv.view.CircleImageView;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class collectFragment extends VideoBaseFragment implements View.OnClickListener {
    private postInfo info;
    private TextView user_post_title;
    private TextView user_post_content;
    private CircleImageView user_post_avatar;
    private TextView user_post_nickname;
    private TextView user_post_time;
    private TextView user_post_more;
    private TextView user_post_empty;
    private LinearLayout user_post_click;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    user_post_content.setText(info.getContent());
                    if(info.getAvatar().equals("")||info.getAvatar()==null){

                    }else{
                        Glide.with(getContext()).load(NET.BaseUrl+"/"+info.getAvatar()).into(user_post_avatar);
                    }
                    user_post_title.setText(info.getTitle());
                    user_post_nickname.setText(info.getNickname());
                    user_post_time.setText(info.getTime().substring(0,info.getTime().indexOf(".")));
                    break;
            }
        }
    };
    public collectFragment(String title) {
        super.title=title;
    }

    @Override
    public void initView() {
        view= View.inflate(ContextUtils.getContext(), R.layout.mt_tiezi,null);
        user_post_avatar = (CircleImageView) view.findViewById(R.id.user_post_avatar);
        user_post_content = (TextView) view.findViewById(R.id.user_post_content);
        user_post_title = (TextView) view.findViewById(R.id.user_post_title);
        user_post_nickname = (TextView) view.findViewById(R.id.user_post_nickname);
        user_post_time = (TextView) view.findViewById(R.id.user_post_time);
        user_post_more = (TextView) view.findViewById(R.id.user_post_more);
        user_post_empty = (TextView) view.findViewById(R.id.user_post_empty);
        user_post_more.setOnClickListener(this);
        user_post_click = (LinearLayout) view.findViewById(R.id.user_post_click);
        user_post_click.setOnClickListener(this);
        initData();
    }

    public void initData() {
        postDetailRequest request=new postDetailRequest();
        if(TagUtils.isAvatar){
            request.getData(NET.getUserCollectPost+"&id="+ TagUtils.userid+"&type=1");
        }else{
            request.getData(NET.getUserCollectPost+"&id="+ TagUtils.id+"&type=1");
        }
        request.setInter(new BaseRequest.BaseInter<postInfo>() {

            @Override
            public void handleData(postInfo data, Boolean isSuccess) {
                if(!isSuccess){
                    return;
                }
                user_post_more.setVisibility(View.VISIBLE);
                user_post_empty.setVisibility(View.GONE);
                if(data == null){
                    user_post_more.setVisibility(View.GONE);
                    user_post_empty.setVisibility(View.VISIBLE);
                    user_post_click.setVisibility(View.GONE);
                    return;
                }
                info=data;
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_post_more:
                Intent intent = new Intent(ContextUtils.getContext(), UserPostActivity.class);
                intent.putExtra("type",2);
                if(TagUtils.isAvatar){
                    intent.putExtra("userid",TagUtils.userid);
                }else{
                    intent.putExtra("userid",TagUtils.id);
                }
                intent.putExtra("nickname",TagUtils.nickname);
                startActivity(intent);
                break;
            case R.id.user_post_click:
                Intent intent1 = new Intent(ContextUtils.getContext(), PostDetailActivity.class);
                intent1.putExtra("postid",info.getPostid());
                startActivity(intent1);
                break;
        }
    }

}
