package com.example.krhotels.Model;

import java.util.ArrayList;

public class Item {

    String name;
    String desc;


    String phone;
    String address;
    String workhour;
    ArrayList<ImageItem> list;


    public Item(String name, String desc, String phone, String address, String workhour, ArrayList<ImageItem> list) {
        this.name = name;
        this.desc = desc;
        this.phone = phone;
        this.address = address;
        this.workhour = workhour;
        this.list = list;
    }

    public Item(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArrayList<ImageItem> getList() {
        return list;
    }

    public void setList(ArrayList<ImageItem> list) {
        this.list = list;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWorkhour() {
        return workhour;
    }

    public void setWorkhour(String workhour) {
        this.workhour = workhour;
    }
}
