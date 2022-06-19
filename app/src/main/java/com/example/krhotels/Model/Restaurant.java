package com.example.krhotels.Model;

public class Restaurant {
    String name;
    int reviewCount;
    double rating;
    String desc;
    String address;
    String phone;
    String workhour;
    String image1;
    String image2;
    String image3;
    String image4;
    double latitude;
    double longitude;

    public Restaurant(){}

    public Restaurant(String name, int reviewCount, double rating, String desc, String address, String phone, String workhour, String image1, String image2, String image3, String image4, double latitude, double longitude) {
        this.name = name;
        this.reviewCount = reviewCount;
        this.rating = rating;
        this.desc = desc;
        this.address = address;
        this.phone = phone;
        this.workhour = workhour;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWorkhour() {
        return workhour;
    }

    public void setWorkhour(String workhour) {
        this.workhour = workhour;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
