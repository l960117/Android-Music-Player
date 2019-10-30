package com.lws.sy.mv.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lws.sy.mv.musicUtils.musicInfo;

import java.util.List;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class MyNetListRequest extends BaseRequest<List<musicInfo>> {

    @Override
    public List<musicInfo> parseJson(String json) {
        Gson gson=new Gson();
        List<musicInfo> lists = gson.fromJson(json, new TypeToken<List<musicInfo>>() {
        }.getType());
        return lists;
    }
}
