package com.lws.sy.mv.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lws.sy.mv.NetInfo.commentInfo;

import java.util.List;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class CommentRequest extends BaseRequest<List<commentInfo>> {
    @Override
    public List<commentInfo> parseJson(String json) {
        Gson gson = new Gson();
        if(json.equals("")||json.equals("[]")){
            return null;
        }
        List<commentInfo> infos = gson.fromJson(json, new TypeToken<List<commentInfo>>() {
        }.getType());
        return infos;
    }
}
