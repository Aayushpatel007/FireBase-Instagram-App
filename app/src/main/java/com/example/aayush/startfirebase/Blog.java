package com.example.aayush.startfirebase;

/**
 * Created by Aayush on 18-04-2017.
 */

public class Blog {
    String title,desc,image,price,username;

    public Blog(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Blog(String title, String desc, String image, String price, String username) {
        this.title = title;
        this.desc = desc;

        this.image = image;
        this.price = price;
    this.username=username;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAmount() {
        return price;
    }

    public void setAmount(String price) {
        this.price = price;
    }
}
