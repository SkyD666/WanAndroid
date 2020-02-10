package com.skyd.wanandroid.item;

import android.content.Context;
import android.util.AttributeSet;

public class FlowLayoutItem extends androidx.appcompat.widget.AppCompatTextView {
    private int id;
    private String surl;

    public void setSurl(String surl) {
        this.surl = surl;
    }

    public String getSurl() {
        return surl;
    }

    public FlowLayoutItem(Context context) {
        super(context);
    }

    public FlowLayoutItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayoutItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
