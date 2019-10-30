package com.lws.sy.mv.Adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lws.sy.mv.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    private Context context;
    private List<Info> data=new ArrayList<>();

    public MyAdapter(Context context, List<Info> data) {
        this.context = context;
        this.data = data;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_img;
        private TextView tv_desc;
        public MyViewHolder(View view) {
            super(view);
            iv_img= (ImageView) view.findViewById(R.id.iv_img);
            tv_desc=(TextView)view.findViewById(R.id.tv_desc);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        onItemClickListener.onItemClick(v,data.get(getLayoutPosition()));
                    }
                }
            });
        }
    }
    /**
     * 相当于getView方法中创建View和ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(context,R.layout.song_list_item,null);
        return new MyViewHolder(view);
    }

    /**
     * 相当于getView绑定数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Info info=data.get(position);
        holder.tv_desc.setText(info.getTitle());
        holder.iv_img.setImageResource(info.getImg());
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * 点击某条的监听
     */
    public interface OnItemClickListener{
        public void onItemClick(View view, Info info);
    }

    public OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
