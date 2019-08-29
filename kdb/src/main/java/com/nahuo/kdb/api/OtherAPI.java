package com.nahuo.kdb.api;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.model.AchieveBean;
import com.nahuo.kdb.model.CdMaBean;
import com.nahuo.kdb.model.CdmDetailBean;
import com.nahuo.kdb.model.CodeBean;
import com.nahuo.kdb.model.InventoryBean;
import com.nahuo.kdb.model.InventoryDetailBean;
import com.nahuo.kdb.model.PublicData;
import com.nahuo.kdb.model.SaleBean;
import com.nahuo.kdb.model.SaleDetailBean;
import com.nahuo.library.helper.GsonHelper;
import com.nahuo.library.helper.MD5Utils;

import java.util.HashMap;
import java.util.Map;

public class OtherAPI {

    /**
     * @description 获取短网址
     * @author pj
     */
    public static String getShortUrl(Context context, String url) throws Exception {
        // Map<String, String> params = new HashMap<String, String>();
        // params.put("url", url);
        // return HttpUtils.httpPost("http://dwz.cn/create.php", "", params);
        return HttpUtils.get("http://api.t.sina.com.cn/short_url/shorten.json?source=1939441632&url_long=" + url);
    }

    /**
     * 获取销售列表数据
     */
    public static SaleBean getSaleLog(Context context, int pageIndex, int pageSize,int status,String start_time,String end_time,int payTypeID,int sellerUserID) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("pageSize", pageSize + "");
        params.put("pageIndex", pageIndex + "");
        params.put("statuID",status+"");
        params.put("fromtime",start_time);
        params.put("totime",end_time);
        params.put("sellerUserID",sellerUserID+"");
        params.put("payTypeID",payTypeID+"");
        String json = HttpUtils.httpGet("pinhuokdb/Order/GetOrderList", params, PublicData.getCookie(context));

        //JSONObject jo = new JSONObject(json);
        SaleBean result = GsonHelper.jsonToObject(json,
                SaleBean.class);
        return result;
    }
    /**
     * 获取商品列表数据
     */
    public static CdMaBean getItemStockSt(Context context, int pageIndex, int pageSize, String start_time, String end_time, String itemCode , String keyword ) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("pageSize", pageSize + "");
        params.put("pageIndex", pageIndex + "");
        params.put("itemCode",itemCode+"");
        params.put("fromtime",start_time);
        params.put("totime",end_time);
        params.put("keyword",keyword+"");
        String json = HttpUtils.httpGet("pinhuokdb/storage/GetItemStockSt", params, PublicData.getCookie(context));

        //JSONObject jo = new JSONObject(json);
        CdMaBean result = GsonHelper.jsonToObject(json,
                CdMaBean.class);
        return result;
    }
    /**
     * 获取业绩列表数据
     */
    public static AchieveBean getPerformancePK(Context context, int pageIndex, int pageSize, String start_time, String end_time, String itemCode , String keyword ) throws Exception {
        Map<String, String> params = new HashMap<>();
//        params.put("pageSize", pageSize + "");
//        params.put("pageIndex", pageIndex + "");
   //     params.put("itemCode",itemCode+"");
        params.put("startDate",start_time);
        params.put("endDate",end_time);
     //   params.put("keyword",keyword+"");
        String json = HttpUtils.httpGet("pinhuokdb/order/PerformancePK", params, PublicData.getCookie(context));

        //JSONObject jo = new JSONObject(json);
        AchieveBean result = GsonHelper.jsonToObject(json,
                AchieveBean.class);
        return result;
    }
    /**
     * 获取盘点列表数据
     */
    public static InventoryBean getSkuStockList(Context context, int pageIndex, int pageSize) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("pageSize", pageSize + "");
        params.put("pageIndex", pageIndex + "");
        String json = HttpUtils.httpGet("pinhuokdb/storage/GetSkuStockList", params, PublicData.getCookie(context));
        InventoryBean result = GsonHelper.jsonToObject(json,
                InventoryBean.class);
        return result;
    }
    /**
     * 获取盘点详情数据
     */
    public static InventoryDetailBean getSkuStockDetail(Context context, String stockTime) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("stockTime", stockTime + "");
        String json = HttpUtils.httpGet("pinhuokdb/storage/GetSkuStockDetail", params, PublicData.getCookie(context));
        InventoryDetailBean result = GsonHelper.jsonToObject(json,
                InventoryDetailBean.class);
        return result;
    }
    /**
     * 获取盘点详情数据
     */
    public static CdmDetailBean getItemStockStDetail(Context context, int sourceID) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("sourceID", sourceID + "");
        String json = HttpUtils.httpGet("pinhuokdb/storage/GetItemStockStDetail", params, PublicData.getCookie(context));
        CdmDetailBean result = GsonHelper.jsonToObject(json,
                CdmDetailBean.class);
        return result;
    }
    /**
     * 获取销售明细数据
     */
    public static SaleDetailBean getSaleDetail(Context context, String ordercode) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("OrderCode", ordercode);
        String json = HttpUtils.httpGet("pinhuokdb/Order/GetOrderDetail", params, PublicData.getCookie(context));

        SaleDetailBean result = GsonHelper.jsonToObject(json,
                new TypeToken<SaleDetailBean>() {
                });
        return result;
    }
    /**
     * 订单取消
     */
    public static void cancelOrder(Context context, String ordercode) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("code", ordercode);
        String json = HttpUtils.httpGet("pinhuokdb/Order/CancelOrder", params, PublicData.getCookie(context));

    }
    /**
     * 订单支付（post）
     */
    public static CodeBean goCodePay4ShopTrade(Context context, String ordercode,String payableAmount,int payType) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("orderCode", ordercode);
        params.put("payableAmount", payableAmount);
        long time=System.currentTimeMillis();
        params.put("onceStr", time+"");
        params.put("payType", payType+"");
        params.put("sign", MD5Utils.encrypt32bit(ordercode+ SpManager.getUserId(context)+time+payType+payableAmount));
        String json = HttpUtils.httpPost("pinhuopay/trade/CodePay4ShopTrade", params, PublicData.getCookie(context));

        CodeBean result = GsonHelper.jsonToObject(json,
                new TypeToken<CodeBean>() {
                });
        return result;
    }
}
