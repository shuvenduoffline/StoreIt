package com.example.storeit;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Items {
    @ServerTimestamp
    Date scanat = new Date();
    int quantity = 0;
    String details;
    String item;
    long id;


    public void setId(int id) {
        this.id = id;
    }

    public Items() {
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDetails() {
        return details;
    }

    public String getItem() {
        return item;
    }

    public void setScanat(Date scanat) {
        this.scanat = scanat;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getId() {
        return id;
    }

    public Date getScanat() {
        return scanat;
    }



    public void setItem(String item) {
        this.item = item;
    }
}
