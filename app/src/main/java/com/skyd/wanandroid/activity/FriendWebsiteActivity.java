package com.skyd.wanandroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.skyd.wanandroid.R;
import com.skyd.wanandroid.adapter.FriendWebsiteAdapter;
import com.skyd.wanandroid.item.ArticleItem;
import com.skyd.wanandroid.item.FriendWebsiteItem;
import com.skyd.wanandroid.item.SearchHotKeyItem;
import com.skyd.wanandroid.tool.ConvertStreamToString;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FriendWebsiteActivity extends AppCompatActivity {
    private List<FriendWebsiteItem> friendWebsiteList = new ArrayList<>();
    private Toolbar toolbar;
    private FriendWebsiteAdapter adapter = new FriendWebsiteAdapter(friendWebsiteList);
    private RecyclerView recyclerView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_website);

        toolbar = findViewById(R.id.tb_friendWebsite);
        recyclerView = findViewById(R.id.rv_friendWebsite);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (HttpUtil.isOnlineByPing("www.baidu.com")) {
            sendRequestAndParseJSON("https://www.wanandroid.com/friend/json");
        } else {
            Toast.makeText(this, "无法连接网络", Toast.LENGTH_LONG).show();
        }

    }

    private void sendRequestAndParseJSON(String sUrl) {
        HttpUtil.setGetRequest(sUrl, new HttpCallbackListener() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject_1 = jsonArray.getJSONObject(i);
                        String[] result = new String[2];
                        result[0] = jsonObject_1.getString("name");
                        result[1] = jsonObject_1.getString("link");

                        addData(result[0], result[1], i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FriendWebsiteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void addData(final String name, final String link, final int addPosition) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                friendWebsiteList.add(addPosition, new FriendWebsiteItem(name, link, friendWebsiteList.size() + 1));

                adapter.notifyItemInserted(addPosition);
                recyclerView.scrollToPosition(0);
            }
        });
    }
}
