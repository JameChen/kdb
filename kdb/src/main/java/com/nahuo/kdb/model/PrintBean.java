package com.nahuo.kdb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

/**
 * Created by jame on 2018/12/11.
 */

public class PrintBean  implements Serializable{
    private static final long serialVersionUID = 1833019229017773428L;
    /**
     * Color : 黑黑黑
     * Size : 0822尺码1
     * Scan : fb57a49ee290fcee
     * PintCount : 0
     * StockName : null
     * BillingID : 0
     * Price : 0
     * TotalQty : 0
     * CreateTime : /Date(-62135596800000)/
     * UserID : 0
     * ItemID : 0
     * StatusID : 0
     * Cover : null
     * Name : lilifeiyang1027内网测试1lilifeiyang1027内网测试1
     * QsId : 0
     * IsDelete : false
     * KdQty : 0
     * UserName : lilifeiyang
     * Warehouse : null
     * OriPrice : null
     * OrderID : null
     * ChildUserID : null
     * ChildUserName : null
     * RefundLockFl : false
     * SrcCreateDate : null
     * SrcBillingID : null
     * whDes : null
     * BuyerBillingDetail : []
     * BuyerRefund : []
     * BuyerStored : []
     * StallModel : null
     */
    @Expose
    @SerializedName("Color")
    private String Color="";
    @Expose
    @SerializedName("Size")
    private String Size="";
    @Expose
    @SerializedName("Scan")
    private String Scan="";
    @SerializedName("PintCount")
    private int PintCount;
    @SerializedName("StockName")
    private Object StockName;
    @SerializedName("BillingID")
    private int BillingID;
    @SerializedName("Price")
    private int Price;
    @SerializedName("TotalQty")
    private int TotalQty;

    public String getRetailPrice() {
        return RetailPrice;
    }

    @SerializedName("CreateTime")

    private String CreateTime;
    @SerializedName("UserID")
    private int UserID;
    @SerializedName("ItemID")
    private int ItemID;
    @SerializedName("StatusID")
    private int StatusID;
    @SerializedName("Cover")
    private Object Cover;
    @Expose
    @SerializedName("ItemCode")
    private String itemcode="";
    @Expose
    @SerializedName("DMCode")
    private String DMCode="";

    public String getDMCode() {
        return DMCode;
    }

    public void setDMCode(String DMCode) {
        this.DMCode = DMCode;
    }

    public String getItemcode() {
        return itemcode;
    }

    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }
    public String time= UUID.randomUUID().toString();

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Expose
    @SerializedName("Name")
    private String Name="";
    private String RetailPrice="";
    private String Code="";
    private String Categroy="";

    public String getSku() {
        return Sku;
    }

    public void setSku(String sku) {
        Sku = sku;
    }

    private String Sku="";
    public String getCategroy() {
        return Categroy;
    }

    public void setCategroy(String categroy) {
        Categroy = categroy;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public void setRetailPrice(String price) {
        RetailPrice = price;
    }

    @SerializedName("QsId")
    private int QsId;
    @SerializedName("IsDelete")
    private boolean IsDelete;
    @SerializedName("KdQty")
    private int KdQty;
    @SerializedName("UserName")
    private String UserName;
    @SerializedName("Warehouse")
    private Object Warehouse;
    @SerializedName("OriPrice")
    private Object OriPrice;
    @SerializedName("OrderID")
    private Object OrderID;
    @SerializedName("ChildUserID")
    private Object ChildUserID;
    @SerializedName("ChildUserName")
    private Object ChildUserName;
    @SerializedName("RefundLockFl")
    private boolean RefundLockFl;
    @SerializedName("SrcCreateDate")
    private Object SrcCreateDate;
    @SerializedName("SrcBillingID")
    private Object SrcBillingID;
    @SerializedName("whDes")
    private Object whDes;
    @SerializedName("StallModel")
    private Object StallModel;
    @SerializedName("BuyerBillingDetail")
    private List<?> BuyerBillingDetail;
    @SerializedName("BuyerRefund")
    private List<?> BuyerRefund;
    @SerializedName("BuyerStored")
    private List<?> BuyerStored;
    private String txt_1="";

    public String getTxt_1() {
        return getFirstTxt(Name);
    }
    public static int define_count=27;
    private String getFirstTxt(String str) {
        try {
//            if (str.length()>define_count){
//              return   str.substring(0,define_count);
//            }else {
//                return str;
//            }
            byte bt[] = str.getBytes("utf-8");
            int legth = bt.length;
            if (legth > define_count ) {
                String subStrx = substr(str, "UTF-8", define_count);
                return subStrx;
            } else {
                return str;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public String getSecondTxt(String str){
        try {
//            if (str.length()>=define_count*2){
//               return str.substring(define_count,define_count*2);
//            }else if (str.length()<define_count*2&&str.length()>define_count){
//                return str.substring(define_count,str.length());
//            }else {
//                return "";
//            }
            byte bt[] = str.getBytes("utf-8");
            int legth = bt.length;
            if (legth > define_count) {
                String subStrx = substr(str, "UTF-8", define_count);
                String subStrx2=str.substring(subStrx.length(), str.length());
                String subStrx22 = substr(subStrx2, "UTF-8", define_count);
                return subStrx22;
            }else  {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public String getThirdTxt(String str){
        try {
//          if (str.length()>=define_count*3){
//              return str.substring(define_count*2,define_count*3);
//          }else if (str.length()<define_count*3&&str.length()>define_count*2){
//              return str.substring(define_count*2,str.length());
//          }else {
//              return "";
//          }
            byte bt[] = str.getBytes("utf-8");
            int legth = bt.length;
            if (legth > define_count*2 ) {
                String subStrx = substr(str, "UTF-8", define_count);
                String subStrx2=str.substring(subStrx.length(), str.length());
                String subStrx22 = substr(subStrx2, "UTF-8", define_count);
                String subStrx3=subStrx2.substring(subStrx22.length(), subStrx2.length());
                String subStrx33 = substr(subStrx3, "UTF-8", define_count);
                return  subStrx33;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public String getTxt_2() {
        return getSecondTxt(Name);
    }

    public String getTxt_3() {
        return getThirdTxt(Name);
    }

    private String txt_2="";
    private String txt_3="";
    private  String substr(String originString, String charsetName, int byteLen)
            throws UnsupportedEncodingException {
        if (originString == null || originString.isEmpty() || byteLen <= 0) {
            return "";
        }
        char[] chars = originString.toCharArray();
        int length = 0, index = chars.length;
        for (int i = 0; i < chars.length; ++i) {
            final int len = String.valueOf(chars[i]).getBytes(charsetName).length + length;
            if (len <= byteLen) {
                length = len;
            } else {
                index = i;
                break;
            }
        }
        return String.valueOf(chars, 0, index);
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

    public String getScan() {
        return Scan;
    }

    public void setScan(String Scan) {
        this.Scan = Scan;
    }

    public int getPintCount() {
        return PintCount;
    }

    public void setPintCount(int PintCount) {
        this.PintCount = PintCount;
    }

    public Object getStockName() {
        return StockName;
    }

    public void setStockName(Object StockName) {
        this.StockName = StockName;
    }

    public int getBillingID() {
        return BillingID;
    }

    public void setBillingID(int BillingID) {
        this.BillingID = BillingID;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int Price) {
        this.Price = Price;
    }

    public int getTotalQty() {
        return TotalQty;
    }

    public void setTotalQty(int TotalQty) {
        this.TotalQty = TotalQty;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int UserID) {
        this.UserID = UserID;
    }

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int ItemID) {
        this.ItemID = ItemID;
    }

    public int getStatusID() {
        return StatusID;
    }

    public void setStatusID(int StatusID) {
        this.StatusID = StatusID;
    }

    public Object getCover() {
        return Cover;
    }

    public void setCover(Object Cover) {
        this.Cover = Cover;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getQsId() {
        return QsId;
    }

    public void setQsId(int QsId) {
        this.QsId = QsId;
    }

    public boolean isIsDelete() {
        return IsDelete;
    }

    public void setIsDelete(boolean IsDelete) {
        this.IsDelete = IsDelete;
    }

    public int getKdQty() {
        return KdQty;
    }

    public void setKdQty(int KdQty) {
        this.KdQty = KdQty;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public Object getWarehouse() {
        return Warehouse;
    }

    public void setWarehouse(Object Warehouse) {
        this.Warehouse = Warehouse;
    }

    public Object getOriPrice() {
        return OriPrice;
    }

    public void setOriPrice(Object OriPrice) {
        this.OriPrice = OriPrice;
    }

    public Object getOrderID() {
        return OrderID;
    }

    public void setOrderID(Object OrderID) {
        this.OrderID = OrderID;
    }

    public Object getChildUserID() {
        return ChildUserID;
    }

    public void setChildUserID(Object ChildUserID) {
        this.ChildUserID = ChildUserID;
    }

    public Object getChildUserName() {
        return ChildUserName;
    }

    public void setChildUserName(Object ChildUserName) {
        this.ChildUserName = ChildUserName;
    }

    public boolean isRefundLockFl() {
        return RefundLockFl;
    }

    public void setRefundLockFl(boolean RefundLockFl) {
        this.RefundLockFl = RefundLockFl;
    }

    public Object getSrcCreateDate() {
        return SrcCreateDate;
    }

    public void setSrcCreateDate(Object SrcCreateDate) {
        this.SrcCreateDate = SrcCreateDate;
    }

    public Object getSrcBillingID() {
        return SrcBillingID;
    }

    public void setSrcBillingID(Object SrcBillingID) {
        this.SrcBillingID = SrcBillingID;
    }

    public Object getWhDes() {
        return whDes;
    }

    public void setWhDes(Object whDes) {
        this.whDes = whDes;
    }

    public Object getStallModel() {
        return StallModel;
    }

    public void setStallModel(Object StallModel) {
        this.StallModel = StallModel;
    }

    public List<?> getBuyerBillingDetail() {
        return BuyerBillingDetail;
    }

    public void setBuyerBillingDetail(List<?> BuyerBillingDetail) {
        this.BuyerBillingDetail = BuyerBillingDetail;
    }

    public List<?> getBuyerRefund() {
        return BuyerRefund;
    }

    public void setBuyerRefund(List<?> BuyerRefund) {
        this.BuyerRefund = BuyerRefund;
    }

    public List<?> getBuyerStored() {
        return BuyerStored;
    }

    public void setBuyerStored(List<?> BuyerStored) {
        this.BuyerStored = BuyerStored;
    }
}
