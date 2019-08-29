package com.nahuo.kdb;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jame on 2017/5/16.
 */

public class ReservationBean {

    /**
     * BookTime : 2017-05-17 15:57:00
     * Code : 17051610097924308
     * ShopName : 旺林服饰
     * Mobile : 13800138000
     * ItemName : yd.新5FA568  5697【MANDALA】10.11【欧美】气质复古百搭连衣裙
     * ItemCover : upyun:item-img:/211329/item/1476160516.jpg
     * itemPrice : 89.25
     * Color : 白色
     * Size : S
     * Address : 荔湾区杨巷路淘沙氹1号
     */
    @SerializedName("LinkMan")
    @Expose
    String LinkMan;
    @SerializedName("LinkMobile")
    @Expose
    String LinkMobile;

    public String getLinkMan() {
        return LinkMan;
    }

    public void setLinkMan(String linkMan) {
        LinkMan = linkMan;
    }

    public String getLinkMobile() {
        return LinkMobile;
    }

    public void setLinkMobile(String linkMobile) {
        LinkMobile = linkMobile;
    }

    @SerializedName("CreateTime")
    @Expose
    String CreateTime;

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    @SerializedName("BookTime")
    @Expose
    private String BookTime;
    @SerializedName("Code")
    @Expose
    private String Code;
    @SerializedName("ShopName")
    @Expose
    private String ShopName;
    @SerializedName("Mobile")
    @Expose
    private String Mobile;
    @SerializedName("ItemName")
    @Expose
    private String ItemName;
    @SerializedName("ItemCover")
    @Expose
    private String ItemCover;
    @SerializedName("itemPrice")
    @Expose
    private String itemPrice;
    @SerializedName("Color")
    @Expose
    private String Color;
    @SerializedName("Size")
    @Expose
    private String Size;
    @SerializedName("Address")
    @Expose
    private String Address;

    public String getBookTime() {
        return BookTime;
    }

    public void setBookTime(String BookTime) {
        this.BookTime = BookTime;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String ShopName) {
        this.ShopName = ShopName;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String Mobile) {
        this.Mobile = Mobile;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String ItemName) {
        this.ItemName = ItemName;
    }

    public String getItemCover() {
        return ItemCover;
    }

    public void setItemCover(String ItemCover) {
        this.ItemCover = ItemCover;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String Color) {
        this.Color = Color;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String Size) {
        this.Size = Size;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }
}
