package com.skyd.wanandroid.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skyd.wanandroid.GlobalData;
import com.skyd.wanandroid.R;
import com.skyd.wanandroid.activity.CoinActivity;
import com.skyd.wanandroid.activity.CollectActivity;
import com.skyd.wanandroid.activity.CollectWebsiteActivity;
import com.skyd.wanandroid.activity.LogInActivity;
import com.skyd.wanandroid.activity.MainActivity;
import com.skyd.wanandroid.activity.TODOActivity;
import com.skyd.wanandroid.tool.ConvertStreamToString;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;

import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserFragment extends Fragment {

    private Toolbar toolbar;
    private TextView tvUserName;
    private TextView tvSignOut;
    private LinearLayout llUserInfo;
    private LinearLayout llSignOut;
    private LinearLayout llCoin;
    private LinearLayout llCollect;
    private LinearLayout llCollectWebsite;
    private LinearLayout llTODO;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeUserInfo();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK){
                    GlobalData.getCollections();
                }
                break;
            default:
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        llUserInfo = view.findViewById(R.id.ll_userInfo);
        llSignOut = view.findViewById(R.id.ll_signOut);
        llTODO = view.findViewById(R.id.ll_todo);
        llCoin = view.findViewById(R.id.ll_coin);
        llCollect = view.findViewById(R.id.ll_collect);
        llCollectWebsite = view.findViewById(R.id.ll_collectWebsite);
        toolbar = view.findViewById(R.id.tb_user);
        tvUserName = view.findViewById(R.id.tv_userName);
        tvSignOut = view.findViewById(R.id.tv_signOut);

        llUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.isSignIn) {

                } else {
                    Intent intent = new Intent(getActivity(), LogInActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        });

        llTODO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.isSignIn) {
                    startActivity((new Intent(getActivity(), TODOActivity.class)));
                } else {
                    Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        llCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.isSignIn) {
                    startActivity((new Intent(getActivity(), CoinActivity.class)));
                } else {
                    Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        llCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.isSignIn) {
                    startActivity((new Intent(getActivity(), CollectActivity.class)));
                } else {
                    Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        llCollectWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.isSignIn) {
                    startActivity((new Intent(getActivity(), CollectWebsiteActivity.class)));
                } else {
                    Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        llSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.isSignIn) {
                    GlobalData.isSignIn = false;
                    GlobalData.username = null;
                    GlobalData.userEmail = null;
                    GlobalData.sessionId = null;
                    GlobalData.cookie = null;
                    GlobalData.collectId.clear();
                    SharedPreferences.Editor editor =
                            getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE).edit();
                    editor.putString("cookie", GlobalData.cookie);
                    editor.putString("username", GlobalData.username);
                    editor.putString("userEmail", GlobalData.userEmail);
                    editor.putString("sessionId", GlobalData.sessionId);
                    editor.apply();
                    sendSignOutRequest("https://www.wanandroid.com/user/logout/json");
                    UserFragment.this.onResume();
                    Toast.makeText(getActivity(), "已退出登录", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        GlobalData.initComplete[4] = 1;     //按照顺序，最后加载的

        return view;
    }

    private void resumeUserInfo() {
        if (GlobalData.isSignIn) {
            tvUserName.setText(GlobalData.username);
        } else {
            tvUserName.setText("点击登录");
        }
    }

    private void sendSignOutRequest(String sUrl) {
        HttpUtil.setGetRequest(sUrl, new HttpCallbackListener() {
            @Override
            public void onComplete(String response) {

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
