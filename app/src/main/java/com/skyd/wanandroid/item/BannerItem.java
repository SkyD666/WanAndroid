package com.skyd.wanandroid.item;

import android.graphics.Bitmap;

public class BannerItem {
    private String surl;
    private String imagePath;
    private String title;
    private Bitmap picture;

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public String getSurl() {
        return surl;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getTitle() {
        return title;
    }

    public BannerItem(String surl, String imagePath, String title) {
        this.surl = surl;
        this.imagePath = imagePath;
        this.title = title;
    }
}
