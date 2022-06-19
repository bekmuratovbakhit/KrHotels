package com.example.krhotels.Model;

public class Review {
    String id;
    String name;
    String text;
    int rate;
    String dateTime;

    public Review(String id, String name, String text, int rate, String dateTime) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.rate = rate;
        this.dateTime = dateTime;
    }

    public Review(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
