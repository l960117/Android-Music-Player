package com.lws.sy.mv.videoUtils;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class videoInfo {
    private String path;
    private String video_name;
    private String video_id;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    @Override
    public String toString() {
        return "videoInfo{" +
                "path='" + path + '\'' +
                ", video_name='" + video_name + '\'' +
                ", video_id='" + video_id + '\'' +
                '}';
    }
}
