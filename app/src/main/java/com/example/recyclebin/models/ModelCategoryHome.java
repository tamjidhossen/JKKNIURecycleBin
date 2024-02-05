package com.example.recyclebin.models;

public class ModelCategoryHome {
    /*---Variables---*/
    String category;
    int icon;
    /*---Constructor with all params---*/
    public ModelCategoryHome(String category, int icon) {
        this.category = category;
        this.icon = icon;
    }
    /*---Getter & Setters to get and set items to/from model of list---*/
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }
}
