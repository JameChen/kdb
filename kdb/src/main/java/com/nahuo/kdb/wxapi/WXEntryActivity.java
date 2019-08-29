package com.nahuo.kdb.wxapi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.api.HttpUtils;
import com.nahuo.kdb.common.Const;
import com.nahuo.kdb.common.Debug;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.library.controls.AutoCompleteTextViewEx;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.FunctionHelper;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * @description 微信回调
 * @created 2014-11-24 下午4:32:08
 * @author ZZB
 */
public class WXEntryActivity extends FragmentActivity implements IWXAPIEventHandler, OnClickListener {
    public static final String     EXTRA_TYPE           = "EXTRA_TYPE";
    private static boolean         IS_LOGIN_FROM_WECHAT = false;
    private static final String    TAG                  = WXEntryActivity.class.getSimpleName();
    public static final String     PHONENO              = "com.nahuo.bw.b.LoginActivity.phoneNo";
    private Context                mContext             = this;
    private AutoCompleteTextViewEx edtAccount;
    private EditText             edtPassword;
    private Button                 btnLogin, btnForgotPwd, btnReg;

    private LoadingDialog loadingDialog;
    private Tencent mTencent;
    private IUiListener mGetQQAccessTokenListener, mGetQQUserInfoListener;

    private IWXAPI                 mWxAPI;
   // private WeChatAccessToken      mWeChatToken;
   // private QQUserInfo             mQQUserInfo;
    private Const.LoginFrom        mLoginFrom;
 //   public WeChatUserInfo          mWechatUserInfo;
    private ScrollView sv;

    private static enum Step {
        LOGIN, WECHAT_LOGIN1, WECHAT_LOGIN2, QQ_LOGIN, THIRD_LOGIN, CHECK_BIND_STATUS
    }

    public static enum Type {
        /** 登录 */
        LOGIN
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_login);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_titlebar_default);
//        initView();
//        initWeChatLogin();
//        initTencentLogin();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWxAPI.handleIntent(intent, this);
    }

    @Override
    public void onBackPressed() {
//        ViewHub.showExitDialog(this);
        ViewHub.showExitLightPopDialog(this);
    }

    /**
     * @description 初始化微信登录
     * @created 2014-12-18 上午11:56:41
     * @author ZZB
     */
    private void initWeChatLogin() {
        mWxAPI = WXAPIFactory.createWXAPI(mContext, Const.WeChatOpen.APP_ID_1);
        mWxAPI.registerApp(Const.WeChatOpen.APP_ID_1);
        mWxAPI.handleIntent(getIntent(), this);
    }

    /**
     * @description 初始化腾讯登录
     * @created 2014-12-18 上午11:29:46
     * @author ZZB
     */
    private void initTencentLogin() {
        mTencent = Tencent.createInstance(Const.TecentOpen.APP_ID, mContext);
        mGetQQAccessTokenListener = new IUiListener() {
            @Override
            public void onError(UiError e) {
                ViewHub.showShortToast(mContext, "onError");
            }

            @Override
            public void onComplete(Object response) {
                if (null == response) {
                    ViewHub.showShortToast(mContext, "登录失败");
                    return;
                }
                JSONObject jsonResponse = (JSONObject)response;
                if (null != jsonResponse && jsonResponse.length() == 0) {
                    ViewHub.showShortToast(mContext, "登录失败");
                    return;
                }
                try {
                    String tokenStr = jsonResponse.getString(Constants.PARAM_ACCESS_TOKEN);
                    long expires = jsonResponse.getLong(Constants.PARAM_EXPIRES_IN);
                    String openId = jsonResponse.getString(Constants.PARAM_OPEN_ID);
                    if (!TextUtils.isEmpty(tokenStr) && !TextUtils.isEmpty(openId)) {
                        mTencent.setAccessToken(tokenStr, expires + "");
                        mTencent.setOpenId(openId);
                      //  QQAccessToken token = new QQAccessToken(tokenStr, expires, openId);
                      //  QQAccessTokenKeeper.writeAccessToken(mContext, token);
                        com.tencent.connect.UserInfo userinfo = new com.tencent.connect.UserInfo(mContext,
                                mTencent.getQQToken());
                        userinfo.getUserInfo(mGetQQUserInfoListener);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onCancel() {}
        };
        mGetQQUserInfoListener = new IUiListener() {

            @Override
            public void onError(UiError e) {}

            @Override
            public void onComplete(Object response) {
                String json = response.toString();
                try {
                    // QQ登录成功，拿到QQ登录用户信息
                  //  mQQUserInfo = GsonHelper.jsonToObject(json, QQUserInfo.class);
                 //   BaiduStats.log(mContext, BaiduStats.EventId.QQ_LOGIN, mQQUserInfo.getNickName());
//                    new Task(Step.CHECK_BIND_STATUS).execute(Const.TecentOpen.APP_ID, QQAccessTokenKeeper
//                            .readAccessToken(mContext).getOpenId());
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onCancel() {}
        };
    }

    /**
     * 初始化视图
     * */
    private void initView() {

        sv = (ScrollView)findViewById(R.id.scroll);
//        findViewById(R.id.tv_test).setVisibility(Debug.CONST_DEBUG ? View.VISIBLE : View.GONE);
      //  findViewById(R.id.tv_test).setVisibility(HttpUtils.IS_LOCAL ? View.VISIBLE : View.GONE);
        TextView tvTitle = (TextView)findViewById(R.id.titlebar_tvTitle);
        tvTitle.setText("登录");
        loadingDialog = new LoadingDialog(mContext);
        edtAccount = (AutoCompleteTextViewEx)findViewById(R.id.login_edtAccount);
        edtPassword = (EditText)findViewById(R.id.login_edtPassword);
        edtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeScrollView();
                return false;
            }
        });
        btnLogin = (Button)findViewById(R.id.login_btnLogin);
        //btnForgotPwd = (Button)findViewById(R.id.login_btnForgotPwd);
       // btnReg = (Button)findViewById(R.id.login_btnReg);
        edtAccount.setOnSearchLogDeleteListener(new AutoCompleteTextViewEx.OnSearchLogDeleteListener() {
            @Override
            public void onSearchLogDeleted(String text) {
                String newChar = SpManager.deleteLoginAccounts(getApplicationContext(), text);
                Log.i(getClass().getSimpleName(), "deleteSearchItemHistories:" + newChar);
                edtAccount.populateData(newChar, ",");
                edtAccount.getFilter().filter(edtAccount.getText());
            }
        });
        edtAccount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeScrollView();
                return false;
            }
        });


        String username = SpManager.getLoginAccounts(mContext);
        edtAccount.populateData(username, ",");

        edtAccount.setText(SpManager.getLoginAccount(mContext));

        btnLogin.setOnClickListener(this);
        btnForgotPwd.setOnClickListener(this);
        btnReg.setOnClickListener(this);
//        findViewById(R.id.btn_qq_login).setOnClickListener(this);
//        findViewById(R.id.btn_wx_login).setOnClickListener(this);
        findViewById(R.id.img_see_pwd).setOnClickListener(this) ; 
        findViewById(R.id.test).setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ViewHub.showLongToast(mContext, (Debug.CONST_DEBUG ? "测试：" : "正式：") + Debug.BUILD_VERSION_DATE + " " + HttpUtils.SERVERURL);
                return false;
            }
        }) ;
    }

    private void changeScrollView(){
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                sv.scrollTo(0, sv.getHeight());
            }
        }, 300);
    }

    Handler h = new Handler();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_see_pwd:
                int length = edtPassword.getText().length() ; 
                if(length> 0){
                    if(edtPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
                        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD) ;
                        edtPassword.invalidate() ; 
                        edtPassword.setSelection(length) ; 
                    }
                    else{
                        edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD) ;
                        edtPassword.invalidate() ; 
                        edtPassword.setSelection(edtPassword.getText().length()) ; 
                    }
                }
                break ; 
            case R.id.login_btnLogin:
                String phoneNo = edtAccount.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                login(phoneNo,password);
//                login();
                break;
//            case R.id.login_btnReg:
//                toReg() ;
//                break;
//            case R.id.login_btnForgotPwd:
//                Intent findPwdIntent = new Intent(mContext, ForgotPwdActivity.class);
//                findPwdIntent.putExtra(PHONENO, edtAccount.getText().toString().trim());
//                startActivity(findPwdIntent);
//                break;
//            case R.id.btn_qq_login:// QQ登录
//                qqLogin();
//                break;
//            case R.id.btn_wx_login:// 微信登录
//                wechatLogin();
//                break;
        }
    }

    private void toReg() {
        String phone = edtAccount.getText().toString() ;
        if(!FunctionHelper.isPhoneNo(phone)){
            phone = null ;
        }
//        Intent regIntent = new Intent(mContext, UserRegActivity.class);
//        regIntent.putExtra("phone", phone) ;
//        startActivity(regIntent);
//        Intent intent = new Intent(this, SignUpActivity.class);
//        intent.putExtra("phone", phone) ;
//        startActivity(intent);
    }

    /**
     * @description QQ登录
     * @created 2014-12-19 下午6:07:10
     * @author ZZB
     */
    private void qqLogin() {
        mLoginFrom = Const.LoginFrom.QQ;
//        QQAccessToken token = QQAccessTokenKeeper.readAccessToken(mContext);
//        long expiresIn = token.getExpiresTime() - System.currentTimeMillis();
//        mTencent.setAccessToken(token.getAccessToken(), expiresIn + "");
//        mTencent.setOpenId(token.getOpenId());
//        if (!mTencent.isSessionValid()) {
//            mTencent.login(this, "all", mGetQQAccessTokenListener);
//        } else {
//            mTencent.logout(mContext);
//            QQAccessTokenKeeper.clear(mContext);
//        }
    }

    /**
     * @description 微信登录
     * @created 2014-12-19 下午5:11:08
     * @author ZZB
     */
    private void wechatLogin() {
        IS_LOGIN_FROM_WECHAT = true;
        mLoginFrom = Const.LoginFrom.WECHAT;
        boolean isWxSupport = mWxAPI.isWXAppInstalled();
        if (!isWxSupport) {
            ViewHub.showLongToast(mContext, "您未安装微信或者微信版本过低");
            return;
        }
//        mWeChatToken = WeChatAccessTokenKeeper.readAccessToken(mContext);
//        if (!mWeChatToken.isSessionValid()) {
//            SendAuth.Req req = new SendAuth.Req();
//            req.scope = "snsapi_userinfo";
//            req.state = "state";
//            mWxAPI.sendReq(req);
//        } else {
//            new Task(Step.WECHAT_LOGIN2).execute();
//        }

    }

    /**
     * 登录
     * */
    private void login(String phoneNo,String password) {



        // 验证用户录入
        if (!validateInput())
            return;
        // 验证网络
        if (!FunctionHelper.CheckNetworkOnline(mContext))
            return;
        // 执行登录操作
//        new Task(Step.LOGIN).execute();
        //new Task(Step.LOGIN,phoneNo,password).execute();
    }

    /**
     * 验证用户输入
     * */
    private boolean validateInput() {
        String phoneNo = edtAccount.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        // 验证手机号
        if (TextUtils.isEmpty(phoneNo)) {
            Toast.makeText(mContext, R.string.login_edtAccount_empty, Toast.LENGTH_SHORT).show();
            edtAccount.requestFocus();
            return false;
        }
        // 验证密码
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(mContext, R.string.login_edtPassword_empty, Toast.LENGTH_SHORT).show();
            edtPassword.requestFocus();
            return false;
        }
        return true;
    }

//    private class Task extends AsyncTask<Object, Void, Object> {
//        private Step mStep;
//        private String phoneNo ;
//        private String password;
//
//        public Task(Step step) {
//            mStep = step;
//        }
//        public Task(Step step,String name ,String pwd) {
//            mStep = step;
//            this.phoneNo = name;
//            this.password = pwd;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            switch (mStep) {
//                case LOGIN:
//                    loadingDialog.start(getString(R.string.login_doLogin_loading));
//                    break;
//                case QQ_LOGIN:
//                    loadingDialog.start("登录中...");
//                    break;
//                case WECHAT_LOGIN1:
//                case WECHAT_LOGIN2:
//                    loadingDialog.start("登录中...");
//                    break;
//                case CHECK_BIND_STATUS:
//                    loadingDialog.start("检查绑定状态中...");
//                    break;
//                case THIRD_LOGIN:
//                    loadingDialog.start("登录中...");
//                    break;
//            }
//
//        }
//
//        @Override
//        protected Object doInBackground(Object... params) {
//            try {
//                switch (mStep) {
////                    case LOGIN:
//////                        String phoneNo = edtAccount.getText().toString().trim();
//////                        String password = edtPassword.getText().toString().trim();
////                        //提交登录记录操作
////                        //imei
////                        String imei = Utils.GetAndroidImei(mContext);
////                        //android版本号
////                        String currentapiVersion = android.os.Build.VERSION.RELEASE;
////                        //手机型号
////                        String phoneName=android.os.Build.MANUFACTURER;
////                        //网络
////                        String netName = Utils.GetNetworkType(mContext);
////                        return AccountAPI.getInstance().userLogin(phoneNo, password,imei,phoneName, "android "+ currentapiVersion,netName);
//
//
//                    case QQ_LOGIN:
//                    case WECHAT_LOGIN1:// 获取access token
//                        String code = params[0].toString();
//                        mWeChatToken = WeChatAPI.getLoginAccessToken(mContext, code);
//                        // 保存access token到本地
//                        WeChatAccessTokenKeeper.writeAccessToken(mContext, mWeChatToken);
//
//                    case WECHAT_LOGIN2:// 获取微信用户信息
//                        WeChatUserInfo userInfo = WeChatAPI.getUserInfo(mContext, mWeChatToken);
//                        BaiduStats.log(mContext, BaiduStats.EventId.WECHAT_LOGIN, userInfo.getNickName());
//                        return userInfo;
//                    case CHECK_BIND_STATUS:// 检查绑定状态
//                        String appId = params[0].toString();
//                        String openId = params[1].toString();
//                        boolean isBinded = AccountAPI.isThirdLoginBinded(appId, openId);
//                        return isBinded;
//                    case THIRD_LOGIN:// 第三方登录
//                        switch (mLoginFrom) {
//                            case QQ:
//                                QQAccessToken token = QQAccessTokenKeeper.readAccessToken(mContext);
//                                AccountAPI.thirdLogin(Const.TecentOpen.APP_ID, token.getOpenId(),
//                                        token.getAccessToken());
//                                break;
//                            case WECHAT:
//                                WeChatAccessToken wechatToken = WeChatAccessTokenKeeper.readAccessToken(mContext);
//                                AccountAPI.thirdLogin(Const.WeChatOpen.APP_ID_1, wechatToken.getOpenId(),
//                                        wechatToken.getAccessToken());
//                                break;
//                        }
//
//                        break;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                return "error:" + e.getMessage();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Object result) {
//            super.onPostExecute(result);
//            if (loadingDialog.isShowing()) {
//                loadingDialog.stop();
//            }
//            if (result instanceof String && ((String)result).startsWith("error:")) {
//                ViewHub.showLongToast(mContext, mStep.toString() + ":" + result.toString().replace("error:", ""));
//            } else {
//                switch (mStep) {
//                    case LOGIN:
//                        if (!TextUtils.isEmpty(PublicData.getCookie(mContext))) {
//                            SpManager.setCookie(mContext, PublicData.getCookie(mContext));
//                        }
//                        String msg = (String)result ;
//                        if("user_no_exist".equals(msg)){ // 用户不存在
//                            DialogUtils.showSureCancelDialog(WXEntryActivity.this, "登陆提示", "该账号还没注册，是否立即去注册？"
//                                    ,"注册账号" , "换号登陆", new OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Dialog dialog = (Dialog)v.getTag() ;
//                                            dialog.dismiss() ;
//                                            //注册账号
//                                            toReg() ;
//                                        }
//                                    }, new OnClickListener(){
//                                        @Override
//                                        public void onClick(View v) {
//                                            Dialog dialog = (Dialog)v.getTag() ;
//                                            dialog.dismiss() ;
//                                            edtAccount.setText(null) ;
//                                            edtPassword.setText(null) ;
//                                            edtAccount.requestFocus() ;
//                                            edtAccount.setSelection(0) ;
//                                        }
//                                    }) ;
//                            break ;
//                        }
//                        else if("password_error".equals(msg)){//密码错误
//                            DialogUtils.showToast(mContext, "你输入的密码错误，\n请重新填写。", 2000) ;
//                            break ;
//                        }
//
//
//
//                        SpManager.setLoginAccount(mContext, edtAccount.getText().toString());
//                        Intent intent = new Intent(mContext, MainActivity.class);
//                        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        startActivity(intent);
//                        finish();
//                        break;
//                    case QQ_LOGIN:
//                        break;
//                    case WECHAT_LOGIN1:
//                    case WECHAT_LOGIN2:
//                        mWechatUserInfo = (WeChatUserInfo)result;
//                        new Task(Step.CHECK_BIND_STATUS).execute(Const.WeChatOpen.APP_ID_1, WeChatAccessTokenKeeper
//                                .readAccessToken(mContext).getOpenId());
//                        break;
//                    case CHECK_BIND_STATUS:
//                        boolean isBinded = (Boolean)result;
//                        if (isBinded) {// 如果已经绑定，执行第三方登录
//                            new Task(Step.THIRD_LOGIN).execute();
//                        } else {
//                            Intent registerIntent = new Intent(mContext, BindOrRegisterUserActivity.class);
//                            registerIntent.putExtra(BindOrRegisterUserActivity.EXTRA_LOGIN_FROM, mLoginFrom);
//                            String iconUrl = "";
//                            String userName = "";
//                            switch (mLoginFrom) {
//                                case QQ:
//                                    iconUrl = mQQUserInfo.getIconUrl100();
//                                    userName = mQQUserInfo.getNickName();
//                                    break;
//                                case WECHAT:
//                                    iconUrl = mWechatUserInfo.getImgUrl();
//                                    userName = mWechatUserInfo.getNickName();
//                                    break;
//                            }
//                            registerIntent.putExtra(BindOrRegisterUserActivity.EXTRA_ICON_URL, iconUrl);
//                            registerIntent.putExtra(BindOrRegisterUserActivity.EXTRA_USER_NAME, userName);
//                            startActivity(registerIntent);
//                        }
//                        break;
//                    case THIRD_LOGIN:
//                        if (!TextUtils.isEmpty(PublicData.getCookie(mContext))) {
//                            SpManager.setCookie(mContext, PublicData.getCookie(mContext));
//                        }
//                        Intent thirdIntent = new Intent(mContext, MainActivity.class);
//                        // thirdIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        startActivity(thirdIntent);
//                        finish();
//                        break;
//                }
//            }
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    public void onReq(BaseReq req) {}

    @Override
    public void onResp(BaseResp resp) {
        if (IS_LOGIN_FROM_WECHAT) {
            IS_LOGIN_FROM_WECHAT = false;
        } else {
            finish();
            return;
        }

        mLoginFrom = Const.LoginFrom.WECHAT;
        try {
            switch (resp.errCode) {
                case 0:// 用户同意
                    SendAuth.Resp r = (SendAuth.Resp)resp;
                //    new Task(Step.WECHAT_LOGIN1).execute(r.code);
                    break;
                case -4:// 用户拒绝授权
                    ViewHub.showShortToast(mContext, "用户拒绝授权");
                    break;
                case -2:// 用户取消
                    ViewHub.showShortToast(mContext, "用户取消");
                    break;
            }
        } catch (Exception e) {
            if (e != null) {
                Log.e(TAG, e.toString());
            }

        }

    }

}
