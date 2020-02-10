package com.skyd.wanandroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.skyd.wanandroid.GlobalData;
import com.skyd.wanandroid.R;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;

public class WebsiteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);

        toolbar = findViewById(R.id.tb_website);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        WebView webView = findViewById(R.id.wv_web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(intent.getStringExtra("surl"));
        webView.setWebChromeClient(new WebChromeClient() {      //获取网页标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                toolbar.setTitle(title);
            }
        });
        //toolbar.setTitle(intent.getStringExtra("title"));     //已废弃
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        toolbar.inflateMenu(R.menu.toolbar_website);       //加载一下菜单
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_website, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (GlobalData.collectId.contains(id)) {
            menu.findItem(R.id.it_addCollection).setVisible(false);
            invalidateOptionsMenu();
        } else {
            menu.findItem(R.id.it_removeCollection).setVisible(false);
            invalidateOptionsMenu();
        }
        if (id == -1) {
            menu.findItem(R.id.it_addCollection).setVisible(false);
            menu.findItem(R.id.it_removeCollection).setVisible(false);
            invalidateOptionsMenu();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_addCollection:
                if (!GlobalData.isSignIn) {
                    Toast.makeText(this, "未登录", Toast.LENGTH_SHORT).show();
                    break;
                } else if (id != -1) {
                    sendRequest("https://www.wanandroid.com/lg/collect/"
                            + id + "/json");
                    toolbar.getMenu().findItem(R.id.it_addCollection).setVisible(false);
                    toolbar.getMenu().findItem(R.id.it_removeCollection).setVisible(true);
                    invalidateOptionsMenu();
                    GlobalData.collectId.add(id);
                    Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.it_removeCollection:
                if (!GlobalData.isSignIn) {
                    Toast.makeText(this, "未登录", Toast.LENGTH_SHORT).show();
                    break;
                } else if (id != -1) {
                    sendRequest("https://www.wanandroid.com/lg/uncollect_originId/"
                            + id + "/json");
                    toolbar.getMenu().findItem(R.id.it_removeCollection).setVisible(false);
                    toolbar.getMenu().findItem(R.id.it_addCollection).setVisible(true);
                    invalidateOptionsMenu();
                    GlobalData.collectId.remove((Integer) id);
                    Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:     //此处要写android. 不写则无法返回
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendRequest(final String sUrl) {
        HttpUtil.setPostRequest(sUrl, null, new HttpCallbackListener() {
            @Override
            public void onComplete(String response) {

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
