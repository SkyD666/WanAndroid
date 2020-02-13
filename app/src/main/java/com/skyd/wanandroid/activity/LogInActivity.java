package com.skyd.wanandroid.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skyd.wanandroid.GlobalData;
import com.skyd.wanandroid.R;
import com.skyd.wanandroid.tool.ConvertStreamToString;
import com.skyd.wanandroid.tool.HttpUtil;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

public class LogInActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvRegister;
    private ImageView ivLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        toolbar = findViewById(R.id.tb_logIn);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvRegister = findViewById(R.id.tv_register);
        ivLogIn = findViewById(R.id.iv_logIn);

        tvRegister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, RegisterActivity.class));
            }
        });

        ivLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etUsername.getText().toString().equals("") ||
                        etPassword.getText().toString().equals("")) {
                    Toast.makeText(LogInActivity.this, "用户名或密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!HttpUtil.isOnlineByPing("www.baidu.com")) {
                    Toast.makeText(LogInActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
                    return;
                }
                sendRequestWithHttpURLConnection("https://www.wanandroid.com/user/login",
                        etUsername.getText().toString(), etPassword.getText().toString());
                ivLogIn.setEnabled(false);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor =
                getSharedPreferences("loginData", MODE_PRIVATE).edit();
        editor.putString("cookie", GlobalData.cookie);
        editor.putString("username", GlobalData.username);
        editor.putString("userEmail", GlobalData.userEmail);
        editor.putString("sessionId", GlobalData.sessionId);
        editor.apply();
    }

    private void sendRequestWithHttpURLConnection(final String surl, final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(surl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.write(("username=" + username + "&password=" + password).getBytes());//这里要这么写，不能写成out.writeBytes("k=" + key);因为有中文，服务器收到的是乱码
                    connection.connect();
                    String temp;
                    for (int i = 1; (temp = connection.getHeaderFieldKey(i)) != null; i++) {
                        if (temp.equalsIgnoreCase("set-cookie")) {
                            GlobalData.cookie = connection.getHeaderField(i);
                            GlobalData.cookie = GlobalData.cookie.substring(0, GlobalData.cookie.indexOf(";"));
                            GlobalData.sessionId = GlobalData.sessionId + GlobalData.cookie + ";";
                        }
                    }
                    out.flush();
                    out.close();
                    InputStream in = connection.getInputStream();
                    parseJSONWithJSONObject(new ConvertStreamToString(in).getResult());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(final String jsonData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final int errorCode;
                    final String errorMsg;
                    JSONObject jsonObject = new JSONObject(jsonData);
                    errorCode = jsonObject.getInt("errorCode");
                    errorMsg = jsonObject.getString("errorMsg");
                    if (errorCode != 0) {
                        GlobalData.sessionId = null;
                        SharedPreferences.Editor editor =
                                getSharedPreferences("loginData", MODE_PRIVATE).edit();
                        editor.putString("cookie", GlobalData.cookie);
                        editor.putString("username", GlobalData.username);
                        editor.putString("userEmail", GlobalData.userEmail);
                        editor.putString("sessionId", GlobalData.sessionId);
                        editor.apply();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(LogInActivity.this);
                        dialog.setTitle("登录失败");
                        dialog.setMessage(errorMsg + "\n错误码：" + errorCode);
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();
                        ivLogIn.setEnabled(true);
                        return;
                    }
                    JSONObject jsonObject_1 = new JSONObject(jsonObject.getString("data"));
                    GlobalData.username = jsonObject_1.getString("username");
                    GlobalData.userEmail = jsonObject_1.getString("email");
                    SharedPreferences.Editor editor =
                            getSharedPreferences("loginData", MODE_PRIVATE).edit();
                    editor.putString("cookie", GlobalData.cookie);
                    //Log.d("-----1", GlobalData.cookie);
                    editor.putString("username", GlobalData.username);
                    editor.putString("userEmail", GlobalData.userEmail);
                    editor.putString("sessionId", GlobalData.sessionId);
                    editor.apply();
                    Toast.makeText(LogInActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    GlobalData.isSignIn = true;
                    Intent intent = new Intent();
                    //intent.putExtra("LoginSuccess", true);
                    setResult(RESULT_OK, intent);
                    //GlobalData.username = etUsername.getText().toString();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
