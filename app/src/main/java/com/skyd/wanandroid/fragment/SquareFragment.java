package com.skyd.wanandroid.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Html;
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
import com.skyd.wanandroid.activity.CollectActivity;
import com.skyd.wanandroid.activity.CollectWebsiteActivity;
import com.skyd.wanandroid.activity.FriendWebsiteActivity;
import com.skyd.wanandroid.activity.NaviActivity;
import com.skyd.wanandroid.activity.ProjectListActivity;
import com.skyd.wanandroid.activity.SearchActivity;
import com.skyd.wanandroid.activity.SquareUsersShareArticlesActivity;
import com.skyd.wanandroid.adapter.ArticleAdapter;
import com.skyd.wanandroid.item.ArticleItem;
import com.skyd.wanandroid.item.FlowLayoutItem;
import com.skyd.wanandroid.tool.ConvertStreamToString;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;
import com.skyd.wanandroid.view.FlowLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SquareFragment extends Fragment {
    private View view;
    private List<ArticleItem> squareArticleList = new ArrayList<>();
    private RecyclerView recyclerView;
    private int refreshCount = 0, isFresh = 0;
    private ArticleAdapter adapter = new ArticleAdapter(squareArticleList);
    private Toolbar toolbar;
    private SwipeRefreshLayout swiperefreshlayout;

    public SquareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_square, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_myShareArticles:
                if (GlobalData.isSignIn) {
                    Intent myShareArticlesIntent = new Intent(getActivity(), SquareUsersShareArticlesActivity.class);
                    myShareArticlesIntent.putExtra("type", 1);
                    myShareArticlesIntent.putExtra("username", GlobalData.username);
                    startActivity(myShareArticlesIntent);
                } else {
                    Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.it_shareArticle:
                alertEdit();
                break;
            default:
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_square, container, false);

        toolbar = view.findViewById(R.id.tb_square);
        recyclerView = view.findViewById(R.id.rv_square);
        swiperefreshlayout = view.findViewById(R.id.swipe_refresh_layout_square);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        toolbar.inflateMenu(R.menu.toolbar_square);       //加载一下菜单

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        swiperefreshlayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swiperefreshlayout.setColorSchemeResources(android.R.color.holo_orange_light,
                android.R.color.holo_purple);
        swiperefreshlayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (HttpUtil.isOnlineByPing("www.baidu.com")) {      //通过ping百度判断网络是否可用
                            squareArticleList.clear();
                            refreshCount = 0;
                            adapter.notifyDataSetChanged();
                            sendRequestAndParseJSON("https://wanandroid.com/user_article/list/" + (refreshCount++) + "/json", 0, 0);
                            swiperefreshlayout.setRefreshing(false);
                        } else {
                            Toast.makeText(getActivity(), "无法连接网络", Toast.LENGTH_LONG).show();
                            swiperefreshlayout.setRefreshing(false);
                        }
                    }
                });
            }
        });

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
                        sendRequestAndParseJSON("https://wanandroid.com/user_article/list/" + (refreshCount++) + "/json", adapter.getItemCount(), adapter.getItemCount());
                    } else {
                        Toast.makeText(getContext(), "无法连接网络", Toast.LENGTH_LONG).show();
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

        if (HttpUtil.isOnlineByPing("www.baidu.com")) {      //通过ping百度判断网络是否可用
            sendRequestAndParseJSON("https://wanandroid.com/user_article/list/" + (refreshCount++) + "/json", 0, 0);
        } else {
            Toast.makeText(getActivity(), "无法连接网络", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void sendRequestAndParseJSON(String sUrl, final int scrollToPos, final int addItemFrom) {
        HttpUtil.setGetRequest(sUrl, new HttpCallbackListener() {
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
                        result[2] = jsonObject_2.getString("shareUser");
                        result[3] = jsonObject_2.getString("link");
                        result[4] = jsonObject_2.getString("niceDate");
                        result[5] = jsonObject_2.getString("superChapterName");
                        result[6] = jsonObject_2.getString("chapterName");
                        int id = jsonObject_2.getInt("id");
                        int userId = jsonObject_2.getInt("userId");

                        addData(result[0], result[1], result[2], result[3], result[4]
                                , result[5], result[6], id, userId,
                                i + addItemFrom, scrollToPos, i + 1 == jsonArray.length() ? 1 : 0);
                    }
                    GlobalData.initComplete[2] = 1;     //按照顺序，最后加载的
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    private void addData(final String title, final String author, final String shareUser, final String link,
                         final String niceDate, final String superChapterName, final String chapterName,
                         final int id, final int userId, final int addPosition, final int scrollToPos,
                         final int finish) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                squareArticleList.add(addPosition, new ArticleItem(title, author, shareUser, link, niceDate, superChapterName
                        , chapterName, 0, id));

                squareArticleList.get(addPosition).setSquare(true);     //是“广场”的文章
                squareArticleList.get(addPosition).setUserId(userId);     //添加用户id数据
                adapter.notifyItemInserted(addPosition);
                recyclerView.scrollToPosition(scrollToPos);
                if (finish == 1) isFresh = 0;
            }
        });
    }

    //添加
    private void sendAddRequest(String sUrl, String data) {
        HttpUtil.setPostRequest(sUrl, data, new HttpCallbackListener() {
            @Override
            public void onComplete(String response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "分享成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    //添加
    public void alertEdit() {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View textEntryView = factory.inflate(R.layout.alert_editor_collect_website, null);
        final EditText editText1 = textEntryView.findViewById(R.id.editText1);
        final EditText editText2 = textEntryView.findViewById(R.id.editText2);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(getActivity());
        ad1.setTitle("分享文章：");
        ad1.setIcon(R.drawable.info);
        ad1.setView(textEntryView);
        ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String text1, text2;
                text1 = editText1.getText().toString();
                text2 = editText2.getText().toString();
                if (!text1.equals("") && !text2.equals("")) {
                    sendAddRequest("https://www.wanandroid.com/lg/user_article/add/json",
                            ("title=" + text1 + "&link=" + text2));
                }
            }
        });
        ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        ad1.show();// 显示对话框
    }
}
