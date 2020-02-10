package com.skyd.wanandroid.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.skyd.wanandroid.GlobalData;
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

public class CollectActivity extends AppCompatActivity {
    private List<ArticleItem> articleList = new ArrayList<>();
    private RecyclerView recyclerView;
    private int refreshCount = 0, isFresh = 0;
    private ArticleAdapter adapter = new ArticleAdapter(articleList);
    private Toolbar toolbar;
    private int isFirst;

    public void refreshList() {
        if (HttpUtil.isOnlineByPing("www.baidu.com")) {
            refreshCount = 0;
            articleList.clear();
            adapter.notifyDataSetChanged();
            sendRequestAndParseJSON("https://www.wanandroid.com/lg/collect/list/" + (refreshCount++) + "/json", 0, 0);
        } else {
            Toast.makeText(CollectActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isFirst == 0) {
            isFirst = 1;
            return;
        }
        refreshList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.it_addOtherArticle:
                alertEdit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        toolbar = findViewById(R.id.tb_collect);
        recyclerView = findViewById(R.id.rv_collectArticle);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        toolbar.inflateMenu(R.menu.toolbar_collect);       //加载一下菜单

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
                        sendRequestAndParseJSON("https://www.wanandroid.com/article/query/" + (refreshCount++) + "/json", adapter.getItemCount(), adapter.getItemCount());
                    } else {
                        Toast.makeText(CollectActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
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
            sendRequestAndParseJSON("https://www.wanandroid.com/lg/collect/list/" + (refreshCount++) + "/json", 0, 0);
        } else {
            Toast.makeText(CollectActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
        }
    }

    private void sendRequestAndParseJSON(String Surl, final int scrollToPos, final int addItemFrom) {
        HttpUtil.setGetRequest(Surl, new HttpCallbackListener() {
            @Override
            public void onComplete(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject_1 = new JSONObject(jsonObject.getString("data"));
                    JSONArray jsonArray = new JSONArray(jsonObject_1.getString("datas"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject_2 = jsonArray.getJSONObject(i);
                        String[] result = new String[8];
                        //0title  1author  2shareUser  3link  4niceDate  5superChapterName  6chapterName
                        result[0] = jsonObject_2.getString("title");
                        result[1] = jsonObject_2.getString("author");
                        result[2] = "";
                        result[3] = jsonObject_2.getString("link");
                        result[4] = jsonObject_2.getString("niceDate");
                        result[5] = "";
                        result[6] = jsonObject_2.getString("chapterName");
                        result[7] = jsonObject_2.getString("envelopePic");

                        addData(result[0], result[1], result[2], result[3], result[4]
                                , result[5], result[6], result[7], jsonObject_2.getInt("originId"),
                                i + addItemFrom, scrollToPos, i + 1 == jsonArray.length() ? 1 : 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(final Exception e) {
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CollectActivity.this, "123", Toast.LENGTH_SHORT).show();
                    }
                });*/
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

    private void sendRequestAndParseJSON_2(String sUrl, String data) {
        HttpUtil.setPostRequest(sUrl, data, new HttpCallbackListener() {
            @Override
            public void onComplete(String response) {
                try {
                    //Log.d("---", response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("errorCode") == 0) {
                        JSONObject jsonObject_1 = new JSONObject(jsonObject.getString("data"));
                        GlobalData.collectId.add(jsonObject_1.getInt("id"));
                        refreshList();
                    } else {
                        final String errorMsg = jsonObject.getString("errorMsg");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CollectActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
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
                        Toast.makeText(CollectActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    //添加
    public void alertEdit() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.alert_editor_collect_other_article, null);
        final EditText editText1 = textEntryView.findViewById(R.id.editText1);
        final EditText editText2 = textEntryView.findViewById(R.id.editText2);
        final EditText editText3 = textEntryView.findViewById(R.id.editText3);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(CollectActivity.this);
        ad1.setTitle("添加：");
        ad1.setIcon(R.drawable.info);
        ad1.setView(textEntryView);
        ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String text1, text2, text3;
                text1 = editText1.getText().toString();
                text2 = editText2.getText().toString();
                text3 = editText3.getText().toString();
                if (!text1.equals("") && !text2.equals("")) {
                    sendRequestAndParseJSON_2("https://www.wanandroid.com/lg/collect/add/json",
                            "title=" + text1 + "&author=" + text2 + "&link=" + text3);
                }
            }
        });
        ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        ad1.show();// 显示对话框
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_collect, menu);
        return true;
    }
}
