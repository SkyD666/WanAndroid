package com.skyd.wanandroid.tool;

import android.widget.Toast;

import com.skyd.wanandroid.GlobalData;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    public static void setPostRequest(final String sUrl, final String data, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(sUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.addRequestProperty("Cookie", GlobalData.sessionId);
                    if (data != null) {
                        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                        out.write((data).getBytes());//这里要这么写，不能写成out.writeBytes("k=" + key);因为有中文，服务器收到的是乱码
                    }
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    if (listener != null) {
                        listener.onComplete(new ConvertStreamToString(in).getResult());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void setGetRequest(final String sUrl, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(sUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.addRequestProperty("Cookie", GlobalData.sessionId);
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    if (listener != null) {
                        listener.onComplete(new ConvertStreamToString(in).getResult());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static boolean isOnlineByPing(String ipAddress) {
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 5000 " + ipAddress);
            int status = process.waitFor();
            if (status == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
