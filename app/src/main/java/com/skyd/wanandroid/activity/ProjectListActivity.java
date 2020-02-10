package com.skyd.wanandroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.skyd.wanandroid.R;
import com.skyd.wanandroid.adapter.ArticleAdapter;
import com.skyd.wanandroid.item.ArticleItem;
import com.skyd.wanandroid.tool.ConvertStreamToString;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProjectListActivity extends AppCompatActivity {
    private List<ArticleItem> articleList = new ArrayList<>();
    private RecyclerView recyclerView;
    private int refreshCount = 1, isFresh = 0;       //从1开始
    private ArticleAdapter adapter = new ArticleAdapter(articleList);
    private Toolbar toolbar;
    private int id;
    private int calledType;       //0也意味着，是体系的文章，surl也会不同
    // (0是体系文章，1是项目文章，2是体系下按作者搜索文章，3是问答)
    private String author;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        calledType = intent.getIntExtra("hasImage", 1);//就是calledType
        name = intent.getStringExtra("name");
        author = intent.getStringExtra("author");

        toolbar = findViewById(R.id.tb_projectList);
        recyclerView = findViewById(R.id.rv_projectListArticle);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        toolbar.setTitle(name);

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
                        if (calledType == 1) {
                            isFresh = 1;
                            sendRequestAndParseJSON("https://www.wanandroid.com/project/list/" + (refreshCount++) + "/json?cid=" + id, adapter.getItemCount(), adapter.getItemCount());
                        } else if (calledType == 0) {
                            isFresh = 1;
                            sendRequestAndParseJSON("https://www.wanandroid.com/article/list/" + ((refreshCount++) - 1) + "/json?cid=" + id, adapter.getItemCount(), adapter.getItemCount());
                        } else if (calledType == 2) {
                            isFresh = 1;
                            sendRequestAndParseJSON("https://wanandroid.com/article/list/" + ((refreshCount++) - 1) + "/json?author=" + author, adapter.getItemCount(), adapter.getItemCount());
                        } else if (calledType == 3) {
                            isFresh = 1;
                            sendRequestAndParseJSON("https://wanandroid.com/wenda/list/" + (refreshCount++) + "/json", adapter.getItemCount(), adapter.getItemCount());
                        }
                    } else {
                        Toast.makeText(ProjectListActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
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
            if (calledType == 1) {
                sendRequestAndParseJSON("https://www.wanandroid.com/project/list/" + (refreshCount++) + "/json?cid=" + id, 0, 0);
            } else if (calledType == 0) {
                sendRequestAndParseJSON("https://www.wanandroid.com/article/list/" + ((refreshCount++) - 1) + "/json?cid=" + id, 0, 0);
            } else if (calledType == 2) {
                sendRequestAndParseJSON("https://wanandroid.com/article/list/" + ((refreshCount++) - 1) + "/json?author=" + author, 0, 0);
            } else if (calledType == 3) {
                sendRequestAndParseJSON("https://wanandroid.com/wenda/list/" + (refreshCount++) + "/json", 0, 0);
            }
        } else {
            Toast.makeText(ProjectListActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
        }
    }

    private void sendRequestAndParseJSON(String sUrl, final int scrollToPos, final int addItemFrom) {
        HttpUtil.setGetRequest(sUrl, new HttpCallbackListener() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject_1 = new JSONObject(jsonObject.getString("data"));
                    JSONArray jsonArray = new JSONArray(jsonObject_1.getString("datas"));
                    int addCount = jsonObject_1.getInt("size");
                    for (int i = 0; i < addCount; i++) {
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
                        result[7] = jsonObject_2.getString("envelopePic");

                        addData(result[0], result[1], result[2], result[3], result[4]
                                , result[5], result[6], result[7],
                                jsonObject_2.getInt("id"), i + addItemFrom, scrollToPos,
                                i + 1 == addCount ? 1 : 0);
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
                        Toast.makeText(ProjectListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void addData(final String title, final String author, final String shareUser, final String link,
                         final String niceDate, final String superChapterName, final String chapterName,
                         final String pictureUrl, final int id, final int addPosition, final int scrollToPos,
                         final int finish) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                articleList.add(addPosition, new ArticleItem(title, author, shareUser, link, niceDate, superChapterName
                        , chapterName, 0, id));
                if (!pictureUrl.equals("")) {
                    articleList.get(addPosition).setHasImage(1);
                    articleList.get(addPosition).setPictureUrl(pictureUrl);
                }

                adapter.notifyItemInserted(addPosition);
                recyclerView.scrollToPosition(scrollToPos);
                if (finish == 1) isFresh = 0;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
