package com.lws.sy.mv.activity;

import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lws.sy.mv.NetInfo.netLunBoInfo;
import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.Utils.Utils;
import com.lws.sy.mv.db.dao.MusicInfoDao;
import com.lws.sy.mv.musicUtils.Music;
import com.lws.sy.mv.musicUtils.musicInfo;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.MyNetListRequest;
import com.lws.sy.mv.request.MyNetLunBoRequest;
import com.lws.sy.mv.request.UpLoadRequest;
import com.lws.sy.mv.services.ServicesUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.lws.sy.mv.Utils.ContextUtils.getContext;

public class MyNetListActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, AdapterView.OnItemClickListener {
    private int id;
    private String name;
    private TextView tv_musicTitle;
    private List<musicInfo> data;
    private ListView lv_net_music;
    private Boolean isAllCheck = false;
    private Boolean isShow=false;
    private HashMap<Integer,Boolean> map;
    private RelativeLayout ll_click;
    private RadioButton rg_next;
    private RadioButton rg_add;
    private RadioButton rg_delete;
    private RadioButton rg_upload;
    private MyListAdapter adapter;
    private RadioButton bottomCancel;
    private CheckBox bottomAll;
    private PopupWindow pw;
    private ImageView goBack;
    private UpLoadRequest request;
    private LinearLayout ll_layout1;
    private ViewPager viewpager;
    private MusicInfoDao dao;
    private ArrayList<ImageView> mImageViews;
    private List<netLunBoInfo> lunboInfos;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                isShow=true;
                ll_click.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }else if(msg.what==2){
                isShow=false;
                ll_click.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }else if(msg.what==3){
                isAllCheck=true;
                adapter.notifyDataSetChanged();
            }else if(msg.what==4){
                isAllCheck=false;
                isShow=true;
                ll_click.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }else if(msg.what==0){
                int i=viewpager.getCurrentItem()+1;
                viewpager.setCurrentItem(i);
                mHandler.sendEmptyMessageDelayed(0,4000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
        ll_layout1= (LinearLayout) findViewById(R.id.ll_layout1);
        tv_musicTitle= (TextView) findViewById(R.id.tv_musicTitle);
        lv_net_music= (ListView) findViewById(R.id.lv_my_list);
        ll_click= (RelativeLayout) findViewById(R.id.ll_click);
        bottomAll= (CheckBox) findViewById(R.id.bottomAll);
        bottomCancel= (RadioButton) findViewById(R.id.bottomCancel);
        bottomCancel.setOnClickListener(this);
        dao = new MusicInfoDao(this);
        goBack= (ImageView) findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        data=new ArrayList<>();
        lunboInfos=new ArrayList<>();
        map=new HashMap<>();
        adapter=new MyListAdapter();
        Intent intent=getIntent();
        id = intent.getIntExtra("id",1);
        name = intent.getStringExtra("name");
        tv_musicTitle.setText(name);
        getLunBoData();
        getMusicData();
        request=new UpLoadRequest();
        lv_net_music.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                bottomAll.setChecked(false);
                mHandler.sendEmptyMessage(4);
                showBottomPw();
                return true;
            }
        });
        bottomAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mHandler.sendEmptyMessage(3);
                }else{
                    mHandler.sendEmptyMessage(4);
                }
            }
        });
        lv_net_music.setOnItemClickListener(this);
    }

    private void initViewPager() {
        viewpager= (ViewPager) findViewById(R.id.viewpager);
        //rg_radio= (RadioGroup) findViewById(R.id.rg_radio);
        mImageViews=new ArrayList<>();
        for (int i = 0; i < lunboInfos.size(); i++) {
            ImageView v = new ImageView(this);
            v.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(this).load(NET.netPath+lunboInfos.get(i).getImgPath()).into(v);
            mImageViews.add(v);
        }
        ImageView imgFirst = new ImageView(this);
        Glide.with(this).load(NET.netPath+lunboInfos.get(0).getImgPath()).into(imgFirst);
        ImageView imgLast = new ImageView(this);
        Glide.with(this).load(NET.netPath+lunboInfos.get(lunboInfos.size()-1).getImgPath()).into(imgFirst);

        mImageViews.add(0, imgLast);
        mImageViews.add(mImageViews.size(), imgFirst);

        for(int i=0;i<mImageViews.size();i++){
            /**
             *
             */
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_selector);
            //            point.setImageResource(R.drawable.point_selector);
            //在代码中设置的都是像数-问题，在所有的手机上都是8个像数
            //把8px当成是dp-->px
            int width = Utils.dip2px(this,8);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,width);
            if(i==0){
                point.setVisibility(View.GONE);
            }
            if(i==mImageViews.size()-1){
                point.setVisibility(View.GONE);
            }
            if(i==1){
                point.setEnabled(true); //显示红色
            }else{
                point.setEnabled(false);//显示灰色
                params.leftMargin = width;
            }
            point.setLayoutParams(params);

            ll_layout1.addView(point);
        }

        viewpager.setAdapter(new MyPagerAdapter());
        viewpager.setCurrentItem(1);
        mHandler.sendEmptyMessageDelayed(0,4000);
        viewpager.addOnPageChangeListener(this);
    }

    private void showBottomPw() {
        Log.e("TAG","长按");
        if(pw==null){
            View pwView=View.inflate(getContext(),R.layout.bottom_list,null);
            pw=new PopupWindow(pwView,lv_net_music.getWidth(), Utils.dip2px(getContext(),60),true);
            rg_add= (RadioButton) pwView.findViewById(R.id.rg_add);
            rg_delete= (RadioButton) pwView.findViewById(R.id.rg_delete);
            rg_next= (RadioButton) pwView.findViewById(R.id.rg_share);
            rg_upload= (RadioButton) pwView.findViewById(R.id.rg_upload);
            setDrawTop();
        }
        pw.setFocusable(false);
        if(pw.isShowing()){
            pw.dismiss();
            return;
        }
        pw.setAnimationStyle(R.style.popwin_anim_style);
        pw.showAsDropDown(lv_net_music, 0,-Utils.dip2px(getContext(),60));
        rg_add.setOnClickListener(this);
        rg_upload.setOnClickListener(this);
        rg_next.setOnClickListener(this);
        rg_delete.setOnClickListener(this);
    }

    private void setDrawTop() {
        Drawable topDrawable = getResources().getDrawable(R.mipmap.download);
        topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
        rg_upload.setCompoundDrawables(null, topDrawable, null, null);
        rg_upload.setText("下载");
    }

    private void getLunBoData() {
        MyNetLunBoRequest request=new MyNetLunBoRequest();
        request.getData(NET.getNetLunBo+"&id="+id);
        request.setInter(new BaseRequest.BaseInter<List<netLunBoInfo>>() {
            @Override
            public void handleData(List<netLunBoInfo> data, Boolean isSuccess) {
                if(!isSuccess){
                    return;
                }
                if(data.size()==0){
                    return;
                }
                lunboInfos = data;
                initViewPager();
            }
        });
    }

    private void getMusicData() {
        MyNetListRequest request=new MyNetListRequest();
        request.getData(NET.getNetList+"&id="+id);
        request.setInter(new BaseRequest.BaseInter<List<musicInfo>>() {

            @Override
            public void handleData(List<musicInfo> infos,Boolean isSuccess) {
                if(!isSuccess){
                    return;
                }
                if(infos.size()==0){
                    return;
                }
                data=infos;
                for(int i = 0;i<data.size();i++){
                    data.get(i).setPath(NET.netPath+data.get(i).getPath());
                }
                lv_net_music.setAdapter(adapter);
            }
        });
    }
    private void addMyList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
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
                        Log.e("TAG","ppppppppppppppppppppppppppppppppppp");
                        while(cursor.moveToNext()){
                            for(int k=0;k<map.size();k++){
                                Set<Integer> set = map.keySet();
                                Iterator<Integer> iterator = set.iterator();
                                while(iterator.hasNext()){
                                    Log.e("duoshan",k+"");
                                    int position = iterator.next();
                                    boolean b = dao.queryList(data.get(position).getPath(), cursor.getString(0));
                                    if(b){
                                        continue;
                                    }
                                    dao.add(data.get(position).getPath(),data.get(position).getMusic_name(),data.get(position).getMusic_singer(),cursor.getString(0));
                                }
                            }
                            //ServicesUtils.addMyList(cursor.getString(0));
                            //Log.e("TAG",cursor.getString(0));
                        }
                    }
                }
                mHandler.sendEmptyMessage(2);
            }
        });
        builder.show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rg_add:
                addMyList();
                mHandler.sendEmptyMessage(2);
                break;
            case R.id.rg_delete:
                mHandler.sendEmptyMessage(2);
                break;
            case R.id.rg_share:
                Toast.makeText(this, "当前仅支持本地音乐分享", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessage(2);
                break;
            case R.id.rg_upload:
                if (map.size()>3){
                    Toast.makeText(this, "当前仅支持3首同时下载", Toast.LENGTH_SHORT).show();
                    mHandler.sendEmptyMessage(2);
                    break;
                }
                Set<Integer> set = map.keySet();
                Iterator<Integer> iterator = set.iterator();
                while(iterator.hasNext()){
                    int k=iterator.next();
                    Log.e("文件path",data.get(k).getPath());
                    if(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/music_list/"+data.get(k).getMusic_singer()+"-"+data.get(k).getMusic_name()+".mp3").exists()){
                        Toast.makeText(this, "文件已存在本机", Toast.LENGTH_SHORT).show();
                        continue;
                    }
                    //downloadNotify.clear();
                    request.download(data.get(k).getPath(),data.get(k).getMusic_name(),data.get(k).getMusic_singer(),k);
                    //downloadNotify.sendDownloadNotify(data.get(k).getMusic_name());
                }
                mHandler.sendEmptyMessage(2);
                break;
            case R.id.bottomCancel:
                mHandler.sendEmptyMessage(2);
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
        if(pw!=null&&pw.isShowing()){
            pw.dismiss();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset == 0) {//只有当图片完全滑入屏幕时才跳转 不然就露馅儿了
            if (position == mImageViews.size() - 1) {
                viewpager.setCurrentItem(1, false); //取消自带的动画效果
            }
            if (position == 0) {
                viewpager.setCurrentItem(mImageViews.size() - 2, false);
            }
        }
    }
    private int preposition=1;
    @Override
    public void onPageSelected(int position) {
        if(position==0)return;
        if(position==mImageViews.size()-1)return;
        int realposition=position;
        ll_layout1.getChildAt(preposition).setEnabled(false);
        //当前的设置为高亮-红色
        ll_layout1.getChildAt(realposition).setEnabled(true);
        preposition=realposition;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state==ViewPager.SCROLL_STATE_DRAGGING){
            mHandler.removeCallbacksAndMessages(null);
        }else if(state==ViewPager.SCROLL_STATE_SETTLING){
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendEmptyMessageDelayed(0,4000);
        }else{}
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String path =data.get(position).getPath();
        String music_name=data.get(position).getMusic_name();
        String music_singer=data.get(position).getMusic_singer();
        Music.addLastMusic(path,music_name,music_singer);
        Log.e("TAG",data.get(position).getPath());
        ServicesUtils.playMusic(data,position);
    }

    private class MyListAdapter extends BaseAdapter {
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView ==null) {
                convertView = View.inflate(getContext(),R.layout.music_list_item,null);
                holder=new ViewHolder();
                holder.tv_music_name= (TextView) convertView.findViewById(R.id.tv_music_name);
                holder.tv_music_singer = (TextView) convertView.findViewById(R.id.tv_music_singer);
                holder.iv_music_add= (ImageView) convertView.findViewById(R.id.iv_add_music);
                holder.iv_music_select= (CheckBox) convertView.findViewById(R.id.iv_music_more);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_music_name.setText(data.get(position).getMusic_name());
            if(isAllCheck){
                holder.iv_music_select.setChecked(true);
                map.put(position,true);
            }else {
                holder.iv_music_select.setChecked(false);
                map.remove(position);
            }
            holder.iv_music_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        map.put(position,true);
                    }else{
                        map.remove(position);
                    }
                }
            });
            if(isShow){
                holder.iv_music_select.setVisibility(View.VISIBLE);
            }else{
                holder.iv_music_select.setVisibility(View.GONE);
            }
            holder.tv_music_singer.setText(data.get(position).getMusic_singer());

            return convertView;
        }
        class ViewHolder {
            TextView tv_music_name;
            TextView tv_music_singer;
            ImageView iv_music_add;
            CheckBox iv_music_select;
        }
    }
    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            //return mImageViews.size();
            return mImageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageViews.get(position));
            //container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = mImageViews.get(position%mImageViews.size());
            if(imageView.getParent()!=null){
                ((ViewPager)imageView.getParent()).removeView(imageView);
            }
            container.addView(mImageViews.get(position));
            return mImageViews.get(position);
        }
    }
}
