package com.skyd.wanandroid.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.skyd.wanandroid.R;
import com.skyd.wanandroid.activity.SearchResultActivity;
import com.skyd.wanandroid.item.SearchHotKeyItem;
import com.skyd.wanandroid.tool.HttpUtil;

import java.util.List;

public class SearchHotKeyAdapter extends RecyclerView.Adapter<SearchHotKeyAdapter.ViewHolder> {
    private List<SearchHotKeyItem> mHotKeyList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View searchHotKeyView;
        TextView tvname;

        public ViewHolder(View view) {
            super(view);
            searchHotKeyView = view;
            tvname = view.findViewById(R.id.tv_name);
        }
    }

    public SearchHotKeyAdapter(List<SearchHotKeyItem> hotKeyList) {
        mHotKeyList = hotKeyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_hotkey_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.searchHotKeyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HttpUtil.isOnlineByPing("www.baidu.com")) {
                    int position = holder.getAdapterPosition();
                    Intent intent = new Intent(v.getContext(), SearchResultActivity.class);
                    intent.putExtra("key", mHotKeyList.get(position).getName());
                    v.getContext().startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(), "无法连接网络", Toast.LENGTH_LONG).show();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SearchHotKeyItem searchHotKeyItem = mHotKeyList.get(position);
        holder.tvname.setText(searchHotKeyItem.getName());
    }

    @Override
    public int getItemCount() {
        return mHotKeyList.size();
    }
}
