package com.lws.sy.mv.Adapter;

/**
 * Name lws
 * QQ 1739573890
 * Phone 15797796165
 * Email llyyhh2014@163.com
 */

public class Info {
    private int img;
    private String title;

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Info{" +
                "img=" + img +
                ", desc='" + title + '\'' +
                '}';
    }
}
