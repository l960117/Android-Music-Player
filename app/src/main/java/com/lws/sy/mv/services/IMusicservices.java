package com.lws.sy.mv.services;

import com.lws.sy.mv.musicUtils.musicInfo;

import java.util.List;

/**
 * Name lws
 * QQ 1749573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public interface IMusicservices {
    public void callPlay(List<musicInfo> palylist, int position);
    public void callStop();
    public int getMusicCurrentPosition();
    public int getMusicDuration();
    public void seek(int progress);
    public int check();
}
