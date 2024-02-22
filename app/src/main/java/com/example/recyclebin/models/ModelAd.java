package com.example.recyclebin.models;

public class ModelAd {
    /*---Variables. spellings and case should be same as in firebase db---*/
    String id;
    String vid;
    String brand;
    String category;
    String condition;
    String address;
    String price;
    String title;
    String description;
    String status;
    String adOwnerName;
    long timestamp;
    double latitude;
    double longitude;
    boolean favorite;
    int favoriteCount;//+++++++++++++++++++++++++++

    public ModelAd() {

    }

    public ModelAd(String adOwnerName, String id, String vid, String brand, String category, String condition, String address, String price, String title, String description, String status, long timestamp, double latitude, double longitude, boolean favorite) {
        this.adOwnerName = adOwnerName;
        this.id = id;
        this.vid = vid;
        this.brand = brand;
        this.category = category;
        this.condition = condition;
        this.address = address;
        this.price = price;
        this.title = title;
        this.description = description;
        this.status = status;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.favorite = favorite;
    }

    public String getAdOwnerName() {return adOwnerName; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return vid;
    }

    public void setUid(String vid) {
        this.vid = vid;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public String getAddress() {
        return address;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setStatus (String status) {
        this.status = status;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this. timestamp = timestamp;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this. latitude = latitude;
    }
    public double getLongitude () {
        return longitude;
    }
    public void setLongitude (double longitude){
        this.longitude = longitude;
    }
    public boolean isFavorite() {
        return favorite;
    }
    public void setFavorite(boolean favorite){
            this.favorite = favorite;
    }
    public String getStatus() {
        return status;
    }
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    // Increment the favorite count by 1
    public void incrementFavoriteCount() {
        this.favoriteCount++;
    }

    // Decrement the favorite count by 1
    public void decrementFavoriteCount() {
        if (this.favoriteCount > 0) {
            this.favoriteCount--;
        }
    }

}