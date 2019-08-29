package com.nahuo.kdb;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.EscCommand.ENABLE;
import com.gprinter.command.EscCommand.FONT;
import com.gprinter.command.EscCommand.JUSTIFICATION;
import com.gprinter.command.GpCom;
import com.gprinter.command.GpUtils;
import com.gprinter.command.LabelCommand;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.nahuo.kdb.activity.BlueToothActivity;
import com.nahuo.kdb.activity.OrderPayActivity;
import com.nahuo.kdb.adapter.SaleDetailItemAdapter;
import com.nahuo.kdb.api.OtherAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.common.Utils;
import com.nahuo.kdb.model.OrderButton;
import com.nahuo.kdb.model.SaleDetailBean;
import com.nahuo.kdb.utils.ScreenUtils;
import com.nahuo.library.controls.LoadingDialog;

import java.util.List;
import java.util.Vector;

import static com.nahuo.kdb.activity.OrderPayActivity.EXTRA_PAYDETAIL;


public class SaleDetailActivity extends BaseSlideBackActivity implements OnClickListener {

    private static final String TAG = "SaleLogActivity";
    private SaleDetailActivity vThis = this;
    private ListView listView;
    private LoadDataTask loadDataTask;
    private LoadingDialog mloadingDialog;
    private SaleDetailItemAdapter adapter;
    private SaleDetailBean data = null;
    private String ordercode;
    private TextView txt1, txt2, txt3, txt4, txt5, txt6, sale_detail_pay_txt1, sale_detail_pay_txt2, tv_order_time, tv_order_status, tv_order_nums,tv_order_price
            ,sale_detail_seller,tv_detail_creater,sale_detail_discount;
    private View foot1, head, layout_buttons, layout_pay;
    private LinearLayout layout_order_buttons;
    private Context mContext;
    private CancelTask cancelTask;

    private int mPrinterIndex = 0;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    private GpService mGpService = null;
    private PrinterServiceConnection conn = null;
    private TextView item_detail_print;
    private static final int REQUEST_ENABLE_BT = 3;
    private TextView titlebar_btnRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 设置自定义标题栏
        setContentView(R.layout.activity_sale_detail);
        BWApplication.addActivity(this);
        mContext = this;
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.layout_titlebar_default);// 更换自定义标题栏布局

        ordercode = getIntent().getStringExtra("orderCode");
        mPrinterIndex = BWApplication.PrinterId;
        connection();
        // 注册实时状态查询广播
        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
        /**
         * 票据模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus()，在打印完成后会接收到
         * action为GpCom.ACTION_DEVICE_STATUS的广播，特别用于连续打印，
         * 可参照该sample中的sendReceiptWithResponse方法与广播中的处理
         **/
        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_RECEIPT_RESPONSE));
        /**
         * 标签模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus(RESPONSE_MODE mode)
         * ，在打印完成后会接收到，action为GpCom.ACTION_LABEL_RESPONSE的广播，特别用于连续打印，
         * 可参照该sample中的sendLabelWithResponse方法与广播中的处理
         **/
        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_LABEL_RESPONSE));
        initView();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
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
                        Toast.makeText(mContext, "检查打印机状态错误码", Toast.LENGTH_SHORT).show();
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
//                /**
//                 * 这里的d的内容根据RESPONSE_MODE去判断返回的内容去判断是否成功，具体可以查看标签编程手册SET
//                 * RESPONSE指令
//                 * 该sample中实现的是发一张就返回一次,这里返回的是{00,00001}。这里的对应{Status,######,ID}
//                 * 所以我们需要取出STATUS
//                 */
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
        if (data != null) {
            EscCommand esc = new EscCommand();
            esc.addInitializePrinter();
            esc.addPrintAndFeedLines((byte) 3);
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
            esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON, ENABLE.OFF);// 设置为倍高倍宽
            esc.addText(SpManager.getShopName(vThis) + "\n"); // 打印文字
            esc.addPrintAndLineFeed();
        /* 打印文字 */
            esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF);// 取消倍高倍宽
            esc.addSelectJustification(JUSTIFICATION.LEFT);// 设置打印左对齐
            esc.addText("单号：" + data.getOrderCode() + "\n"); // 打印文字
            esc.addText("日期：" + data.getCreateTime() + "\n"); // 打印文字
            esc.addText("---------------------------\n");
            esc.addText("商品列表\n\n");
            if (!ListUtils.isEmpty(data.getProducts())) {
                for (SaleDetailBean.ProductsBean pm : data.getProducts()) {
                    esc.addText(pm.getName() + "\n");
                    esc.addText(pm.getColor() + "   " + pm.getSize() + "   ￥" + Utils.moneyFormat(Double.parseDouble(pm.getPrice())) + "*" + pm.getQty() + "\n");
                    esc.addPrintAndLineFeed();
                }
            }
            esc.addText("---------------------------\n");
//        esc.addText("数量:" + kd_order.getProductCount() + "     应收" + Utils.moneyFormat(kd_order.getProductAmount()) + "\n");
//        esc.addText("优惠:" + Utils.moneyFormat(kd_order.getDiscount()) + "     实收" + Utils.moneyFormat(ss_money) + "\n");
//        esc.addPrintAndLineFeed();
            esc.addText("数量：" + data.getProductCount());
            esc.addSetHorAndVerMotionUnits((byte) 4, (byte) 1);
            esc.addSetAbsolutePrintPosition((short) 4);
            esc.addText("应收：" + Utils.moneyFormat(Double.parseDouble(data.getProductAmount())));
            esc.addPrintAndLineFeed();
            esc.addText("优惠：" + data.getDiscount());
            esc.addSetHorAndVerMotionUnits((byte) 4, (byte) 1);
            esc.addSetAbsolutePrintPosition((short) 4);
            esc.addText("实收：" + Utils.moneyFormat(Double.parseDouble(data.getPayableAmount())));
            // esc.addPrintAndLineFeed();
        /* 打印图片 */
//        esc.addText("Print bitmap!\n"); // 打印文字
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.gprinter);
//        //esc.addRastBitImage(b, 384, 0); // 打印图片
//
//		/* 打印一维条码 */
//        esc.addText("Print code128\n"); // 打印文字
//        esc.addSelectPrintingPositionForHRICharacters(HRI_POSITION.BELOW);//
//        // 设置条码可识别字符位置在条码下方
//        esc.addSetBarcodeHeight((byte) 60); // 设置条码高度为60点
//        esc.addSetBarcodeWidth((byte) 1); // 设置条码单元宽度为1
//        esc.addCODE128(esc.genCodeB("SMARNET")); // 打印Code128码
//        esc.addPrintAndLineFeed();

		/*
         * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
		 */
//        esc.addText("Print QRcode\n"); // 打印文字
//        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31); // 设置纠错等级
//        esc.addSelectSizeOfModuleForQRCode((byte) 3);// 设置qrcode模块大小
//        esc.addStoreQRCodeData("www.smarnet.cc");// 设置qrcode内容
//        esc.addPrintQRCode();// 打印QRCode
//        esc.addPrintAndLineFeed();
            // esc.addPrintAndLineFeed();
        /* 打印文字 */
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
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                } else {
                    ViewHub.showLongToast(vThis, "蓝牙异常");
                }
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
                        startActivity(new Intent(mContext, BlueToothActivity.class));
                    }

                    @Override
                    public void onNegativeClick(DialogInterface dialog, int which) {

                    }
                });
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ServiceConnection", "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn); // unBindService
        }
        if (mBroadcastReceiver != null)
            unregisterReceiver(mBroadcastReceiver);
    }

    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(this, GpPrintService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    private void initView() {
        // 标题栏
        TextView tvTitle = (TextView) findViewById(R.id.titlebar_tvTitle);
        Button btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        item_detail_print = (TextView) findViewById(R.id.item_detail_print);
        titlebar_btnRight=(TextView)findViewById(R.id.titlebar_btnRight);
        titlebar_btnRight.setText("发货");
        titlebar_btnRight.setVisibility(View.GONE);
        titlebar_btnRight.setOnClickListener(this);
        item_detail_print.setOnClickListener(this);
        tvTitle.setText("销售详情");
        btnLeft.setText(R.string.titlebar_btnBack);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setOnClickListener(this);

        mloadingDialog = new LoadingDialog(vThis);
        listView = (ListView) findViewById(R.id.sale_detail_list_view);
        //foot1 = LayoutInflater.from(this).inflate(R.layout.sale_detail_order_info, null);
        head = LayoutInflater.from(this).inflate(R.layout.sale_detail_head, null);
        foot1=head;
        //foot1.setVisibility(View.GONE);
        head.setVisibility(View.GONE);
        layout_pay = foot1.findViewById(R.id.layout_pay);
        sale_detail_discount=(TextView)head.findViewById(R.id.sale_detail_discount);
        tv_detail_creater=(TextView)head.findViewById(R.id.tv_detail_creater);
        sale_detail_seller=(TextView)head.findViewById(R.id.sale_detail_seller);
        tv_order_time = (TextView) head.findViewById(R.id.tv_order_time);
        tv_order_status = (TextView) head.findViewById(R.id.tv_order_status);
        tv_order_nums = (TextView) head.findViewById(R.id.tv_order_nums);
        tv_order_price=(TextView) head.findViewById(R.id.tv_order_price);
        layout_buttons = head.findViewById(R.id.layout_buttons);
        layout_order_buttons = (LinearLayout) head.findViewById(R.id.layout_order_buttons);
        txt1 = (TextView) foot1.findViewById(R.id.sale_detail_txt1);
        txt2 = (TextView) foot1.findViewById(R.id.sale_detail_txt2);
        txt3 = (TextView) foot1.findViewById(R.id.sale_detail_txt3);
        txt4 = (TextView) foot1.findViewById(R.id.sale_detail_txt4);
        txt5 = (TextView) foot1.findViewById(R.id.sale_detail_txt5);
        txt6 = (TextView) foot1.findViewById(R.id.sale_detail_txt6);
        sale_detail_pay_txt1 = (TextView) foot1.findViewById(R.id.sale_detail_pay_txt1);
        sale_detail_pay_txt2 = (TextView) foot1.findViewById(R.id.sale_detail_pay_txt2);
        listView.addHeaderView(head);
        //listView.addFooterView(foot1);
        initItemAdapter();
        loadData();
    }

    private void loadData() {
        loadDataTask = new LoadDataTask();
        loadDataTask.execute((Void) null);
    }

    private void initItemAdapter() {
        adapter = new SaleDetailItemAdapter(this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_btnRight:

                break;
            case R.id.titlebar_btnLeft:
                finish();
                break;
            case R.id.item_detail_print:
                printReceiptClicked();
                break;
        }
    }

    public class LoadDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected void onPostExecute(String result) {
            mloadingDialog.stop();
            if (!result.equals("OK")) {
                Toast.makeText(vThis, result, Toast.LENGTH_LONG).show();
            } else {
                //foot1.setVisibility(View.VISIBLE);
                head.setVisibility(View.VISIBLE);
                if (data != null) {
                    List<OrderButton> buttons = data.getButtons();
                    layout_order_buttons.removeAllViews();
                    if (ListUtils.isEmpty(buttons)) {
                        layout_buttons.setVisibility(View.GONE);
                    } else {
                        layout_buttons.setVisibility(View.VISIBLE);
                        addOrderDetailButton(layout_order_buttons, data.getButtons(), data);
                    }
                    adapter.models = data.getProducts();
                    sale_detail_seller.setText("销售员："+data.getSellerUserUserName());
                    tv_detail_creater.setText("收银员："+data.getCreateUserName());
                    sale_detail_discount.setText("销售折扣："+data.getGetDiscountPercent());
                    txt1.setText("日期：" + data.getCreateTime());
                    txt2.setText("销售单号：" + data.getOrderCode());
                    txt3.setText("总数：" + data.getProductCount() + "件");
                    txt4.setText("应收：¥" + data.getProductAmount());
                    txt5.setText("优惠：¥" + data.getDiscount());
                    txt6.setText("实收：¥" + data.getPayableAmount());
                    if (data.getPayInfo() != null) {
//                        if (data.getPayInfo().getType().equals("未支付")) {
//                            layout_pay.setVisibility(View.GONE);
//                        } else {
//                            layout_pay.setVisibility(View.VISIBLE);
//                        }
                        sale_detail_pay_txt1.setText("收款方式：" + data.getPayInfo().getType());
                        sale_detail_pay_txt2.setText("交易号：" + data.getPayInfo().getCode());
                    } else {
                        layout_pay.setVisibility(View.GONE);
                    }
                    tv_order_price.setText(data.getPayableAmount());
                    tv_order_time.setText("销售时间：" + data.getCreateTime());
                    tv_order_status.setText(data.getStatu());
                    tv_order_nums.setText("成交数量：" + data.getProductCount() );
//                    if (data.getProductCount() > 0 && Double.parseDouble(data.getPayableAmount()) > 0) {
//                        tv_order_nums.setText("销售数量：" + data.getProductCount() + "件    " + "收款金额：¥ " + data.getPayableAmount());
//                    } else if (data.getProductCount() <= 0 && Double.parseDouble(data.getPayableAmount()) > 0) {
//                        tv_order_nums.setText("收款金额：¥ " + data.getPayableAmount());
//                    } else if (data.getProductCount() > 0 && Double.parseDouble(data.getPayableAmount()) <= 0) {
//                        tv_order_nums.setText("销售数量：" + data.getProductCount() + "件    ");
//                    }
                }
                adapter.notifyDataSetChanged();
                try {
                    item_detail_print.setVisibility(View.VISIBLE);
                    if (data != null && mGpService.getPrinterConnectStatus(BWApplication.getInstance().PrinterId) == GpDevice.STATE_CONNECTED) {
                        item_detail_print.setBackground(getResources().getDrawable(R.drawable.btn_d_red));
                        item_detail_print.setEnabled(true);
                    } else {
                        item_detail_print.setEnabled(false);
                        item_detail_print.setBackground(getResources().getDrawable(R.drawable.btn_d_gray));
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                data = OtherAPI.getSaleDetail(vThis, ordercode);
                return "OK";
            } catch (Exception ex) {
                Log.e(TAG, "获取销售列表发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }

    }

    public class CancelTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mloadingDialog != null)
                mloadingDialog.start("取消中....");
        }

        @Override
        protected void onPostExecute(String result) {
            if (mloadingDialog != null)
                mloadingDialog.stop();
            if (result.equals("OK")) {
                Toast.makeText(vThis, "取消成功", Toast.LENGTH_LONG).show();
                loadDataTask = new LoadDataTask();
                loadDataTask.execute();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                OtherAPI.cancelOrder(vThis, ordercode);
                return "OK";
            } catch (Exception ex) {
                Log.e(TAG, "取消销售订单发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }

    }

    public void addOrderDetailButton(LinearLayout parent, List<OrderButton> buttons
            , SaleDetailBean bean) {
        ButtonOnClickListener l = new ButtonOnClickListener();
        parent.removeAllViews();
        l.setBean(bean);
        if (buttons != null) {
            int margin = ScreenUtils.dip2px(mContext, 10);
            int top_margin = ScreenUtils.dip2px(mContext, 6);
            int top_pad = ScreenUtils.dip2px(mContext, 4);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = top_margin;
            for (OrderButton model : buttons) {
                TextView child = new TextView(parent.getContext());
                child.setPadding(margin, top_pad, margin, top_pad);
                child.setEllipsize(TextUtils.TruncateAt.END);
                child.setSingleLine(true);
                child.setTextSize(15);
                child.setText(model.getTitle());
                child.setGravity(Gravity.CENTER_VERTICAL);
                child.setClickable(model.isEnable());
                // child.getPaint().measureText(text, start, end)
                highlightButton(child, model.isPoint(), model.getType().equals("text"));
                if (model.isEnable()) {
                    child.setTag(model);
                    child.setOnClickListener(l);
                    child.setClickable(true);
                    child.setEnabled(true);
                } else {
                    child.setClickable(false);
                    child.setEnabled(false);
                }
                parent.addView(child, params);
            }
        }
    }

    private void highlightButton(TextView btn, boolean highlight, boolean isText) {

        if (isText) {
            btn.setBackgroundColor(ContextCompat.getColor(mContext, R.color.btn_bg_gray));
            btn.setTextColor(ContextCompat.getColor(mContext, R.color.lightblack));
        } else {
            btn.setBackgroundResource(highlight ? R.drawable.order_button_red_bg : R.drawable.order_button_white_gray_bg);
            btn.setTextColor(highlight ? mContext.getResources().getColor(R.color.white) : mContext.getResources().getColor(R.color.txt_gray));
        }
    }

    private class ButtonOnClickListener implements View.OnClickListener {
        SaleDetailBean bean;

        public void setBean(SaleDetailBean bean) {
            this.bean = bean;
        }

        @Override
        public void onClick(final View v) {
            final OrderButton btn = (OrderButton) v.getTag();
            String action = btn.getAction();
            if (action.equals(OrderButton.ACTION_PAYORDER)) {
                Intent intent = new Intent(vThis, OrderPayActivity.class);
                intent.putExtra(EXTRA_PAYDETAIL, bean);
                vThis.startActivity(intent);
            } else if (action.equals(OrderButton.ACTION_CANCELORDER)) {
                cancelTask = new CancelTask();
                cancelTask.execute();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }
}
