package com.nahuo.kdb.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class ShopCartModel implements Serializable {

    private static final long serialVersionUID = -2492773240079820019L;

    private boolean isSelect;
    @Expose
    private String Color;
    @Expose
    private String Size;
    @Expose
    private int Qty;
    @Expose
    private double Price=0;
    @Expose
    private int ItemID;
    @Expose
    private int ID;
    @Expose
    private String Name;
    @Expose
    private String Cover;

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int itemID) {
        ItemID = itemID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCover() {
        return Cover;
    }

    public void setCover(String cover) {
        Cover = cover;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
