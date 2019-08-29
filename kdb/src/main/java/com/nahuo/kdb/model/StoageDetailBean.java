package com.nahuo.kdb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jame on 2018/11/28.
 */

public class StoageDetailBean implements Serializable {
    private static final long serialVersionUID = 8090536453998819613L;

    public String getRetailPrice() {
        return RetailPrice;
    }

    public void setRetailPrice(String retailPrice) {
        RetailPrice = retailPrice;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    /**
     * SourceID : 1117939
     * Name : lilifeiyang内网测试20181015第一
     * Sku : 1117939【】
     * StockQty : 7
     * Cover : upyun:nahuo-img-server://129766/item/1539568261018.jpg
     * LastStockTime :
     */
    @Expose
    @SerializedName("RetailPrice")
    private String RetailPrice = "";
    @SerializedName("Code")
    @Expose
    private String Code = "";
    @SerializedName("Categroy")
    @Expose
    private String Categroy = "";

    public String getCategroy() {
        return Categroy;
    }

    public void setCategroy(String categroy) {
        Categroy = categroy;
    }

    @Expose
    @SerializedName("ColorSize")
    private ArrayList<ProductModel> ColorSize;

    public ArrayList<ProductModel> getColorSize() {
        return ColorSize;
    }

    public void setColorSize(ArrayList<ProductModel> colorSize) {
        ColorSize = colorSize;
    }

    @Expose
    @SerializedName("SourceID")
    private int SourceID;
    @Expose
    @SerializedName("Name")
    private String Name = "";
    @Expose
    @SerializedName("StockQty")
    private int StockQty;
    @Expose
    @SerializedName("Cover")
    private String Cover = "";
    @Expose
    @SerializedName("LastStockTime")
    private String LastStockTime = "";

    public int getSourceID() {
        return SourceID;
    }

    public void setSourceID(int SourceID) {
        this.SourceID = SourceID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getStockQty() {
        return StockQty;
    }

    public void setStockQty(int StockQty) {
        this.StockQty = StockQty;
    }

    public String getCover() {
        return Cover;
    }

    public void setCover(String Cover) {
        this.Cover = Cover;
    }

    public String getLastStockTime() {
        return LastStockTime;
    }

    public void setLastStockTime(String LastStockTime) {
        this.LastStockTime = LastStockTime;
    }
}
