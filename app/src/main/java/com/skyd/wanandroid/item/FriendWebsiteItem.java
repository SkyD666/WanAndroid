package com.skyd.wanandroid.item;

public class FriendWebsiteItem {
    private int index = 0;
    private String name;
    private String surl;

    public FriendWebsiteItem(String name, String surl, int index) {
        this.name = name;
        this.surl = surl;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public String getSurl() {
        return surl;
    }

    public int getIndex() {
        return index;
    }
}
