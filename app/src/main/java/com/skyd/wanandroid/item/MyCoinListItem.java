package com.skyd.wanandroid.item;

public class MyCoinListItem {
    private String desc;
    private String reason;

    public String getDesc() {
        return desc;
    }

    public String getReason() {
        return reason;
    }

    public MyCoinListItem(String desc, String reason){
        this.desc = desc;
        this.reason = reason;
    }
}
