package com.skyd.wanandroid.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.skyd.wanandroid.R;
import com.skyd.wanandroid.activity.WebsiteActivity;
import com.skyd.wanandroid.item.FlowLayoutItem;
import com.skyd.wanandroid.item.NaviItem;
import com.skyd.wanandroid.view.FlowLayout;

import java.util.List;

public class NaviAdapter extends RecyclerView.Adapter<NaviAdapter.ViewHolder> {
    private List<NaviItem> mNaviItem;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View naviView;
        FlowLayout flowLayout;
        TextView tvname;

        public ViewHolder(View view) {
            super(view);
            naviView = view;
            tvname = view.findViewById(R.id.tv_navi);
            flowLayout = view.findViewById(R.id.fl_navi);
        }
    }

    public NaviAdapter(List<NaviItem> naviItem) {
        mNaviItem = naviItem;
    }

    @Override
    public NaviAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.navi_item, parent, false);
        final NaviAdapter.ViewHolder holder = new NaviAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NaviAdapter.ViewHolder holder, int position) {
        NaviItem naviItem = mNaviItem.get(position);
        holder.tvname.setText(naviItem.getItemName());
        List<FlowLayoutItem> flowLayoutItem;
        flowLayoutItem = naviItem.getFlowLayoutItem();
        holder.flowLayout.removeAllViews();
        for (int i = 0; i < flowLayoutItem.size(); i++) {
            if (flowLayoutItem.get(i).getParent() != null) {
                ((ViewGroup) flowLayoutItem.get(i).getParent()).removeView(flowLayoutItem.get(i));
            }

            holder.flowLayout.addView(flowLayoutItem.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return mNaviItem.size();
    }
}
