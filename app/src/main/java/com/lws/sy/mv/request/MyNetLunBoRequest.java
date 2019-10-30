package com.lws.sy.mv.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lws.sy.mv.NetInfo.netLunBoInfo;

import java.util.List;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class MyNetLunBoRequest extends BaseRequest<List<netLunBoInfo>> {
    @Override
    public List<netLunBoInfo> parseJson(String json) {
        Gson gson = new Gson();
        List<netLunBoInfo> infos = gson.fromJson(json, new TypeToken<List<netLunBoInfo>>() {
        }.getType());
        return infos;
    }
}
