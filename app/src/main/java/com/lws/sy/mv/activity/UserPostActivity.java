package com.lws.sy.mv.activity;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lws.sy.mv.NetInfo.postInfo;
import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.postRequest;
import com.lws.sy.mv.view.CircleImageView;

import java.util.List;

public class UserPostActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private int type;
    private int userid;
    private ListView lv_user_post;
    private List<postInfo> postInfos;
    private UserPostAdapter adapter;
    private TextView post_main_title;
    private String nickname;
    private ImageView user_post_back;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    lv_user_post.setAdapter(adapter);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);
        Intent intent =getIntent();
        type = intent.getIntExtra("type",1);
        userid = intent.getIntExtra("userid",-1);
        nickname = intent.getStringExtra("nickname");
        lv_user_post = (ListView) findViewById(R.id.lv_user_post);
        post_main_title = (TextView) findViewById(R.id.post_main_title);
        user_post_back = (ImageView) findViewById(R.id.user_post_back);
        user_post_back.setOnClickListener(new View.OnClickListener() {
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
        if(type==2){
            post_main_title.setText(nickname+"收藏的帖子");
        }else{
            post_main_title.setText(nickname+"的帖子");
        }
        adapter = new UserPostAdapter();
        initData();
        lv_user_post.setOnItemClickListener(this);
    }

    private void initData() {
        postRequest request = new postRequest();
        String url="";
        if(type==1){
            url = NET.getUserPost;
        }else{
            url = NET.getUserCollectPost;
        }
        request.getData(url+"&id="+userid+"&type=2");
        request.setInter(new BaseRequest.BaseInter<List<postInfo>>() {

            @Override
            public void handleData(List<postInfo> data, Boolean isSuccess) {
                if(!isSuccess){
                    Toast.makeText(UserPostActivity.this, "网络连接出错。请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                postInfos = data;
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent1 = new Intent(ContextUtils.getContext(), PostDetailActivity.class);
        intent1.putExtra("postid",postInfos.get(position).getPostid());
        startActivity(intent1);
    }

    private class UserPostAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return postInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return postInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if(convertView==null){
                convertView = View.inflate(UserPostActivity.this,R.layout.user_post_item,null);
                holder = new ViewHolder();
                holder.user_post_item_avatar = (CircleImageView) convertView.findViewById(R.id.user_post_item_avatar);
                holder.user_post_item_content = (TextView) convertView.findViewById(R.id.user_post_item_content);
                holder.user_post_item_nickname = (TextView) convertView.findViewById(R.id.user_post_item_nickname);
                holder.user_post_item_time = (TextView) convertView.findViewById(R.id.user_post_item_time);
                holder.user_post_item_title = (TextView) convertView.findViewById(R.id.user_post_item_title);
                holder.user_post_item_loveSum = (TextView) convertView.findViewById(R.id.user_post_item_loveSum);
                holder.user_post_item_commentSum = (TextView) convertView.findViewById(R.id.user_post_item_commentSum);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            if(postInfos.get(position).getAvatar().equals("")||postInfos.get(position).getAvatar()==null){

            }else{
                Glide.with(UserPostActivity.this).load(NET.BaseUrl+"/"+postInfos.get(position).getAvatar()).into(holder.user_post_item_avatar);
            }
            holder.user_post_item_time.setText(postInfos.get(position).getTime().substring(0,postInfos.get(position).getTime().indexOf(".")));
            holder.user_post_item_nickname.setText(postInfos.get(position).getNickname());
            holder.user_post_item_content.setText(postInfos.get(position).getContent());
            holder.user_post_item_title.setText(postInfos.get(position).getTitle());
            holder.user_post_item_loveSum.setText(postInfos.get(position).getLoveSum()+"");
            holder.user_post_item_commentSum.setText(postInfos.get(position).getCommentSum()+"");
            return convertView;
        }
        class ViewHolder {
            CircleImageView user_post_item_avatar;
            TextView user_post_item_nickname;
            TextView user_post_item_time;
            TextView user_post_item_title;
            TextView user_post_item_content;
            TextView user_post_item_loveSum;
            TextView user_post_item_commentSum;
        }
    }
}
