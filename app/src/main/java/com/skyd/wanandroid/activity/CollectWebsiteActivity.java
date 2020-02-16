package com.skyd.wanandroid.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.skyd.wanandroid.GlobalData;
import com.skyd.wanandroid.R;
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

public class CollectWebsiteActivity extends AppCompatActivity {
    private int clickType;      //0是默认常规进入，1为编辑，2为删除
    private Toolbar toolbar;
    private FlowLayout flowLayout;
    private SwipeRefreshLayout swiperefreshlayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_collect_website, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.it_edit:
                if (clickType == 0) {
                    clickType = 1;
                    flowLayout.setBackgroundColor(flowLayout.getResources().getColor(R.color.DarkSeaGreen));
                    item.setTitle("完成“编辑”操作");
                } else if (clickType == 1) {
                    clickType = 0;
                    flowLayout.setBackgroundColor(Color.WHITE);
                    item.setTitle("编辑");
                } else if (clickType == 2) {
                    Toast.makeText(this, "请先进行“删除”操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.it_delete:
                if (clickType == 0) {
                    clickType = 2;
                    flowLayout.setBackgroundColor(flowLayout.getResources().getColor(R.color.Salmon));
                    item.setTitle("完成“删除操作”");
                } else if (clickType == 2) {
                    clickType = 0;
                    flowLayout.setBackgroundColor(Color.WHITE);
                    item.setTitle("删除");
                } else if (clickType == 1) {
                    Toast.makeText(this, "请先进行“编辑”操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.it_add:
                if (clickType == 0) {
                    alertEdit();
                } else if (clickType == 1 || clickType == 2) {
                    Toast.makeText(this, "请先完成其它操作", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_website);

        toolbar = findViewById(R.id.tb_collectWebsite);
        swiperefreshlayout = findViewById(R.id.swipe_refresh_layout_collectWebsite);
        flowLayout = findViewById(R.id.fl_collectWebsite);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        toolbar.inflateMenu(R.menu.toolbar_collect_website);       //加载一下菜单

        if (HttpUtil.isOnlineByPing("www.baidu.com")) {      //通过ping百度判断网络是否可用
            sendRequestAndParseJSONFresh("https://www.wanandroid.com/lg/collect/usertools/json");
        } else {
            Toast.makeText(CollectWebsiteActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
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
                            sendRequestAndParseJSONFresh("https://www.wanandroid.com/lg/collect/usertools/json");
                            swiperefreshlayout.setRefreshing(false);
                        } else {
                            Toast.makeText(CollectWebsiteActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
                            swiperefreshlayout.setRefreshing(false);
                        }
                    }
                });
            }
        });
    }

    //刷新
    private void sendRequestAndParseJSONFresh(final String sUrl) {
        HttpUtil.setGetRequest(sUrl, new HttpCallbackListener() {
            @Override
            public void onComplete(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject_1 = jsonArray.getJSONObject(i);
                                int id = jsonObject_1.getInt("id");
                                String name = jsonObject_1.getString("name");
                                String surl = jsonObject_1.getString("link");
                                //加载TextView并设置名称，并设置名称
                                FlowLayoutItem tv = (FlowLayoutItem) LayoutInflater.from(CollectWebsiteActivity.this).inflate(R.layout.flowlayout_item, flowLayout, false);
                                tv.setText(Html.fromHtml(name));
                                tv.setSurl(surl);
                                tv.setId(id);
                                //把TextView加入流式布局
                                tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //必须把view强转成一个新控件,不然一直都是最后一个item
                                        FlowLayoutItem fl = (FlowLayoutItem) view;
                                        if (clickType == 0) {
                                            Intent intent = new Intent(CollectWebsiteActivity.this, WebsiteActivity.class);
                                            intent.putExtra("id", -1);
                                            intent.putExtra("name", fl.getText().toString());
                                            intent.putExtra("surl", fl.getSurl());
                                            Toast.makeText(CollectWebsiteActivity.this, fl.getText(), Toast.LENGTH_SHORT).show();
                                            startActivity(intent);
                                        } else if (clickType == 1) {
                                            alertEdit(fl);
                                        } else if (clickType == 2) {
                                            sendRequest
                                                    ("https://www.wanandroid.com/lg/collect/deletetool/json", ("id=" + fl.getId()));
                                        }
                                    }
                                });
                                flowLayout.addView(tv);
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
                        Toast.makeText(CollectWebsiteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void sendRequest(final String sUrl, final String data) {
        HttpUtil.setPostRequest(sUrl, data, new HttpCallbackListener() {
            @Override
            public void onComplete(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        flowLayout.removeAllViews();
                        sendRequestAndParseJSONFresh("https://www.wanandroid.com/lg/collect/usertools/json");
                    }
                });
            }

            @Override
            public void onError(final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CollectWebsiteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //编辑
    public void alertEdit(final FlowLayoutItem fl) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.alert_editor_collect_website, null);
        final EditText editText1 = textEntryView.findViewById(R.id.editText1);
        final EditText editText2 = textEntryView.findViewById(R.id.editText2);
        editText1.setText(fl.getText().toString());
        editText2.setText(fl.getSurl());
        AlertDialog.Builder ad1 = new AlertDialog.Builder(CollectWebsiteActivity.this);
        ad1.setTitle("编辑：");
        ad1.setIcon(R.drawable.info);
        ad1.setView(textEntryView);
        ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String text1, text2;
                text1 = editText1.getText().toString();
                text2 = editText2.getText().toString();
                if (!text1.equals("") && !text2.equals("")) {
                    sendRequest("https://www.wanandroid.com/lg/collect/updatetool/json",
                            ("id=" + fl.getId() + "&name=" + text1 + "&link=" + text2));
                }
            }
        });
        ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        ad1.show();// 显示对话框
    }

    //添加
    public void alertEdit() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.alert_editor_collect_website, null);
        final EditText editText1 = textEntryView.findViewById(R.id.editText1);
        final EditText editText2 = textEntryView.findViewById(R.id.editText2);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(CollectWebsiteActivity.this);
        ad1.setTitle("添加：");
        ad1.setIcon(R.drawable.info);
        ad1.setView(textEntryView);
        ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String text1, text2;
                text1 = editText1.getText().toString();
                text2 = editText2.getText().toString();
                if (!text1.equals("") && !text2.equals("")) {
                    sendRequest("https://www.wanandroid.com/lg/collect/addtool/json",
                            ("name=" + text1 + "&link=" + text2));
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
