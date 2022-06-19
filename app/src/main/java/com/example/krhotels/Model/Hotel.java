package com.example.krhotels.Model;

public class Hotel {
    String name;
    int reviewCount;
    double rating;
    String address;
    String desc;
    String phone;
    String workhour;
    String image1;
    String image2;
    String image3;
    String image4;

    public Hotel (){}

    public Hotel(String name, int reviewCount, double rating, String address, String desc, String phone, String workhour, String image1, String image2, String image3, String image4) {
        this.name = name;
        this.reviewCount = reviewCount;
        this.rating = rating;
        this.address = address;
        this.desc = desc;
        this.phone = phone;
        this.workhour = workhour;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
