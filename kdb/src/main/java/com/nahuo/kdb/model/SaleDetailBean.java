package com.nahuo.kdb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jame on 2018/9/13.
 */

public class SaleDetailBean implements Serializable{
    private static final long serialVersionUID = 6267976614602283166L;
    @Expose
    @SerializedName("SellerUserID")
    private  int SellerUserID;
    /**
     * CreateUserName : 好韵来
     * CreateUserUserID : 339925
     */
    @Expose
    @SerializedName("CreateUserName")
    private String CreateUserName="";
    @Expose
    @SerializedName("CreateUserUserID")
    private int CreateUserUserID;

    public int getSellerUserID() {
        return SellerUserID;
    }

    public void setSellerUserID(int sellerUserID) {
        SellerUserID = sellerUserID;
    }

    public String getSellerUserUserName() {
        return SellerUserUserName;
    }

    public void setSellerUserUserName(String sellerUserUserName) {
        SellerUserUserName = sellerUserUserName;
    }

    @Expose
    @SerializedName("SellerUserName")
    private  String SellerUserUserName="";
    /**
     * ShopInfo : {"Name":"天天拼货"}
     * CreateTime : 2018-09-12 15:54:19
     * OrderCode : 18091215541940318369
     * ProductCount : 2
     * ProductAmount : 8.00
     * Discount : 3.20
     * PayableAmount : 4.80
     * Statu : 已完成
     * PayInfo : {"Type":"未支付","Code":""}
     * Products : [{"Name":"zzb 0519 内网测试 第4款","Cover":"upyun:nahuo-img-server://33306/item/1495160487.jpg","Color":"15","Size":"123","Qty":2,"Price":"4.00"}]
     */
    @Expose
    @SerializedName("Buttons")
    private List<OrderButton> Buttons;
    @Expose
    @SerializedName("Charge")
    private String Charge="0.00";

    public String getCharge() {
        return Charge;
    }

    public void setCharge(String charge) {
        Charge = charge;
    }

    public List<OrderButton> getButtons() {
        return Buttons;
    }

    public void setButtons(List<OrderButton> buttons) {
        Buttons = buttons;
    }

    @Expose
    @SerializedName("ShopInfo")
    private ShopInfoBean ShopInfo;
    @Expose
    @SerializedName("CreateTime")
    private String CreateTime = "";
    @Expose
    @SerializedName("OrderCode")
    private String OrderCode = "";
    @Expose
    @SerializedName("ProductCount")
    private int ProductCount;
    @Expose
    @SerializedName("ProductAmount")
    private String ProductAmount = "0.00";
    @Expose
    @SerializedName("Discount")
    private String Discount = "0.00";
    @Expose
    @SerializedName("GetDiscountPercent")
    private String GetDiscountPercent="0.00";

    public String getGetDiscountPercent() {
        return GetDiscountPercent;
    }

    public void setGetDiscountPercent(String getDiscountPercent) {
        GetDiscountPercent = getDiscountPercent;
    }

    @Expose
    @SerializedName("PayableAmount")
    private String PayableAmount = "0.00";
    @Expose
    @SerializedName("Statu")
    private String Statu = "";
    @Expose
    @SerializedName("PayInfo")
    private PayInfoBean PayInfo;
    @Expose
    @SerializedName("Products")
    private List<ProductsBean> Products;
    @Expose
    @SerializedName("CollectedAmount")
    private String CollectedAmount = "0.00";

    public String getCollectedAmount() {
        return CollectedAmount;
    }

    public void setCollectedAmount(String collectedAmount) {
        CollectedAmount = collectedAmount;
    }

    public ShopInfoBean getShopInfo() {
        return ShopInfo;
    }

    public void setShopInfo(ShopInfoBean ShopInfo) {
        this.ShopInfo = ShopInfo;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public String getOrderCode() {
        return OrderCode;
    }

    public void setOrderCode(String OrderCode) {
        this.OrderCode = OrderCode;
    }

    public int getProductCount() {
        return ProductCount;
    }

    public void setProductCount(int ProductCount) {
        this.ProductCount = ProductCount;
    }

    public String getProductAmount() {
        return ProductAmount;
    }

    public void setProductAmount(String ProductAmount) {
        this.ProductAmount = ProductAmount;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String Discount) {
        this.Discount = Discount;
    }

    public String getPayableAmount() {
        return PayableAmount;
    }

    public void setPayableAmount(String PayableAmount) {
        this.PayableAmount = PayableAmount;
    }

    public String getStatu() {
        return Statu;
    }

    public void setStatu(String Statu) {
        this.Statu = Statu;
    }

    public PayInfoBean getPayInfo() {
        return PayInfo;
    }

    public void setPayInfo(PayInfoBean PayInfo) {
        this.PayInfo = PayInfo;
    }

    public List<ProductsBean> getProducts() {
        return Products;
    }

    public void setProducts(List<ProductsBean> Products) {
        this.Products = Products;
    }

    public String getCreateUserName() {
        return CreateUserName;
    }

    public void setCreateUserName(String CreateUserName) {
        this.CreateUserName = CreateUserName;
    }

    public int getCreateUserUserID() {
        return CreateUserUserID;
    }

    public void setCreateUserUserID(int CreateUserUserID) {
        this.CreateUserUserID = CreateUserUserID;
    }

    public static class ShopInfoBean implements Serializable {
        private static final long serialVersionUID = 6267976614502283166L;
        /**
         * Name : 天天拼货
         */

        @SerializedName("Name")
        private String Name;

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }
    }

    public static class PayInfoBean implements Serializable {
        private static final long serialVersionUID = 6267976814502283166L;
        /**
         * Type : 未支付
         * Code :
         */
        @Expose
        @SerializedName("Type")
        private String Type = "";
        @Expose
        @SerializedName("Code")
        private String Code = "";

        public String getType() {
            return Type;
        }

        public void setType(String Type) {
            this.Type = Type;
        }

        public String getCode() {
            return Code;
        }

        public void setCode(String Code) {
            this.Code = Code;
        }
    }

    public static class ProductsBean implements Serializable {
        private static final long serialVersionUID = 6257976614502283166L;
        /**
         * Name : zzb 0519 内网测试 第4款
         * Cover : upyun:nahuo-img-server://33306/item/1495160487.jpg
         * Color : 15
         * Size : 123
         * Qty : 2
         * Price : 4.00
         */
        @SerializedName("Code")
        @Expose
        private String Sku="";

        public String getSku() {
            return Sku;
        }

        public void setSku(String sku) {
            Sku = sku;
        }

        @Expose
        @SerializedName("Name")
        private String Name = "";
        @SerializedName("Cover")
        @Expose
        private String Cover = "";
        @Expose
        @SerializedName("Color")
        private String Color = "";
        @Expose
        @SerializedName("Size")
        private String Size = "";
        @Expose
        @SerializedName("Qty")
        private int Qty;
        @Expose
        @SerializedName("Price")
        private String Price = "0.00";

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getCover() {
            return Cover;
        }

        public void setCover(String Cover) {
            this.Cover = Cover;
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

        public int getQty() {
            return Qty;
        }

        public void setQty(int Qty) {
            this.Qty = Qty;
        }

        public String getPrice() {
            return Price;
        }

        public void setPrice(String Price) {
            this.Price = Price;
        }
    }
}
