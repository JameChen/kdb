package com.nahuo.kdb;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.nahuo.kdb.activity.OrderPayActivity;
import com.nahuo.kdb.api.KdbAPI;
import com.nahuo.kdb.common.Utils;
import com.nahuo.kdb.model.CreateProduceBean;
import com.nahuo.kdb.model.SaleDetailBean;
import com.nahuo.kdb.model.ShopCartModel;
import com.nahuo.library.controls.LoadingDialog;

import java.util.ArrayList;

import static com.nahuo.kdb.PrintOrderActivity.Step.Create_Order_Preview;
import static com.nahuo.kdb.PrintOrderActivity.Step.SUBMIT_KD;
import static com.nahuo.kdb.activity.OrderPayActivity.EXTRA_PAYDETAIL;


public class PrintOrderActivity extends BaseActivity2 implements OnClickListener {

    private static final int REQUEST_ENABLE_BT = 3;
    protected static final String TAG = "PrintOrderActivity";
    public static final String Extra_Bean = "Extra_Bean";
    public static final String Extra_ShoppingIDS = "Extra_ShoppingIDS";
    private PrintOrderActivity vThis = this;
    private LoadingDialog mloadingDialog;

    private TextView tv, tvzl;
    private EditText etMoney, etZK, etSS;

    private double ss_money;
    private double zk;
    private double payment_money;
    private double product_money;
    private int product_count;
    private TextView print_order_btn;
    private SaleDetailBean kd_order = null;
    private ArrayList<ShopCartModel> submit_arr = null;
    private ArrayList<ShopCartModel> submit_arr1 = new ArrayList<>();
    private int Type = 1;
    private int mPrinterIndex = 0;
    // private double discount;
    private CreateProduceBean createProduceBean;
    private CheckBox cb_small_chang, cb_vip;
    private String ShoppingIDS = "";

    public enum Step {
        SUBMIT_KD, Create_Order_Preview
    }

//    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
//    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
//    private GpService mGpService = null;
    // private PrinterServiceConnection conn = null;

    //    private List<ShopItemListModel.ReasonListBean> mWaitDaysReasonList;
//    private List<ShopItemListModel.ReasonListBean> mOutSupplyReasonList;
//    private List<ShopItemListModel.ReasonListBean> mCloseStorageReasonList;
//    class PrinterServiceConnection implements ServiceConnection {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            Log.i("ServiceConnection", "onServiceDisconnected() called");
//            mGpService = null;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mGpService = GpService.Stub.asInterface(service);
//        }
//    }
//
//    private void connection() {
//        conn = new PrinterServiceConnection();
//        Intent intent = new Intent(this, GpPrintService.class);
//        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (conn != null) {
//            unbindService(conn); // unBindService
//        }
//        if (mBroadcastReceiver!=null)
//            unregisterReceiver(mBroadcastReceiver);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        // connection();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_order);
        BWApplication.addActivity(this);
        setTitle("开单");
//        mPrinterIndex = BWApplication.PrinterId;
//        connection();
//        // 注册实时状态查询广播
//        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
//        /**
//         * 票据模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus()，在打印完成后会接收到
//         * action为GpCom.ACTION_DEVICE_STATUS的广播，特别用于连续打印，
//         * 可参照该sample中的sendReceiptWithResponse方法与广播中的处理
//         **/
//        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_RECEIPT_RESPONSE));
//        /**
//         * 标签模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus(RESPONSE_MODE mode)
//         * ，在打印完成后会接收到，action为GpCom.ACTION_LABEL_RESPONSE的广播，特别用于连续打印，
//         * 可参照该sample中的sendLabelWithResponse方法与广播中的处理
//         **/
//        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_LABEL_RESPONSE));
        if (getIntent() != null) {
            createProduceBean = (CreateProduceBean) getIntent().getSerializableExtra(Extra_Bean);
            ShoppingIDS = getIntent().getStringExtra(Extra_ShoppingIDS);
        }
//        submit_arr = (ArrayList<ShopCartModel>) getIntent().getSerializableExtra("datas");
//        double t = 0;
//        int c = 0;
//        for (ShopCartModel m : submit_arr) {
//            t += m.getQty() * m.getPrice();
//            c += m.getQty();
//        }
//        zk = 0;
//        product_count = c;
//        payment_money = t;
//        product_money = t;
//        ss_money = product_money;

        initViews();

        //  startService();
        // registerBroadcast();
        //connection();

    }

  /*  private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("TAG", action);
            // GpCom.ACTION_DEVICE_REAL_STATUS 为广播的IntentFilter
            if (action.equals(GpCom.ACTION_DEVICE_REAL_STATUS)) {

                // 业务逻辑的请求码，对应哪里查询做什么操作
                int requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);
                // 判断请求码，是则进行业务操作
                if (requestCode == MAIN_QUERY_PRINTER_STATUS) {

                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    String str;
                    if (status == GpCom.STATE_NO_ERR) {
                        str = "打印机正常";
                    } else {
                        str = "打印机 ";
                        if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
                            str += "脱机";
                        }
                        if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
                            str += "缺纸";
                        }
                        if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
                            str += "打印机开盖";
                        }
                        if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
                            str += "打印机出错";
                        }
                        if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
                            str += "查询超时";
                        }
                    }

                    Toast.makeText(getApplicationContext(), "打印机：" + mPrinterIndex + " 状态：" + str, Toast.LENGTH_SHORT)
                            .show();
                }
//                else if (requestCode == REQUEST_PRINT_LABEL) {
//                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
//                    if (status == GpCom.STATE_NO_ERR) {
//                        sendLabel();
//                    } else {
//                        Toast.makeText(MainActivity.this, "query printer status error", Toast.LENGTH_SHORT).show();
//                    }
//                }
                else if (requestCode == REQUEST_PRINT_RECEIPT) {
                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    if (status == GpCom.STATE_NO_ERR) {
                        sendReceipt();
                    } else {
                        Toast.makeText(PrintOrderActivity.this, "检查打印机状态错误码", Toast.LENGTH_SHORT).show();
                    }
                }
            }
//            else if (action.equals(GpCom.ACTION_RECEIPT_RESPONSE)) {
//                if (--mTotalCopies > 0) {
//                    sendReceiptWithResponse();
//                }
//            } else if (action.equals(GpCom.ACTION_LABEL_RESPONSE)) {
//                byte[] data = intent.getByteArrayExtra(GpCom.EXTRA_PRINTER_LABEL_RESPONSE);
//                int cnt = intent.getIntExtra(GpCom.EXTRA_PRINTER_LABEL_RESPONSE_CNT, 1);
//                String d = new String(data, 0, cnt);
//                */

    /**
     * //                 * 这里的d的内容根据RESPONSE_MODE去判断返回的内容去判断是否成功，具体可以查看标签编程手册SET
     * //                 * RESPONSE指令
     * //                 * 该sample中实现的是发一张就返回一次,这里返回的是{00,00001}。这里的对应{Status,######,ID}
     * //                 * 所以我们需要取出STATUS
     * //
     *//*
//                Log.d("LABEL RESPONSE", d);
//
//                if (--mTotalCopies > 0 && d.charAt(1) == 0x00) {
//                    sendLabelWithResponse();
//                }
            //  }
        }
    };

    private void sendReceipt() {
      //  EscCommand esc = new EscCommand();
//        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
//        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);//设置为倍高倍宽
//        esc.addText(SpManager.getShopName(vThis) + "\n");
//        esc.addPrintAndLineFeed();
//
//        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);//取消倍高倍宽
//        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
//        esc.addText("单号:" + kd_order.getOrderCode() + "\n");
//        esc.addText("日期:" + kd_order.getCreateTime() + "\n");
//        esc.addText("---------------------------\n");
//        esc.addText("商品\n");
//        for (SubmitOrderModel.OrderProductModel pm : kd_order.getProducts()) {
//            esc.addText(pm.getName() + "\n");
//            esc.addText(pm.getColor() + "   " + pm.getSize() + "   " + Utils.moneyFormat(pm.getPrice()) + "*" + pm.getQty() + "\n");
//            esc.addPrintAndLineFeed();
//        }
//        esc.addText("---------------------------\n");
//        esc.addText("数量:" + kd_order.getProductCount() + "     应收" + Utils.moneyFormat(kd_order.getProductAmount()) + "\n");
//        esc.addText("优惠:" + Utils.moneyFormat(kd_order.getDiscount()) + "     实收" + Utils.moneyFormat(ss_money) + "\n");
//        esc.addPrintAndLineFeed();
//        esc.addText("谢谢惠顾");
//        esc.addPrintAndLineFeed();
//        esc.addPrintAndFeedLines((byte) 8);

        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
        esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON, ENABLE.OFF);// 设置为倍高倍宽
        esc.addText(SpManager.getShopName(vThis) + "\n"); // 打印文字
        esc.addPrintAndLineFeed();
		*//* 打印文字 *//*
        esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF);// 取消倍高倍宽
        esc.addSelectJustification(JUSTIFICATION.LEFT);// 设置打印左对齐
        esc.addText("单号："+ kd_order.getOrderCode()+"\n"); // 打印文字
        esc.addText("日期："+kd_order.getCreateTime()+"\n"); // 打印文字
        esc.addText("---------------------------\n");
        esc.addText("商品列表\n\n");
        if (!ListUtils.isEmpty(kd_order.getProducts())) {
            for (SubmitOrderModel.OrderProductModel pm : kd_order.getProducts()) {
                esc.addText(pm.getName() + "\n");
                esc.addText(pm.getColor() + "   " + pm.getSize() + "   ￥" + Utils.moneyFormat(pm.getPrice()) + "*" + pm.getQty() + "\n");
                esc.addPrintAndLineFeed();
            }
        }
        esc.addText("---------------------------\n");
//        esc.addText("数量:" + kd_order.getProductCount() + "     应收" + Utils.moneyFormat(kd_order.getProductAmount()) + "\n");
//        esc.addText("优惠:" + Utils.moneyFormat(kd_order.getDiscount()) + "     实收" + Utils.moneyFormat(ss_money) + "\n");
//        esc.addPrintAndLineFeed();
        esc.addText("数量："+kd_order.getProductCount());
        esc.addSetHorAndVerMotionUnits((byte) 4, (byte) 1);
        esc.addSetAbsolutePrintPosition((short) 4);
        esc.addText("应收："+Utils.moneyFormat(kd_order.getProductAmount()));
        esc.addPrintAndLineFeed();
        esc.addText("优惠："+Utils.moneyFormat(kd_order.getDiscount()));
        esc.addSetHorAndVerMotionUnits((byte) 4, (byte) 1);
        esc.addSetAbsolutePrintPosition((short) 4);
        esc.addText("实收："+Utils.moneyFormat(ss_money));
       // esc.addPrintAndLineFeed();
		*//* 打印图片 *//*
//        esc.addText("Print bitmap!\n"); // 打印文字
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.gprinter);
//        //esc.addRastBitImage(b, 384, 0); // 打印图片
//
//		*//* 打印一维条码 *//*
//        esc.addText("Print code128\n"); // 打印文字
//        esc.addSelectPrintingPositionForHRICharacters(HRI_POSITION.BELOW);//
//        // 设置条码可识别字符位置在条码下方
//        esc.addSetBarcodeHeight((byte) 60); // 设置条码高度为60点
//        esc.addSetBarcodeWidth((byte) 1); // 设置条码单元宽度为1
//        esc.addCODE128(esc.genCodeB("SMARNET")); // 打印Code128码
//        esc.addPrintAndLineFeed();

		*//*
         * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
		 *//*
//        esc.addText("Print QRcode\n"); // 打印文字
//        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31); // 设置纠错等级
//        esc.addSelectSizeOfModuleForQRCode((byte) 3);// 设置qrcode模块大小
//        esc.addStoreQRCodeData("www.smarnet.cc");// 设置qrcode内容
//        esc.addPrintQRCode();// 打印QRCode
//        esc.addPrintAndLineFeed();
       // esc.addPrintAndLineFeed();
		*//* 打印文字 *//*
        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印左对齐
        esc.addText("**** 谢谢惠顾 ****\r\n"); // 打印结束
        // 开钱箱
        esc.addGeneratePlus(LabelCommand.FOOT.F5, (byte) 255, (byte) 255);
        esc.addPrintAndFeedLines((byte) 8);

        Vector<Byte> datas = esc.getCommand(); // 发送数据
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs;
        try {
            rs = mGpService.sendEscCommand(mPrinterIndex, sss);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void printReceiptClicked() {
        try {
            if (mGpService.getPrinterConnectStatus(BWApplication.getInstance().PrinterId) == GpDevice.STATE_CONNECTED) {
                int type = mGpService.getPrinterCommandType(mPrinterIndex);
                if (type == GpCom.ESC_COMMAND) {
                    mGpService.queryPrinterStatus(mPrinterIndex, 1000, REQUEST_PRINT_RECEIPT);
                } else {
                    Toast.makeText(this, "Printer is not receipt mode", Toast.LENGTH_SHORT).show();
                }
            } else {
                ViewHub.showOkDialog(vThis, "蓝牙打印机未连接", "连接打印机？", "确定", "取消", new ViewHub.OkDialogListener() {
                    @Override
                    public void onPositiveClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(PrintOrderActivity.this, BlueToothActivity.class));
                    }

                    @Override
                    public void onNegativeClick(DialogInterface dialog, int which) {

                    }
                });
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }*/
    private void initViews() {
        mloadingDialog = new LoadingDialog(vThis);
        cb_small_chang = (CheckBox) findViewById(R.id.cb_small_chang);
        cb_vip = (CheckBox) findViewById(R.id.cb_vip);
        print_order_btn = (TextView) findViewById(R.id.print_order_btn);
        print_order_btn.setOnClickListener(this);
        etMoney = (EditText) findViewById(R.id.print_order_payment_money);
        etMoney.setFocusable(false);
        etMoney.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        etMoney.setClickable(false);
        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etMoney.isFocused()) {
                    double money = 0;
                    try {
                        money = Double.parseDouble(etMoney.getText().toString());
                        if (money > product_money) {
                            money = product_money;
                            etMoney.setText(Utils.moneyFormat(money));
                        }
                    } catch (Exception ex) {

                    }
                    payment_money = money;
                    etZK.setText(Utils.moneyFormat(payment_money / product_money * 10) + "");
                    reloadMoney();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tv = (TextView) findViewById(R.id.print_order_payment_detail);
        etZK = (EditText) findViewById(R.id.print_order_zk);
        etZK.setFocusable(false);
        etZK.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        etZK.setClickable(false);
        etZK.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etZK.isFocused()) {
                    double t = 0;
                    try {
                        t = Double.parseDouble(etZK.getText().toString());
                        if (t > 10) {
                            t = 10;
                            etZK.setText(Utils.moneyFormat(t));
                        }
                        t = t / 10;
                    } catch (Exception ex) {

                    }
                    zk = t;
                    payment_money = zk * product_money;
                    etMoney.setText(Utils.moneyFormat(payment_money) + "");
                    reloadMoney();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvzl = (TextView) findViewById(R.id.print_order_zl);
        etSS = (EditText) findViewById(R.id.print_order_ss);
        etSS.setFocusable(false);
        etSS.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        etSS.setClickable(false);
        etSS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double ss = 0;
                try {
                    ss = Double.parseDouble(etSS.getText().toString());
                } catch (Exception ex) {

                }
                ss_money = ss;

                tvzl.setText("找零:" + Utils.moneyFormat(ss_money - payment_money));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        findViewById(R.id.print_order_btn).setOnClickListener(this);

        cb_small_chang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSmallChange = isChecked;
                new Task(Create_Order_Preview, isSmallChange, isVip).execute();
            }
        });
        cb_vip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb_small_chang.isChecked()) {
                    cb_small_chang.setChecked(false);
                    isSmallChange = false;
                }
                isVip = isChecked;
                new Task(Create_Order_Preview, isSmallChange, isVip).execute();
            }
        });
        initPriceView(createProduceBean);
//
//        etMoney.setText(Utils.moneyFormat(payment_money) + "");
//        etZK.setText(10 + "");
//        tvzl.setText("找零:0");
//        reloadMoney();
    }

    private void initPriceView(CreateProduceBean createProduceBean) {
        if (createProduceBean != null) {
            PayableAmount = Double.parseDouble(createProduceBean.getPayableAmount());
            payAmount = Double.parseDouble(createProduceBean.getPayableAmount());
            VipDiscount = createProduceBean.getVipDiscount();
            Discount = createProduceBean.getDiscount();
            discount = Discount;
            etMoney.setText(Utils.moneyFormat(Double.parseDouble(createProduceBean.getPayableAmount())));
            etZK.setText(createProduceBean.getDiscount() + "");
            tv.setText("数量:" + createProduceBean.getProductCount() + "        " +
                    "应收:" + createProduceBean.getProductAmount() + "        " +
                    "优惠:" + createProduceBean.getDiscountAmount());
        }
    }

    private void reloadMoney() {
        tv.setText("数量:" + product_count + "        " +
                "应收:" + Utils.moneyFormat(product_money) + "        " +
                "优惠:" + Utils.moneyFormat(product_money - payment_money));

        tvzl.setText("找零:" + Utils.moneyFormat(ss_money - payment_money));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.print_order_btn:
                gotoPay();
//                if (Type == 1) {
//
//                } else {
//                    startprint();
//                }
                break;
            default:
                break;
        }
    }

    private void gotoPay() {
//        double ss = 0;
//        double jj = 0;
//        try {
//            ss = Double.parseDouble(etSS.getText().toString());
//        } catch (Exception ex) {
//
//        }
        //try {
        new Task(SUBMIT_KD).execute();
//            jj = Double.parseDouble(etMoney.getText().toString());
//        } catch (Exception ex) {
//
//        }
//        if (TextUtils.isEmpty(etZK.getText().toString())) {
//            discount = 0;
//        } else {
//            discount = Double.parseDouble(etZK.getText().toString());
//        }
//        if (jj > 0) {
//            new Task(SUBMIT_KD).execute();
//        } else {
//            ViewHub.showLongToast(vThis, "请输入实收金额");
//        }
//        if (ss == 0) {
//            ViewHub.showLongToast(vThis, "请输入金额");
//        } else
//            if (jj <= ss) {
//            new Task(Step.SUBMIT_KD).execute();
//        } else {
//            ViewHub.showLongToast(vThis, "收款必须大于实际金额");
//        }
    }

    //蓝牙打印相关
    private void startService() {
//        Intent i = new Intent(this, GpPrintService.class);
//        startService(i);
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        conn = new PrinterServiceConnection();
////        Intent intent = new Intent("com.gprinter.aidl.GpPrintService");
//        final Intent intent = new Intent(this, GpPrintService.class);
//        bindService(intent, conn, Service.BIND_AUTO_CREATE);

    }

//    private GpService mGpService = null;
//    private PrinterServiceConnection conn = null;
//
//    class PrinterServiceConnection implements ServiceConnection {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            Log.i("ServiceConnection", "onServiceDisconnected() called");
//            mGpService = null;
//            ViewHub.showLongToast(vThis, "未找到打印机");
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mGpService = GpService.Stub.asInterface(service);
//
//           // ViewHub.showLongToast(vThis, "打印机初始化成功");
//        }
//    }
//
//    ;
//
//    private void connection() {
//        int type = -1;
//        java.lang.String btMac = "";
//        if (mGpService != null) {
//            BluetoothAdapter bAdapt = BluetoothAdapter.getDefaultAdapter();
//            if (bAdapt != null) {
//                if (!bAdapt.isEnabled()) {
//                    Intent enBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enBT, REQUEST_ENABLE_BT);
//                }
//                btMac = bAdapt.getAddress();
//            } else {
//                ViewHub.showLongToast(vThis, "No Bluetooth Device!");
//            }
//
//            try {
//                type = mGpService.openPort(0, PortParameters.BLUETOOTH, btMac, 0);
//                if (type == GpCom.ESC_COMMAND) {
//                    ViewHub.showLongToast(vThis, "打印机连接成功");
//                } else {
//                    ViewHub.showLongToast(vThis, "请使用票据打印机");
//                }
//            } catch (RemoteException e1) {
//                e1.printStackTrace();
//                ViewHub.showLongToast(vThis, "蓝牙连接失败");
//            }
//        }
//    }

    /* void printOrder() {
         EscCommand esc = new EscCommand();
         esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
         esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);//设置为倍高倍宽
         esc.addText(SpManager.getShopName(vThis) + "\n");
         esc.addPrintAndLineFeed();

         esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);//取消倍高倍宽
         esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
         esc.addText("单号:" + kd_order.getOrderCode() + "\n");
         esc.addText("日期:" + kd_order.getCreateTime() + "\n");
         esc.addText("---------------------------\n");
         esc.addText("商品\n");
         for (SubmitOrderModel.OrderProductModel pm : kd_order.getProducts()) {
             esc.addText(pm.getName() + "\n");
             esc.addText(pm.getColor() + "   " + pm.getSize() + "   " + Utils.moneyFormat(pm.getPrice()) + "*" + pm.getQty() + "\n");
             esc.addPrintAndLineFeed();
         }
         esc.addText("---------------------------\n");
         esc.addText("数量:" + kd_order.getProductCount() + "     应收" + Utils.moneyFormat(kd_order.getProductAmount()) + "\n");
         esc.addText("优惠:" + Utils.moneyFormat(kd_order.getDiscount()) + "     实收" + Utils.moneyFormat(ss_money) + "\n");
         esc.addPrintAndLineFeed();
         esc.addText("谢谢惠顾");
         esc.addPrintAndLineFeed();
         esc.addPrintAndFeedLines((byte) 8);

         Vector<Byte> datas = esc.getCommand(); //发送数据
         Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
         byte[] bytes = ArrayUtils.toPrimitive(Bytes);
         String str = Base64.encodeToString(bytes, Base64.DEFAULT);
         int rel;
         try {
             rel = mGpService.sendEscCommand(0, str);
             GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
             if (r != GpCom.ERROR_CODE.SUCCESS) {
                 Toast.makeText(getApplicationContext(), GpCom.getErrorText(r),
                         Toast.LENGTH_SHORT).show();
             }
         } catch (RemoteException e) {
             ViewHub.showLongToast(vThis, "打印失败,请检查打印机");
             e.printStackTrace();
         }
     }
 */
    private String Discount = "0";
    double payAmount;
    double PayableAmount;
    private String VipDiscount = "0.00";
    private boolean isSmallChange, isVip;
    String pay = "0", discount = "0";

    private class Task extends AsyncTask<Object, Void, Object> {
        private Step mStep;
        private boolean isSmallChange, isVip;

        public Task(Step step) {
            mStep = step;
        }

        public Task(Step step, boolean isSmallChange, boolean isVip) {
            mStep = step;
            this.isSmallChange = isSmallChange;
            this.isVip = isVip;
        }

        @Override
        protected void onPreExecute() {
            switch (mStep) {
                case SUBMIT_KD:
                    if (mloadingDialog != null)
                        mloadingDialog.start("提交订单中...");
                    break;
                case Create_Order_Preview:
                    if (mloadingDialog != null)
                        mloadingDialog.start("加载中...");
                    break;

            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {

                switch (mStep) {
                    case Create_Order_Preview:
                        if (isSmallChange && isVip) {
                            discount = VipDiscount;
                            pay = (int) Math.floor(payAmount) + "";
                        } else if (!isSmallChange && isVip) {
                            discount = VipDiscount;
                            pay = "0";
                        } else if (isSmallChange && !isVip) {
                            pay = (int) Math.floor(PayableAmount) + "";
                            discount = "0";
                        } else if (!isSmallChange && !isVip) {
                            pay = "0";
                            discount = "0";
                        }
//                        if (isSmallChange){
//                            pay=(int)Math.floor(payAmount)+"";
//                          //  discount="0";
//                            if (isVip) {
//                                discount=VipDiscount;
//                            }else {
//                                discount=Discount;
//                            }
//                        }else {
//                            pay="0";
//                            if (isVip) {
//                                discount=VipDiscount;
//                            }else {
//                                discount=Discount;
//                            }
                        //  }
                        return KdbAPI.createOrderPreview(vThis, ShoppingIDS, discount, pay);
                    case SUBMIT_KD:
//                        submit_arr1.clear();
//                        for (ShopCartModel m : submit_arr) {
//                            if (m.isSelect()) {
//                                submit_arr1.add(m);
//                            }
//                        }
                        kd_order = KdbAPI.submitOrder(vThis, ShoppingIDS, discount, pay);
                        return kd_order;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (mloadingDialog.isShowing()) {
                mloadingDialog.stop();
            }
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(vThis, ((String) result).replace("error:", ""));
            } else {
                switch (mStep) {
                    case Create_Order_Preview:
                        if (result instanceof CreateProduceBean) {
                            CreateProduceBean bean = (CreateProduceBean) result;
                            if (bean != null) {
                                if (bean != null) {
//                                    payAmount=Double.parseDouble(createProduceBean.getPayableAmount());
//                                    VipDiscount=createProduceBean.getVipDiscount();
//                                    Discount=createProduceBean.getDiscount();
                                    payAmount = Double.parseDouble(bean.getPayableAmount());
                                    etMoney.setText(Utils.moneyFormat(payAmount));
                                    etZK.setText(bean.getDiscount() + "");
                                    tv.setText("数量:" + bean.getProductCount() + "        " +
                                            "应收:" + bean.getProductAmount() + "        " +
                                            "优惠:" + bean.getDiscountAmount());
                                }
                            } else {
                                ViewHub.showLongToast(vThis, "获取数据为空");
                            }
                        }
                        break;
                    case SUBMIT_KD:
                        Type = 2;
                        SaleDetailBean kd_order = (SaleDetailBean) result;
                        Intent intent = new Intent(vThis, OrderPayActivity.class);
                        intent.putExtra(EXTRA_PAYDETAIL, kd_order);
                        vThis.startActivity(intent);
//                        print_order_btn.setText("打印小票");
//                        ViewHub.showOkDialog(vThis, "提示", "提交订单成功，是否打印小票", "不了", "确定", new ViewHub.OkDialogListener() {
//                            @Override
//                            public void onPositiveClick(DialogInterface dialog, int which) {
//
//                            }
//
//                            @Override
//                            public void onNegativeClick(DialogInterface dialog, int which) {
//                               // printReceiptClicked();
//                                // startprint();
//                            }
//                        });

                        // ViewHub.showLongToast(vThis, "开单完毕,开始打印");

                        break;
                }
            }
        }
    }

    private void startprint() {
        //printReceiptClicked();
//        try {
//            int type = -1;
//            if (mGpService != null) {
//                type = mGpService.getPrinterCommandType(0);
//            }
//            String str = "打印机连接出错";
//            if (type == GpCom.ESC_COMMAND) {
//                int status = mGpService.queryPrinterStatus(0, 500);
//                if (status == GpCom.STATE_NO_ERR) {
//                    printOrder();
//                    return;
//                } else if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
//                    str = "打印机脱机";
//                } else if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
//                    str = "打印机缺纸";
//                } else if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
//                    str = "打印机开盖";
//                } else if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
//                    str = "打印机出错";
//                }
//            } else {
//                str = "请使用票据打印机";
//            }
//            ViewHub.showOkDialog(vThis, "提示", str, "好的", "尝试重新连接", new ViewHub.OkDialogListener() {
//                @Override
//                public void onPositiveClick(DialogInterface dialog, int which) {
//
//                }
//
//                @Override
//                public void onNegativeClick(DialogInterface dialog, int which) {
//                    connection();
//                }
//            });
//
//        } catch (RemoteException e1) {
//            ViewHub.showLongToast(vThis, e1.getMessage());
//            e1.printStackTrace();
//        }
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//
//            case REQUEST_ENABLE_BT:
//                // When the request to enable Bluetooth returns
//                if (resultCode == Activity.RESULT_OK) {
//                    // Bluetooth is now enabled, so set up a chat session
//                } else {
//                    ViewHub.showLongToast(vThis, "蓝牙异常");
//                }
//        }
//    }

    public static final String ACTION_CONNECT_STATUS = "action.connect.status";

    /*private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CONNECT_STATUS);
        this.registerReceiver(PrinterStatusBroadcastReceiver, filter);
    }

    private BroadcastReceiver PrinterStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_CONNECT_STATUS.equals(intent.getAction())) {
                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
                int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);
                Log.d("hahahahah", "connect status " + type);
                if (type == GpDevice.STATE_CONNECTING) {

                } else if (type == GpDevice.STATE_NONE) {

                } else if (type == GpDevice.STATE_VALID_PRINTER) {

                } else if (type == GpDevice.STATE_INVALID_PRINTER) {
                    ViewHub.showLongToast(vThis, "打印机异常");

                }
            }
        }
    };*/
}
