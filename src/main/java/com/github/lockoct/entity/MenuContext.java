package com.github.lockoct.entity;

public class MenuContext {
    private Item itemInfo;
    private CollectArea areaInfo;
    private int boxCount;
    private int fromPage;
    private int areaChestCount;

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

    public CollectArea getAreaInfo() {
        return areaInfo;
    }

    public void setAreaInfo(CollectArea areaInfo) {
        this.areaInfo = areaInfo;
    }

    public int getAreaChestCount() {
        return areaChestCount;
    }

    public void setAreaChestCount(int areaChestCount) {
        this.areaChestCount = areaChestCount;
    }
}
