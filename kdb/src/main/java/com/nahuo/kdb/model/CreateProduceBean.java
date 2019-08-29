package com.nahuo.kdb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jame on 2018/11/23.
 */

public class CreateProduceBean implements Serializable{

    private static final long serialVersionUID = -4622438584606105521L;
    /**
     * ProductCount : 2
     * PayableAmount : 106.00
     * ProductAmount : 106.00
     * DiscountAmount : 0.00
     * VipDiscount : 8.8
     */
    @Expose
    @SerializedName("ProductCount")
    private int ProductCount;
    @Expose
    @SerializedName("PayableAmount")
    private String PayableAmount="0.00";
    @Expose
    @SerializedName("ProductAmount")
    private String ProductAmount="0.00";
    @Expose
    @SerializedName("DiscountAmount")
    private String DiscountAmount="0.00";
    @Expose
    @SerializedName("VipDiscount")
    private String VipDiscount="0.00";
    @Expose
    @SerializedName("Discount")
    private String Discount="0.00";

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public int getProductCount() {
        return ProductCount;
    }

    public void setProductCount(int ProductCount) {
        this.ProductCount = ProductCount;
    }

    public String getPayableAmount() {
        return PayableAmount;
    }

    public void setPayableAmount(String PayableAmount) {
        this.PayableAmount = PayableAmount;
    }

    public String getProductAmount() {
        return ProductAmount;
    }

    public void setProductAmount(String ProductAmount) {
        this.ProductAmount = ProductAmount;
    }

    public String getDiscountAmount() {
        return DiscountAmount;
    }

    public void setDiscountAmount(String DiscountAmount) {
        this.DiscountAmount = DiscountAmount;
    }

    public String getVipDiscount() {
        return VipDiscount;
    }

    public void setVipDiscount(String VipDiscount) {
        this.VipDiscount = VipDiscount;
    }
}
