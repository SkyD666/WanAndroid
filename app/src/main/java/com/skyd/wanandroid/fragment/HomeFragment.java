package com.skyd.wanandroid.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.ProxyInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skyd.wanandroid.GlobalData;
import com.skyd.wanandroid.R;
import com.skyd.wanandroid.activity.FriendWebsiteActivity;
import com.skyd.wanandroid.activity.NaviActivity;
import com.skyd.wanandroid.activity.ProjectListActivity;
import com.skyd.wanandroid.activity.SearchActivity;
import com.skyd.wanandroid.activity.WeChatArticleActivity;
import com.skyd.wanandroid.adapter.ArticleAdapter;
import com.skyd.wanandroid.adapter.ViewPagerAdapter;
import com.skyd.wanandroid.item.ArticleItem;
import com.skyd.wanandroid.item.BannerItem;
import com.skyd.wanandroid.tool.ConvertStreamToString;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private Handler handler = new Handler();
    private List<ArticleItem> articleList = new ArrayList<>();
    private ArrayList<BannerItem> bannerItemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiperefreshlayout;
    //private int recyclerViewFirstCompletelyItem = -1;      //是否滑动到顶
    private int refreshCount = 0, isFresh = 0;
    private int bannerItemCount = 0;
    private int currentPosition = 1;        //banner当前位置
    private int previousPosition = 0;        //banner当前位置
    private ViewGroup bannerPoints;
    private ArticleAdapter adapter = new ArticleAdapter(articleList);
    private ViewPagerAdapter bannerAdapter = new ViewPagerAdapter(bannerItemList);
    private Toolbar toolbar;
    private ViewPager banner;
    private int topArticleCount;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_bannerController:
                if (banner.getVisibility() == View.GONE) {
                    //ObjectAnimator.ofFloat(swiperefreshlayout, "translationY",
                    //       0, 0).setDuration(500).start();//设置Y轴移动200像素
                    banner.setVisibility(View.VISIBLE);
                    bannerPoints.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams linearParams = swiperefreshlayout.getLayoutParams(); //取控件textView当前的布局参数
                    linearParams.height = (swiperefreshlayout.getHeight() - banner.getHeight());// 控件的高
                    swiperefreshlayout.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                    item.setIcon(R.drawable.banner_controller_1);
                    item.setTitle("收起轮播");
                } else if (banner.getVisibility() == View.VISIBLE) {
                    banner.setVisibility(View.GONE);
                    bannerPoints.setVisibility(View.GONE);
                    //ObjectAnimator.ofFloat(swiperefreshlayout, "translationY",
                    //        0, -banner.getHeight()).setDuration(500).start();//设置Y轴移动xx像素
                    ViewGroup.LayoutParams linearParams = swiperefreshlayout.getLayoutParams(); //取控件textView当前的布局参数
                    linearParams.height = (swiperefreshlayout.getHeight() + banner.getHeight());// 控件的高
                    swiperefreshlayout.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                    item.setIcon(R.drawable.banner_controller_2);
                    item.setTitle("显示轮播");
                }
                break;
            case R.id.it_search:
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
                break;
            case R.id.it_friendWebsite:
                Intent friendWebsiteIntent = new Intent(getActivity(), FriendWebsiteActivity.class);
                startActivity(friendWebsiteIntent);
                break;
            case R.id.it_navi:
                Intent naviIntent = new Intent(getActivity(), NaviActivity.class);
                startActivity(naviIntent);
                break;
            case R.id.it_weChatArticle:
                Intent weChatArticleIntent = new Intent(getActivity(), WeChatArticleActivity.class);
                startActivity(weChatArticleIntent);
                break;
            case R.id.it_questionAnswer:
                Intent questionAnswerIntent = new Intent(getActivity(), ProjectListActivity.class);
                questionAnswerIntent.putExtra("hasImage", 3);
                questionAnswerIntent.putExtra("name", "问答");
                startActivity(questionAnswerIntent);
                break;
            default:
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        toolbar = view.findViewById(R.id.tb_home);
        banner = view.findViewById(R.id.vp_banner);
        bannerPoints = view.findViewById(R.id.ll_container);
        swiperefreshlayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.rv_article);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        toolbar.inflateMenu(R.menu.toolbar_home);       //加载一下菜单

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //到下面设置了 banner.setAdapter(bannerAdapter);

        // 设置viewPager的监听事件
        banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            // 滑动状态改变的方法 state :draaging 拖拽 idle 静止 settling 惯性过程
            @Override
            public void onPageScrollStateChanged(int state) {
                //如果是静止状态,将当前页进行替换
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (banner.getCurrentItem() == 0) {
                        banner.setCurrentItem(bannerAdapter.getCount() - 2, false);
                    } else if (banner.getCurrentItem() == bannerAdapter.getCount() - 1) {
                        banner.setCurrentItem(1, false);
                    }
                    //banner.setCurrentItem(currentPosition, false);
                }
            }

            // 滑动过程中的方法 position 索引值
            // positionOffset 0-1
            // positionOffsetPixels 偏移像素值
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            // 选中某一页的监听
            @Override
            public void onPageSelected(int position) {
                previousPosition = currentPosition;
                if (position == bannerItemList.size() - 1) {
                    // 设置当前值为1
                    currentPosition = 1;
                    bannerPoints.getChildAt(previousPosition - 1).setBackgroundResource(R.drawable.point_white);
                    bannerPoints.getChildAt(0).setBackgroundResource(R.drawable.point_blue);
                } else if (position == 0) {
                    // 如果索引值为0了,就设置索引值为倒数第二个
                    currentPosition = bannerItemList.size() - 2;
                    bannerPoints.getChildAt(previousPosition - 1).setBackgroundResource(R.drawable.point_white);
                    bannerPoints.getChildAt(bannerItemList.size() - 3).setBackgroundResource(R.drawable.point_blue);
                } else {
                    currentPosition = position;
                    bannerPoints.getChildAt(previousPosition - 1).setBackgroundResource(R.drawable.point_white);
                    bannerPoints.getChildAt(position - 1).setBackgroundResource(R.drawable.point_blue);
                }
            }
        });

        //设置刷新时动画的颜色
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
                        if (HttpUtil.isOnlineByPing("www.baidu.com") && isFresh == 0) {      //通过ping百度判断网络是否可用
                            /*for (int i = 0; i < topArticleCount; i++) {
                                articleList.remove(0);  //每次刷新时先删除置顶，在重新获取
                            }*/
                            isFresh = 1;
                            articleList.clear();
                            refreshCount = 0;
                            topArticleCount = 0;
                            adapter.notifyDataSetChanged();
                            //置顶文章
                            sendRequestAndParseJSON("https://www.wanandroid.com/article/top/json", 1, 0, 0);
                            //非置顶文章
                            //sendRequestAndParseJSON("https://www.wanandroid.com/article/list/" + (refreshCount++) + "/json", 0, topArticleCount, topArticleCount);
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
                        sendRequestAndParseJSON("https://www.wanandroid.com/article/list/" + (refreshCount++) + "/json", 0, adapter.getItemCount(), adapter.getItemCount());
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
            //置顶文章
            sendRequestAndParseJSON("https://www.wanandroid.com/article/top/json", 1, 0, 0);
            swiperefreshlayout.setRefreshing(false);
            //banner
            sendRequestAndParseJSON("https://www.wanandroid.com/banner/json", -1, 0, 0);
        } else {
            Toast.makeText(getActivity(), "无法连接网络", Toast.LENGTH_LONG).show();
            swiperefreshlayout.setRefreshing(false);
        }
        return view;
    }

    private void sendRequestAndParseJSON(final String sUrl, final int isTop, final int scrollToPos, final int addItemFrom) {        //isTop为-1时是banner
        HttpUtil.setGetRequest(sUrl, new HttpCallbackListener() {
            @Override
            public void onComplete(String response) {
                if (isTop == 1) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                        int addCount = jsonArray.length();
                        topArticleCount = addCount;
                        for (int i = 0; i < addCount; i++) {
                            JSONObject jsonObject_1 = jsonArray.getJSONObject(i);
                            String[] result = new String[7];
                            //0title  1author  2shareUser  3link  4niceDate  5superChapterName  6chapterName
                            result[0] = jsonObject_1.getString("title");
                            result[1] = jsonObject_1.getString("author");
                            result[2] = jsonObject_1.getString("shareUser");
                            result[3] = jsonObject_1.getString("link");
                            result[4] = jsonObject_1.getString("niceDate");
                            result[5] = jsonObject_1.getString("superChapterName");
                            result[6] = jsonObject_1.getString("chapterName");
                            int id = jsonObject_1.getInt("id");
                            Log.d("---", topArticleCount + "");
                            showResponse(result, id, 1, scrollToPos, i + addItemFrom,
                                    i + 1 == addCount ? 1 : 0);
                        }
                        //非置顶文章
                        sendRequestAndParseJSON("https://www.wanandroid.com/article/list/" + (refreshCount++) + "/json", 0, topArticleCount, topArticleCount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (isTop == 0) {
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
                            int id = jsonObject_2.getInt("id");

                            showResponse(result, id, 0, scrollToPos, i + addItemFrom,
                                    i + 1 == addCount ? 1 : 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (isTop == -1) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                        String[] result_0 = new String[3];
                        bannerItemCount = jsonArray.length();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject_2 = jsonArray.getJSONObject(i);
                            String[] result = new String[3];
                            //0title  1author  2shareUser  3link  4niceDate  5superChapterName  6chapterName
                            result[0] = jsonObject_2.getString("url");
                            result[1] = jsonObject_2.getString("imagePath");
                            result[2] = jsonObject_2.getString("title");
                            if (i == 0) {
                                result_0[0] = jsonObject_2.getString("url");
                                result_0[1] = jsonObject_2.getString("imagePath");
                                result_0[2] = jsonObject_2.getString("title");
                            }
                            if (i == (jsonArray.length() - 1)) {
                                addBannerItem(result, i);
                                //Log.d("---", i + "i");
                                addBannerItem(result_0, i + 1);
                                //Log.d("---", i + "i");
                                addBannerItem(result, 0);
                                //Log.d("---", i + "i");
                            } else {
                                addBannerItem(result, i);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    View view = new View(getContext());
                                    view.setBackgroundResource(R.drawable.point_white);
                                    //有多少张图就放置几个点
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(15, 15);
                                    layoutParams.leftMargin = 30;
                                    bannerPoints.addView(view, layoutParams);
                                    GlobalData.initComplete[0] = 1;     //按照顺序，banner是最后加载的
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //自动滑动banner
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            banner.setCurrentItem(banner.getCurrentItem() + 1);
            handler.postDelayed(this, 5000);
        }
    };

    private void addBannerItem(final String[] response, final int pos) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bannerItemList.add(pos, new BannerItem(response[0], response[1], response[2]));
                //bannerAdapter.notifyDataSetChanged();

                if (bannerItemCount + 2 == bannerItemList.size()) {
                    banner.setOffscreenPageLimit(bannerItemList.size());
                    banner.setAdapter(bannerAdapter);
                    banner.setCurrentItem(1); //选择第"2"个
                    if (bannerItemCount >= 2) handler.postDelayed(runnable, 5000);
                }
            }
        });
    }

    private void showResponse(final String[] response, final int id, final int isTop,
                              final int scrollToPos, final int addPosition, final int finish) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addData(response[0], response[1], response[2], response[3], response[4]
                        , response[5], response[6], id, isTop, addPosition, scrollToPos, finish);
            }
        });
    }

    private void addData(String title, String author, String shareUser, String link,
                         String niceDate, String superChapterName, String chapterName, int id,
                         int isTop, final int addPosition, final int scrollToPos, int finish) {
        articleList.add(addPosition, new ArticleItem(title, author, shareUser, link, niceDate, superChapterName
                , chapterName, isTop, id));

        adapter.notifyItemInserted(addPosition);
        recyclerView.scrollToPosition(scrollToPos);

        if (isTop == 0 && finish == 1) isFresh = 0;
    }
}
