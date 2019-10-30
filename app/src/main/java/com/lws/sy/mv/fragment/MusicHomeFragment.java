package com.lws.sy.mv.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.IdRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lws.sy.mv.Adapter.Info;
import com.lws.sy.mv.Adapter.MyAdapter;
import com.lws.sy.mv.NetInfo.myListAll;
import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.activity.MusicActivity;
import com.lws.sy.mv.activity.MyListActivity;
import com.lws.sy.mv.activity.MyNetListActivity;
import com.lws.sy.mv.db.dao.MusicInfoDao;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.NetListAllRequest;
import com.lws.sy.mv.services.ServicesUtils;
import com.lws.sy.mv.view.CircleImageView;
import com.lws.sy.mv.view.MusicNotify;
import com.lws.sy.mv.view.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class MusicHomeFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private RadioGroup rg_music;
    private LinearLayout iv_music_next;
    private TextView music_name;
    private TextView music_singer;
    private LinearLayout ll_music;
    private RecyclerView rl_song;
    private List<Info> data1;
    private MusicInfoDao dao;
    private SharedPreferences sp;
    private RadioButton local;
    private RadioButton love;
    private RadioButton down;
    private RadioButton last;
    private MyGridView gv_song;
    private List<myListAll> lists;
    private ImageView loadingfail;
    private ImageView loading;
    private CircleImageView circle;
    private Boolean Success=false;
    @Override
    public void initView() {
        view= View.inflate(ContextUtils.getContext(), R.layout.activity_main_musichome,null);
        rg_music=(RadioGroup) view.findViewById(R.id.rg_music);
        circle= (CircleImageView) view.findViewById(R.id.circle);
        iv_music_next= (LinearLayout) view.findViewById(R.id.iv_music_next);
        local= (RadioButton) view.findViewById(R.id.rb_local);
        love= (RadioButton) view.findViewById(R.id.rb_love);
        loadingfail= (ImageView) view.findViewById(R.id.loadingfail);
        loading = (ImageView) view.findViewById(R.id.loading);
        down= (RadioButton) view.findViewById(R.id.rb_down);
        last= (RadioButton) view.findViewById(R.id.rb_last);
        lists=new ArrayList<>();
        gv_song= (MyGridView) view.findViewById(R.id.gv_song);
        music_name= (TextView) view.findViewById(R.id.music_name);
        music_singer= (TextView) view.findViewById(R.id.music_singer);
        iv_music_next.setOnClickListener(this);
        rg_music.setOnCheckedChangeListener(this);
        initRecycleView();
        //getNetData();
        gv_song.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //lists.get(position).getPath();
                Intent intent =new Intent(ContextUtils.getContext(), MyNetListActivity.class);
                intent.putExtra("id",lists.get(position).getId());
                intent.putExtra("name",lists.get(position).getName());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void closeOther() {

    }

    private void getNetData() {
        loading.setVisibility(View.VISIBLE);
        loadingfail.setVisibility(View.GONE);
        NetListAllRequest request=new NetListAllRequest();
        request.getData(NET.getNetTitle);
        request.setInter(new BaseRequest.BaseInter<List<myListAll>>() {
            @Override
            public void handleData(List<myListAll> data,Boolean isSuccess) {
                loading.setVisibility(View.GONE);
                loadingfail.setVisibility(View.GONE);
                Success = isSuccess;
                if(!isSuccess){
                    loadingfail.setVisibility(View.VISIBLE);
                    return;
                }
                lists=data;
                gv_song.setAdapter(new MyNetAdapter());
            }
        });
    }
    @Override
    protected void lazyLoad() {
    }


    @Override
    public void onStart() {
        super.onStart();
        initHead();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!Success){
            getNetData();
        }
    }

    private void initHead() {
        dao=new MusicInfoDao(ContextUtils.getContext());
        if(ServicesUtils.lastposition==-1) {
            Cursor cursor = dao.query("lastMusic");
            if(cursor.moveToLast()) {
                music_name.setText(cursor.getString(1));
                ServicesUtils.defaultMusic_name = cursor.getString(1);
                if(cursor.getString(2).equals("<unknown>")){
                    music_singer.setText("未知");
                    ServicesUtils.defaultMusic_singer = "未知";
                }else {
                    music_singer.setText(cursor.getString(2));
                    ServicesUtils.defaultMusic_singer = cursor.getString(2);
                }
            }
            cursor.close();
        } else {
            music_name.setText(ServicesUtils.getMusicName());
            music_singer.setText(ServicesUtils.getMusicSinger());
        }
        MusicNotify.updateNotify();
    }

    private void initRecycleView() {
        rl_song= (RecyclerView) view.findViewById(R.id.rl_song);
        data1=new ArrayList<>();
        Info info =new Info();
        info.setImg(R.mipmap.w1);
        info.setTitle("看繁华落尽");
        data1.add(info);
        info=new Info();
        info.setImg(R.mipmap.w2);
        info.setTitle("看春来春去");
        data1.add(info);
        MyAdapter adapter =new MyAdapter(ContextUtils.getContext(),data1);
        rl_song.setAdapter(adapter);
        rl_song.setLayoutManager(new GridLayoutManager(ContextUtils.getContext(),2,GridLayoutManager.VERTICAL,false));
        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Info info) {
                Intent intent=new Intent(ContextUtils.getContext(), MyListActivity.class);
                if(info.getTitle().equals("看繁华落尽")){
                    intent.putExtra("type",0);
                }else {
                    intent.putExtra("type",1);
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        Intent intent = new Intent(ContextUtils.getContext(),MusicActivity.class);
        switch (checkedId){
            case R.id.rb_local:
                local.setChecked(false);
                Log.e("跳转","跳转");
                intent.putExtra("list", 1);
                startActivity(intent);
                break;
            case R.id.rb_love:
                love.setChecked(false);
                intent.putExtra("list", 2);
                startActivity(intent);
                break;
            case R.id.rb_down:
                down.setChecked(false);
                intent.putExtra("list", 3);
                startActivity(intent);
                break;
            case R.id.rb_last:
                last.setChecked(false);
                intent.putExtra("list", 4);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_music_next:
                Intent intent=new Intent(ContextUtils.getContext(),MusicActivity.class);
                intent.putExtra("tag",1);
                startActivity(intent);
                break;
        }
    }
    private class MyNetAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return lists.get(position);
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
                convertView=View.inflate(ContextUtils.getContext(),R.layout.song_list_item,null);
                holder.tv_desc= (TextView) convertView.findViewById(R.id.tv_desc);
                holder.iv_img=(ImageView) convertView.findViewById(R.id.iv_img);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.tv_desc.setText(lists.get(position).getName());
            Glide.with(getContext()).load(NET.netPath+lists.get(position).getImgPath()).into(holder.iv_img);
            return convertView;
        }
        class ViewHolder {
            ImageView iv_img;
            TextView tv_desc;
        }
    }
}
