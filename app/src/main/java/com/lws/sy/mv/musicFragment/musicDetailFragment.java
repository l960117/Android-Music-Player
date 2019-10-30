package com.lws.sy.mv.musicFragment;

import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.Utils.Utils;
import com.lws.sy.mv.db.dao.MusicInfoDao;
import com.lws.sy.mv.fragment.BaseFragment;
import com.lws.sy.mv.musicUtils.LyricView;
import com.lws.sy.mv.musicUtils.TagUtils;
import com.lws.sy.mv.musicUtils.musicInfo;
import com.lws.sy.mv.services.ServicesUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class musicDetailFragment extends BaseFragment implements View.OnClickListener{
    private List mTimeList;
    private ImageView iv_pattern;
    private ImageView iv_music_list;
    private ListView lv_list;
    private ListView lv_list1;
    private PopupWindow pw;
    private int musicPattern;
    private MusicInfoDao dao;
    private SeekBar sb_bar;
    private List<musicInfo> data;
    private SharedPreferences mShared;
    private LinearLayout ll_total;
    private LinearLayout ll_list_position;
    private ImageView iv_music;
    private ImageView music_last;
    private ImageView music_play;
    private ImageView music_next;
    private TextView music_play_name;
    private TextView music_play_singer;
    private RadioButton setlove;
    private RadioButton add_download;
    private ImageView goBack;
    private LyricView wd_lrc;
    private static String music_name1=null;
    private static String music_singer1=null;
    private final String[] titleData={"顺序播放","随机播放","单曲循环"};
    private final int[] imgData={R.mipmap.order,
            R.mipmap.random,
            R.mipmap.only};
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1) {
                //Log.e("播放下一首","111");
                // 播放下一首
                ServicesUtils.playNext(musicPattern,1,false);
                music_play_name.setText(ServicesUtils.getMusicName());
                music_play_singer.setText(ServicesUtils.getMusicSinger());
                Log.e("音乐名",ServicesUtils.getMusicName());
                Log.e("音乐min",ServicesUtils.getMusicSinger());
                checkLove(ServicesUtils.getMusicName());
                SerchLrc();
            }
        }
    };
    public musicDetailFragment(int type) {
        super();
    }
    @Override
    public void initView() {
        //Toast.makeText(ContextUtils.getContext(), isVisible + "", Toast.LENGTH_SHORT).show();
        view= View.inflate(ContextUtils.getContext(), R.layout.music_detail,null);
        iv_pattern = (ImageView) view.findViewById(R.id.iv_pattern);
        ll_total = (LinearLayout) view.findViewById(R.id.ll_total);
        iv_music_list = (ImageView) view.findViewById(R.id.iv_music_list);
        ll_list_position = (LinearLayout) view.findViewById(R.id.ll_list_position);
        iv_music= (ImageView) view.findViewById(R.id.iv_music);
        music_play_name= (TextView) view.findViewById(R.id.music_play_name);
        music_play_singer= (TextView) view.findViewById(R.id.music_play_singer);
        setlove = (RadioButton) view.findViewById(R.id.setlove);
        music_play_singer.setText(music_singer1);
        music_play_name.setText(music_name1);
        add_download= (RadioButton) view.findViewById(R.id.add_download);
        wd_lrc= (LyricView) view.findViewById(R.id.lv_lrc);
        sb_bar= (SeekBar) view.findViewById(R.id.sb_bar);
        music_play = (ImageView) view.findViewById(R.id.music_play);
        music_last = (ImageView) view.findViewById(R.id.music_last);
        music_next = (ImageView) view.findViewById(R.id.music_next);
        goBack= (ImageView) view.findViewById(R.id.goBack);
        initLrc();
        if(TagUtils.tag==1){
            setView();
        }
        goBack.setOnClickListener(this);
        setlove.setOnClickListener(this);
        add_download.setOnClickListener(this);
        music_next.setOnClickListener(this);
        music_last.setOnClickListener(this);
        music_play.setOnClickListener(this);
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sb_bar.setMax(ServicesUtils.getMax());
                    try {
                        sleep(1000);
                        sb_bar.setProgress(ServicesUtils.getProgress());
                        if (ServicesUtils.getProgress() == ServicesUtils.getMax()) {
                            Log.e("最大", "" + ServicesUtils.getMax());
                            Log.e("当前", "" + ServicesUtils.getProgress());
                            //Toast.makeText(ContextUtils.getContext(), "放完了", Toast.LENGTH_SHORT).show();
                            mHandler.sendEmptyMessage(1);
                        }
                        Log.e("TAG", ServicesUtils.getProgress() + "");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        setProgress();
        getData();
        ll_total.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(pw!=null&&pw.isShowing()){
                    pw.dismiss();
                }
                return false;
            }
        });
        iv_pattern.setOnClickListener(this);
        iv_music_list.setOnClickListener(this);
    }

    @Override
    protected void closeOther() {

    }

    private void initLrc() {
        SerchLrc();
    }

    public void SerchLrc() {
        if(ServicesUtils.lastposition==-1)return;
        String lrc = ServicesUtils.getPath();
        lrc = lrc.substring(0, lrc.length() - 4).trim() + ".lrc".trim();
        LyricView.read(lrc);
        //wd_lrc.SetTextSize();
        wd_lrc.setOffsetY(350);
        //wd_lrc.setSIZEWORD(30);
        wd_lrc.setOffsetY(220 - wd_lrc.SelectIndex(ServicesUtils.getProgress()) * (wd_lrc.getSIZEWORD() + 44));
        new Thread(new runable()).start();
    }
    class runable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                    if(ServicesUtils.lastposition!=-1){
                        wd_lrc.setOffsetY(wd_lrc.getOffsetY() - wd_lrc.SpeedLrc());
                        wd_lrc.SelectIndex(ServicesUtils.getProgress());
                        handler.post(mUpdateResults);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    Handler handler = new Handler();
    Runnable mUpdateResults = new Runnable() {
        public void run() {
            wd_lrc.invalidate(); // 更新视图
        }
    };

    @Override
    protected void lazyLoad() {
        if(TagUtils.tag==1){
            return;
        }
        setView();
    }
    private void setView(){
        setDetail();
        //Toast.makeText(ContextUtils.getContext(), "888", Toast.LENGTH_SHORT).show();
        if(ServicesUtils.lastposition!=-1){
            if (ServicesUtils.checkIsPlaying() == 1) {
                //Toast.makeText(ContextUtils.getContext(), "111", Toast.LENGTH_SHORT).show();
                music_play.setImageResource(R.mipmap.pause);
            } else {
                //Toast.makeText(ContextUtils.getContext(), "222", Toast.LENGTH_SHORT).show();
                music_play.setImageResource(R.mipmap.play);
            }
            initLrc();
        }
    }

    private void setProgress() {
        sb_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    if(progress==ServicesUtils.getMax()){
                        mHandler.sendEmptyMessage(1);
                    }
                    ServicesUtils.setProgress(progress);
                    wd_lrc.setOffsetY(220 - wd_lrc.SelectIndex(progress) * (wd_lrc.getSIZEWORD() + 44));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.iv_pattern:
                initPw();
//                if(musicPattern==imgData.length-1){
//                    iv_pattern.setImageResource(imgData[0]);
//                    musicPattern = 0;
//                }else{
//                    iv_pattern.setImageResource(imgData[musicPattern+1]);
//                    musicPattern = musicPattern+1;
//                }
//                mShared= ContextUtils.getContext().getSharedPreferences("share", MODE_PRIVATE);
//                SharedPreferences.Editor editor = mShared.edit();
//                editor.putInt("musicType", musicPattern);
//                editor.commit();
                break;
            case R.id.iv_music_list:
                addMyList();
                break;
            case R.id.music_last:
                if(ServicesUtils.lastposition==-1){
                    ServicesUtils.playMusic(data,0);
                    music_play.setImageResource(R.mipmap.pause);
                    music_play_name.setText(ServicesUtils.getMusicName());
                    music_play_singer.setText(ServicesUtils.getMusicSinger());
                }else {
                    ServicesUtils.playNext(musicPattern,0,true);
                    music_play_name.setText(ServicesUtils.getMusicName());
                    music_play_singer.setText(ServicesUtils.getMusicSinger());
                }
                if(ServicesUtils.checkIsPlaying()==1){
                    music_play.setImageResource(R.mipmap.pause);
                }else{
                    music_play.setImageResource(R.mipmap.play);
                }
                checkLove(ServicesUtils.getMusicName());
                SerchLrc();
                break;
            case R.id.music_play:
                if(data.size()==0){
                    music_play.setImageResource(R.mipmap.pause);
                    return;
                }
                if(ServicesUtils.lastposition==-1){
                    ServicesUtils.playMusic(data,0);
                    music_play.setImageResource(R.mipmap.pause);
                    music_play_name.setText(ServicesUtils.getMusicName());
                    music_play_singer.setText(ServicesUtils.getMusicSinger());
                }else{
                    if (ServicesUtils.checkIsPlaying() == 0) {
                        music_play.setImageResource(R.mipmap.pause);
                        wd_lrc.setOffsetY(220 - wd_lrc.SelectIndex(ServicesUtils.getProgress())
                                * (wd_lrc.getSIZEWORD() + 44));
                    } else {
                        music_play.setImageResource(R.mipmap.play);
                    }
                    ServicesUtils.play();
                }
                break;
            case R.id.music_next:
                if(ServicesUtils.lastposition==-1){
                    ServicesUtils.playMusic(data,0);
                    music_play.setImageResource(R.mipmap.pause);
                    music_play_name.setText(ServicesUtils.getMusicName());
                    music_play_singer.setText(ServicesUtils.getMusicSinger());
                }else {
                    ServicesUtils.playNext(musicPattern,1,true);
                    music_play_name.setText(ServicesUtils.getMusicName());
                    music_play_singer.setText(ServicesUtils.getMusicSinger());
                }
                music_play.setImageResource(R.mipmap.pause);
                if(ServicesUtils.checkIsPlaying()==1){
                    music_play.setImageResource(R.mipmap.pause);
                }else{
                    music_play.setImageResource(R.mipmap.play);
                }
                checkLove(ServicesUtils.getMusicName());
                SerchLrc();
                break;
            case R.id.setlove:
                if (isChecked==0) {
                    setlove.setButtonDrawable(R.mipmap.setlove1);
                    setlove.setChecked(true);
                    isChecked=1;
                    ServicesUtils.addLove();
                } else {
                    setlove.setButtonDrawable(R.mipmap.setlove);
                    setlove.setChecked(false);
                    isChecked=0;
                    ServicesUtils.deleteLove();
                }
                break;
            case R.id.add_download:
                if(!new File(ServicesUtils.getPath()).exists()){
                    Toast.makeText(ContextUtils.getContext(), "已添加至下载列表", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ContextUtils.getContext(),"该歌曲已下载" ,Toast.LENGTH_SHORT).show();
                }
                add_download.setChecked(false);
                break;
            case R.id.goBack:
                new Thread() {
                    public void run() {
                        try {
                            Instrumentation inst = new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                        } catch (Exception e) {
                        }
                    }
                }.start();
                break;
        }
    }

    private void addMyList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
        builder.setTitle("添加到我的歌单");
        final String[] items={"看繁华落尽","看春来春去"};
        final boolean[] checkeds= {false, false};
        builder.setMultiChoiceItems(items, checkeds, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                //Toast.makeText(ContextUtils.getContext(), items[which] + isChecked, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int i=0;i<checkeds.length;i++){
                    if(checkeds[i]){
                        //添加item[i]
                        Cursor cursor = dao.queryMyList(items[i]);
                        //Log.e("TAG","ppppppppppppppppppppppppppppppppppp");
                        while(cursor.moveToNext()){
                            ServicesUtils.addMyList(cursor.getString(0));
                            Log.e("TAG",cursor.getString(0));
                        }
                    }
                }
            }
        });
        builder.show();
    }

    private int isChecked = 0;

    private void initPw() {
        if(pw==null){
            View pwView=View.inflate(ContextUtils.getContext(),R.layout.menu_pw,null);
            pw=new PopupWindow(pwView, Utils.dip2px(ContextUtils.getContext(),120),Utils.dip2px(ContextUtils.getContext(),126),true);
            lv_list=(ListView) pwView.findViewById(R.id.lv_list);
        }
        lv_list.setAdapter(new MyAdapter());
        pw.setFocusable(false);
        if(pw.isShowing()){
            pw.dismiss();
            return;
        }
        pw.showAsDropDown(iv_pattern, 0, -Utils.dip2px(ContextUtils.getContext(),10));
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                iv_pattern.setImageResource(imgData[position]);
                musicPattern = position;
                mShared= ContextUtils.getContext().getSharedPreferences("share", MODE_PRIVATE);
                SharedPreferences.Editor editor = mShared.edit();
                editor.putInt("musicType", position);
                editor.commit();
                pw.dismiss();
            }
        });
    }

    public void getData() {
        mShared= ContextUtils.getContext().getSharedPreferences("share", MODE_PRIVATE);
        musicPattern = mShared.getInt("musicType", 0);
        setPattern();
        data = new ArrayList<>();
        dao = new MusicInfoDao(ContextUtils.getContext());
        switch (mShared.getInt("musicList",1)){
            case 1:
                data=getMusic("music");
                //本地音乐
                break;
            case 2:
                data=getMusic("loveMusic");
                //喜欢列表
                break;
            case 3:
                data=getMusic("downMusic");
                //下载列表
                break;
            case 4:
                data=getMusic("lastMusic");
                //最近列表
                break;
            default:
                break;
        }

    }
    private List<musicInfo> getMusic(String tableName){
        List<musicInfo> list =new ArrayList<>();
        Cursor cursor = dao.query(tableName);
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
        return list;
    }
    private void setPattern() {
        iv_pattern.setImageResource(imgData[musicPattern]);
    }
    private void checkLove(String music_name){
        dao=new MusicInfoDao(ContextUtils.getContext());
        if(music_name==null){
            return;
        }
        if(dao.queryLove(music_name)){
            setlove.setButtonDrawable(R.mipmap.setlove1);
        }else{
            setlove.setButtonDrawable(R.mipmap.setlove);
        }
    }
    public void setDetail() {
        //Toast.makeText(ContextUtils.getContext(), "111111", Toast.LENGTH_SHORT).show();
        dao=new MusicInfoDao(ContextUtils.getContext());
        if(ServicesUtils.lastposition==-1) {
            Cursor cursor = dao.query("lastMusic");
            if(cursor.moveToLast()) {
//                Log.e("musicDetail",cursor.getString(1));
//                Log.e("musicDetail",cursor.getString(2));
                music_name1=cursor.getString(1);
                music_singer1=cursor.getString(2);
                music_play_name.setText(music_name1);
                if(music_singer1.equals("<unknown>")){
                    music_play_singer.setText("未知");
                }else{
                    music_play_singer.setText(music_singer1);
                }
            }
            cursor.close();
        } else {
            music_name1=ServicesUtils.getMusicName();
            music_singer1=ServicesUtils.getMusicSinger();
            music_play_name.setText(music_name1);
            music_play_singer.setText(music_singer1);
//            Log.e("musicDetail",music_name1);
//            Log.e("musicDetail",music_singer1);
        }
        checkLove(music_name1);
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if(convertView==null){
                holder=new ViewHolder();
                convertView=View.inflate(ContextUtils.getContext(),R.layout.menu_pw_item,null);
                holder.tv_title= (TextView) convertView.findViewById(R.id.tv_title);
                holder.img_logo=(ImageView) convertView.findViewById(R.id.img_logo);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.tv_title.setText(titleData[position]);
            holder.img_logo.setImageResource(imgData[position]);
            return convertView;
        }
        class ViewHolder{
            TextView tv_title;
            ImageView img_logo;
        }
    }

    private BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent){
            //Toast.makeText(context, "更新", Toast.LENGTH_SHORT).show();
            music_play_name.setText(ServicesUtils.getMusicName());
            music_play_singer.setText(ServicesUtils.getMusicSinger());
            if(ServicesUtils.checkIsPlaying()==1){
                music_play.setImageResource(R.mipmap.pause);
            }else{
                music_play.setImageResource(R.mipmap.play);
            }
            SerchLrc();
            checkLove(ServicesUtils.getMusicName());
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private LocalBroadcastManager broadcastManager;
    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(ContextUtils.getContext(), "注册广播", Toast.LENGTH_SHORT).show();
        /** 注册广播 */
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.update");//建议把它写一个公共的变量，这里方便阅读就不写了。
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(mItemViewListClickReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}