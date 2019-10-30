package com.lws.sy.mv.request;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lws.sy.mv.NetInfo.lunboInfo;

import java.util.List;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class LunBoRequest extends BaseRequest<List<lunboInfo>> {


    @Override
    public List<lunboInfo> parseJson(String json) {
        Gson gson=new Gson();
        List<lunboInfo> infos = gson.fromJson(json, new TypeToken<List<lunboInfo>>() {
        }.getType());
        return infos;
    }
}
