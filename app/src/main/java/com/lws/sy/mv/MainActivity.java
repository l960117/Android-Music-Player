package com.lws.sy.mv;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lws.sy.mv.NetInfo.commonResponse;
import com.lws.sy.mv.NetInfo.userInfo;
import com.lws.sy.mv.Utils.FragmentUtils;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.activity.LeftAboutActivity;
import com.lws.sy.mv.activity.LeftNewsActivity;
import com.lws.sy.mv.activity.LoginActivity;
import com.lws.sy.mv.activity.UserActivity;
import com.lws.sy.mv.db.dao.MusicInfoDao;
import com.lws.sy.mv.db.dao.VideoInfoDao;
import com.lws.sy.mv.dialog.CommonDialog;
import com.lws.sy.mv.musicUtils.TagUtils;
import com.lws.sy.mv.musicUtils.musicInfo;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.CommonRequest;
import com.lws.sy.mv.request.userInfoRequest;
import com.lws.sy.mv.services.ServicesUtils;
import com.lws.sy.mv.view.CircleImageView;
import com.lws.sy.mv.view.MusicNotify;
import com.lws.sy.mv.view.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;

import static com.lws.sy.mv.Utils.ContextUtils.getContext;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private MyViewPager vp_main;
    private RadioGroup music;
    private FrameLayout fl_left;
    private ImageView iv_open;
    private DrawerLayout dl_left;
    private TextView title;
    private MusicInfoDao dao;
    private VideoInfoDao dao1;
    private ImageView out;
    private userInfo userinfo;
    private int id = -1;
    private LinearLayout ll_user;
    private userInfoRequest request;
    private CircleImageView cv_avatar;
    private TextView tv_nickname;
    private TextView tv_desc;
    private ImageView iv_news_point;
    private TextView left_msg;
    private TextView left_about;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                if(!userinfo.getAvatar().equals("")){
                    Glide.with(MainActivity.this).load(NET.BaseUrl+"/"+userinfo.getAvatar()).into(cv_avatar);
                }
                if(!userinfo.getNickname().equals("")){
                    tv_nickname.setText(userinfo.getNickname());
                }
                if(!userinfo.getSign().equals("")){
                    tv_desc.setText(userinfo.getSign());
                }else{
                    tv_desc.setText("这个人很懒,什么都没写~");
                }
            }else if(msg.what==2){
                iv_news_point.setVisibility(View.VISIBLE);
            }else if(msg.what==3){
                iv_news_point.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDB();
        if(ServicesUtils.lastposition==-1){
            ServicesUtils.addServices();
            initMusicData();
        }
        vp_main=(MyViewPager) findViewById(R.id.vp_pager);
        vp_main.setNoScroll(true);
        vp_main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        music=(RadioGroup) findViewById(R.id.music);
        fl_left=(FrameLayout) findViewById(R.id.fl_left);
        title= (TextView)findViewById(R.id.title);
        out= (ImageView) findViewById(R.id.out);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ServicesUtils.lastposition!=-1){
                    ServicesUtils.destory();
                }
                MusicNotify.cancel();
                finish();
            }
        });
        music.setOnCheckedChangeListener(this);
        vp_main.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        vp_main.setCurrentItem(0);
        vp_main.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                JZVideoPlayer.releaseAllVideos();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        music.check(R.id.music_home);
        initleft();
        initNET();
        iv_open=(ImageView) findViewById(R.id.iv_open);
        dl_left=(DrawerLayout) findViewById(R.id.dl_left);
        iv_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dl_left.openDrawer(GravityCompat.START);
            }
        });
        while(dao.queryMyList("看繁华落尽").moveToNext()||dao.queryMyList("看春来春去").moveToNext()){
            return;
        }
        dao.addMyList("看繁华落尽","flowers");
        dao.addMyList("看春来春去","spring");

    }
    private void initNews() {
        final CommonRequest request = new CommonRequest();
        new Thread(){
            @Override
            public void run() {
                while(true){
                    try {
                        sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(TagUtils.id == -1){
                        continue;
                    }
                    getNewsState();
                }
            }

            private void getNewsState() {
                request.getData(NET.getNewsState+"&id="+id);
                request.setInter(new BaseRequest.BaseInter<commonResponse>() {

                    @Override
                    public void handleData(commonResponse data, Boolean isSuccess) {
                        if(!isSuccess){
                            return;
                        }
                        if(data==null){
                            return;
                        }
                        if(data.getMsg().equals("1")){
                            mHandler.sendEmptyMessage(2);
                        }else{
                            mHandler.sendEmptyMessage(3);
                        }
                    }
                });
            }
        }.start();
    }
    private void initMusicData() {
        List<musicInfo> list =new ArrayList<>();
        Cursor cursor = dao.query("music");
        musicInfo info = null;
        while(cursor.moveToNext()){
            info =new musicInfo();
            String path = cursor.getString(0);
            String name = cursor.getString(1);
            String singer = cursor.getString(2);
            info.setMusic_name(name);
            info.setMusic_singer(singer);
            info.setPath(path);
            list.add(info);
        }
        ServicesUtils.data = list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor cursor = dao.queryLogin();
        while(cursor.moveToNext()){
            id=cursor.getInt(0);
        }
        TagUtils.id = id;
        if(id!=-1){
            initUser();
        }
        initNews();
    }

    private void initUser() {
        request=new userInfoRequest();
        request.getData(NET.getInfo+"&id="+id);
        request.setInter(new BaseRequest.BaseInter<userInfo>() {
            @Override
            public void handleData(userInfo data, Boolean isSuccess) {
                if(!isSuccess){
                    return;
                }
                userinfo=data;
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    private void initNET() {
    }

    private void initDB() {
        dao=new MusicInfoDao(this);
        dao1=new VideoInfoDao(this);
    }

    private void initleft() {
        View left=View.inflate(this,R.layout.activity_left,null);
        cv_avatar= (CircleImageView) left.findViewById(R.id.cv_avatar);
        tv_desc= (TextView) left.findViewById(R.id.tv_desc);
        tv_nickname= (TextView) left.findViewById(R.id.tv_nickname);
        ll_user = (LinearLayout) left.findViewById(R.id.ll_user);
        left_msg = (TextView) left.findViewById(R.id.left_msg);
        iv_news_point = (ImageView) left.findViewById(R.id.iv_news_point);
        left_about = (TextView) left.findViewById(R.id.left_about);
        left_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LeftAboutActivity.class));
            }
        });
        left_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TagUtils.id==-1){
                    Toast.makeText(MainActivity.this, "请登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), LeftNewsActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        ll_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id!=-1){
                    startActivity(new Intent(getContext(), UserActivity.class));
                    return;
                }
                CommonDialog dialog = new CommonDialog(MainActivity.this);
                dialog.show("您尚未登录，是否前往登录？",false);
                dialog.setInter(new CommonDialog.onDialogClickListener() {
                    @Override
                    public void OnOkClick() {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }

                    @Override
                    public void OnCancelClick() {

                    }
                });
            }
        });
        fl_left.addView(left);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId) {
            case R.id.music_home:
                title.setText("音乐中心");
                vp_main.setCurrentItem(0);
                break;
            case R.id.video_home:
                title.setText("视频中心");
                vp_main.setCurrentItem(1);
                break;
            case R.id.user_center:
                title.setText("空间");
                vp_main.setCurrentItem(2);
                break;
        }
    }


    class MyViewPagerAdapter extends FragmentPagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentUtils.getFragment(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
    boolean exit=false;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
            if(!exit){
                exit=true;
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessageDelayed(1,2000);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                exit=false;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
