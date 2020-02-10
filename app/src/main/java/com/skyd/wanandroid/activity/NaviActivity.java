package com.skyd.wanandroid.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.skyd.wanandroid.R;
import com.skyd.wanandroid.adapter.NaviAdapter;
import com.skyd.wanandroid.item.FlowLayoutItem;
import com.skyd.wanandroid.item.NaviItem;
import com.skyd.wanandroid.tool.ConvertStreamToString;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;
import com.skyd.wanandroid.view.FlowLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NaviActivity extends AppCompatActivity {
    private List<NaviItem> naviList = new ArrayList<>();
    private Toolbar toolbar;
    private NaviAdapter adapter = new NaviAdapter(naviList);
    private RecyclerView recyclerView;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:     //此处要写android. 不写则无法返回
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        toolbar = findViewById(R.id.tb_navi);
        recyclerView = findViewById(R.id.rv_navi);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (HttpUtil.isOnlineByPing("www.baidu.com")) {
            sendRequestAndParseJSON("https://www.wanandroid.com/navi/json", this);
        } else {
            Toast.makeText(this, "无法连接网络", Toast.LENGTH_LONG).show();
        }
    }

    private void sendRequestAndParseJSON(String sUrl, final Context context) {
        HttpUtil.setGetRequest(sUrl, new HttpCallbackListener() {
            @Override
            public void onComplete(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray_0 = new JSONArray(jsonObject.getString("data"));
                            for (int i = 0; i < jsonArray_0.length(); i++) {
                                JSONObject jsonObject_2 = jsonArray_0.getJSONObject(i);
                                JSONArray jsonArray = new JSONArray(jsonObject_2.getString("articles"));
                                final String itemName = jsonObject_2.getString("name");
                                final List<FlowLayoutItem> flowLayoutItemList = new ArrayList<>();
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject jsonObject_3 = jsonArray.getJSONObject(j);
                                    FlowLayoutItem flowLayoutItem = (FlowLayoutItem) LayoutInflater.from(context).inflate(R.layout.flowlayout_item, new FlowLayout(context), false);
                                    flowLayoutItem.setId(jsonObject_3.getInt("id"));
                                    flowLayoutItem.setText(jsonObject_3.getString("title"));
                                    flowLayoutItem.setSurl(jsonObject_3.getString("link"));
                                    flowLayoutItemList.add(flowLayoutItem);
                                }
                                addData(itemName, flowLayoutItemList);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NaviActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void addData(String itemName, List<FlowLayoutItem> flowLayoutItemList) {

        for (int i = 0; i < flowLayoutItemList.size(); i++) {
            flowLayoutItemList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //必须把view强转成一个新控件,不然一直都是最后一个item
                    FlowLayoutItem fl = (FlowLayoutItem) view;
                    Intent intent = new Intent(view.getContext(), WebsiteActivity.class);
                    intent.putExtra("id", fl.getId());
                    intent.putExtra("name", fl.getText().toString());
                    intent.putExtra("surl", fl.getSurl());
                    Toast.makeText(view.getContext(), fl.getText(), Toast.LENGTH_SHORT).show();
                    view.getContext().startActivity(intent);
                }
            });
        }

        naviList.add(new NaviItem(flowLayoutItemList, itemName));

        adapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);
    }
}
