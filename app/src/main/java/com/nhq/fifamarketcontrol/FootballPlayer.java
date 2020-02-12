package com.nhq.fifamarketcontrol;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FootballPlayer {
    int id;
    String name;
    String postion;
    int ovr;
    Boolean isBuy;

    int price;
    Calendar date;

    public FootballPlayer(int id, String name, String postion, int ovr, Boolean isBuy, int price, Calendar date) {
        this.id = id;
        this.name = name;
        this.postion = postion;
        this.ovr = ovr;
        this.isBuy = isBuy;
        this.price = price;
        this.date = date;
    }

    public Calendar getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        String result = isBuy ? "Buy" : "Sell";
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(date.getTime());

        result += " | " + name + " | " + postion + " | " + ovr + " | " + price + " | " + strDate;
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostion() {
        return postion;
    }

    public void setPostion(String postion) {
        this.postion = postion;
    }

    public int getOvr() {
        return ovr;
    }

    public void setOvr(int ovr) {
        this.ovr = ovr;
    }

    public Boolean isBuy() {
        return isBuy;
    }

    public void setBuy(Boolean buy) {
        isBuy = buy;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
