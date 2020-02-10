package com.skyd.wanandroid.item;

public class CoinRankItem {
    private String username;
    private int userId;
    private int rank;
    private int level;
    private int coinCount;

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
    }

    public int getRank() {
        return rank;
    }

    public int getLevel() {
        return level;
    }

    public int getCoinCount() {
        return coinCount;
    }

    public CoinRankItem(String username, int userId, int rank, int level, int coinCount) {
        this.username = username;
        this.userId = userId;
        this.rank = rank;
        this.level = level;
        this.coinCount = coinCount;
    }
}
