package com.skyd.wanandroid.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etRepassword;
    private Button btnRegister;
    private int errorCode;
    private String errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.tb_register);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etRepassword = findViewById(R.id.et_repassword);
        btnRegister = findViewById(R.id.btn_register);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etUsername.getText().toString().equals("") ||
                        etPassword.getText().toString().equals("") ||
                        etRepassword.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "用户名或密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!HttpUtil.isOnlineByPing("www.baidu.com")) {
                    Toast.makeText(RegisterActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
                    return;
                }
                sendRequestWithHttpURLConnection("https://www.wanandroid.com/user/register",
                        etUsername.getText().toString(), etPassword.getText().toString(), etRepassword.getText().toString());
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

    private void sendRequestWithHttpURLConnection(final String surl, final String username,
                                                  final String password, final String repassword) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                errorCode = 0;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(surl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.write(("username=" + username + "&password=" +
                            password + "&repassword=" + repassword).getBytes());//这里要这么写，不能写成out.writeBytes("k=" + key);因为有中文，服务器收到的是乱码
                    connection.connect();
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

    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            errorCode = jsonObject.getInt("errorCode");
            errorMsg = jsonObject.getString("errorMsg");
            Log.d("?--", jsonObject.getString("errorCode"));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (errorCode != 0) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                        dialog.setTitle("注册失败");
                        dialog.setMessage(errorMsg + "\n错误码：" + errorCode);
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
