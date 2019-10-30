package com.lws.sy.mv.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lws.sy.mv.NetInfo.myListAll;

import java.util.List;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class NetListAllRequest extends BaseRequest<List<myListAll>> {
    @Override
    public List<myListAll> parseJson(String json) {
        //Log.e("网络歌单列表",json);
        Gson gson=new Gson();
        List<myListAll> lists = gson.fromJson(json, new TypeToken<List<myListAll>>() {
        }.getType());
        return lists;
    }
}
