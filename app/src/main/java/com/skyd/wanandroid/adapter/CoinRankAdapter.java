package com.skyd.wanandroid.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.skyd.wanandroid.R;
import com.skyd.wanandroid.item.CoinRankItem;
import com.skyd.wanandroid.item.MyCoinListItem;

import java.util.List;

public class CoinRankAdapter extends RecyclerView.Adapter<CoinRankAdapter.ViewHolder> {
    private List<CoinRankItem> mCoinRankList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View coinRankItemView;
        TextView tvUsername;
        TextView tvUserId;
        TextView tvLevel;
        TextView tvRank;
        TextView tvCoinCount;

        public ViewHolder(View view) {
            super(view);
            coinRankItemView = view;
            tvUsername = view.findViewById(R.id.tv_coinRankUsername);
            tvUserId = view.findViewById(R.id.tv_coinRankId);
            tvLevel = view.findViewById(R.id.tv_coinRankLevel);
            tvRank = view.findViewById(R.id.tv_coinRank);
            tvCoinCount = view.findViewById(R.id.tv_coinRankCoinCount);
        }
    }

    public CoinRankAdapter(List<CoinRankItem> coinRankList) {
        mCoinRankList = coinRankList;
    }

    @Override
    public CoinRankAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coin_rank_item, parent, false);
        final CoinRankAdapter.ViewHolder holder = new CoinRankAdapter.ViewHolder(view);
        holder.coinRankItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(CoinRankAdapter.ViewHolder holder, int position) {
        CoinRankItem coinRankItem = mCoinRankList.get(position);
        if(coinRankItem.getRank() == 1){
            holder.tvRank.setTextColor(Color.RED);
        } else if(coinRankItem.getRank() == 2){
            holder.tvRank.setTextColor(holder.tvRank.getResources().getColor(R.color.colorOrange01));
        } else if(coinRankItem.getRank() == 3){
            holder.tvRank.setTextColor(holder.tvRank.getResources().getColor(R.color.colorBlue01));
        } else {
            holder.tvRank.setTextColor(holder.tvRank.getResources().getColor(R.color.colorGray04));
        }
        holder.tvUsername.setText(coinRankItem.getUsername());
        holder.tvUserId.setText("ID: " + coinRankItem.getUserId());
        holder.tvRank.setText(String.valueOf(coinRankItem.getRank()));
        holder.tvLevel.setText("等级: " + coinRankItem.getLevel());
        holder.tvCoinCount.setText("积分: " + coinRankItem.getCoinCount());
    }

    @Override
    public int getItemCount() {
        return mCoinRankList.size();
    }
}
