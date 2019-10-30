package com.lws.sy.mv.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lws.sy.mv.NetInfo.commonResponse;
import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.Utils.DisplayUtil;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.Utils.Utils;
import com.lws.sy.mv.musicUtils.TagUtils;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.CommonRequest;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.ImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class CenterPostActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_post_close;
    private TextView send_post;
    private ImagePicker imagePicker;
    private TextView iv_post_photo;
    private TextView iv_post_video;
    private TextView iv_post_music;
    private LinearLayout ll_post_photo;
    private LinearLayout ll_photo;
    private LinearLayout ll_video;
    private LinearLayout ll_music;
    private LinearLayout ll_post_video;
    private LinearLayout ll_post_music;
    private int type = 1;
    private TextView tv_post_clear;
    private List<String> photo;
    private String videoPath;
    private String musicPath;
    private CommonRequest request;
    private EditText post_title;
    private EditText post_content;
    private LinearLayout ll_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_post);
        initType();
        initImagePick();
        photo=new ArrayList<>();
        request = new CommonRequest();
        request.setInter(new BaseRequest.BaseInter<commonResponse>() {

            @Override
            public void handleData(commonResponse data, Boolean isSuccess) {
                pw.dismiss();
                if(!isSuccess){
                    Toast.makeText(CenterPostActivity.this, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(data.getState().equals("ok")){
                    Toast.makeText(CenterPostActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
                    TagUtils.isPost = true;
                    finish();
                }else{
                    Toast.makeText(CenterPostActivity.this, "发表失败，请重试", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ll_main = (LinearLayout) findViewById(R.id.ll_main);
        post_title = (EditText) findViewById(R.id.post_title);
        post_content = (EditText) findViewById(R.id.post_content);
        send_post = (TextView) findViewById(R.id.send_post);
        send_post.setOnClickListener(this);
        iv_post_photo = (TextView) findViewById(R.id.iv_post_photo);
        iv_post_photo.setOnClickListener(this);
        iv_post_close = (ImageView) findViewById(R.id.iv_post_close);
        iv_post_close.setOnClickListener(this);
        ll_post_photo = (LinearLayout) findViewById(R.id.ll_post_photo);
        iv_post_video = (TextView) findViewById(R.id.iv_post_video);
        iv_post_video.setOnClickListener(this);
        tv_post_clear = (TextView) findViewById(R.id.tv_post_clear);
        tv_post_clear.setOnClickListener(this);
        iv_post_music.setOnClickListener(this);
    }

    private void initType() {
        iv_post_music = (TextView) findViewById(R.id.iv_post_music);
        ll_photo = (LinearLayout) findViewById(R.id.ll_photo);
        ll_video = (LinearLayout) findViewById(R.id.ll_video);
        ll_music = (LinearLayout) findViewById(R.id.ll_music);
        ll_post_music = (LinearLayout) findViewById(R.id.ll_post_music);
        ll_post_video = (LinearLayout) findViewById(R.id.ll_post_video);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 1);
        switch (type){
            case 1:
                ll_photo.setVisibility(View.VISIBLE);
                break;
            case 2:
                musicPath = intent.getStringExtra("path");
                ll_music.setVisibility(View.VISIBLE);
                if(!musicPath.equals("")){
                    iv_post_music.setText("重新选择音乐");
                }
                break;
            case 3:
                ll_video.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initImagePick() {
        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setMultiMode(true);
        imagePicker.setSelectLimit(3);    //选中数量限制
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setOutPutX(800);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(800);//保存文件的高度。单位像素
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_post_close:
                finish();
                break;
            case R.id.iv_post_photo:
                imagePicker.setFocusWidth(800);
                imagePicker.setFocusHeight(600);
                imagePicker.setSelectLimit(3);
                Intent intent1 = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent1, 5);
                break;
            case R.id.iv_post_music:
                Intent intent2 = new Intent();
                intent2.setType("audio/*");
                intent2.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent2, 2);
                break;
            case R.id.iv_post_video:
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    startActivityForResult(intent,0);
                } else {
                    startActivityForResult(intent,1);
                }
                break;
            case R.id.tv_post_clear:
                iv_post_photo.setVisibility(View.VISIBLE);
                iv_post_video.setVisibility(View.VISIBLE);
                ll_post_photo.removeAllViews();
                ll_post_video.removeAllViews();
                JZVideoPlayerStandard.releaseAllVideos();
                photo.clear();
                videoPath ="";
                tv_post_clear.setVisibility(View.GONE);
                break;
            case R.id.send_post:
                String title = post_title.getText().toString().trim();
                String content = post_content.getText().toString().trim();
                if(title.equals("")||content.equals("")){
                    Toast.makeText(this, "标题或正文不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = NET.sendPost+"&id="+ TagUtils.id+"&type="+type+"&title="+title+"&content="+content;
                showPostPw();
                if(type==2){
                    request.postData(url,musicPath);
                }else if(type==3){
                    request.postData(url,videoPath);
                }else {
                    if(photo.size()==1){
                        request.postData(url,photo.get(0));
                    }else if(photo.size()==2){
                        request.postData(url,photo.get(0),photo.get(1));
                    }else if(photo.size()==3){
                        request.postData(url,photo.get(0),photo.get(1),photo.get(2));
                    }else{
                        request.postData(url);
                    }
                }
                break;
        }
    }

    private PopupWindow pw;
    private void showPostPw() {
        if(pw==null){
            View pwView=View.inflate(ContextUtils.getContext(), R.layout.center_post_pw,null);
            pw=new PopupWindow(pwView, DisplayUtil.getSreenWidth(ContextUtils.getContext()),DisplayUtil.getSreenHeight(ContextUtils.getContext()),true);
        }
        pw.setFocusable(false);
        if(pw.isShowing()){
            pw.dismiss();
            return;
        }
        pw.showAsDropDown(ll_main, 0, -DisplayUtil.getSreenHeight(ContextUtils.getContext()),0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if(data != null && requestCode == 5){
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                ll_post_photo.removeAllViews();
                photo.clear();
                if(images.size()<3){
                    ll_photo.setOrientation(LinearLayout.HORIZONTAL);
                }else{
                    ll_photo.setOrientation(LinearLayout.VERTICAL);
                }
                for(int i=0;i<images.size();i++){
                    tv_post_clear.setVisibility(View.VISIBLE);
                    String path = images.get(i).path;
                    Log.e("path",path);
                    photo.add(path);
                    ImageView imageView=new ImageView(this);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    if(images.size()<3){
                        imageView.setLayoutParams(new LinearLayout.LayoutParams((DisplayUtil.getSreenWidth(this)-Utils.dip2px(this,120))/2,Utils.dip2px(this,90)));
                    }else{
                        imageView.setLayoutParams(new LinearLayout.LayoutParams((DisplayUtil.getSreenWidth(this))/3,Utils.dip2px(this,90)));
                    }
                    imageView.setPadding(2,0,2,0);
                    imageView.setImageBitmap(BitmapFactory.decodeFile(path));
                    ll_post_photo.addView(imageView);
                }
            }else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }else if(resultCode == Activity.RESULT_OK){
            if(requestCode==0){
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri,null,null,null,null);
                String path;
                String name;
                while (cursor.moveToNext()){
                    iv_post_video.setText("重新选择");
                    path = cursor.getString(1);
                    name = cursor.getString(2);
                    videoPath=path;
                    JZVideoPlayerStandard jz = new JZVideoPlayerStandard(this);
                    jz.setUp(path,JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, name);
                    jz.setLayoutParams(new LinearLayout.LayoutParams(ll_post_video.getWidth(),Utils.dip2px(this,230)));
                    jz.setPadding(Utils.dip2px(this,20),Utils.dip2px(this,20),Utils.dip2px(this,20),Utils.dip2px(this,20));
                    ll_post_video.addView(jz);
                }
            }else if(requestCode==2){
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri,null,null,null,null);
                String path;
                String name;
                while (cursor.moveToNext()){
                    iv_post_music.setText("重新选择音乐");
                    path = cursor.getString(1);
                    name = cursor.getString(2);
                    musicPath = path;
                }
            }
        }
    }
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
