package com.nahuo.kdb.common;

/***
 * @author pj
 * @description 成团相关方法
 */
public class TuanPiUtil {

    private static String dealCount0Tips = "已拼0件，拼够%d件成团";
    private static String dealCount1NotEnoughTips = "已拼%d件，差%d件成团";
    private static String dealCountChengTuanTips = "已拼%d件，拼货成功啦";
    private static String closeTuanNotDealTips = "已拼%d件，不满%d件没拼成功";
    private static String closeTuanTips = "已付款%d件，已成团";

    private static String dealCount0DetailTips = "成团后即向档口报单发货，不成团退款";
    private static String dealCount1NotEnoughDetailTips = "快成团了，约熟悉的店主一起拼～";
    private static String dealCountChengTuanDetailTips = "已成团，即将向档口报单，想拼的赶紧下单哈！";
    private static String closeTuanDetailTips = "拼货取消，已付货款退回到您的余额账户";

    /***
     * @description 成团的进度文案
     * @author pj
     */
    public static String getChengTuanTips(boolean isStart, int chengTuanCount, int dealCount) {
        if (!isStart) {
            if (dealCount>=chengTuanCount)
            {
                return String.format(closeTuanTips, dealCount);}
                        else {
                return String.format(closeTuanNotDealTips, dealCount, chengTuanCount);
            }
        } else {
            if (dealCount == 0) {//没人拼
                return String.format(dealCount0Tips, chengTuanCount);
            } else if (dealCount < chengTuanCount) {//半成团
                return String.format(dealCount1NotEnoughTips, dealCount, chengTuanCount - dealCount);
            } else {//成团
                return String.format(dealCountChengTuanTips, dealCount);
            }
        }
    }

    /***
     * @description 成团的进度提示文案
     * @author pj
     */
    public static String getChengTuanDetailTips(boolean isStart, int chengTuanCount, int dealCount) {
        if (!isStart) {
            if (dealCount>=chengTuanCount)
            {
                return "";
            }
            else {
                return closeTuanDetailTips;
            }
        } else {
            if (dealCount == 0) {//没人拼
                return dealCount0DetailTips;
            } else if (dealCount < chengTuanCount) {//半成团
                return dealCount1NotEnoughDetailTips;
            } else {//成团
                return dealCountChengTuanDetailTips;
            }
        }
    }
}
