package com.lws.sy.mv.request;

import com.google.gson.Gson;
import com.lws.sy.mv.NetInfo.commonResponse;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class UpLoadRequest extends BaseRequest<commonResponse> {
    @Override
    public commonResponse parseJson(String json) {
        Gson gson=new Gson();
        commonResponse data = gson.fromJson(json, commonResponse.class);
        return data;
    }
}
