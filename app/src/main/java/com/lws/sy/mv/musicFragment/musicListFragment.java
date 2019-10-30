package com.lws.sy.mv.musicFragment;

import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.Utils.PinYinUtils;
import com.lws.sy.mv.Utils.Utils;
import com.lws.sy.mv.activity.CenterPostActivity;
import com.lws.sy.mv.db.dao.MusicInfoDao;
import com.lws.sy.mv.fragment.BaseFragment;
import com.lws.sy.mv.musicUtils.Music;
import com.lws.sy.mv.musicUtils.TagUtils;
import com.lws.sy.mv.musicUtils.musicInfo;
import com.lws.sy.mv.services.ServicesUtils;
import com.lws.sy.mv.view.IndexView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class musicListFragment extends BaseFragment implements IndexView.OnIndexChangeListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {
    private int type = 1;
    private TextView tv_musicTitle;
    private ImageView goBack;
    private List<musicInfo> data = null;
    private ListView lv_music_list;
    private IndexView iv_words;
    private MusicInfoDao dao;
    private PopupWindow pw;
    private PopupWindow pw1;
    private boolean isDelete;
    private ImageView iv_list_more;
    private ListView lv_list;
    private SharedPreferences sp;
    private LinearLayout ll_total;
    private MyAdapter adapter;
    private Boolean isAllCheck=false;
    private HashMap<Integer,Boolean> map;
    private RadioButton rg_share;
    private RadioButton rg_add;
    private RadioButton rg_delete;
    private RadioButton rg_upload;
    private RelativeLayout ll_click;
    private RadioButton bottomCancel;
    private CheckBox bottomAll;
    private Boolean isShow=false;
    private ProgressBar pb_loading;
    private boolean isReflush=false;
    private RelativeLayout rl_main;
    private final String[] titleData={"扫描音乐"};
    private final int[] imgData={R.mipmap.scan,
            R.mipmap.random,
            R.mipmap.only};
    public musicListFragment(int type) {
        this.type = type;
    }
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
            }else if(msg.what==5){
                selectData();
                isReflush=false;
                adapter.notifyDataSetChanged();
                pb_loading.setVisibility(View.GONE);
            }else if(msg.what==6){
                selectData();
                initList();
                isReflush=false;
                lv_music_list.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
            }
        }
    };
    @Override
    public void initView() {
        view= View.inflate(ContextUtils.getContext(), R.layout.music_list,null);
        tv_musicTitle= (TextView) view.findViewById(R.id.tv_musicTitle);
        goBack = (ImageView) view.findViewById(R.id.goBack);
        ll_click= (RelativeLayout) view.findViewById(R.id.ll_click);
        bottomCancel= (RadioButton) view.findViewById(R.id.bottomCancel);
        bottomAll= (CheckBox) view.findViewById(R.id.bottomAll);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        rl_main= (RelativeLayout) view.findViewById(R.id.rl_main);
        map=new HashMap<>();
        bottomCancel.setOnClickListener(this);
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
        lv_music_list = (ListView) view.findViewById(R.id.lv_music_list);
        iv_list_more= (ImageView) view.findViewById(R.id.iv_list_more);
        ll_total= (LinearLayout) view.findViewById(R.id.ll_total);
        ll_total.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(pw!=null&&pw.isShowing()){
                    pw.dismiss();
                }
                return false;
            }
        });
        lv_music_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(pw!=null&&pw.isShowing()){
                    pw.dismiss();
                }
                return false;
            }
        });
        iv_list_more.setOnClickListener(this);
        iv_words= (IndexView) view.findViewById(R.id.iv_words);
        dao=new MusicInfoDao(ContextUtils.getContext());
        data = new ArrayList<>();
        sp= ContextUtils.getContext().getSharedPreferences("share", MODE_PRIVATE);
        boolean isFirst = sp.getBoolean("isMusicFirst", true);
        if (isFirst) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isMusicFirst",false);
            editor.commit();
            isReflush=true;
            lv_music_list.setVisibility(View.GONE);
            pb_loading.setVisibility(View.VISIBLE);
            new Thread(){
                @Override
                public void run() {
                    Music.getMusic();
                    mHandler.sendEmptyMessage(6);
                }
            }.start();
        }else{
            selectData();
            initList();
        }
    }

    @Override
    protected void closeOther() {

    }

    private void initList() {
        adapter=new MyAdapter();
        lv_music_list.setAdapter(adapter);
        lv_music_list.setOnItemClickListener(this);
        lv_music_list.setOnItemLongClickListener(this);
        iv_words.setOnIndexChangeListener(this);
        goBack.setOnClickListener(this);
    }


    @Override
    protected void lazyLoad() {

    }

    /**
     * 获取音乐列表数据
     */
    private void selectData() {
        SharedPreferences sp= ContextUtils.getContext().getSharedPreferences("share", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        switch(type){
            case 1:
                tv_musicTitle.setText("本地音乐");
                data=getMusic("music");
                edit.putInt("musicList",1);
                isDelete=false;
                iv_words.setVisibility(View.VISIBLE);
                //本地音乐
                break;
            case 2:
                tv_musicTitle.setText("喜欢");
                data=getMusic("loveMusic");
                edit.putInt("musicList",2);
                isDelete=true;
                iv_words.setVisibility(View.GONE);
                //喜欢列表
                break;
            case 3:
                tv_musicTitle.setText("下载");
                data=getMusic("downMusic");
                edit.putInt("musicList",3);
                isDelete=true;
                iv_words.setVisibility(View.GONE);
                //下载列表
                break;
            case 4:
                tv_musicTitle.setText("最近");
                data=getMusic("lastMusic");
                edit.putInt("musicList",4);
                isDelete=false;
                iv_words.setVisibility(View.GONE);
                //最近列表
                break;
            default:
                break;
        }
        edit.commit();
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
    @Override
    public void OnIndexChange(String word) {
        LocationData(word);
    }
    private void LocationData(String word) {
        for (int i = 0; i < data.size(); i++) {
            String listword = PinYinUtils.getPinYin(data.get(i).getMusic_name()).substring(0, 1);
            if (word.equals(listword)) {
                lv_music_list.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(pw!=null&&pw.isShowing()){
            pw.dismiss();
        }
        if (pw!=null&&pw.isShowing()){
            pw.dismiss();
        }
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        bottomAll.setChecked(false);
        mHandler.sendEmptyMessage(4);
        showBottomPw();
        return true;
    }
    private void showBottomPw() {
        Log.e("TAG","长按");
        if(pw1==null){
            View pwView=View.inflate(getContext(),R.layout.bottom_list,null);
            pw1=new PopupWindow(pwView,lv_music_list.getWidth()+Utils.dip2px(getContext(),10),Utils.dip2px(getContext(),60),true);
            rg_add= (RadioButton) pwView.findViewById(R.id.rg_add);
            rg_delete= (RadioButton) pwView.findViewById(R.id.rg_delete);
            rg_share= (RadioButton) pwView.findViewById(R.id.rg_share);
            rg_upload= (RadioButton) pwView.findViewById(R.id.rg_upload);
        }
        pw1.setFocusable(false);
        if(pw1.isShowing()){
            pw1.dismiss();
            return;
        }
        pw1.setAnimationStyle(R.style.popwin_anim_style);
        pw1.showAsDropDown(rl_main, 0,-Utils.dip2px(getContext(),60));
        rg_add.setOnClickListener(this);
        rg_upload.setOnClickListener(this);
        rg_share.setOnClickListener(this);
        rg_delete.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_list_more:
                if(isReflush){
                    return;
                }
                if(type!=1){
                    return;
                }
                showPW();
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
            case R.id.rg_add:
                addMyList();
                break;
            case R.id.rg_delete:
                Set<Integer> set = map.keySet();
                Iterator<Integer> iterator = set.iterator();
                while(iterator.hasNext()){
                    dao.delete(data.get(iterator.next()).getMusic_name(),type);
                }
                selectData();
                mHandler.sendEmptyMessage(2);
                break;
            case R.id.rg_upload:
                isShow=false;
                ll_click.setVisibility(View.GONE);
                Toast.makeText(ContextUtils.getContext(), "暂不支持本地上传", Toast.LENGTH_SHORT).show();
                //mHandler.sendEmptyMessage(2);
                break;
            case R.id.bottomCancel:
                mHandler.sendEmptyMessage(2);
                break;
            case R.id.rg_share:
                if(map.size()>1){
                    Toast.makeText(ContextUtils.getContext(), "当前仅支持单曲分享", Toast.LENGTH_SHORT).show();
                    mHandler.sendEmptyMessage(2);
                    if(pw1!=null&&pw1.isShowing()){
                        pw1.dismiss();
                    }
                    return;
                }
                if(TagUtils.id==-1){
                    Toast.makeText(ContextUtils.getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                    mHandler.sendEmptyMessage(2);
                    if(pw1!=null&&pw1.isShowing()){
                        pw1.dismiss();
                    }
                    return;
                }
                Set<Integer> set1 = map.keySet();
                Iterator<Integer> iterator1 = set1.iterator();
                while(iterator1.hasNext()){
                    Intent music_intent = new Intent(ContextUtils.getContext(),CenterPostActivity.class);
                    music_intent.putExtra("type",2);
                    music_intent.putExtra("path",data.get(iterator1.next()).getPath());
                    startActivity(music_intent);
                }
                mHandler.sendEmptyMessage(2);
                break;
        }
        if(pw1!=null&&pw1.isShowing()){
            pw1.dismiss();
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
    private void showPW() {
        Log.e("TAG","显示扫描");
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0) {
                    selectData();
                    lv_music_list.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    isReflush = false;
                    pb_loading.setVisibility(View.GONE);
                }
            }
        };
        if(pw==null){
            View pwView=View.inflate(ContextUtils.getContext(),R.layout.menu_pw,null);
            pw=new PopupWindow(pwView, Utils.dip2px(ContextUtils.getContext(),120),Utils.dip2px(ContextUtils.getContext(),35),true);
            lv_list=(ListView) pwView.findViewById(R.id.lv_list);
        }
        lv_list.setAdapter(new MyPWAdapter());
        pw.setFocusable(false);
        if(pw.isShowing()){
            pw.dismiss();
            return;
        }
        pw.showAsDropDown(iv_list_more,-40,-20);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position==0) {
                    lv_music_list.setVisibility(View.INVISIBLE);
                    pb_loading.setVisibility(View.VISIBLE);
                    isReflush=true;
                    new Thread(){
                        @Override
                        public void run() {
                            Music.getMusic();
                            handler.sendEmptyMessage(0);
                        }
                    }.start();
                }
                pw.dismiss();
            }
        });
    }
    class MyPWAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return titleData.length;
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
                convertView=View.inflate(ContextUtils.getContext(),R.layout.music_list_pw_item,null);
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

    private class MyAdapter extends BaseAdapter {
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
                convertView = View.inflate(ContextUtils.getContext(),R.layout.music_list_item,null);
                holder=new ViewHolder();
                holder.tv_music_name= (TextView) convertView.findViewById(R.id.tv_music_name);
                holder.tv_music_singer = (TextView) convertView.findViewById(R.id.tv_music_singer);
                holder.iv_music_add= (ImageView) convertView.findViewById(R.id.iv_add_music);
                holder.iv_music_select= (CheckBox) convertView.findViewById(R.id.iv_music_more);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.iv_music_add.setOnClickListener(new AddMusicClick(position));
            holder.tv_music_name.setText(data.get(position).getMusic_name());
            if(isAllCheck){
                holder.iv_music_select.setChecked(true);
                map.put(position,true);
            }else {
                holder.iv_music_select.setChecked(false);
                map.clear();
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
        class AddMusicClick implements View.OnClickListener{
            public int position;
            public AddMusicClick(int position) {
                this.position=position;
            }
            @Override
            public void onClick(View v) {
                Toast.makeText(ContextUtils.getContext(), "已添加到播放队列", Toast.LENGTH_SHORT).show();
                ServicesUtils.addSum(position);
            }
        }
    }
}
