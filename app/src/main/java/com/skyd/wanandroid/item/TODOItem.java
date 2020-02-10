package com.skyd.wanandroid.item;

import android.app.Activity;
import android.content.Context;

public class TODOItem {
    private Activity activity;
    private long timeStamp;
    private long completeTimeStamp;
    private String time;
    private String completeTime;
    private String content;
    private String title;
    private int id;
    private int type;
    private int status;
    private int priority;

    public TODOItem(long timeStamp, String time, long completeTimeStamp, String completeTime, String title,
                    String content, int id, int type, int status, int priority) {
        this.timeStamp = timeStamp;
        this.time = time;
        this.completeTimeStamp = completeTimeStamp;
        this.completeTime = completeTime;
        this.title = title;
        this.content = content;
        this.id = id;
        this.type = type;
        this.status = status;
        this.priority = priority;
    }

    public void setCompleteTimeStamp(long completeTimeStamp) {
        this.completeTimeStamp = completeTimeStamp;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public long getCompleteTimeStamp() {
        return completeTimeStamp;
    }

    public String getTime() {
        return time;
    }

    public String getCompleteTime() {
        return completeTime.equals("") ? "未完成" : completeTime;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public int getPriority() {
        return priority;
    }
}
