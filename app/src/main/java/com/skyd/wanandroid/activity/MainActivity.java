package com.skyd.wanandroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.skyd.wanandroid.GlobalData;
import com.skyd.wanandroid.R;
import com.skyd.wanandroid.fragment.HomeFragment;
import com.skyd.wanandroid.fragment.ProjectFragment;
import com.skyd.wanandroid.fragment.SquareFragment;
import com.skyd.wanandroid.fragment.TreeFragment;
import com.skyd.wanandroid.fragment.UserFragment;

public class MainActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private HomeFragment homeFragment;
    private TreeFragment treeFragment;
    private ProjectFragment projectFragment;
    private UserFragment userFragment;
    private SquareFragment squareFragment;
    private RadioButton homeButton;
    private RadioButton treeButton;
    private RadioButton projectButton;
    private RadioButton squareButton;
    private RadioButton userButton;
    private long firstTime = 0;     // 双击退出记录第一次时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getSharedPreferences("loginData", MODE_PRIVATE);
        GlobalData.cookie = pref.getString("cookie", null);
        GlobalData.username = pref.getString("username", null);
        GlobalData.sessionId = pref.getString("sessionId", null);

        if (GlobalData.sessionId != null) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            GlobalData.isSignIn = true;
        } else {
            Toast.makeText(this, "未登录", Toast.LENGTH_SHORT).show();
        }

        GlobalData.getCollections();

        radioGroup = findViewById(R.id.rg_tabs);
        radioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
        homeButton = findViewById(R.id.tab_home);
        treeButton = findViewById(R.id.tab_tree);
        projectButton = findViewById(R.id.tab_project);
        squareButton = findViewById(R.id.tab_square);
        userButton = findViewById(R.id.tab_user);
        homeButton.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.home), null, null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        homeFragment = new HomeFragment();
        treeFragment = new TreeFragment();
        projectFragment = new ProjectFragment();
        squareFragment = new SquareFragment();
        userFragment = new UserFragment();
        transaction.add(R.id.fragment_container, homeFragment);
        transaction.add(R.id.fragment_container, treeFragment);
        transaction.add(R.id.fragment_container, projectFragment);
        transaction.add(R.id.fragment_container, squareFragment);
        transaction.add(R.id.fragment_container, userFragment);
        setHideAllFragment(transaction);
        transaction.show(homeFragment);
        transaction.commit();
        radioGroup.check(R.id.tab_home);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        GlobalData.initComplete[5] = 1;
    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            setHideAllFragment(transaction);
            setGreyAllRadioButton();
            switch (checkedId) {
                case R.id.tab_home:
                    homeButton.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.home), null, null);
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                        transaction.add(R.id.fragment_container, homeFragment);
                    } else {
                        transaction.show(homeFragment);
                    }
                    break;
                case R.id.tab_tree:
                    treeButton.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.tree), null, null);
                    if (treeFragment == null) {
                        treeFragment = new TreeFragment();
                        transaction.add(R.id.fragment_container, treeFragment);
                    } else {
                        transaction.show(treeFragment);
                    }
                    break;
                case R.id.tab_project:
                    projectButton.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.project), null, null);
                    if (projectFragment == null) {
                        projectFragment = new ProjectFragment();
                        transaction.add(R.id.fragment_container, projectFragment);
                    } else {
                        transaction.show(projectFragment);
                    }
                    break;
                case R.id.tab_square:
                    squareButton.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.square), null, null);
                    if (squareFragment == null) {
                        squareFragment = new SquareFragment();
                        transaction.add(R.id.fragment_container, squareFragment);
                    } else {
                        transaction.show(squareFragment);
                    }
                    break;
                case R.id.tab_user:
                    userButton.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.user_2), null, null);
                    if (userFragment == null) {
                        userFragment = new UserFragment();
                        transaction.add(R.id.fragment_container, userFragment);
                    } else {
                        transaction.show(userFragment);
                    }
                    break;
            }
            transaction.commit();
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void setGreyAllRadioButton() {
        homeButton.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.home_grey), null, null);
        treeButton.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.tree_grey), null, null);
        projectButton.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.project_grey), null, null);
        squareButton.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.square_grey), null, null);
        userButton.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.user_2_grey), null, null);
    }

    private void setHideAllFragment(FragmentTransaction transaction) {
        transaction.hide(homeFragment);
        transaction.hide(treeFragment);
        transaction.hide(projectFragment);
        transaction.hide(squareFragment);
        transaction.hide(userFragment);
    }


}
