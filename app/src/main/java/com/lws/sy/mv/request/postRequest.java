package com.lws.sy.mv.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lws.sy.mv.NetInfo.postInfo;

import java.util.List;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class postRequest extends BaseRequest<List<postInfo>> {
    @Override
    public List<postInfo> parseJson(String json) {
        Gson gson = new Gson();
        List<postInfo> infos = gson.fromJson(json, new TypeToken<List<postInfo>>() {
        }.getType());
        return infos;
    }
}
