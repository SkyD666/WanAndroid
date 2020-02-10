package com.skyd.wanandroid.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.skyd.wanandroid.GlobalData;
import com.skyd.wanandroid.R;
import com.skyd.wanandroid.adapter.MyCoinListAdapter;
import com.skyd.wanandroid.item.ArticleItem;
import com.skyd.wanandroid.item.MyCoinListItem;
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

public class CoinActivity extends AppCompatActivity {
    private List<MyCoinListItem> myCoinList = new ArrayList<>();
    private MyCoinListAdapter adapter = new MyCoinListAdapter(myCoinList);
    private TextView tvUserName;
    private TextView tvID;
    private TextView tvCoin;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

        toolbar = findViewById(R.id.tb_coin);
        tvUserName = findViewById(R.id.tv_userName);
        tvID = findViewById(R.id.tv_id);
        tvCoin = findViewById(R.id.tv_coin);
        recyclerView = findViewById(R.id.rv_myCoin);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        toolbar.inflateMenu(R.menu.toolbar_coin);       //加载一下菜单

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (HttpUtil.isOnlineByPing("www.baidu.com")) {
            sendRequestAndParseJSON("https://www.wanandroid.com/lg/coin/userinfo/json", 0);
            sendRequestAndParseJSON("https://www.wanandroid.com//lg/coin/list/1/json", 1);
        } else {
            Toast.makeText(CoinActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_coin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_coinRank:
                Intent intent = new Intent(CoinActivity.this, CoinRankActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    //个人信息
    private void sendRequestAndParseJSON(final String sUrl, final int type) {
        HttpUtil.setGetRequest(sUrl, new HttpCallbackListener() {
            @Override
            public void onComplete(final String response) {
                if (type == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonObject_1 = new JSONObject(jsonObject.getString("data"));
                                String username = GlobalData.username + "    等级:" + jsonObject_1.getString("level");
                                String id = "ID:" + jsonObject_1.getString("userId")
                                        + "    排名:" + jsonObject_1.getString("rank");
                                tvUserName.setText(username);
                                tvID.setText(id);
                                tvCoin.setText(jsonObject_1.getString("coinCount"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (type == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonObject_1 = new JSONObject(jsonObject.getString("data"));
                                JSONArray jsonArray = new JSONArray(jsonObject_1.getString("datas"));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject_2 = jsonArray.getJSONObject(i);
                                    final String[] result = new String[2];
                                    //0title  1author  2shareUser  3link  4niceDate  5superChapterName  6chapterName
                                    result[0] = jsonObject_2.getString("desc");
                                    result[1] = jsonObject_2.getString("reason");

                                    addData(result[0], result[1], i);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CoinActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void addData(final String desc, final String reason, final int addPosition) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myCoinList.add(addPosition, new MyCoinListItem(desc, reason));

                adapter.notifyItemInserted(addPosition);
            }
        });
    }
}
