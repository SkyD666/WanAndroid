package com.skyd.wanandroid.tool;

public interface HttpCallbackListener {
    // 请求成功
    void onComplete(String response);

    // 请求失败
    void onError(Exception e);
}
