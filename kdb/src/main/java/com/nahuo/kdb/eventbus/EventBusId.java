package com.nahuo.kdb.eventbus;

/**
 * @author ZZB
 * @description EventBus 事件id
 * @created 2015-4-24 上午9:56:38
 */
public class EventBusId {
    /**************
     * 注意，所有新添加的id不能重复
     ****************/
    public static final int COMMON_LIST_RELOAD = 33;
    public static final int SEARCH_销售 = 32;
    public static final int SEARCH_库存 = 31;
    public static final int SEARCH_入库 = 30;
    public static final int SEARCH_BACK_入库 = 34;

    /**
     * 退出应用
     */
    public static final int ON_APP_EXIT = 21;
    /**
     * 店铺logo修改
     */
    public static final int SHOP_LOGO_UPDATED = 20;
}
