package com.skyd.wanandroid.activity;

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
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import com.skyd.wanandroid.R;
import com.skyd.wanandroid.adapter.ArticleAdapter;
import com.skyd.wanandroid.item.ArticleItem;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeChatArticleActivity extends AppCompatActivity {
    private Map<String, Integer> chapterMap = new HashMap<>();
    private List<String> chapterOrder = new ArrayList<>();
    private List<ArticleItem> articleList = new ArrayList<>();
    private RecyclerView recyclerView;
    private int selectedAuthorId = 0;
    private int refreshCount = 1, isFresh = 0;
    private ArticleAdapter adapter = new ArticleAdapter(articleList);
    private Toolbar toolbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_we_chat_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == 0) {

        } else {
            selectedAuthorId = chapterMap.get(item.getTitle());
            articleList.clear();
            refreshCount = 1;
            adapter.notifyDataSetChanged();
            sendRequestAndParseJSON("https://wanandroid.com/wxarticle/list/" +
                    selectedAuthorId + "/" + (refreshCount++) + "/json", 0, 0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        menu.add(1, 0, 0, "在此公众号中搜索历史文章");
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = new SearchView(this);
        menu.getItem(0).setActionView(searchView);
        searchView.setQueryHint("在此公众号中搜索历史文章");
        searchView.setIconified(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.clearFocus();
        searchView.onActionViewCollapsed();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);         //输入法回车搜索
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (HttpUtil.isOnlineByPing("www.baidu.com")) {
                    if (chapterMap.size() == chapterOrder.size() && chapterMap.size() != 0) {
                        Intent intent = new Intent(WeChatArticleActivity.this, SearchResultActivity.class);
                        intent.putExtra("key", s);
                        intent.putExtra("authorId", selectedAuthorId);
                        intent.putExtra("type", 1);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(WeChatArticleActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //Log.d("---", chapterOrder.toString());
        if (chapterMap.size() == chapterOrder.size() && chapterMap.size() != 0) {
            for (int i = 0; i < chapterOrder.size(); i++) {
                menu.add(100, i + 1, i + 1, chapterOrder.get(i));
                menu.getItem(i + 1).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_chat_article);

        toolbar = findViewById(R.id.tb_weChatArticle);
        recyclerView = findViewById(R.id.rv_weChatArticle);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        toolbar.inflateMenu(R.menu.toolbar_we_chat_article);       //加载一下菜单

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
                        sendRequestAndParseJSON("https://wanandroid.com/wxarticle/list/" + selectedAuthorId + "/" + (refreshCount++) + "/json", adapter.getItemCount(), adapter.getItemCount());
                    } else {
                        Toast.makeText(WeChatArticleActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
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
            sendChaptersRequestAndParseJSON("https://wanandroid.com/wxarticle/chapters/json");
        } else {
            Toast.makeText(WeChatArticleActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
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
                        String[] result = new String[7];
                        //0title  1author  2shareUser  3link  4niceDate  5superChapterName  6chapterName
                        result[0] = jsonObject_2.getString("title");
                        result[1] = jsonObject_2.getString("author");
                        result[2] = jsonObject_2.getString("shareUser");
                        result[3] = jsonObject_2.getString("link");
                        result[4] = jsonObject_2.getString("niceDate");
                        result[5] = jsonObject_2.getString("superChapterName");
                        result[6] = jsonObject_2.getString("chapterName");

                        addData(result[0], result[1], result[2], result[3], result[4], result[5], result[6],
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
                        Toast.makeText(WeChatArticleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void addData(final String title, final String author, final String shareUser, final String link,
                         final String niceDate, final String superChapterName, final String chapterName,
                         final int id, final int addPosition, final int scrollToPos, final int finish) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                articleList.add(addPosition, new ArticleItem(title, author, shareUser, link, niceDate, superChapterName
                        , chapterName, 0, id));

                adapter.notifyItemInserted(addPosition);
                recyclerView.scrollToPosition(scrollToPos);
                if (finish == 1) isFresh = 0;
            }
        });
    }

    private void sendChaptersRequestAndParseJSON(String sUrl) {
        HttpUtil.setGetRequest(sUrl, new HttpCallbackListener() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject_2 = jsonArray.getJSONObject(i);
                        addChaptersData(jsonObject_2.getInt("id"),
                                jsonObject_2.getString("name"), i);
                        if (i == 0) {
                            selectedAuthorId = jsonObject_2.getInt("id");
                            sendRequestAndParseJSON("https://wanandroid.com/wxarticle/list/" +
                                    selectedAuthorId + "/" + (refreshCount++) + "/json", 0, 0);
                        }
                    }
                    invalidateOptionsMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeChatArticleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void addChaptersData(int id, String name, int addPosition) {
        chapterOrder.add(addPosition, name);
        chapterMap.put(name, id);
    }
}
