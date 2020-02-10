package com.skyd.wanandroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.skyd.wanandroid.R;
import com.skyd.wanandroid.adapter.SearchHotKeyAdapter;
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

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private Button btnCancel;
    private List<SearchHotKeyItem> searchHotKeyList = new ArrayList<>();
    private SearchHotKeyAdapter adapter = new SearchHotKeyAdapter(searchHotKeyList);
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView = findViewById(R.id.searchView);
        btnCancel = findViewById(R.id.cancel);
        recyclerView = findViewById(R.id.rv_hotKey);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        sendRequestAndParseJSON("https://www.wanandroid.com/hotkey/json");

        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {    //输入完毕后点击搜索按钮事件
                if (HttpUtil.isOnlineByPing("www.baidu.com")) {
                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("key", s);
                    startActivity(intent);
                } else {
                    Toast.makeText(SearchActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {    //搜索框变化监听事件
                return true;
            }
        });
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
                        String[] result = new String[5];
                        result[0] = jsonObject_1.getString("name");
                        result[1] = jsonObject_1.getString("order");
                        result[2] = jsonObject_1.getString("id");
                        result[3] = jsonObject_1.getString("link");
                        result[4] = jsonObject_1.getString("visible");

                        addData(result[0], i);
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
                        Toast.makeText(SearchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void addData(final String name, final int addPosition) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchHotKeyList.add(addPosition, new SearchHotKeyItem(name));

                adapter.notifyItemInserted(addPosition);
                recyclerView.scrollToPosition(0);
            }
        });
    }
}
