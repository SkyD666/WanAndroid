package com.skyd.wanandroid.tool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConvertStreamToString {
    private InputStream is;
    private String result;

    public ConvertStreamToString(InputStream is) {
        this.is = is;
        startConvert();
    }

    public String getResult() {
        return result;
    }

    private void startConvert() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.result = sb.toString();
    }
}
