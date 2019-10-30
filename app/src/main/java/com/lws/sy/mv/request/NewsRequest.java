package com.lws.sy.mv.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lws.sy.mv.newsFragment.newsInfo;

import java.util.List;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class NewsRequest extends BaseRequest <List<newsInfo>>{

    @Override
    public List<newsInfo> parseJson(String json) {
        Gson gson = new Gson();
        if(json.equals("")){
            return null;
        }
        List<newsInfo> infos = gson.fromJson(json,new TypeToken<List<newsInfo>>() {
        }.getType());
        return infos;
    }
}
