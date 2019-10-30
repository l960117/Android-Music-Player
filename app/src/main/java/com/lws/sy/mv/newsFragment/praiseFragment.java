package com.lws.sy.mv.newsFragment;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;
import com.lws.sy.mv.Utils.NET;
import com.lws.sy.mv.musicUtils.TagUtils;
import com.lws.sy.mv.request.BaseRequest;
import com.lws.sy.mv.request.NewsRequest;

import java.util.List;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class praiseFragment extends NewsBaseFragment {
    private SwipeRefreshLayout sf_fresh;
    private NewsRequest request;
    private List<newsInfo> infos;
    private PraiseAdapter adapter;
    private ListView lv_news_list;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    lv_news_list.setAdapter(adapter);
                    break;
            }
        }
    };

    public praiseFragment(String title) {
        super.title = title;
    }

    @Override
    public void initView() {
        view = View.inflate(ContextUtils.getContext(), R.layout.news_list,null);
        sf_fresh = (SwipeRefreshLayout) view.findViewById(R.id.sf_fresh);
        lv_news_list = (ListView) view.findViewById(R.id.lv_news_list);
        sf_fresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sf_fresh.setColorSchemeColors(Color.RED,Color.BLUE,Color.GREEN);
                initData();
            }
        });
        request = new NewsRequest();
        adapter = new PraiseAdapter();
        initData();
    }

    @Override
    protected void lazyLoad() {
        //sf_fresh.setRefreshing(true);

    }

    private void initData() {
        Log.e("TAG",NET.getNews+"&type=1"+"&id="+ TagUtils.id);
        request.getData(NET.getNews+"&type=1"+"&id="+ TagUtils.id);
        request.setInter(new BaseRequest.BaseInter<List<newsInfo>>() {

            @Override
            public void handleData(List<newsInfo> data, Boolean isSuccess) {
                sf_fresh.setRefreshing(false);
                if(!isSuccess){
                    Toast.makeText(ContextUtils.getContext(), "网络连接出错，请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(data==null){
                    //无消息
                    return;
                }
                infos = data;
                mHandler.sendEmptyMessage(0);
            }
        });
    }

    private class PraiseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int position) {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView==null){
                convertView = View.inflate(ContextUtils.getContext(),R.layout.news_list_item,null);
                holder = new ViewHolder();
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.iv_news_icon = (ImageView) convertView.findViewById(R.id.iv_news_icon);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_content.setText(""+infos.get(position).getNickname()+"赞了您的帖子");
            holder.tv_title.setText(infos.get(position).getTitle());
            holder.iv_news_icon.setImageResource(R.mipmap.praise1);
            holder.tv_time.setText(infos.get(position).getNewstime().substring(0,infos.get(position).getNewstime().indexOf(".")));
            return convertView;
        }
        class ViewHolder {
            TextView tv_content;
            TextView tv_time;
            TextView tv_title;
            ImageView iv_news_icon;
        }
    }
}
