package com.lws.sy.mv.activity;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.db.dao.VideoInfoDao;
import com.lws.sy.mv.videoUtils.Video;
import com.lws.sy.mv.videoUtils.videoInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.lws.sy.mv.R.id.iv_view;
import static com.lws.sy.mv.R.id.tv_name;


public class VideoActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView lv_video;
    private List<videoInfo> data;
    private VideoAdapter adapter;
    private VideoInfoDao dao;
    private SwipeRefreshLayout sf_fresh;
    private SharedPreferences sp;
    private ImageView goBack;
    private ProgressBar pb_loading;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0){
                initData();
                initList();
                lv_video.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        dao=new VideoInfoDao(ContextUtils.getContext());
        performCodeWithPermission("获取音频权限", new PermissionCallback() {
            @Override
            public void hasPermission() {
                initView();
            }

            @Override
            public void noPermission() {

            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    public void initView() {
        lv_video= (ListView)findViewById(R.id.lv_video);
        sf_fresh= (SwipeRefreshLayout)findViewById(R.id.sf_fresh);
        goBack= (ImageView) findViewById(R.id.goBack);
        pb_loading= (ProgressBar) findViewById(R.id.pb_loading);
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
        sp= getSharedPreferences("share", MODE_PRIVATE);
        boolean isFirst = sp.getBoolean("isVideoFirst", true);
        if (isFirst) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isVideoFirst",false);
            editor.commit();
            lv_video.setVisibility(View.GONE);
            pb_loading.setVisibility(View.VISIBLE);
            new Thread() {
                public void run() {
                    Video.getVideo();
                    mHandler.sendEmptyMessage(0);
                }
            }.start();
        } else {
            initData();
            initList();
        }

    }

    private void initList() {
        adapter=new VideoAdapter();
        lv_video.setAdapter(adapter);
        sf_fresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Video.getVideo();
                initData();
                sf_fresh.setRefreshing(false);
            }
        });
        lv_video.setOnItemClickListener(this);
    }

    public void initData() {
        data=new ArrayList<>();
        videoInfo info;
        Cursor cursor = dao.query("video");
        while (cursor.moveToNext()) {
            info=new videoInfo();
            String path = cursor.getString(0);
            String video_name = cursor.getString(1);
            String id = cursor.getString(2);
            info.setVideo_name(video_name);
            info.setVideo_id(id);
            info.setPath(path);
            data.add(info);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!new File(data.get(position).getPath()).exists()){
            Toast.makeText(this, "该视频不存在", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent=new Intent(this, VideoPlayActivity.class);
            intent.putExtra("path",data.get(position).getPath());
            startActivity(intent);
        }
    }

    private class VideoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder =null;
            if (convertView == null) {
                convertView = View.inflate(ContextUtils.getContext(), R.layout.video_item, null);
                holder=new ViewHolder();
                holder.tv_name = (TextView) convertView.findViewById(tv_name);
                holder.iv_view = (ImageView) convertView.findViewById(iv_view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_name.setText(data.get(position).getVideo_name());
            if(!new File(data.get(position).getPath()).exists()){
                holder.iv_view.setImageBitmap(null);
            } else {
                holder.iv_view.setImageBitmap(getVideoThumbnail(data.get(position).getPath()));
            }
            return convertView;
        }
        class ViewHolder {
            TextView tv_name;
            ImageView iv_view;
        }
    }
    public static Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever media =new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        return bitmap;
    }
}
