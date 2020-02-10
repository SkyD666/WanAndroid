package com.skyd.wanandroid.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.skyd.wanandroid.GlobalData;
import com.skyd.wanandroid.R;
import com.skyd.wanandroid.activity.ProjectListActivity;
import com.skyd.wanandroid.item.FlowLayoutItem;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;
import com.skyd.wanandroid.view.FlowLayout;

import org.json.JSONArray;
import org.json.JSONObject;


public class ProjectClassifyFragment extends Fragment {
    private View view;
    private FlowLayout flowLayout;
    private SwipeRefreshLayout swiperefreshlayout;

    public ProjectClassifyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_project_classify, container, false);

        swiperefreshlayout = view.findViewById(R.id.swipe_refresh_layout_project);
        flowLayout = view.findViewById(R.id.fl_project);
        //FlowLayout.MarginLayoutParams params = new FlowLayout.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.setMargins(10, 10, 10, 10);
        if (HttpUtil.isOnlineByPing("www.baidu.com")) {      //通过ping百度判断网络是否可用
            sendRequestAndParseJSON("https://www.wanandroid.com/project/tree/json");
        } else {
            Toast.makeText(getActivity(), "无法连接网络", Toast.LENGTH_LONG).show();
        }

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
                            flowLayout.removeAllViews();
                            sendRequestAndParseJSON("https://www.wanandroid.com/project/tree/json");
                            swiperefreshlayout.setRefreshing(false);
                        } else {
                            Toast.makeText(getActivity(), "无法连接网络", Toast.LENGTH_LONG).show();
                            swiperefreshlayout.setRefreshing(false);
                        }
                    }
                });
            }
        });

        return view;
    }

    private void sendRequestAndParseJSON(String sUrl) {
        HttpUtil.setGetRequest(sUrl, new HttpCallbackListener() {
            @Override
            public void onComplete(final String response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject_1 = jsonArray.getJSONObject(i);
                                int id = jsonObject_1.getInt("id");
                                String name = jsonObject_1.getString("name");
                                //加载TextView并设置名称，并设置名称
                                FlowLayoutItem tv = (FlowLayoutItem) LayoutInflater.from(view.getContext()).inflate(R.layout.flowlayout_item, flowLayout, false);
                                tv.setText(Html.fromHtml(name));
                                tv.setId(id);
                                //把TextView加入流式布局
                                tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //必须把view强转成一个新控件,不然一直都是最后一个item
                                        FlowLayoutItem fl= (FlowLayoutItem) view;
                                        Intent intent = new Intent(getContext(), ProjectListActivity.class);
                                        intent.putExtra("id", fl.getId());
                                        intent.putExtra("name", fl.getText().toString());
                                        Toast.makeText(getContext(), fl.getText(), Toast.LENGTH_SHORT).show();
                                        getContext().startActivity(intent);
                                    }
                                });
                                flowLayout.addView(tv);
                            }
                            GlobalData.initComplete[1] = 1;     //按照顺序，最后加载的
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }
}
