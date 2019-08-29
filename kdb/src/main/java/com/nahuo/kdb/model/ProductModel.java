package com.nahuo.kdb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductModel implements Serializable {

    private static final long serialVersionUID = -3718423961923385896L;
    @SerializedName("SourceID")
    @Expose
    public int AgentItemId;
    //    @Expose
//    private boolean isCheck;// 是否选中
    @Expose
    private String SKU;//

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    @Expose
    private String Color;// 颜色
    @Expose
    private String Size;// 尺码
    @Expose
    private int Qty;//库存
    @Expose
    private int BookQty;
    @Expose
    private int ShipQty;
    @Expose
    private int DHQty;
    @Expose
    private int BuyQty;
    @Expose
    private int NewKCQty;
    @Expose
    private int StoreQty;
    private int InventoryQty;
    /**
     * CanStockQty : 1
     * InStockQty : 4
     */
    @Expose
    @SerializedName("CanStockQty")
    private int CanStockQty;
    @Expose
    @SerializedName("InStockQty")
    private int InStockQty;

    public int getInventoryQty() {
        return InventoryQty;
    }

    public void setInventoryQty(int inventoryQty) {
        InventoryQty = inventoryQty;
    }

    public int getProfitLossQty() {
        return getInventoryQty()-getQty();
    }


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
        if (qty < 0) {
            qty = 0;
        }
        this.Qty = qty;
    }

    public int getBookQty() {
        return BookQty;
    }

    public void setBookQty(int bookQty) {
        if (bookQty < 0) {
            bookQty = 0;
        }
        BookQty = bookQty;
    }

    public int getShipQty() {
        return ShipQty;
    }

    public void setShipQty(int shipQty) {
        if (shipQty < 0) {
            shipQty = 0;
        }
        if (shipQty > getBookQty()) {
            shipQty = getBookQty();
        }
        ShipQty = shipQty;
    }

    public int getDHQty() {
        return DHQty;
    }

    public void setDHQty(int DHQty) {
        if (DHQty < 0) {
            DHQty = 0;
        }
        if (DHQty > getCanStockQty()) {
            DHQty = getCanStockQty();
        }
        this.DHQty = DHQty;
    }

    public int getBuyQty() {
        return BuyQty;
    }

    public void setBuyQty(int buyQty) {
        if (buyQty < 0) {
            buyQty = 0;
        }
        if (buyQty > getQty()) {
            buyQty = getQty();
        }
        BuyQty = buyQty;
    }

    public int getNewKCQty() {
        return NewKCQty;
    }

    public void setNewKCQty(int newKCQty) {
        if (newKCQty < 0) {
            newKCQty = 0;
        }
        NewKCQty = newKCQty;
    }

    public int getAgentItemId() {
        return AgentItemId;
    }

    public void setAgentItemId(int agentItemId) {
        AgentItemId = agentItemId;
    }

    public int getStoreQty() {
        return StoreQty;
    }

    public void setStoreQty(int storeQty) {
        StoreQty = storeQty;
    }

    public int getCanStockQty() {
        return CanStockQty;
    }

    public void setCanStockQty(int CanStockQty) {
        this.CanStockQty = CanStockQty;
    }

    public int getInStockQty() {
        return InStockQty;
    }

    public void setInStockQty(int InStockQty) {
        this.InStockQty = InStockQty;
    }
}
