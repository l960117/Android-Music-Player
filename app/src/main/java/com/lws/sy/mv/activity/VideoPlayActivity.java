package com.lws.sy.mv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.TabUtils;
import com.lws.sy.mv.Utils.VpFragmentUtils;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;


public class VideoPlayActivity extends AppCompatActivity {
    private JZVideoPlayerStandard jzVideoPlayerStandard;
    private String path;
    private FrameLayout fl_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        fl_main= (FrameLayout) findViewById(R.id.fl_main);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Intent intent=getIntent();
        path=intent.getStringExtra("path");
        jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.vp_player);
        jzVideoPlayerStandard.setUp(path, JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, path.substring(path.lastIndexOf("/")+1));
        //jzVideoPlayerStandard.thumbImageView.setImageURI(Uri.parse(path));
        //initFram();
    }

    private void initFram() {
        View view=View.inflate(VideoPlayActivity.this,R.layout.activity_video_vp,null);
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
        fl_main.addView(view);
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
    class VpAdapter extends FragmentPagerAdapter {


        public VpAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return VpFragmentUtils.getFragment(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return VpFragmentUtils.getFragment(position).getTitle();
        }
    }
}
