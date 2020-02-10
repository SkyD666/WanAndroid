package com.skyd.wanandroid.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.skyd.wanandroid.R;
import com.skyd.wanandroid.item.MyCoinListItem;

import java.util.List;

public class MyCoinListAdapter extends RecyclerView.Adapter<MyCoinListAdapter.ViewHolder> {
    private List<MyCoinListItem> mMyCoinList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View myCoinListItemView;
        TextView tvDesc;

        public ViewHolder(View view) {
            super(view);
            myCoinListItemView = view;
            tvDesc = view.findViewById(R.id.tv_desc);
        }
    }

    public MyCoinListAdapter(List<MyCoinListItem> myCoinList) {
        mMyCoinList = myCoinList;
    }

    @Override
    public MyCoinListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_coin_list_item, parent, false);
        final MyCoinListAdapter.ViewHolder holder = new MyCoinListAdapter.ViewHolder(view);
        holder.myCoinListItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyCoinListItem myCoinListItem = mMyCoinList.get(position);
        holder.tvDesc.setText(myCoinListItem.getDesc());
    }

    @Override
    public int getItemCount() {
        return mMyCoinList.size();
    }
}
