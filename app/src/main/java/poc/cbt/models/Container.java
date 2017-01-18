package poc.cbt.models;

import java.util.List;

/**
 * Created by taro on 1/11/17.
 */

public class Container {

    String id;
    Enum type;
    String titleEn;
    String titleTh;
    int itemWidth;
    int itemHeight;

    List<Item> itemList;

    int numberOfItemsPerRow;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Enum getType() {
        return type;
    }

    public void setType(Enum type) {
        this.type = type;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleTh() {
        return titleTh;
    }

    public void setTitleTh(String titleTh) {
        this.titleTh = titleTh;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public int getNumberOfItemsPerRow() {
        return numberOfItemsPerRow;
    }

    public void setNumberOfItemsPerRow(int numberOfItemsPerRow) {
        this.numberOfItemsPerRow = numberOfItemsPerRow;
    }
}
