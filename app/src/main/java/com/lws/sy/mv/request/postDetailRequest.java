package com.lws.sy.mv.request;

import com.google.gson.Gson;
import com.lws.sy.mv.NetInfo.postInfo;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class postDetailRequest extends BaseRequest<postInfo>{
    @Override
    public postInfo parseJson(String json) {
        Gson gson = new Gson();
        postInfo info = gson.fromJson(json, postInfo.class);
        return info;
    }
}
