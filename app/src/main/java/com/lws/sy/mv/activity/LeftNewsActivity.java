package com.lws.sy.mv.activity;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.TabUtils;
import com.lws.sy.mv.newsFragment.NewsFragmentUtils;

public class LeftNewsActivity extends AppCompatActivity {
    private FrameLayout fl_user;
    private ImageView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_news);
        Intent intent = getIntent();
        int id = intent.getIntExtra("id",-1);
        fl_user = (FrameLayout) findViewById(R.id.fl_user);
        goBack = (ImageView) findViewById(R.id.goBack);
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
        initFram();
    }

    private void initFram() {
        View view=View.inflate(this,R.layout.activity_video_vp,null);
        final TabLayout tl_ty= (TabLayout) view.findViewById(R.id.tl_ty);
        ViewPager vp_main= (ViewPager) view.findViewById(R.id.vp_main);
        vp_main.setAdapter(new VpAdapter(getSupportFragmentManager()));
        tl_ty.post(new Runnable() {
            @Override
            public void run() {
                TabUtils.setIndicator(tl_ty,20,20);
            }
        });
        tl_ty.setupWithViewPager(vp_main);
        tl_ty.setTabMode(TabLayout.MODE_FIXED);
        fl_user.addView(view);
    }
    class VpAdapter extends FragmentPagerAdapter {


        public VpAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return NewsFragmentUtils.getFragment(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return NewsFragmentUtils.getFragment(position).getTitle();
        }
    }
}
