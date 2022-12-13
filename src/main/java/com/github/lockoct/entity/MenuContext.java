package com.github.lockoct.entity;

public class MenuContext {
    private Item itemInfo;
    private int fromPage;
    private int boxCount;

    public Item getItemInfo() {
        return itemInfo;
    }

    public void setItemInfo(Item itemInfo) {
        this.itemInfo = itemInfo;
    }

    public int getFromPage() {
        return fromPage;
    }

    public void setFromPage(int fromPage) {
        this.fromPage = fromPage;
    }

    public int getBoxCount() {
        return boxCount;
    }

    public void setBoxCount(int boxCount) {
        this.boxCount = boxCount;
    }
}
