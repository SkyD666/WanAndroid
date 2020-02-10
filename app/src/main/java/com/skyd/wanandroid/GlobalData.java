package com.skyd.wanandroid;

import android.util.Log;
import android.widget.Toast;

import com.skyd.wanandroid.activity.MainActivity;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GlobalData {
    public static boolean isSignIn;
    public static String username;
    public static String userEmail;
    public static String cookie;
    public static String sessionId;
    public static List<Integer> collectId = new ArrayList<>();
    public static int[] initComplete = new int[6];

    private static int cnt = 0;
    private static int collectTotal = -1;
    public static void getCollections() {
        collectTotal = -1;
        cnt = 0;
        sendRequestAndParseJSON("https://www.wanandroid.com/lg/collect/list/" + (cnt++) + "/json");
    }

    private static void sendRequestAndParseJSON(String sUrl) {
        if (GlobalData.cookie != null) {
            HttpUtil.setGetRequest(sUrl, new HttpCallbackListener() {
                @Override
                public void onComplete(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonObject_1 = new JSONObject(jsonObject.getString("data"));
                        if (collectTotal == -1) {
                            collectTotal = jsonObject_1.getInt("total");
                        }
                        if (collectTotal == 0) {
                            return;
                        }
                        JSONArray jsonArray = new JSONArray(jsonObject_1.getString("datas"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject_2 = jsonArray.getJSONObject(i);
                            GlobalData.collectId.add(jsonObject_2.getInt("originId"));
                        }
                        if (GlobalData.collectId.size() < collectTotal) {
                            sendRequestAndParseJSON("https://www.wanandroid.com/lg/collect/list/" + (cnt++) + "/json");
                        }
                        //Log.d("---", collectTotal + "  " + GlobalData.collectId.size() + "  " + response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(final Exception e) {
                }
            });
        }
    }
}
