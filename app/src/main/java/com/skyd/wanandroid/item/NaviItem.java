package com.skyd.wanandroid.item;

import java.util.List;

public class NaviItem {
    private List<FlowLayoutItem> flowLayoutItem;
    private String itemName;

    public List<FlowLayoutItem> getFlowLayoutItem() {
        return flowLayoutItem;
    }

    public String getItemName() {
        return itemName;
    }

    public NaviItem(List<FlowLayoutItem> flowLayoutItem, String itemName) {
        this.flowLayoutItem = flowLayoutItem;
        this.itemName = itemName;
    }
}
