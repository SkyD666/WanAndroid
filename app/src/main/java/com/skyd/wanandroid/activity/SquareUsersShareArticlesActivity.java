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
import com.skyd.wanandroid.adapter.ArticleAdapter;
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

public class SquareUsersShareArticlesActivity extends AppCompatActivity {
    private List<ArticleItem> articleList = new ArrayList<>();
    private ArticleAdapter adapter = new ArticleAdapter(articleList);
    private TextView tvUserName;
    private TextView tvID;
    private TextView tvCoin;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private int type;
    private int refreshCount = 1, isFresh = 0;
    private int userId = -1;
    private String username;

    public void refreshList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (HttpUtil.isOnlineByPing("www.baidu.com")) {
                    refreshCount = 0;
                    articleList.clear();
                    adapter.notifyDataSetChanged();
                    if (type == 0) {
                        sendRequestAndParseJSONUserInfo("https://www.wanandroid.com/user/" + userId
                                + "/share_articles/" + (refreshCount) + "/json");
                        sendRequestAndParseJSONArticle("https://www.wanandroid.com/user/" + userId
                                + "/share_articles/" + (refreshCount++) + "/json", 0, 0);
                    } else if (type == 1) {
                        sendRequestAndParseJSONUserInfo("https://wanandroid.com/user/lg/private_articles/"
                                + (refreshCount) + "/json");
                        sendRequestAndParseJSONArticle("https://wanandroid.com/user/lg/private_articles/"
                                + (refreshCount++) + "/json", 0, 0);
                    }
                } else {
                    Toast.makeText(SquareUsersShareArticlesActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_users_share_articles);

        toolbar = findViewById(R.id.tb_squareUsersArticles);
        tvUserName = findViewById(R.id.tv_shareUserName);
        tvID = findViewById(R.id.tv_shareUserId);
        tvCoin = findViewById(R.id.tv_shareUserCoin);
        recyclerView = findViewById(R.id.rv_squareUsersArticles);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        username = intent.getStringExtra("username");
        type = intent.getIntExtra("type", 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        if (type == 0) {
            toolbar.setTitle(username + "的文章");
        } else if (type == 1) {
            toolbar.setTitle("我的文章");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem = 0;

            //滚动中调用
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //这个就是判断当前滑动停止了，并且获取当前屏幕最后一个可见的条目是第几个，当前屏幕数据已经显示完毕的时候就去加载数据
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        lastVisibleItem + 1 == adapter.getItemCount() && isFresh == 0) {
                    if (HttpUtil.isOnlineByPing("www.baidu.com")) {
                        isFresh = 1;
                        if (type == 0) {
                            sendRequestAndParseJSONArticle("https://www.wanandroid.com/user/" + userId
                                    + "/share_articles/" + (refreshCount++) + "/json", adapter.getItemCount(), adapter.getItemCount());
                        } else if (type == 1) {
                            sendRequestAndParseJSONArticle("https://wanandroid.com/user/lg/private_articles/"
                                    + (refreshCount++) + "/json", adapter.getItemCount(), adapter.getItemCount());
                        }
                    } else {
                        Toast.makeText(SquareUsersShareArticlesActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
                    }
                }
            }

            //滚动停止后调用
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //获取最后一个可见的条目的位置,如果是线性加载更多就换成这个
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
            }
        });

        if (HttpUtil.isOnlineByPing("www.baidu.com")) {
            if (type == 0) {
                sendRequestAndParseJSONUserInfo("https://www.wanandroid.com/user/" + userId
                        + "/share_articles/" + (refreshCount) + "/json");
                sendRequestAndParseJSONArticle("https://www.wanandroid.com/user/" + userId
                        + "/share_articles/" + (refreshCount++) + "/json", 0, 0);
            } else if (type == 1) {
                sendRequestAndParseJSONUserInfo("https://wanandroid.com/user/lg/private_articles/"
                        + (refreshCount) + "/json");
                sendRequestAndParseJSONArticle("https://wanandroid.com/user/lg/private_articles/"
                        + (refreshCount++) + "/json", 0, 0);
            }
        } else {
            Toast.makeText(SquareUsersShareArticlesActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    //个人信息
    private void sendRequestAndParseJSONUserInfo(String sUrl) {
        HttpUtil.setGetRequest(sUrl, new HttpCallbackListener() {
            @Override
            public void onComplete(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject_0 = new JSONObject(jsonObject.getString("data"));
                            JSONObject jsonObject_1 = new JSONObject(jsonObject_0.getString("coinInfo"));
                            String username_2 = username + "    等级:" + jsonObject_1.getString("level");
                            String id = "ID:" + jsonObject_1.getString("userId")
                                    + "    排名:" + jsonObject_1.getString("rank");
                            tvUserName.setText(username_2);
                            tvID.setText(id);
                            tvCoin.setText(jsonObject_1.getString("coinCount"));
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
                        Toast.makeText(SquareUsersShareArticlesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void sendRequestAndParseJSONArticle(String sUrl, final int scrollToPos, final int addItemFrom) {
        HttpUtil.setGetRequest(sUrl, new HttpCallbackListener() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject_0 = new JSONObject(jsonObject.getString("data"));
                    JSONObject jsonObject_1 = new JSONObject(jsonObject_0.getString("shareArticles"));
                    JSONArray jsonArray = new JSONArray(jsonObject_1.getString("datas"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject_2 = jsonArray.getJSONObject(i);
                        String[] result = new String[8];
                        //0title  1author  2shareUser  3link  4niceDate  5superChapterName  6chapterName
                        result[0] = jsonObject_2.getString("title");
                        result[1] = jsonObject_2.getString("author");
                        result[2] = jsonObject_2.getString("shareUser");
                        result[3] = jsonObject_2.getString("link");
                        result[4] = jsonObject_2.getString("niceDate");
                        result[5] = jsonObject_2.getString("superChapterName");
                        result[6] = jsonObject_2.getString("chapterName");
                        int id = jsonObject_2.getInt("id");
                        int userId = jsonObject_2.getInt("userId");

                        addData(result[0], result[1], result[2], result[3], result[4]
                                , result[5], result[6], id, userId, i + addItemFrom, scrollToPos,
                                i + 1 == jsonArray.length() ? 1 : 0);
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
                        Toast.makeText(SquareUsersShareArticlesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void addData(final String title, final String author, final String shareUser, final String link,
                         final String niceDate, final String superChapterName, final String chapterName,
                         final int id, final int userId, final int addPosition, final int scrollToPos,
                         final int finish) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                articleList.add(addPosition, new ArticleItem(title, author, shareUser, link, niceDate, superChapterName
                        , chapterName, 0, id));

                articleList.get(addPosition).setUserId(userId);     //添加用户id数据
                if (type == 1) {
                    articleList.get(addPosition).setMySquare(true);
                    articleList.get(addPosition).setActivity(SquareUsersShareArticlesActivity.this);
                }
                adapter.notifyItemInserted(addPosition);
                recyclerView.scrollToPosition(scrollToPos);

                if (finish == 1) isFresh = 0;
            }
        });
    }
}
