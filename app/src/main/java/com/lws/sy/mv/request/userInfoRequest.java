package com.lws.sy.mv.request;

import com.google.gson.Gson;
import com.lws.sy.mv.NetInfo.userInfo;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class userInfoRequest extends BaseRequest<userInfo>{
    @Override
    public userInfo parseJson(String json) {
        Gson gson=new Gson();
        userInfo info = gson.fromJson(json, userInfo.class);
        return info;
    }
}
