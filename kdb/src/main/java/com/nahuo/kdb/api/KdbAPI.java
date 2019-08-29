package com.nahuo.kdb.api;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.nahuo.kdb.CommonListActivity;
import com.nahuo.kdb.ReservationBean;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.model.AuthBean;
import com.nahuo.kdb.model.CreateProduceBean;
import com.nahuo.kdb.model.MBean;
import com.nahuo.kdb.model.PackageBean;
import com.nahuo.kdb.model.PackageDetailBean;
import com.nahuo.kdb.model.PayBean;
import com.nahuo.kdb.model.ProdectBean;
import com.nahuo.kdb.model.ProductModel;
import com.nahuo.kdb.model.PublicData;
import com.nahuo.kdb.model.SaleDetailBean;
import com.nahuo.kdb.model.ScanQrcodeModel;
import com.nahuo.kdb.model.ShopCartModel;
import com.nahuo.kdb.model.ShopItemListModel;
import com.nahuo.kdb.model.StoageDetailBean;
import com.nahuo.library.helper.GsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.nahuo.kdb.api.HttpUtils.httpPost;

public class KdbAPI {

    private static final String TAG = KdbAPI.class.getSimpleName();
    private static KdbAPI instance = null;

    /**
     * 单例
     */
    public static KdbAPI getInstance() {
        if (instance == null) {
            instance = new KdbAPI();
        }
        return instance;
    }

    /**
     * 获取列表数据
     */
    public static List<ShopItemListModel> getList(Context context, CommonListActivity.ListType type, String searchKeyword, int pageIndex, int pageSize, String itemcode) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        if (searchKeyword != null && searchKeyword.length() > 0) {
            params.put("keyword", URLEncoder.encode(searchKeyword, "UTF-8"));
        }
        params.put("pageSize", pageSize + "");
        params.put("pageIndex", pageIndex + "");
        String json = "";
        switch (type) {
            case 入库:
                json = HttpUtils.httpGet("pinhuokdb/Storage/GetWaitStoageList", params, PublicData.getCookie(context));
                break;
            case 销售:
            case 库存:
                params.put("itemcode", itemcode);
                json = HttpUtils.httpGet("pinhuokdb/Storage/GetInStoageList", params, PublicData.getCookie(context));
                break;
            default:
                break;
        }
        JSONObject jo = new JSONObject(json);
        List<ShopItemListModel> result = GsonHelper.jsonToObject(jo.get("List").toString(),
                new TypeToken<List<ShopItemListModel>>() {
                });
        return result;
    }

    /**
     * 获取预约单列表
     */
    public static List<ReservationBean> getAppointmentList(Context context, int Type, int mPageIndex,
                                                           int mPageSize) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        String json = "";
        params.put("pageIndex", mPageIndex + "");
        params.put("pageSize", mPageSize + "");
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = sf.format(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(sf.parse(nowDate));
        cal.add(Calendar.DAY_OF_YEAR, +1);
        String nextDate = sf.format(cal.getTime());
        if (Type == 1) {
            params.put("fromTime", nowDate);
            params.put("toTime", nowDate);
        } else if (Type == 2) {
            params.put("fromTime", nextDate);
            params.put("toTime", nextDate);
        }
        json = HttpUtils.httpGet("pinhuokdb/order/GetAppointmentList", params, PublicData.getCookie(context));
        List<ReservationBean> result = GsonHelper.jsonToObject(json,
                new TypeToken<List<ReservationBean>>() {
                });
        return result;
    }

    /**
     * 获取详细内容
     */
    public static Object getDetail(Context context, CommonListActivity.ListType type, int id, int qsid) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        Object result = null;

        params.put("SourceID", id + "");
        String json = "";
        switch (type) {
            case 入库:
                // params.put("qsid", qsid + "");
                json = HttpUtils.httpGet("pinhuokdb/Storage/GetWaitStoageDetail", params, PublicData.getCookie(context));

                JSONObject jo = new JSONObject(json);
                result = GsonHelper.jsonToObject(jo.get("ColorSize").toString(),
                        new TypeToken<ArrayList<ProductModel>>() {
                        });
                break;
            case 销售:
                json = HttpUtils.httpGet("pinhuokdb/Storage/GetInStoageDetail", params, PublicData.getCookie(context));
                JSONObject jo1 = new JSONObject(json);
                result = GsonHelper.jsonToObject(jo1.get("ColorSize").toString(),
                        new TypeToken<ArrayList<ProductModel>>() {
                        });
                break;
            case 库存:
                json = HttpUtils.httpGet("pinhuokdb/Storage/GetInStoageDetail", params, PublicData.getCookie(context));
                JSONObject jo2 = new JSONObject(json);
                result = GsonHelper.jsonToObject(jo2.toString(),
                        StoageDetailBean.class);
                break;
            default:
                break;
        }

        return result;
    }

    /**
     * 入库
     */
    public static void submitRK(Context context, List<ProductModel> colorSizeData) throws Exception {
        String strSaveJson = "";
        //  String qtyJson = "[";
        JSONArray jsonArray = new JSONArray();
        if (ListUtils.isEmpty(colorSizeData))
            throw new Exception("请入库至少一个SKU");
        for (ProductModel pm : colorSizeData) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Color", pm.getColor());
            jsonObject.put("Size", pm.getSize());
            jsonObject.put("Qty", pm.getDHQty());
            jsonObject.put("SourceID", pm.getAgentItemId());
            jsonArray.put(jsonObject);
//            qtyJson += "{";
//            qtyJson += "'Color':'" + pm.getColor() + "',";
//            qtyJson += "'Size':'" + pm.getSize() + "',";
//            qtyJson += "'Qty':'" + pm.getDHQty() + "',";
//            qtyJson += "'SourceID':'" + pm.getAgentItemId() + "'";
//            qtyJson += "},";
        }
//        if (qtyJson.length() > 3) {
//            qtyJson = qtyJson.substring(0, qtyJson.length() - 1);
//        } else {
//            throw new Exception("请入库至少一个SKU");
//        }
//        qtyJson += "]";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("qtySizeJson", jsonArray);
        strSaveJson = jsonObject.toString();
        //pinhuokdb/Storage/SaveInStorage

        httpPost("pinhuokdb/Storage/SaveInStock", strSaveJson, PublicData.getCookie(context));
    }


    public static boolean judeItemID(List<ScanQrcodeModel> data) {
        HashSet<Integer> set = new HashSet();
        set.clear();
        boolean flag = false;
        if (data.size() > 1) {
            for (ScanQrcodeModel bean : data) {
                set.add(bean.getItemID());
            }
            if (set.size() > 1) {
                flag = true;
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * 扫描入库
     */
    public static void ScanSingleInStock(Context context, List<ScanQrcodeModel> data) throws Exception {
        String strSaveJson = "";
        try {

            int ItemID = -1;
            JSONArray items = null;
            if (judeItemID(data)) {
                HashSet<Integer> set = new HashSet();
                set.clear();
                for (ScanQrcodeModel bean : data) {
                    set.add(bean.getItemID());
                }
                Iterator<Integer> it = set.iterator();
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = null;
                while (it.hasNext()) {
                    jsonObject = new JSONObject();
                    JSONArray jsonArray1 = new JSONArray();
                    int id = it.next();

                    for (ScanQrcodeModel bean : data) {
                        if (bean.getItemID() == id) {
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("Color", bean.getColor());
                            jsonObject1.put("Size", bean.getSize());
                            jsonObject1.put("Qty", bean.getDhQty());
                            if (bean.getDhQty() > 0)
                                jsonArray1.put(jsonObject1);
                        }
                    }
                    if (jsonArray1.length() > 0) {
                        jsonObject.put("SourceID", id);
                        jsonObject.put("Products", jsonArray1);
                        jsonArray.put(jsonObject);
                    }
                }
                items = jsonArray;

            } else {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray1 = new JSONArray();
                for (ScanQrcodeModel bean : data) {
                    ItemID = bean.getItemID();
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("Color", bean.getColor());
                    jsonObject1.put("Size", bean.getSize());
                    jsonObject1.put("Qty", bean.getDhQty());
                    if (bean.getDhQty() > 0)
                        jsonArray1.put(jsonObject1);
                }
                jsonObject.put("SourceID", ItemID);
                jsonObject.put("Products", jsonArray1);
                jsonArray.put(jsonObject);
                items = jsonArray;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("items", items);
            strSaveJson = jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpPost("pinhuokdb/Storage/SaveInStock", strSaveJson, PublicData.getCookie(context));
    }

    /**
     * 单个入库
     */
    public static void SaveSingleInStock(Context context, List<ProductModel> data) throws Exception {
        String strSaveJson = "";
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray1 = new JSONArray();
            int ItemID = -1;
            for (ProductModel bean : data) {
                ItemID = bean.getAgentItemId();

                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("Color", bean.getColor());
                jsonObject1.put("Size", bean.getSize());
                jsonObject1.put("Qty", bean.getDHQty());
                if (bean.getDHQty() > 0)
                    jsonArray1.put(jsonObject1);
            }
            jsonObject.put("SourceID", ItemID);
            jsonObject.put("Products", jsonArray1);
            jsonArray.put(jsonObject);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("items", jsonArray);
            strSaveJson = jsonObject1.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpPost("pinhuokdb/Storage/SaveInStock", strSaveJson, PublicData.getCookie(context));
    }

    /**
     * 批量入库
     */
    public static void SaveInStock(Context context, List<ShopItemListModel> data) throws Exception {
        String strSaveJson = "";
        try {
            JSONArray jsonArray = new JSONArray();
            for (ShopItemListModel bean : data) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("SourceID", bean.getAgentItemId());
                jsonArray.put(jsonObject);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("items", jsonArray);
            strSaveJson = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpPost("pinhuokdb/Storage/SaveInStock", strSaveJson, PublicData.getCookie(context));
    }

    /**
     * 批量入库
     */
    public static void SaveInStock(Context context, String strSaveJson) throws Exception {

        httpPost("pinhuokdb/Storage/SaveInStock", strSaveJson, PublicData.getCookie(context));
    }

    /**
     * 批量入库
     */
    public static void SaveSkuStock(Context context, String strSaveJson) throws Exception {

        httpPost("pinhuokdb/storage/SaveSkuStock", strSaveJson, PublicData.getCookie(context));
    }

    /**
     * 展示和取消展示
     */
    public static void SetItemHide(Context context, List<ShopItemListModel> data, boolean isHide) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            List<String> sList = new ArrayList<>();
            for (ShopItemListModel bean : data) {
                sList.add(bean.getAgentItemId() + "");
            }
            String ss = sList.toString().substring(1, sList.toString().length() - 1);
            params.put("ItemIDS", ss);
            params.put("isHide", isHide);
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpPost("pinhuokdb/Storage/SetItemHide", params, PublicData.getCookie(context));
    }

    /**
     * 修改库存
     */
    public static void submitKC(Context context, ShopItemListModel shopItem, List<ProductModel> colorSizeData) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("SourceID", shopItem.getAgentItemId() + "");
        String qtyJson = "[";
        for (ProductModel pm : colorSizeData) {
            qtyJson += "{";
            qtyJson += "'Color':'" + pm.getColor() + "',";
            qtyJson += "'Size':'" + pm.getSize() + "',";
            qtyJson += "'Qty':'" + pm.getNewKCQty() + "'";
            qtyJson += "},";
        }
        if (qtyJson.length() > 3) {
            qtyJson = qtyJson.substring(0, qtyJson.length() - 1);
        } else {
            throw new Exception("请选择购买数量");
        }
        qtyJson += "]";
        params.put("qtySizeJson", qtyJson);

        httpPost("pinhuokdb/Storage/UpdateStoage", params, PublicData.getCookie(context));
    }

    public static ScanQrcodeModel getScanRecord(Context context, String qrcode) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("guid", qrcode);

        String json = HttpUtils.httpGet("pinhuokdb/Storage/GetScanRecord", params, PublicData.getCookie(context));

        ScanQrcodeModel result = GsonHelper.jsonToObject(json,
                new TypeToken<ScanQrcodeModel>() {
                });
        return result;
    }

    public static void addFromCode(Context context, String qrcode) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("scanCode", qrcode);

        String json = httpPost("pinhuokdb/shoppingcart/AddFromCode", params, PublicData.getCookie(context));

    }

    /**
     * 扫码添加购物车
     */
    public static void addQrcodeItem2ShopCard(Context context, ScanQrcodeModel saveToShopCart) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("itemID", saveToShopCart.getItemID() + "");
        params.put("ActionType", "0");
        String qtyJson = "[";
        qtyJson += "{";
        qtyJson += "'Color':'" + saveToShopCart.getColor() + "',";
        qtyJson += "'Size':'" + saveToShopCart.getSize() + "',";
        qtyJson += "'Qty':1";
        qtyJson += "}";
        qtyJson += "]";
        try {
            params.put("products", new JSONArray(qtyJson));
        } catch (Exception ex) {
            throw ex;
        }

        HttpUtils.httpPostWithJson("pinhuokdb/ShoppingCart/Add", params, PublicData.getCookie(context));
    }

    /**
     * 修改购物车
     */
    public static void updateShopCart(Context context, ShopCartModel shopcart) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("itemID", shopcart.getItemID() + "");
        params.put("ActionType", "2");
        String qtyJson = "[";
        qtyJson += "{";
        qtyJson += "'Color':'" + shopcart.getColor() + "',";
        qtyJson += "'Size':'" + shopcart.getSize() + "',";
        qtyJson += "'Qty':'" + shopcart.getQty() + "'";
        qtyJson += "}";
        qtyJson += "]";
        try {
            params.put("products", new JSONArray(qtyJson));
        } catch (Exception ex) {
            throw ex;
        }

        HttpUtils.httpPostWithJson("pinhuokdb/ShoppingCart/Add", params, PublicData.getCookie(context));
    }

    /**
     * 添加到购物车
     */
    public static void addShopCart(Context context, ShopItemListModel shopItem, List<ProductModel> colorSizeData) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("itemID", shopItem.getAgentItemId() + "");
        params.put("ActionType", "1");
        String qtyJson = "[";
        for (ProductModel pm : colorSizeData) {
            if (pm.getBuyQty() > 0) {
                qtyJson += "{";
                qtyJson += "'Color':'" + pm.getColor() + "',";
                qtyJson += "'Size':'" + pm.getSize() + "',";
                qtyJson += "'Qty':'" + pm.getBuyQty() + "'";
                qtyJson += "},";
            }
        }
        if (qtyJson.length() > 3) {
            qtyJson = qtyJson.substring(0, qtyJson.length() - 1);
        } else {
            throw new Exception("请选择购买数量");
        }
        qtyJson += "]";
        try {
            params.put("products", new JSONArray(qtyJson));
        } catch (Exception ex) {
            throw ex;
        }

        HttpUtils.httpPostWithJson("pinhuokdb/ShoppingCart/Add", params, PublicData.getCookie(context));
    }

    /**
     * 删除购物车项
     */
    public static void deleteShopCart(Context context, String ids) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ids", ids);

        httpPost("pinhuokdb/ShoppingCart/delete", params, PublicData.getCookie(context));
    }

    /**
     * 获取详细内容
     */
    public static ProdectBean getShopCart(Context context) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        ArrayList<ShopCartModel> result = null;
        String json = HttpUtils.httpGet("pinhuokdb/ShoppingCart/GetProdectList", params, PublicData.getCookie(context));

        ProdectBean bean = GsonHelper.jsonToObject(json,
                ProdectBean.class);
        return bean;
    }

    /**
     * 获取发货
     */
    public static PackageBean getPackageList(Context context) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        String json = HttpUtils.httpGet("pinhuokdb/storage/GetPackageList", params, PublicData.getCookie(context));
//
        PackageBean bean = GsonHelper.jsonToObject(json,
                PackageBean.class);
        return bean;
    }

    /**
     * 获取发货detail
     */
    public static PackageDetailBean getPackageDetail(Context context, int packageID) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("packageID", packageID + "");
        String json = HttpUtils.httpGet("pinhuokdb/storage/GetPackageDetail", params, PublicData.getCookie(context));
//
        PackageDetailBean bean = GsonHelper.jsonToObject(json,
                PackageDetailBean.class);
        return bean;
    }

    /**
     * 获取详细内容
     */
    public static CreateProduceBean createOrderPreview(Context context, String ShoppingIDS
            , String Discount, String PayableAmount) throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("ShoppingIDS", ShoppingIDS);
        params.put("Discount", Discount + "");
        params.put("PayableAmount", PayableAmount + "");
        String json = HttpUtils.httpPost("pinhuokdb/Order/CreateOrderPreview", params, PublicData.getCookie(context));
        // Log.d("yu", json);
        CreateProduceBean bean = GsonHelper.jsonToObject(json,
                CreateProduceBean.class);
        return bean;
    }

    public static void delRC(Context context, String itemid) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("SourceID", itemid);

        httpPost("pinhuokdb/Storage/DelWaitStoage", params, PublicData.getCookie(context));
    }

    public static SaleDetailBean submitOrder(Context context, String shopcartIDS,
                                             String discount, String PayableAmount) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
//        String shopcartIDS = "";
//        double money = 0;
//        for (ShopCartModel m : shopcarts) {
//            shopcartIDS += "," + m.getID();
//            money += m.getQty() * m.getPrice();
//        }
//        if (shopcartIDS.length() > 0) {
//            shopcartIDS = shopcartIDS.substring(1);
//        }
        params.put("shoppingIDS", shopcartIDS);
        params.put("discount", discount + "");
        params.put("PayableAmount", PayableAmount + "");
        params.put("SellerUserID", SpManager.getSellerUsersId(context));
        params.put("OrderCode", "");
        String json = httpPost("pinhuokdb/Order/SaveOrder", params, PublicData.getCookie(context));

        SaleDetailBean result = GsonHelper.jsonToObject(json,
                new TypeToken<SaleDetailBean>() {
                });

        return result;
    }

    public static PayBean checkOrder(Context context, String OrderCode) throws Exception {
     /*   已取消
                已完成
        待支付*/
        Map<String, String> params = new HashMap<>();
        params.put("OrderCode", OrderCode);
        String json = HttpUtils.httpGet("pinhuokdb/order/CheckOrderPayStatu", params, PublicData.getCookie(context));

        PayBean result = GsonHelper.jsonToObject(json,
                new TypeToken<PayBean>() {
                });
        return result;
    }

    /**
     * 修改款式数据
     */
    public static void editItem(Context context, ShopItemListModel shopItem) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("itemid", shopItem.getAgentItemId() + "");
        params.put("ReName", shopItem.getName() + "");
        params.put("RePrice", shopItem.getRetailPrice() + "");

        httpPost("pinhuokdb/Storage/SaveItemConfig", params, PublicData.getCookie(context));
    }

    /**
     * 获取人员列表（人员管理）
     */
    public static MBean getAuthManageList(Context context) throws Exception {
        Map<String, String> params = new HashMap<>();
        String json = HttpUtils.httpGet("pinhuokdb/authmanage/GetListV2", params, PublicData.getCookie(context));
        MBean result = GsonHelper.jsonToObject(json,
                MBean.class);
        return result;
    }

    /**
     * 删除成员
     */
    public static void deletemember(Context context, int memberID) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("memberID", memberID + "");
        String json = HttpUtils.httpGet("pinhuokdb/authmanage/deletemember", params, PublicData.getCookie(context));
    }

    /**
     * 删除角色
     */
    public static void deletegroup(Context context, int groupID) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("groupID", groupID + "");
        String json = HttpUtils.httpGet("pinhuokdb/authmanage/deletegroup", params, PublicData.getCookie(context));
    }

    /**
     * 添加角色
     */
    public static void addgroup(Context context, String name, String authIDs) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("authIDs", authIDs);
        String json = HttpUtils.httpPost("pinhuokdb/authmanage/addgroup", params, PublicData.getCookie(context));
    }

    /**
     * 添加成员
     */
    public static void addmember(Context context, String userName, String password, int groupID
            , String alias) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        params.put("password", password);
        params.put("groupID", groupID + "");
        params.put("alias", alias);
        String json = HttpUtils.httpPost("pinhuokdb/authmanage/addmemberV2", params, PublicData.getCookie(context));
    }

    /**
     * 编辑成员
     */
    public static void editmember(Context context, String newPassword, int memberUserID
            , int groupID, String alias) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("password", newPassword);
        params.put("memberUserID", memberUserID + "");
        params.put("groupID", groupID + "");
        params.put("alias", alias);
        String json = HttpUtils.httpPost("pinhuokdb/authmanage/editmemberV2", params, PublicData.getCookie(context));
    }

    /**
     * 编辑角色
     */
    public static void editgroup(Context context, String name, String authIDs, int groupid) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("groupid", groupid + "");
        params.put("authIDs", authIDs);
        String json = HttpUtils.httpPost("pinhuokdb/authmanage/editgroup", params, PublicData.getCookie(context));
    }

    /**
     * 获取人员列表（人员管理）
     */
    public static AuthBean getGroup(Context context, int groupID) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("groupID", groupID + "");
        String json = HttpUtils.httpGet("pinhuokdb/authmanage/getGroup", params, PublicData.getCookie(context));
        AuthBean result = GsonHelper.jsonToObject(json,
                new TypeToken<AuthBean>() {
                });
        return result;
    }
}
