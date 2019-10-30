package com.lws.sy.mv.request;

import com.google.gson.Gson;
import com.lws.sy.mv.NetInfo.PCInfo;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class PCRequest extends BaseRequest<PCInfo> {
    @Override
    public PCInfo parseJson(String json) {
        Gson gson = new Gson();
        PCInfo pcInfo = gson.fromJson(json, PCInfo.class);
        return pcInfo;
    }
}
