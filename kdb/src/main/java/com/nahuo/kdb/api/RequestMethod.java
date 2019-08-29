package com.nahuo.kdb.api;

/**
 * @author JorsonWong
 * @description Http 请求method
 * @created 2015年4月2日 上午10:51:40
 */
public class RequestMethod {
    public static class QuickSaleMethod{
        //拼货列表与预告列表
        public static final String GET_PIN_AND_FORECAST_LIST = "shop/agent/GetQuickSaleList";
        //收藏列表
        public static final String GET_FAVORITES = "shop/agent/GetMyFavoriteForQuickSale";
        //取消收藏
        public static final String REMOVE_FAVORITE = "shop/agent/RemoveItemFromMyFavorite";
        //收藏
        public static final String ADD_FAVORITE = "shop/agent/AddItemToMyFavorite";
        //闪批列表
        public static String RECOMMEND_SHOP_ITEMS = "shop/agent/GetQuickSaleItems";
        //用户签名
        public static String USERS_SIGNATURE = "user/user/getsignaturelist";
        //获取已拼
        public static String GET_QSORDER= "shop/agent/order/GetMyQSOrderList";
    }
    public static class UserMethod {

        private static final String USER_BASE = "user/";

        //扫描登录
        public static String SCAN_LOGIN = "user/user/ScanLogin";
        /**
         * 注册
         */
        public static String USER_REGISTER = USER_BASE + "user/register";
        /**
         * 登录
         */
        public static String USER_LOGIN = USER_BASE + "user/login";
        /**
         * 连接登录
         */
        public static String        USER_CONNECT_LOGIN = USER_BASE + "connect/login";
        /**获取注册验证码*/
        public static final String GET_SIGN_UP_VERIFY_CODE = "user/user/getmobileverifycode";

        public static final String VALIDATE_SIGN_UP_VERIFY_CODE = "user/user/checkmobileverifycode";

    }

    /**
     * @author JorsonWong
     * @description 购物车
     * @created 2015年4月2日 上午10:52:06
     */
    public static class ShopCartMethod {
        // private static final String CART_BASE = "shop/Cart/";

        private static final String SHOP_AGENT_CART = "shop/agent/Cart/";

        /** 加入购物车 */
//        public static final String  SHOP_AGENT_CART_ADD_ITEM       = SHOP_AGENT_CART + "AddItem";

        /**
         * 加入购物车
         */
        public static final String SHOP_AGENT_CART_ADD_ITEMS = SHOP_AGENT_CART + "AddItems";

        /**
         * 设置商品购物车数量
         */
        public static final String SET_QTY = SHOP_AGENT_CART + "SetQty";

        /**
         * 获取购物车商品数量
         */
        public static final String CART_GET_ITEM_COUNT = SHOP_AGENT_CART + "GetItemCount";

        /**
         * 获取购物车列表
         */
        public static final String GET_ITEMS_FOR_ALL_SHOP = SHOP_AGENT_CART + "GetItemsForAllShop";

        /**
         * 创建临时订单
         */
        public static final String CREATE_TEMP_ORDER_FOR_ALL_SHOP = SHOP_AGENT_CART + "CreateTempOrderForAllShop";

        /**
         * 提交订单
         */
        public static final String SUBMIT_ORDER_LIST = SHOP_AGENT_CART + "SubmitOrderForQuickSale";

        /**
         * 批量删除购物车商品
         */
        public static final String REMOVE_ITEMS = SHOP_AGENT_CART + "RemoveItems";

    }

    public static class ShopMethod {

        /**注册店铺*/
        public static final String REGISTER_SHOP = "shop/shop/register";
        /**某供货商商品*/
        public static final String GET_SHOP_ITEMS = "shop/agent/GetShopItems2";
        /**
         * 获取订单
         */
        public static final String ORDER_LIST = "shop/agent/order/GetOrderList";

        /**
         * 获取地址列表
         */
        public static final String SHOP_ADDRESS_GET_ADDRESSES = "shop/address/GetAddresses";
        /**
         * 删除地址
         */
        public static final String SHOP_ADDRESS_DELETE = "shop/address/delete";

        /**
         * 增加收货地址
         */
        public static final String SHOP_ADDRESS_ADD = "shop/address/add";

        /**
         * 修改收货地址
         */
        public static final String SHOP_ADDRESS_UPDATE = "shop/address/update";
        /**
         * 获取商品基本信息
         */
        public static final String SHOP_GET_ITEM_BASE_INFO = "shop/agent/getitembaseinfo";

        /**
         * 获取所有待处理订单的数目
         */
        public static final String SHOP_AGENT_ORDER_GET_PENDING_ORDER_COUNT = "shop/agent/order/GetPendingOrderCount";
        /**
         * 获取店铺的商品分类
         */
        public static final String SHOP_AGENT_ITEMCAT_GETITEM_CATS = "shop/agent/itemcat/getitemcats";
        /**
         * 获取我的商品信息
         */
        public static final String SHOP_AGENT_GET_MY_ITEMS = "shop/agent/getmyitems";

        /**
         * 获取供货商商品
         */
        public static final String GET_AGENT_ITEMS = "shop/agent/getagentitems";
        /**
         * 删除商品
         */
        public static final String SHOP_AGENT_OFF_SHELF_ITEMS = "shop/agent/offShelfItems";
        /**
         * 获取商品详情
         */
        public static final String SHOP_AGENT_GET_ITEM_DETAIL = "shop/agent/getitemdetail";
        /**
         * 搜索供货商
         */
        public static final String SEARCH_MY_VENDORS = "shop/agent/GetMySuppliers";
        /**
         * 设置分享店铺方案
         */
        public static final String SET_SHOP_SHARE_DESC = "shop/agent/SetShopShareDesc";

    }

    public static class XiaoZuMethod {
        /**
         * 获取最新广播
         */
        public static final String XIAOZU_TOPIC_LATEST_BROADCAST = "xiaozu/topic/latest/60087";
    }

    public static class OrderDetailMethod {
        /**
         * 拿货单
         */
        public static final String GET_BUY_ORDER = "shop/agent/order/GetBuyOrder";
        /**
         * 代理单
         */
        public static final String GET_AGENT_ORDER = "shop/agent/order/GetAgentOrder4Seller";
        /**
         * 发货单
         */
        public static final String GET_SEND_GOODS_ORDER = "shop/agent/order/GetShipOrder";
        /**
         * 售货单
         */
        public static final String GET_SLLER_ORDER = "shop/agent/order/GetSellOrder";
        /**
         * 子单
         */
        public static final String GET_CHILD_ORDER = "shop/agent/order/GetAgentOrder4Buyer";
        /**
         * 拣货单
         */
        public static final String GET_PICKING_ORDER = "shop/agent/order/GetPickingOrder";

    }

    public static class PayMethod {
        /**
         * 获取账号基本信息
         */
        public static final String GET_ACCOUNT_BASE_INFO = "pay/Account/GetAccountBaseInfo";
        /**
         * 设置支付密码
         */
        public static final String SET_PASSWORD = "pay/Account/SetPassword";
    }

    public static class OrderMethod {
        /**
         * 退款中数目
         */
        public static final String GET_REFUND_COUNT = "shop/agent/order/GetPendingRefundOrderCount";
        /**
         * 取消订单
         */
        public static final String CANCEL_ORDER = "shop/agent/order/CancelOrder";
        /**
         * 配货单
         */
        public static final String GET_SHIP_ITEMS = "shop/agent/order/getshipitems";
    }


    public static final class SearchMethod {

        public static final String GET_AGENT_ITEMS = "Shop/agent/GetAgentItems";

        public static final String GET_ITEM_HOT_KEYWORD = "Shop/agent/GetItemHotKeyword";

        public static final String GET_SHOP_INFO_LIST = "shop/shop/getShopInfoList";

        public static final String GET_HOT_SHOPS = "Shop/agent/GetItemHotUsers";

        public static final String GET_SHOP_ITEM2 = "shop/agent/GetShopItems2";


    }
}
