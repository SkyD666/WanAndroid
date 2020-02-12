package com.skyd.wanandroid.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.skyd.wanandroid.GlobalData;
import com.skyd.wanandroid.R;
import com.skyd.wanandroid.activity.ProjectListActivity;
import com.skyd.wanandroid.adapter.NaviAdapter;
import com.skyd.wanandroid.item.FlowLayoutItem;
import com.skyd.wanandroid.item.NaviItem;
import com.skyd.wanandroid.tool.ConvertStreamToString;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;
import com.skyd.wanandroid.view.FlowLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TreeFragment extends Fragment {            //与navi导航结构一致，所以直接用Navi的东西
    private SearchView searchView;
    private View view;
    private List<NaviItem> naviList = new ArrayList<>();
    private Toolbar toolbar;
    private NaviAdapter adapter = new NaviAdapter(naviList);
    private RecyclerView recyclerView;

    public TreeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tree, container, false);

        toolbar = view.findViewById(R.id.tb_tree);
        recyclerView = view.findViewById(R.id.rv_tree);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        toolbar.inflateMenu(R.menu.toolbar_tree);       //加载一下菜单

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (HttpUtil.isOnlineByPing("www.baidu.com")) {
            sendRequestAndParseJSON("https://www.wanandroid.com/tree/json");
        } else {
            Toast.makeText(getActivity(), "无法连接网络", Toast.LENGTH_LONG).show();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //加载menu布局
        inflater.inflate(R.menu.toolbar_tree, menu);
        //得到SearchView对象，SearchView一些属性可以直接使用，比如：setSubmitButtonEnabled，setQueryHint等
        searchView = view.findViewById(R.id.sv_tree);
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.setQueryHint("按作者搜索文章");
        //如果想单独对SearchView定制，比如需要更换搜索图标等，可以通过一下代码实现。
        //searchView.setIconified(false);       //会导致键盘闪一下后消失
        searchView.setSubmitButtonEnabled(true);
        searchView.setFocusable(false);
        searchView.clearFocus();
        searchView.onActionViewCollapsed();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);         //输入法回车搜索
        //Log.d("---0", searchView.toString());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (HttpUtil.isOnlineByPing("www.baidu.com")) {
                    //Log.d("---0", "sdcdsv");
                    Intent intent = new Intent(view.getContext(), ProjectListActivity.class);
                    intent.putExtra("author", s);
                    intent.putExtra("name", "作者为" + s + "的文章");
                    intent.putExtra("hasImage", 2);
                    view.getContext().startActivity(intent);
                } else {
                    Toast.makeText(view.getContext(), "无法连接网络", Toast.LENGTH_LONG).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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
                            JSONArray jsonArray_0 = new JSONArray(jsonObject.getString("data"));
                            for (int i = 0; i < jsonArray_0.length(); i++) {
                                JSONObject jsonObject_2 = jsonArray_0.getJSONObject(i);
                                JSONArray jsonArray = new JSONArray(jsonObject_2.getString("children"));
                                final String itemName = jsonObject_2.getString("name");
                                final List<FlowLayoutItem> flowLayoutItemList = new ArrayList<>();
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject jsonObject_3 = jsonArray.getJSONObject(j);
                                    FlowLayoutItem flowLayoutItem = (FlowLayoutItem) LayoutInflater.from(getActivity()).inflate(R.layout.flowlayout_item, new FlowLayout(getActivity()), false);
                                    flowLayoutItem.setId(jsonObject_3.getInt("id"));
                                    flowLayoutItem.setText(jsonObject_3.getString("name"));
                                    //flowLayoutItem.setSurl(jsonObject_3.getString("link"));
                                    flowLayoutItemList.add(flowLayoutItem);
                                }
                                addData(itemName, flowLayoutItemList);
                            }
                            GlobalData.initComplete[3] = 1;     //按照顺序，最后加载的
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

    private void addData(String itemName, List<FlowLayoutItem> flowLayoutItemList) {

        for (int i = 0; i < flowLayoutItemList.size(); i++) {
            flowLayoutItemList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //必须把view强转成一个新控件,不然一直都是最后一个item
                    FlowLayoutItem fl = (FlowLayoutItem) view;
                    Intent intent = new Intent(view.getContext(), ProjectListActivity.class);
                    intent.putExtra("id", fl.getId());
                    intent.putExtra("name", fl.getText().toString());
                    intent.putExtra("hasImage", 0);
                    Toast.makeText(view.getContext(), fl.getText(), Toast.LENGTH_SHORT).show();
                    view.getContext().startActivity(intent);
                }
            });
        }

        naviList.add(new NaviItem(flowLayoutItemList, itemName));

        adapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);
    }
}
