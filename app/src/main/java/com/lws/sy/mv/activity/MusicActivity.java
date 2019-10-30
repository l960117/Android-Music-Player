package com.lws.sy.mv.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.MusicFragmentUtis;
import com.lws.sy.mv.musicUtils.TagUtils;

public class MusicActivity extends BaseActivity {
    private ViewPager vp_music;
    private int type;
    public int tag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_music);
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

    private void initView() {
        vp_music=(ViewPager) findViewById(R.id.vp_music);
        vp_music.setAdapter(new MyMusicAdapter(getSupportFragmentManager()));
        Intent intent = getIntent();
        type= intent.getIntExtra("list", 1);
        tag=intent.getIntExtra("tag",0);
        TagUtils.tag=tag;
        if (tag==1) {
            vp_music.setCurrentItem(1);
        }
    }

    private class MyMusicAdapter extends FragmentPagerAdapter{

        public MyMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MusicFragmentUtis.getFragment(position,type);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
