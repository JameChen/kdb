package com.nahuo.kdb;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gprinter.aidl.GpService;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.nahuo.kdb.activity.AchievementActivity;
import com.nahuo.kdb.activity.BlueToothActivity;
import com.nahuo.kdb.activity.CommodityManagementActivity;
import com.nahuo.kdb.activity.ReservationActivity;
import com.nahuo.kdb.activity.Sq_meActivity;
import com.nahuo.kdb.adapter.MainAdapter;
import com.nahuo.kdb.adapter.MeItemAdatper;
import com.nahuo.kdb.adapter.MenuAdapter;
import com.nahuo.kdb.api.AccountAPI;
import com.nahuo.kdb.api.ApiHelper;
import com.nahuo.kdb.api.HttpUtils;
import com.nahuo.kdb.common.BlurTransform;
import com.nahuo.kdb.common.Const;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.common.MenuID;
import com.nahuo.kdb.common.ShareBottomMenu;
import com.nahuo.kdb.common.ShareEntity;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.common.WeChatShareUtil;
import com.nahuo.kdb.eventbus.BusEvent;
import com.nahuo.kdb.eventbus.EventBusId;
import com.nahuo.kdb.hx.Constant;
import com.nahuo.kdb.hx.ConversationActivity;
import com.nahuo.kdb.hx.DemoHelper;
import com.nahuo.kdb.hx.LoginMenu;
import com.nahuo.kdb.hx.PermissionsManager;
import com.nahuo.kdb.hx.PermissionsResultAction;
import com.nahuo.kdb.model.MeItemModel;
import com.nahuo.kdb.model.MenuBean;
import com.nahuo.kdb.model.PublicData;
import com.nahuo.kdb.model.ShopInfoModel;
import com.nahuo.kdb.model.UserModel;
import com.nahuo.kdb.provider.UserInfoProvider;
import com.nahuo.library.controls.LightPopDialog;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.DisplayUtil;
import com.nahuo.library.helper.ImageUrlExtends;
import com.nahuo.library.helper.MD5Utils;
import com.nahuo.service.autoupdate.AppUpdate;
import com.nahuo.service.autoupdate.AppUpdateService;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.nahuo.kdb.api.HttpUtils.ECC_OPEN;
import static com.nahuo.kdb.common.MenuID.ID_InStorage;
import static com.nahuo.kdb.common.MenuID.ID_Item;
import static com.nahuo.kdb.common.MenuID.ID_Stock;

public class MainAcivity extends FragmentActivity implements MeItemAdatper.OnMeItemListener, View.OnClickListener {//, OnTitleBarClickListener

    private static final String TAG = "MainAcivity";
    private MainAcivity Vthis = this;
    private GridView mGrdClass;
    private MeItemAdatper adatper;
    private EventBus mEventBus = EventBus.getDefault();
    private CircleImageView mIvAvatar;
    private AppUpdate mAppUpdate;
    private boolean viewIsLoaded = false;
    private LoadingDialog loadingDialog = null;
    private Dialog dialog = null;
    private static final String ERROR_PREFIX = "error:";
    private List<String> menus = new ArrayList<>();
    private boolean isExceptionDialogShow = false;
    private ShareBottomMenu sharePOP;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver broadcastReceiver;
    private List<MeItemModel> list = new ArrayList<>();
    private TextView my_bluetool_info;

    private RecyclerView recyclerView,rv_item;
    private TextView tv_account_name, tv_exit,tv_head_name;
    private MenuAdapter menuAdapter;
    private MainAdapter mainAdapter;
    private View head1, head2;
    private ImageView iv_head_icon;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_exit:
                ViewHub.showLightPopDialog(this, getString(R.string.dialog_title),
                        getString(R.string.shopset_exit_confirm), "取消", "退出登录", new LightPopDialog.PopDialogListener() {
                            @Override
                            public void onPopDialogButtonClick(int which) {
                                UserInfoProvider.exitLogin(Vthis);
                                finish();
                            }
                        });
                break;
        }
    }

    private enum Step {
        LOAD_USER_INFO, LOAD_SHOP_INFO, LOAD_MAIN_MENUS
    }

    public boolean isConflict = false;
    private boolean isCurrentAccountRemoved = false;

    /**
     * check if current user account was remove
     */
    public boolean getCurrentAccountRemoved() {
        return isCurrentAccountRemoved;
    }

    private BroadcastReceiver internalDebugReceiver;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private GpService mGpService;
    private PrinterServiceConnection conn = null;

    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Log.i(DEBUG_TAG, "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
        }
    }

    private void connection() {
        conn = new PrinterServiceConnection();
        // Log.i(DEBUG_TAG, "connection");
        Intent intent = new Intent(this, GpPrintService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                try {
                    //some device doesn't has activity to handle this intent
                    //so add try catch
                    Intent intent = new Intent();
                    intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);
                } catch (Exception e) {
                }
            }
        }
        if (ECC_OPEN) {
            if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
                DemoHelper.getInstance().logout(false, null);
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            } else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
        }
        setContentView(R.layout.activity_main);
        connection();
        initMenu();
        init();
        requestPermissions();
        mEventBus.registerSticky(this);
        if (ECC_OPEN) {
            registerInternalDebugReceiver();
        }
        loadingDialog = new LoadingDialog(Vthis);
        // 初始化版本自动更新组件
        mAppUpdate = AppUpdateService.getAppUpdate(this);
        if (SpManager.getUserId(this) > 0) {
            initView();
        } else {
            viewIsLoaded = false;
        }
        new Task(Step.LOAD_MAIN_MENUS).execute();
        new Task(Step.LOAD_USER_INFO).execute();
        new Task(Step.LOAD_SHOP_INFO).execute();
    }

    private void initMenu() {
        recyclerView = (RecyclerView) findViewById(R.id.rv_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        tv_exit.setOnClickListener(this);
        tv_account_name = (TextView) findViewById(R.id.tv_account_name);
        head1 = getLayoutInflater().inflate(R.layout.head_main_lv1, (ViewGroup) recyclerView.getParent(), false);
        if (head1!=null){
            iv_head_icon= (ImageView) head1.findViewById(R.id.iv_head_icon);
            tv_head_name= (TextView) head1.findViewById(R.id.tv_head_name);
            head1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Vthis, ShopCartActivity.class);
                    startActivity(intent);
                }
            });
        }
        head2 = LayoutInflater.from(this).inflate(R.layout.head_main_lv2, (ViewGroup) recyclerView.getParent(), false);
        menuAdapter=new MenuAdapter(this,null);
        mainAdapter=new MainAdapter(this);
        recyclerView.setAdapter(mainAdapter);
        menuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<MenuBean.MenusBean> data=  adapter.getData();
                if (position>=0) {
                    MenuBean.MenusBean bean = data.get(position);
                    if (bean!=null){
                        Intent intent = new Intent(Vthis, CommonListActivity.class);
                        switch (bean.getID()) {
                            case ID_InStorage:
                                //入库
                                intent.putExtra("type", 2);
                                startActivity(intent);
                                break;
                            case ID_Stock:
                                //库存
                                intent.putExtra("type", 3);
                                startActivity(intent);
                                break;
                            case ID_Item:
                                //商品
                                startActivity(new Intent(Vthis,CommodityManagementActivity.class));
                                break;

                        }
                    }
                }
            }
        });
        mainAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
              List<MenuBean.NavBarBean> data=  adapter.getData();
                if (position>=0) {
                    MenuBean.NavBarBean bean=  data.get(position);
                    if (bean!=null){
                        switch (bean.getID()) {
                            case MenuID.ID_Bluetooth:
                                startActivity(new Intent(MainAcivity.this, BlueToothActivity.class));
                                break;
                            case MenuID.ID_SaleBill:
//                                Intent intent = new Intent(Vthis, CommonListActivity.class);
//                                intent.putExtra("type", 1);
//                                startActivity(intent);
                                Intent intent1 = new Intent(Vthis, SaleLogActivity.class);
                                startActivity(intent1);
                                break;
                            case MenuID.ID_Set:
                                Intent sintent = new Intent(Vthis, MeSettingActivity.class);
                                startActivity(sintent);
                                break;
                            case MenuID.ID_ModifyPriceBill:
                                //调价单
                                break;
                            case MenuID.ID_Achievement:
                                //业绩PK
                                startActivity(new Intent(Vthis, AchievementActivity.class));
                                break;
                        }
                    }
                }
            }
        });

        if (head2 != null) {
            rv_item= (RecyclerView) head2.findViewById(R.id.rv_item);
            rv_item.setNestedScrollingEnabled(false);
            rv_item.setLayoutManager(new GridLayoutManager(this,3));
            rv_item.setAdapter(menuAdapter);
        }
    }

    private String blue_conect="蓝牙连接状态(未连接)";
    @Override
    protected void onResume() {
        super.onResume();
        if (ECC_OPEN) {
            EMClient.getInstance().chatManager().addMessageListener(messageListener);
            EaseUI.getInstance().getNotifier().reset();
            DemoHelper sdkHelper = DemoHelper.getInstance();
            sdkHelper.pushActivity(this);
            updateUnreadLabel();
        }
        try {
            if (mGpService != null) {
                if (mGpService.getPrinterConnectStatus(BWApplication.getInstance().PrinterId) == GpDevice.STATE_CONNECTED) {
                    if (my_bluetool_info != null) {
                        my_bluetool_info.setText("蓝牙连接状态：已连接");
                    }
                    blue_conect="蓝牙连接状态(已连接)";

                } else {
                    if (my_bluetool_info != null) {
                        my_bluetool_info.setText("蓝牙连接状态：未连接");
                    }
                    blue_conect="蓝牙连接状态(未连接)";
                }
            } else {
                if (my_bluetool_info != null) {
                    my_bluetool_info.setText("蓝牙连接状态：未连接");
                }
                blue_conect="蓝牙连接状态(未连接)";
            }
            if (mainAdapter!=null)
                mainAdapter.setBlueToothData(blue_conect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ECC_OPEN) {
            EMClient.getInstance().chatManager().removeMessageListener(messageListener);
            DemoHelper sdkHelper = DemoHelper.getInstance();
            sdkHelper.popActivity(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (ECC_OPEN) {
            outState.putBoolean("isConflict", isConflict);
            outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
        }
        super.onSaveInstanceState(outState);
    }

    private static IWXAPI mWxAPI;

    public void init() {
        mGrdClass = (GridView) Vthis.findViewById(R.id.grd_class);
        adatper = new MeItemAdatper(Vthis, list);
        mGrdClass.setAdapter(adatper);
        adatper.setStyleListener(this);
        my_bluetool_info = (TextView) findViewById(R.id.my_bluetool_info);
        findViewById(R.id.my_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Vthis, MeSettingActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.my_bluetool_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainAcivity.this, BlueToothActivity.class));
            }
        });
        findViewById(R.id.my_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sharePOP = new ShareBottomMenu(MainAcivity.this);
//                sharePOP.setTouchable(true);
//                sharePOP.show((View) v.getParent());
//                else if (supportApi < WX_TIMELINE_SUPPORT_API && toWxTimeLine) {
//                    ViewHub.showLongToast(mContext, "您的微信版本不支持分享到朋友圈");
//                    return;
//                }
                if (mWxAPI == null) {
                    mWxAPI = WXAPIFactory.createWXAPI(MainAcivity.this, Const.WeChatOpen.APP_ID);
                    mWxAPI.registerApp(Const.WeChatOpen.APP_ID);
                }
                final ShareEntity share = new ShareEntity();

                new AsyncTask<Void, Void, SendMessageToWX.Req>() {

                    @Override
                    protected void onPostExecute(SendMessageToWX.Req req) {
                        super.onPostExecute(req);
                        if (req != null) {
                            mWxAPI.sendReq(req);
                        }
                    }

                    @Override
                    protected SendMessageToWX.Req doInBackground(Void... params) {
                        try {
                            return WeChatShareUtil.shareMiNiAppToWX(MainAcivity.this, share.getTargetUrl(), Const.WECHAT_MINIAPP_IDS, share.getMiniAppUrl(), share.getTitle(), share.getSummary(), share);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();

                    }
                }.execute();

            }
        });
    /*    findViewById(R.id.rl_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转分享小程序
            }
        });*/
        // 检查更新
       /* findViewById(R.id.my_setting).postDelayed(new Runnable() {
            @Override
            public void run() {
                new CheckUpdateTask(Vthis, mAppUpdate, false, false).execute();
            }
        }, 10000);*/
    }

    // 初始化数据
    private void initView() {
        if (viewIsLoaded) {
            return;
        }
        // initgrid();
        initData();
        viewIsLoaded = true;
//        Intent intent = new Intent(this, ItemDetailsActivity.class);
//            intent.putExtra(ItemDetailsActivity.EXTRA_ID, 123);
//        intent.putExtra(ItemDetailsActivity.EXTRA_QSID, 123);
//        intent.putExtra("type", CommonListActivity.ListType.待开单);
//        startActivity(intent);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        String userName = SpManager.getUserName(BWApplication.getInstance());
        String logo = SpManager.getShopLogo(BWApplication.getInstance()).trim();// SpManager.getUserLogo(NHApplication.getInstance());
        TextView name = (TextView) Vthis.findViewById(R.id.txt_name);
        mIvAvatar = (CircleImageView) Vthis.findViewById(R.id.iv_userhead);
        mIvAvatar.setBorderWidth(DisplayUtil.dip2px(this, 2));
        mIvAvatar.setBorderColor(getResources().getColor(R.color.white));
        if (userName.length() > 0)
            name.setText(userName);
        String url = "";
        if (TextUtils.isEmpty(logo)) {
            url = Const.getShopLogo(SpManager.getUserId(BWApplication.getInstance()));
        } else {
            url = ImageUrlExtends.getImageUrl(logo, 8);
        }
        Picasso.with(BWApplication.getInstance()).load(url)
                .placeholder(R.drawable.empty_photo).into(mIvAvatar);
        initLogoBgView(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEventBus!=null)
        mEventBus.unregister(this);
        try {
            if (ECC_OPEN) {
                unregisterReceiver(internalDebugReceiver);
            }
            if (conn != null) {
                unbindService(conn); // unBindService
            }
        } catch (Exception e) {
        }
    }

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
//            for (EMMessage message : messages) {
//               // DemoHelper.getInstance().getNotifier().onNewMsg(message);
//            }
            updateUnreadLabel();

        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //red packet code : 处理红包回执透传消息
//            for (EMMessage message : messages) {
//                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
//                final String action = cmdMsgBody.action();//获取自定义action
//                if (action.equals(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
//                    RedPacketUtil.receiveRedPacketAckMessage(message);
//                }
//            }
//            //end of red packet code
//            refreshUIWithMessage();
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
        }
    };


    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        //intentFilter.addAction(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                updateUnreadLabel();
                // updateUnreadAddressLable();
              /*  if (currentTabIndex == 0) {
                    // refresh conversation list
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                } else if (currentTabIndex == 1) {
                    if(contactListFragment != null) {
                        contactListFragment.refresh();
                    }
                }*/
//                String action = intent.getAction();
//                if(action.equals(Constant.ACTION_GROUP_CHANAGED)){
//                    if (EaseCommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//                        GroupsActivity.instance.onResume();
//                    }
//                }

            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * update unread message count
     */
    public void updateUnreadLabel() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int count = getUnreadMsgCountTotal();
                if (adatper != null) {
                    adatper.showUnRead("消息", count);
                }
            }
        });

    }

    /**
     * get unread message count
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        return EMClient.getInstance().chatManager().getUnreadMsgsCount();
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.id) {

            case EventBusId.SHOP_LOGO_UPDATED:// 店铺logo有修改
                String logo = SpManager.getShopLogo(Vthis);
                if (!TextUtils.isEmpty(logo)) {
                    String url = ImageUrlExtends.getImageUrl(logo, Const.LIST_HEADER_COVER_SIZE);
                    Picasso.with(Vthis).load(url).skipMemoryCache().placeholder(R.drawable.empty_photo)
                            .into(mIvAvatar);

                    initLogoBgView(url);
                }
                break;
            case EventBusId.ON_APP_EXIT:
                finish();
                break;
        }
    }

    private void initLogoBgView(String url) {
//        ((ImageView)Vthis.findViewById(R.id.iv_logobg)).setImageBitmap(ImageTools.blurBitmap(Vthis,mIvAvatar.get));
        Picasso.with(BWApplication.getInstance())
                .load(url)
                .transform(new BlurTransform(80))
                .placeholder(R.drawable.empty_photo).into((ImageView) Vthis.findViewById(R.id.iv_logobg));
    }

    private void initgrid() {
        list.clear();
        if (menus != null && menus.size() > 0) {
            int nums = menus.size();
            for (String menu : menus) {
                MeItemModel model = new MeItemModel();
                model.setName(menu);
                switch (menu) {
                    case "销售":
                        model.setSourceId(R.drawable.icon_xiaoshou);
                        break;
                    case "入库":
                        model.setSourceId(R.drawable.icon_rku);
                        break;
                    case "库存":
                        model.setSourceId(R.drawable.icon_kuc);
                        break;
                    case "共享":

                        break;
                    case "消息":
                        model.setSourceId(R.drawable.icon_message);
                        break;
                    case "我的":
                        break;
                    case "预约单":
                        model.setSourceId(R.drawable.icon_yuydan);
                        break;
                }
                list.add(model);
            }
            if (nums % 2 != 0) {
                MeItemModel model = new MeItemModel();
                model.setName("");
                list.add(model);
            }
        }
        if (adatper != null) {
            adatper.notifyDataSetChanged();
        }
        updateUnreadLabel();
//        MeItemModel model1 = new MeItemModel();
//        model1.setName("销售");
//        model1.setType(1);
//        model1.setSourceId(R.drawable.wodedingdan);
//        MeItemModel model2 = new MeItemModel();
//        model2.setName("入库");
//        model2.setType(2);
//        model2.setSourceId(R.drawable.wodedingdan);
//        MeItemModel model3 = new MeItemModel();
//        model3.setName("库存");
//        model3.setType(3);
//        model3.setSourceId(R.drawable.wodedingdan);
//        MeItemModel model4 = new MeItemModel();
//        model4.setName("共享");
//        model4.setType(4);
//        model4.setSourceId(R.drawable.wodedingdan);
//        MeItemModel model5 = new MeItemModel();
//        model5.setName("消息");
//        model5.setType(5);
//        model5.setSourceId(R.drawable.wodedingdan);
//        MeItemModel model6 = new MeItemModel();
//        model6.setName("我的");
//        model6.setType(6);
//        model6.setSourceId(R.drawable.wodedingdan);
//        MeItemModel model7 = new MeItemModel();
//        model7.setName("预约单");
//        model7.setType(7);
//        model7.setSourceId(R.drawable.wodedingdan);
//        list.add(model1);
//        list.add(model2);
//        list.add(model3);
//        // list.add(model4);
//        list.add(model7);
//        list.add(model5);
//        MeItemModel model = new MeItemModel();
//        list.add(model);
//        // list.add(model6);
//        adatper = new MeItemAdatper(Vthis, list);
//        mGrdClass.setAdapter(adatper);
//        adatper.setStyleListener(this);
    }


    @Override
    public void OnMeItemClick(MeItemModel item) {
        Intent intent = new Intent(Vthis, CommonListActivity.class);
        switch (item.getName()) {
            case "销售":
                //销售
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
            case "入库":
                //入库
                intent.putExtra("type", 2);
                startActivity(intent);
                break;
            case "库存":
                //库存
                intent.putExtra("type", 3);
                startActivity(intent);
                break;
            case "共享":
                //共享

                break;
            case "消息":
                //消息
                Intent intentCo = new Intent(Vthis, ConversationActivity.class);
                startActivity(intentCo);

                break;
            case "我的":
                //我的
                Intent intentMe = new Intent(Vthis, Sq_meActivity.class);
                startActivity(intentMe);
                break;
            case "预约单":
                //预约单
                Intent intentre = new Intent(Vthis, ReservationActivity.class);
                startActivity(intentre);
                break;
        }

    }

    private class Task extends AsyncTask<Object, Void, Object> {
        private Step mStep;

        public Task(Step step) {
            mStep = step;
        }

        @Override
        protected void onPreExecute() {
            switch (mStep) {
                case LOAD_USER_INFO:
                    loadingDialog.start("加载数据中...");
                    break;
                case LOAD_SHOP_INFO:
                    break;
            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                switch (mStep) {
                    case LOAD_USER_INFO:
                        return loadUserInfo();
                    case LOAD_SHOP_INFO:
                        loadShopInfo();
                        return "OK";
                    case LOAD_MAIN_MENUS:
                        MenuBean menuBean = AccountAPI.getInstance().getMainMenus(PublicData.getCookie(Vthis));
                        return menuBean;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (loadingDialog.isShowing()) {
                loadingDialog.stop();
            }
            if (result == null || result.equals(""))
                return;
            if (result instanceof String && ((String) result).startsWith(ERROR_PREFIX)) {
                String msg = ((String) result).replace(ERROR_PREFIX, "");
                if ( mStep == Step.LOAD_MAIN_MENUS){
                    if (msg.equals("您不具备权限!")){
                        Intent intent = new Intent(Vthis, LoginActivity.class);
                        intent.putExtra(LoginActivity.EXTA_ISSHOWERROR,true);
                        Vthis.startActivity(intent);
                        Vthis.finish();
                    }
                }else {
                    ViewHub.showLongToast(Vthis, msg);
                }
                // 验证result
                if (msg.toString().startsWith("401") || msg.toString().startsWith("not_registered")) {
                    ApiHelper.checkResult(msg, Vthis);
                }

                return;
            }
            switch (mStep) {
                case LOAD_USER_INFO:
                    userInfoLoaded(result);
                    break;
                case LOAD_SHOP_INFO:
                    break;
                case LOAD_MAIN_MENUS:
                    menus.clear();
                    MenuBean menuBean = (MenuBean) result;
                    if (menuBean != null) {
                        if (tv_account_name!=null)
                            tv_account_name.setText("欢迎，"+menuBean.getUserName());
                        SpManager.setUserId(MainAcivity.this, menuBean.getUserID());
                        if (menuBean.getPageElement() != null) {
                            SpManager.setPurchase(Vthis, menuBean.getPageElement().isIsShowBuyingPrice());
                            SpManager.setRetail(Vthis, menuBean.getPageElement().isIsShowRetailPrice());
                            SpManager.setEditTitle(Vthis, menuBean.getPageElement().isIsEditableTitle());
                            SpManager.setEditPrice(Vthis, menuBean.getPageElement().isIsEditablePrice());
                            SpManager.setIsadd_Group(Vthis, menuBean.getPageElement().isIsAddGroup());
                        }
                        if (mainAdapter!=null){
                            mainAdapter.removeAllHeaderView();
                        }
                        if (menuBean.getCashier()!=null){
                            if (menuBean.getCashier().isShow()){
                                if (mainAdapter!=null){
                                    mainAdapter.addHeaderView(head1);
                                }
                                if (tv_head_name!=null)
                                    tv_head_name.setText(menuBean.getCashier().getName());
                                String path=ImageUrlExtends.getImageUrl(menuBean.getCashier().getImgUrl());
                                if (!TextUtils.isEmpty(path)){
                                    if (iv_head_icon!=null)
                                    Picasso.with(BWApplication.getInstance()).load(path).placeholder(R.drawable.empty_photo).into(iv_head_icon);
                                }else {
                                    if (iv_head_icon!=null)
                                        iv_head_icon.setImageResource(R.drawable.empty_photo);
                                }
                            }
                        }

                        if (!ListUtils.isEmpty(menuBean.getMenus())) {
                            if (mainAdapter!=null){
                                if (head2!=null)
                                mainAdapter.addHeaderView(head2);
                            }
                            if (menuAdapter!=null)
                           menuAdapter.addMyData(menuBean.getMenus());
//                            menus.addAll(menuBean.getMenus());
//                            initgrid();
                        }else {
                            if (menuAdapter!=null)
                                menuAdapter.addMyData(null);
                        }
                        List<MenuBean.NavBarBean> data=new ArrayList<>();
                        if (!ListUtils.isEmpty(menuBean.getNavBar())){
                                data.addAll(menuBean.getNavBar());

                        }
                        MenuBean.NavBarBean navBarBean=new MenuBean.NavBarBean();
                        navBarBean.setID(MenuID.ID_Bluetooth);
                        navBarBean.setName(blue_conect);
                        data.add(navBarBean);
                        if (mainAdapter!=null)
                            mainAdapter.setMyData(data);
                        if (mainAdapter!=null)
                            mainAdapter.notifyDataSetChanged();
                            if (menuAdapter!=null)
                            menuAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        // ViewHub.showExitDialog(this);
        ViewHub.showExitLightPopDialog(Vthis);
    }

    /**
     * @description 基础数据加载完成
     */
    private void baseSetLoaded(Object result) {
    }

    /**
     * @description 读取完了是否有新广播帖子
     */
    // private void hasNewsLoaded(Object result) {
    // hasNews = Boolean.valueOf(result.toString());
    // }
    private void showExceptionDialogFromIntent(Intent intent) {
        if (ECC_OPEN) {
            EMLog.e(TAG, "showExceptionDialogFromIntent");
            if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false)) {
                showExceptionDialog(Constant.ACCOUNT_CONFLICT);
            } else if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false)) {
                showExceptionDialog(Constant.ACCOUNT_REMOVED);
            } else if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_FORBIDDEN, false)) {
                showExceptionDialog(Constant.ACCOUNT_FORBIDDEN);
            }
        }
    }

    private int getExceptionMessageId(String exceptionType) {
        if (exceptionType.equals(Constant.ACCOUNT_CONFLICT)) {
            return R.string.connect_conflict1;
        } else if (exceptionType.equals(Constant.ACCOUNT_REMOVED)) {
            return R.string.em_user_remove1;
        } else if (exceptionType.equals(Constant.ACCOUNT_FORBIDDEN)) {
            return R.string.user_forbidden1;
        }
        return R.string.Network_error;
    }

    /**
     * show the dialog when user met some exception: such as login on another device, user removed or user forbidden
     */
    private void showExceptionDialog(String exceptionType) {
        isExceptionDialogShow = true;
        DemoHelper.getInstance().logout(false, null);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainAcivity.this.isFinishing()) {
            // clear up global variables
            try {

                LoginMenu.getInstance(this, st, getString(getExceptionMessageId(exceptionType)) + "")
                        .setPositive(new LoginMenu.PopDialogListener() {
                            @Override
                            public void onPopDialogButtonClick(int which) {
                                isExceptionDialogShow = false;
                                finish();
                                Intent intent = new Intent(MainAcivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();

                isConflict = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
            }
        }
    }

    /**
     * debug purpose only, you can ignore this
     */
    private void registerInternalDebugReceiver() {
        internalDebugReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (ECC_OPEN){
                DemoHelper.getInstance().logout(false, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                finish();
                                startActivity(new Intent(MainAcivity.this, LoginActivity.class));
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                    }
                });
            }}
        };
        try {
            IntentFilter filter = new IntentFilter(getPackageName() + ".em_internal_debug");
            registerReceiver(internalDebugReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showExceptionDialogFromIntent(intent);
    }

    /**
     * @description 加载用户数据
     * @created 2014-9-3 下午1:42:46
     * @author ZZB
     */
    private Object loadUserInfo() {
        try {
            UserModel userinfo = AccountAPI.getInstance().getUserInfo(PublicData.getCookie(Vthis));
            SpManager.setUserInfo(this, userinfo);
            if (HttpUtils.ECC_OPEN) {
                String username = String.valueOf(userinfo.getUserID());
                String pwd = MD5Utils.encrypt32bit(username);
                if (!EMClient.getInstance().isConnected()) {
                    if (!AccountAPI.IsRegIMUser(PublicData.getCookie(this), username)) {
                        try {
                            EMClient.getInstance().createAccount(username, pwd);
                            chatlogin(username, pwd, userinfo.getUserName());
                        } catch (final HyphenateException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    int errorCode = e.getErrorCode();
                                    if (errorCode == EMError.NETWORK_ERROR) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                                    } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                                    } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                        Toast.makeText(getApplicationContext(), "无权限", Toast.LENGTH_SHORT).show();
                                    } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "环信注册失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    } else {
                        chatlogin(username, pwd, userinfo.getUserName());
                    }
                } else {
                    String userid = EMClient.getInstance().getCurrentUser();
                    if (!userid.equals(username)) {
                        EMClient.getInstance().logout(false, new EMCallBack() {

                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "logout: onSuccess");
                                startActivity(new Intent(MainAcivity.this, LoginActivity.class));
                            }

                            @Override
                            public void onProgress(int progress, String status) {
                            }

                            @Override
                            public void onError(int code, String error) {
                                Log.d(TAG, "logout: onSuccess");
                            }
                        });
                    }
                }
            }
            if (userinfo != null) {
                return userinfo;
            } else {
                return "error:" + "没有找到个人";
            }
        } catch (Exception ex) {
            Log.e(TAG, "加载个人信息发生异常");
            ex.printStackTrace();
            return "error:" + ex.getMessage();
        }
    }

    /**
     * @desc ription 环信登录
     */
    // chat登录
    private void chatlogin(final String username, final String pwd, String nick) {
        EMClient.getInstance().login(username, pwd, new EMCallBack() {

            @Override
            public void onSuccess() {
                updateUnreadLabel();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(final int code, final String error) {

            }
        });
    }

    private void loadShopInfo() {
        try {
            ShopInfoModel shopinfo = AccountAPI.getInstance().getShopInfo(PublicData.getCookie(Vthis));
            SpManager.setShopName(this, shopinfo.getName());
            SpManager.setShopSignature(this, shopinfo.getSignature());
            SpManager.setShopMobile(this, shopinfo.getMobile());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @description 用户数据加载完成
     * @created 2014-9-3 下午1:45:19
     * @author ZZB
     */
    private void userInfoLoaded(Object result) {
        PublicData.mUserInfo = (UserModel) result;
        initView();
    }

}
