package com.lws.sy.mv.activity;

import android.app.Instrumentation;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.Utils.Utils;
import com.lws.sy.mv.db.dao.MusicInfoDao;
import com.lws.sy.mv.musicUtils.Music;
import com.lws.sy.mv.musicUtils.musicInfo;
import com.lws.sy.mv.services.ServicesUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.lws.sy.mv.Utils.ContextUtils.getContext;

public class MyListActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, ViewPager.OnPageChangeListener, AdapterView.OnItemClickListener {
    private List<musicInfo> data;
    private MusicInfoDao dao;
    private ListView lv_my_list;
    private PopupWindow pw;
    private LinearLayout ll_my_list;
    private RadioButton rg_next;
    private RadioButton rg_add;
    private RadioButton rg_delete;
    private RadioButton rg_upload;
    private RadioButton bottomCancel;
    private CheckBox bottomAll;
    private Boolean isShow=false;
    private MyListAdapter adapter;
    private RelativeLayout ll_click;
    private ImageView lunbo_last;
    private ImageView lunbo_next;
    private Boolean isAllCheck=false;
    private HashMap<Integer,Boolean> map;
    private ImageView goBack;
    private int type;
    private LinearLayout ll_layout1;
    private ViewPager viewpager;
    private ArrayList<ImageView> mImageViews;

    private final int[] imageIds = {
            R.mipmap.flower1,
            R.mipmap.flower2,
            R.mipmap.flower3};
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
        lv_my_list= (ListView) findViewById(R.id.lv_my_list);
        ll_my_list= (LinearLayout) findViewById(R.id.ll_my_list);
        ll_click= (RelativeLayout) findViewById(R.id.ll_click);
        initViewPager();
        bottomCancel= (RadioButton) findViewById(R.id.bottomCancel);
        goBack= (ImageView) findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        bottomAll= (CheckBox) findViewById(R.id.bottomAll);
        map=new HashMap<>();
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
        bottomCancel.setOnClickListener(this);
        data=new ArrayList<>();
        dao=new MusicInfoDao(getContext());
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        data=getData(type);
        adapter=new MyListAdapter();
        lv_my_list.setAdapter(adapter);
        lv_my_list.setOnItemLongClickListener(this);
        lv_my_list.setOnItemClickListener(this);
    }
    private int item;
    private int prePosition = 0;
    private void initViewPager() {
        viewpager= (ViewPager) findViewById(R.id.viewpager);
        //rg_radio= (RadioGroup) findViewById(R.id.rg_radio);
        mImageViews=new ArrayList<>();
        for(int i=0;i<imageIds.length;i++) {
            ImageView view = new ImageView(getContext());
            view.setBackgroundResource(imageIds[i]);
            mImageViews.add(view);
            /**
             *
             */
            ImageView point = new ImageView(getContext());
            point.setBackgroundResource(R.drawable.point_selector);
            //            point.setImageResource(R.drawable.point_selector);
            //在代码中设置的都是像数-问题，在所有的手机上都是8个像数
            //把8px当成是dp-->px
            int width = Utils.dip2px(getContext(),8);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,width);
            if(i==0){
                point.setEnabled(true); //显示红色
            }else{
                point.setEnabled(false);//显示灰色
                params.leftMargin = width;
            }
            point.setLayoutParams(params);

            ll_layout1.addView(point);
        }
        viewpager.setAdapter(new MyPagerAdapter());



        item = Integer.MAX_VALUE/2 - Integer.MAX_VALUE/2%mImageViews.size();

        viewpager.setCurrentItem(item);

        mHandler.sendEmptyMessageDelayed(0,4000);

        viewpager.addOnPageChangeListener(this);
    }

    public List<musicInfo> getData(int type) {
        List<musicInfo> list=new ArrayList<>();
        switch (type){
            case 0:
                list=getMusic("flowers");
                break;
            case 1:
                list=getMusic("spring");
                break;
        }
        return list;
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

    private void showBottomPw() {
        Log.e("TAG","长按");
        if(pw==null){
            View pwView=View.inflate(getContext(),R.layout.bottom_list,null);
            pw=new PopupWindow(pwView,ll_my_list.getWidth(),Utils.dip2px(getContext(),60),true);
            rg_add= (RadioButton) pwView.findViewById(R.id.rg_add);
            rg_delete= (RadioButton) pwView.findViewById(R.id.rg_delete);
            rg_next= (RadioButton) pwView.findViewById(R.id.rg_share);
            rg_upload= (RadioButton) pwView.findViewById(R.id.rg_upload);
//            bottomAll= (RadioButton) pwView.findViewById(R.id.bottomAll);
//            bottomCancel= (RadioButton) pwView.findViewById(R.id.bottomCancel);
        }
        pw.setFocusable(false);
        if(pw.isShowing()){
            pw.dismiss();
            return;
        }
        pw.setAnimationStyle(R.style.popwin_anim_style);
        pw.showAsDropDown(ll_my_list, 0,-Utils.dip2px(getContext(),60));
        rg_add.setOnClickListener(this);
        rg_upload.setOnClickListener(this);
        rg_next.setOnClickListener(this);
        rg_delete.setOnClickListener(this);
//        bottomAll.setOnClickListener(this);
//        bottomCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rg_add:
                Toast.makeText(this, "请到本地歌单列表添加", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessage(2);
                break;
            case R.id.rg_delete:
                Set<Integer> set = map.keySet();
                Iterator<Integer> iterator = set.iterator();
                while(iterator.hasNext()){
                    dao.deleteMyList(data.get(iterator.next()).getMusic_name(),type);
                }
                data=getData(type);
                mHandler.sendEmptyMessage(2);
                break;
            case R.id.rg_share:
                if(map.size()>1){
                    Toast.makeText(ContextUtils.getContext(), "当前仅支持单曲分享", Toast.LENGTH_SHORT).show();
                    mHandler.sendEmptyMessage(2);
                    if(pw!=null&&pw.isShowing()){
                        pw.dismiss();
                    }
                    return;
                }
                Set<Integer> set1 = map.keySet();
                Iterator<Integer> iterator1 = set1.iterator();
                while(iterator1.hasNext()){
                    int k = iterator1.next();
                    if(data.get(k).getPath().indexOf(NET.netPath)!=-1){
                        Toast.makeText(this, "当前仅支持本地音乐分享", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Intent music_intent = new Intent(ContextUtils.getContext(),CenterPostActivity.class);
                    music_intent.putExtra("type",2);
                    music_intent.putExtra("path",data.get(k).getPath());
                    Log.e("音乐路径",data.get(k).getPath());
                    startActivity(music_intent);
                }
                mHandler.sendEmptyMessage(2);
                break;
            case R.id.rg_upload:
                isShow=false;
                ll_click.setVisibility(View.GONE);
                Toast.makeText(this, "暂不支持本地上传", Toast.LENGTH_SHORT).show();
                //mHandler.sendEmptyMessage(2);
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
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        bottomAll.setChecked(false);
        mHandler.sendEmptyMessage(4);
        showBottomPw();
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    private int preposition=0;
    @Override
    public void onPageSelected(int position) {
        int realposition=position%mImageViews.size();
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
        if(!new File(data.get(position).getPath()).exists()&&data.get(position).getPath().indexOf(NET.netPath)==-1){
            Toast.makeText(ContextUtils.getContext(), "该歌曲不存在", Toast.LENGTH_SHORT).show();
        }else {
            String path =data.get(position).getPath();
            String music_name=data.get(position).getMusic_name();
            String music_singer=data.get(position).getMusic_singer();
            Music.addLastMusic(path,music_name,music_singer);
            Log.e("TAG",data.get(position).getPath());
            ServicesUtils.playMusic(data,position);
        }
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
            if (data.get(position).getMusic_singer().equals("<unknown>")){
                holder.tv_music_singer.setText("未知");
            }else {
                holder.tv_music_singer.setText(data.get(position).getMusic_singer());
            }
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
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //container.removeView(mImageViews.get(position%mImageViews.size()));
            //container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = mImageViews.get(position%mImageViews.size());
            if(imageView.getParent()!=null){
                ((ViewGroup)imageView.getParent()).removeView(imageView);
            }
            container.addView(mImageViews.get(position%mImageViews.size()));
            return mImageViews.get(position%mImageViews.size());
        }
    }
}
