package com.skyd.wanandroid.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.skyd.wanandroid.GlobalData;
import com.skyd.wanandroid.R;
import com.skyd.wanandroid.activity.ProjectListActivity;
import com.skyd.wanandroid.item.FlowLayoutItem;
import com.skyd.wanandroid.tool.ConvertStreamToString;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;
import com.skyd.wanandroid.view.FlowLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProjectFragment extends Fragment {
    private View view;
    private static final int FRAGMENT_COUNT = 2;
    private List<Fragment> fragmentList = new ArrayList<>();
    TabLayout tabLayout;
    ViewPager viewPager;

    public ProjectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_project, container, false);

        fragmentList.add(new NewProjectFragment());
        fragmentList.add(new ProjectClassifyFragment());

        tabLayout = view.findViewById(R.id.tl_project);
        viewPager = view.findViewById(R.id.vp_project);
        FragmentAdapter fragmentAdapter = new FragmentAdapter((getActivity()).getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    public class FragmentAdapter extends FragmentPagerAdapter {
        public FragmentAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return FRAGMENT_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ? "最新项目" : "项目分类";
        }
    }

}
