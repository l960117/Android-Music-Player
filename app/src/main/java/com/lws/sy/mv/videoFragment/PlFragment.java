package com.lws.sy.mv.videoFragment;

import android.view.View;

import com.lws.sy.mv.R;
import com.lws.sy.mv.Utils.ContextUtils;


/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class PlFragment extends VideoBaseFragment {
    public PlFragment(String title) {
        super.title=title;
    }

    @Override
    public void initView() {
        view= View.inflate(ContextUtils.getContext(), R.layout.activity_video_vp_pl,null);
    }
}
