package com.lws.sy.mv.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lws.sy.mv.NetInfo.commonResponse;
import com.lws.sy.mv.NetInfo.userInfo;
import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.Utils.TabUtils;
import com.lws.sy.mv.Utils.UserFragmentUtils;
import com.lws.sy.mv.Utils.Utils;
import com.lws.sy.mv.db.dao.MusicInfoDao;
import com.lws.sy.mv.musicUtils.TagUtils;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.UpLoadRequest;
import com.lws.sy.mv.request.userInfoRequest;
import com.lws.sy.mv.view.CircleImageView;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.ImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.io.File;
import java.util.ArrayList;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView cv_avatar;
    private FrameLayout fl_user;
    private userInfoRequest request;
    private int id=0;
    private UpLoadRequest request1;
    private userInfo userinfo;
    private MusicInfoDao dao;
    private TextView tv_title;
    private TextView tv_desc;
    private ImageView iv_photo;
    private ImagePicker imagePicker;
    private ImageView user_setting;
    private boolean isAvatar;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                if(!userinfo.getAvatar().equals("")){
                    Glide.with(UserActivity.this).load(NET.BaseUrl+"/"+userinfo.getAvatar()).into(cv_avatar);
                }else{
                    cv_avatar.setImageResource(R.mipmap.avatar);
                }
                if(!userinfo.getNickname().equals("")){
                    tv_title.setText(userinfo.getNickname());
                    TagUtils.nickname = userinfo.getNickname();
                }
                if(!userinfo.getSign().equals("")){
                    tv_desc.setText(userinfo.getSign());
                }else{
                    tv_desc.setText("这个人很懒,什么都没写~");
                }
                if(!userinfo.getPhoto().equals("")){
                    Glide.with(UserActivity.this).load(NET.BaseUrl+"/"+userinfo.getPhoto()).into(iv_photo);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Intent intent =getIntent();
        isAvatar = intent.getBooleanExtra("isAvatar", false);
        if(TagUtils.id==TagUtils.userid){
            isAvatar=false;
        }
        TagUtils.isAvatar = isAvatar;
        fl_user= (FrameLayout) findViewById(R.id.fl_user);
        cv_avatar= (CircleImageView) findViewById(R.id.cv_avatar);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        cv_avatar.setBorderWidth(6);
        request1 = new UpLoadRequest();
        request1.setInter(new BaseRequest.BaseInter<commonResponse>() {
            @Override
            public void handleData(commonResponse data, Boolean isSuccess) {
                if(!isSuccess){
                    Toast.makeText(UserActivity.this, "网络连接出错，请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(data.getState().equals("ok")){
                    Toast.makeText(UserActivity.this, data.getMsg(), Toast.LENGTH_SHORT).show();
                    initData();
                }else{
                    Toast.makeText(UserActivity.this, data.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        tv_title= (TextView) findViewById(R.id.tv_title);
        tv_desc= (TextView) findViewById(R.id.tv_desc);
        user_setting= (ImageView) findViewById(R.id.user_setting);
        user_setting.setOnClickListener(this);
        cv_avatar.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
        initID();
        initFram();
        initImagePick();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initImagePick() {
        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setMultiMode(false);
        imagePicker.setCrop(true);//允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(800);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(800);//保存文件的高度。单位像素
    }

    private void initID() {
        dao=new MusicInfoDao(this);
        Cursor cursor = dao.queryLogin();
        while(cursor.moveToNext()){
            id=cursor.getInt(0);
        }
    }
    private void initData() {
        request=new userInfoRequest();
        if(isAvatar){
            request.getData(NET.getInfo+"&id="+TagUtils.userid);
        }else{
            request.getData(NET.getInfo+"&id="+id);
        }
        request.setInter(new BaseRequest.BaseInter<userInfo>() {
            @Override
            public void handleData(userInfo data, Boolean isSuccess) {
                if(!isSuccess){
                    Toast.makeText(UserActivity.this, "网络连接出错，请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                userinfo=data;
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    private void initFram() {
        View view=View.inflate(this,R.layout.activity_video_vp,null);
        final TabLayout tl_ty= (TabLayout) view.findViewById(R.id.tl_ty);
        ViewPager vp_main= (ViewPager) view.findViewById(R.id.vp_main);
        vp_main.setAdapter(new VpAdapter(getSupportFragmentManager()));
        tl_ty.post(new Runnable() {
            @Override
            public void run() {
                TabUtils.setIndicator(tl_ty,50,50);
            }
        });
        tl_ty.setupWithViewPager(vp_main);
        tl_ty.setTabMode(TabLayout.MODE_FIXED);
        fl_user.addView(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 6) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                String path = images.get(0).path;
//                cv_avatar.setImageBitmap(BitmapFactory.decodeFile(path));
                request1.upload(path,NET.upload+"&id="+id,"avatar");
            } else if(data != null && requestCode == 5){
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                String path = images.get(0).path;
//                iv_photo.setImageBitmap(BitmapFactory.decodeFile(path));
                request1.upload(path,NET.upload+"&id="+id,"photo");
            }else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cv_avatar:
                if(isAvatar){
                    return;
                }
                imagePicker.setFocusWidth(iv_photo.getWidth());
                imagePicker.setFocusHeight(iv_photo.getWidth());
                imagePicker.setOutPutX(iv_photo.getWidth());
                imagePicker.setOutPutY(iv_photo.getWidth());
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, 6);
                break;
            case R.id.user_setting:
                if(isAvatar){
                    return;
                }
                startActivity(new Intent(UserActivity.this,UserSettingActivity.class));
                break;
            case R.id.iv_photo:
                if(isAvatar){
                    return;
                }
                imagePicker.setFocusWidth(iv_photo.getWidth());
                imagePicker.setFocusHeight(Utils.dip2px(this,200));
                imagePicker.setOutPutX(iv_photo.getWidth());
                imagePicker.setOutPutY(Utils.dip2px(this,200));
                Intent intent1 = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent1, 5);
                break;
        }
    }

    class VpAdapter extends FragmentPagerAdapter {


        public VpAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return UserFragmentUtils.getFragment(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return UserFragmentUtils.getFragment(position).getTitle();
        }
    }
    public class GlideImageLoader implements ImageLoader {

        @Override
        public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
            Glide.with(activity)
                    .load(Uri.fromFile(new File(path)))
                    .into(imageView);
        }

        @Override
        public void clearMemoryCache() {
            //这里是清除缓存的方法,根据需要自己实现
        }
    }
}
