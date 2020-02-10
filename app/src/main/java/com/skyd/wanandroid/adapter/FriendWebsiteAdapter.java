package com.skyd.wanandroid.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.skyd.wanandroid.R;
import com.skyd.wanandroid.activity.WebsiteActivity;
import com.skyd.wanandroid.item.FriendWebsiteItem;
import com.skyd.wanandroid.tool.HttpUtil;

import java.util.List;

public class FriendWebsiteAdapter extends RecyclerView.Adapter<FriendWebsiteAdapter.ViewHolder> {
    private List<FriendWebsiteItem> mFriendWebsiteList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View friendWebsiteView;
        TextView tvname;

        public ViewHolder(View view) {
            super(view);
            friendWebsiteView = view;
            tvname = view.findViewById(R.id.tv_name);
        }
    }

    public FriendWebsiteAdapter(List<FriendWebsiteItem> friendWebsiteList) {
        mFriendWebsiteList = friendWebsiteList;
    }

    @Override
    public FriendWebsiteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_website_item, parent, false);
        final FriendWebsiteAdapter.ViewHolder holder = new FriendWebsiteAdapter.ViewHolder(view);
        holder.friendWebsiteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HttpUtil.isOnlineByPing("www.baidu.com")) {
                    int position = holder.getAdapterPosition();
                    Intent intent = new Intent(v.getContext(), WebsiteActivity.class);
                    intent.putExtra("surl", mFriendWebsiteList.get(position).getSurl());
                    v.getContext().startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(), "无法连接网络", Toast.LENGTH_LONG).show();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(FriendWebsiteAdapter.ViewHolder holder, int position) {
        FriendWebsiteItem friendWebsiteItem = mFriendWebsiteList.get(position);
        holder.tvname.setText(friendWebsiteItem.getIndex() + ". " + friendWebsiteItem.getName());
    }

    @Override
    public int getItemCount() {
        return mFriendWebsiteList.size();
    }
}
