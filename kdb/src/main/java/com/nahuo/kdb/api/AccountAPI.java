package com.nahuo.kdb.api;

import android.content.Context;
import android.util.Log;

import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.common.Utils;
import com.nahuo.kdb.model.MenuBean;
import com.nahuo.kdb.model.PublicData;
import com.nahuo.kdb.model.ShopInfoModel;
import com.nahuo.kdb.model.UserInfo;
import com.nahuo.kdb.model.UserModel;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.GsonHelper;
import com.nahuo.library.helper.MD5Utils;

import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class AccountAPI {

    private static final String TAG = AccountAPI.class.getSimpleName();
    private static AccountAPI instance = null;

    /**
     * 单例
     */
    public static AccountAPI getInstance() {
        if (instance == null) {
            instance = new AccountAPI();
        }
        return instance;
    }

    /**
     * 扫描登录
     *
     * @author ZZB
     * created at 2015/8/26 16:31
     */
    public static void scanLogin(Context context, HttpRequestHelper helper, HttpRequestListener listener, String uid) {
        HttpRequestHelper.HttpRequest request = helper.getRequest(context, RequestMethod.UserMethod.SCAN_LOGIN, listener);
        request.addParam("id", uid);
        request.doPost();
    }

    /**
     * @description 上传错误日志
     * @created 2015-4-2 下午1:54:23
     * @author ZZB
     */
    public static void uploadErrorLog(Context context, String error) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("UserName", SpManager.getUserName(context));
        params.put("AppType", "android");
        params.put("AppVersion", Utils.getAppVersion(context));
        params.put("Device", FunctionHelper.getPhoneModel());
        params.put("ErrorMsg", error);
        HttpUtils.httpPost("http://wp-app.service.nahuo.com/", "wp_app_service.asmx/ErrorLog", params);
    }

    /**
     * @description 保存招募方案
     */
    public static void setShopRecruitDesc(Context context, String desc) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("desc", desc);
        HttpUtils.httpPost("shop/agent/SetShopRecruitDesc", params, PublicData.getCookie(context));
    }

    /**
     * @description 备注用户信息
     * @created 2014-12-15 上午11:32:20
     * @author ZZB
     */
    public static void setUserDesc(Context context, int userId, String desc) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("intro", desc);
        params.put("toUserID", userId + "");
        HttpUtils.httpPost("shop/user/SetUseInfoBetweenUsers", params, PublicData.getCookie(context));
    }

    /**
     * @description 获取用户信息
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */
    public static UserInfo getUserInfo(Context context, int userId) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId + "");
        String json = HttpUtils.httpGet("shop/user/GetUseCardInfo", params, PublicData.getCookie(context));
        UserInfo user = GsonHelper.jsonToObject(json, UserInfo.class);
        return user;
    }

    /**
     * 获取个人信息
     *
     * @param cookie 当前登录用户cookie值
     */
    public UserModel getUserInfo(String cookie) throws Exception {
        UserModel userInfo;
        try {
            String json = HttpUtils.httpPost("user/user/getmyuserinfo", new HashMap<String, Object>(), cookie);
            Log.i(TAG, "Json：" + json);
            userInfo = GsonHelper.jsonToObject(json, UserModel.class);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "getUserInfo", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return userInfo;
    }

    /**
     * 设置签名
     *
     * @param cookie 当前登录用户cookie值
     */
    public boolean setSignature(String value, String cookie) throws Exception {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("signature", value);
            HttpUtils.httpPost("user/user/setsignature", params, cookie);
            return true;
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "setSignature", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * 预加载访问
     *
     * @author 彭君
     */
    public String testHttpAllItems(String cookie) throws Exception {
        String msg = "";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("pageIndex", "1");
            params.put("pageSize", "1");
            msg = HttpUtils.httpPost("shop/agent/getagentitems", params, cookie);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return msg;
    }

    /**
     * 预加载访问
     *
     * @author 彭君
     */
    public String testHttpMyItems(String cookie) throws Exception {
        String msg = "";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("pageIndex", "1");
            params.put("pageSize", "1");
            params.put("keyword", "");
            msg = HttpUtils.httpPost("shop/agent/getmyitems", params, cookie);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return msg;
    }

    /**
     * 预加载访问
     *
     * @author 彭君
     */
    public String getWebToken(String cookie) throws Exception {
        String token = "";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            token = HttpUtils.httpPost("user/user/getlogintoken", params, cookie);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return token;
    }

    /**
     * @description 绑定第三方用户
     * @created 2015-1-5 下午2:31:58
     * @author ZZB
     */
    public static void bindThirdUser(String appId, String openId, String accessToken, String userName, String password)
            throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appId", appId);
        params.put("openId", openId);
        params.put("accessToken", accessToken);
        params.put("userName", userName);
        params.put("password", MD5Utils.encrypt32bit(password));
        params.put("isEncode", "true");
        String result = HttpUtils.httpPost("user/connect/Bind", params);
        // JSONObject obj = new JSONObject(result);
        // int userId = obj.getInt("UserID");
        // return userId;

    }

    /**
     * 获取店铺信息
     * shop/shop/getshopinfo
     *
     * @param cookie 当前登录用户cookie值
     */
    public ShopInfoModel getShopInfo(String cookie) throws Exception {
        ShopInfoModel shopInfo;
        try {
//            String json = HttpUtils.httpPost("pinhuokdb/shop/GetShopInfo",
//                    new HashMap<String, Object>(), cookie);
            String json = HttpUtils.httpGet("pinhuokdb/shop/GetShopInfo",
                    new HashMap<String, String>(), cookie);
            Log.i(TAG, "Json：" + json);
            shopInfo = GsonHelper.jsonToObject(json, ShopInfoModel.class);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
                    "getShopInfo", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return shopInfo;
    }

    /**
     * APP首页菜单
     * pinhuokdb/home/GetMenus
     *
     * @param cookie 当前登录用户cookie值
     */
    public MenuBean getMainMenus(String cookie) throws Exception {
        MenuBean menuBean;
        try {

            String json = HttpUtils.httpGet("pinhuokdb/home/GetMenusV2",
                    new HashMap<String, String>(), cookie);
            Log.i(TAG, "Json：" + json);
            menuBean = GsonHelper.jsonToObject(json, MenuBean.class);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
                    "getShopInfo", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return menuBean;
    }

    /**
     * 店铺设置
     * pinhuokdb/SaveShopInfo
     *
     * @param Signature
     * @param mobile
     * @param ShopName  当前登录用户cookie值
     */
    public static void SaveShopInfo(Context context, String Signature, String mobile, String ShopName) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Signature", Signature);
        params.put("mobile", mobile);
        params.put("ShopName", ShopName);
        HttpUtils.httpPost("pinhuokdb/shop/SaveShopInfo", params, SpManager.getCookie(context));
    }
    /**
     * 根据userid获取用户信息
     *
     * @param cookie 当前登录用户cookie值
     * */
    public static boolean IsRegIMUser(String cookie, String userid) throws Exception {

        try {
            Map<String, Object> params = new TreeMap<String, Object>();
            params.put("userid", userid);
            String json = HttpUtils.httpPostTest("user/ImUser/GetImUserinfoByBanwo", params, cookie);
            JSONObject repObj = new JSONObject(json);
            boolean status = repObj.getBoolean("status");
//            if (!status) {
//                return false;
//            } else {
//                return true;
//            }
            return status;

        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "IsRegIMUser", ex.getMessage()));
            ex.printStackTrace();
            throw ex;

        }

    }

    /**
     * @description 第三方注册用户，返回用户id
     * @created 2015-1-5 下午2:04:33
     * @author ZZB
     */
    public static int thirdRegisterUser(String appId, String openId, String accessToken, String userName)
            throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appId", appId);
        params.put("openId", openId);
        params.put("accessToken", accessToken);
        params.put("userName", userName);
        String result = HttpUtils.httpPost("user/connect/CreateUser", params);
        JSONObject obj = new JSONObject(result);
        int userId = obj.getInt("UserID");
        return userId;

    }

    /**
     * @description 检查第三方登录是否绑定
     * @created 2015-1-5 下午12:09:02
     * @author ZZB
     */
    public static boolean isThirdLoginBinded(String appId, String openId) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appId", appId);
        params.put("openId", openId);
        String result = HttpUtils.httpGet("user/connect/CheckConnectStatu", params);
        JSONObject jObj = new JSONObject(result);
        boolean isBinded = jObj.getBoolean("IsConnected");
        return isBinded;
    }

    /**
     * @description 第三方登录，返回token
     * @created 2015-1-5 上午10:38:24
     * @author ZZB
     */
    public static String thirdLogin(String appId, String openId, String accessToken) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appId", appId);
        params.put("openId", openId);
        params.put("accessToken", accessToken);
        String result = HttpUtils.httpPost("user/connect/login", params);
        JSONObject obj = new JSONObject(result);
        String token = obj.getString("Token");
        return token;
    }

    /**
     * 用户登录
     *
     * @param phoneNo 手机号
     * @param pwd     密码
     * @author Chiva Liang
     */
    public String userLogin(String phoneNo, String pwd) throws Exception {
        String msg = "";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("account", phoneNo);
//            params.put("password", pwd);
            params.put("password", MD5Utils.encrypt32bit(pwd));
            params.put("isEncode", "true");
            msg = HttpUtils.httpPost("user/user/login", params);
            Log.i(TAG, "Json：" + msg);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "userLogin", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return msg;
    }

    /**
     * 获取注册验证码
     *
     * @author ZZB
     * created at 2015/8/10 10:02
     */
    public static void getSignUpVerifyCode(Context context, HttpRequestHelper helper, HttpRequestListener listener, String phoneNo, String username) {
        HttpRequestHelper.HttpRequest request = helper.getRequest(context, RequestMethod.UserMethod.GET_SIGN_UP_VERIFY_CODE, listener);
        request.addParam("mobile", phoneNo);
        request.addParam("usefor", "register");
        request.addParam("username", username);
        request.doPost();
    }

    /**
     * 校验注册验证码
     *
     * @author ZZB
     * created at 2015/8/10 15:06
     */
    public static void validateSignUpVerifyCode(Context context, HttpRequestHelper helper, HttpRequestListener listener, String phoneNo, String verifyCode) {
        HttpRequestHelper.HttpRequest request = helper.getRequest(context, RequestMethod.UserMethod.VALIDATE_SIGN_UP_VERIFY_CODE, listener);
        request.addParam("mobile", phoneNo);
        request.addParam("code", verifyCode);
        request.doPost();
    }

    /**
     * 发送验证码到指定手机（1.用户注册 2.找回密码）
     *
     * @param phoneNo 手机号
     * @param type    指定验证码的类型：1.用户注册 2.找回密码
     * @return 返回提示信息
     * @author Chiva Liang
     */
    public String getMobileVerifyCode(String phoneNo, String username, int type) throws Exception {
        String msg = "";
        try {
            String usefor = "";
            switch (type) {
                case 1:
                    usefor = "register";
                    break;
                case 2:
                    usefor = "findpassword";
                    break;
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("mobile", phoneNo);
            params.put("usefor", usefor);
            params.put("username", username);
            params.put("messageFrom", "微铺");
            msg = HttpUtils.httpPost("user/user/getmobileverifycode", params);
            Log.i(TAG, "Json：" + msg);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "getMobileVerifyCode", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return msg;
    }

    /**
     * 判断短信验证码是否正确
     *
     * @param phoneNo    手机号
     * @param verifyCode 短信验证码
     * @author Chiva Liang
     */
    public String checkMobileVerifyCode(String phoneNo, String verifyCode) throws Exception {
        String msg = "";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("mobile", phoneNo);
            params.put("code", verifyCode);
            msg = HttpUtils.httpPost("user/user/checkmobileverifycode", params);
            Log.i(TAG, "Json：" + msg);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "checkMobileVerifyCode", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return msg;
    }

    /**
     * 找回密码
     *
     * @param phoneNo 手机号
     * @param pwd     新密码
     * @param smsKey  短信验证码
     * @author Chiva Liang
     */
    public String resetPassword(String phoneNo, String pwd, String smsKey) throws Exception {
        String msg = "";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("mobile", phoneNo);
            params.put("password", MD5Utils.encrypt32bit(pwd));
            params.put("isEncode", "true");
            params.put("code", smsKey);
            msg = HttpUtils.httpPost("user/user/resetpassword", params);
            Log.i(TAG, "Json：" + msg);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "resetPassword", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return msg;
    }

    /**
     * 修改密码
     *
     * @param cookie cookie值
     * @param oldPwd 原密码
     * @param newPwd 新密码
     * @author Chiva Liang
     */
    public String updatePwd(String cookie, String oldPwd, String newPwd) throws Exception {
        String msg = "";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("OldPwd", oldPwd);
            params.put("NewPwd", newPwd);
            String json = HttpUtils.httpPost("UpdatePwd", params, cookie);
            Log.i(TAG, "Json：" + json);
            int result = GsonHelper.jsonToObject(json, int.class);
            msg = HttpUtils.returnDataToString(result);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "updatePwd", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return msg;
    }
    /**
     * 开通店铺
     * */
    public String registerShop(String phoneNo, String username ,String cookie)throws Exception{
        String msg = "";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("mobile", phoneNo);
            params.put("name", username);
            params.put("qq", "");
            params.put("qq_name", "");
            params.put("wx", "");
            params.put("code", "");
            params.put("wx_name", "");
            msg = HttpUtils.httpPost("shop/shop/register", params,cookie);
            Log.i(TAG, "Json：" + msg);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "completeAccount", ex.getMessage()));
            ex.printStackTrace();
            return  ex.getMessage();
        }
        return msg;
    }
    /**
     * 开通微铺
     *
     * @param phoneNo  手机号
     * @param username 用户昵称
     * @param password 密码
     * @param code     短信验证码
     * @return {"UserID":436720.0}
     * @author Chiva Liang
     */
    public String registerUser(String phoneNo, String username, String password, String code) throws Exception {
        String msg = "";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("mobile", phoneNo);
            params.put("name", username);
            params.put("password", MD5Utils.encrypt32bit(password));
            params.put("isEncode", "true");
            params.put("code", code);
            msg = HttpUtils.httpPost("user/user/register", params);
            Log.i(TAG, "Json：" + msg);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "completeAccount", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return msg;
    }

    /**
     * Description:修改登录密码 2014-7-14下午1:44:01
     *
     * @throws Exception
     */
    public static boolean changeLoginPassword(String oldPsw, String newPsw, String cookie) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("oldPassword", MD5Utils.encrypt32bit(oldPsw));
        params.put("newPassword", MD5Utils.encrypt32bit(newPsw));
        params.put("isEncode", "true");
        String result = HttpUtils.httpPost("user/user/changepassword", params, cookie);
        Log.d(TAG + ":修改登录密码", result);
        return true;
    }

    /**
     * @description 修改店铺名
     * @created 2014-11-25 下午5:13:00
     * @author ZZB
     */
    public static void updateShopName(Context context, String shopName) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", shopName);
        HttpUtils.httpPost("shop/shop/updateshopname", params, SpManager.getCookie(context));
    }
}
