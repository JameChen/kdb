package com.nahuo.kdb.activity;

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
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.SaleDetailActivity;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.api.KdbAPI;
import com.nahuo.kdb.api.OtherAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.common.Utils;
import com.nahuo.kdb.controls.PopCodeMenu;
import com.nahuo.kdb.model.CodeBean;
import com.nahuo.kdb.model.PayBean;
import com.nahuo.kdb.model.SaleDetailBean;
import com.nahuo.library.controls.LoadingDialog;

import java.util.Vector;

public class OrderPayActivity extends AppCompatActivity implements View.OnClickListener {
    public static String EXTRA_PAYDETAIL = "ETRA_PAYDETAIL";
    private RadioButton radio_wechat, radio_alipay, radio_cash;
    private TextView tvzl;
    private EditText etSS;
    private double ss_money;
    private double payment_money;
    private TextView tv_top, tv_bottom;
    private int mPrinterIndex = 0;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    private GpService mGpService = null;
    private PrinterServiceConnection conn = null;
    private TextView item_detail_print;
    private static final int REQUEST_ENABLE_BT = 3;
    private Context mContext;
    private int pay_type = 2;
    private SaleDetailBean data = null;
    private LoadingDialog mloadingDialog;
    private Task task;
    private CheckTask checkTask;
    private String ordercode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pay);
        BWApplication.addActivity(this);
        mloadingDialog = new LoadingDialog(this);
        mContext = this;
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
        findViewById(R.id.tvTLeft).setOnClickListener(this);
        findViewById(R.id.btn_pay).setOnClickListener(this);
        findViewById(R.id.ll_wechat).setOnClickListener(this);
        findViewById(R.id.ll_alipay).setOnClickListener(this);
        findViewById(R.id.ll_cash).setOnClickListener(this);
        tv_top = (TextView) findViewById(R.id.tv_top);
        tv_bottom = (TextView) findViewById(R.id.tv_bottom);
        radio_wechat = (RadioButton) findViewById(R.id.radio_wechat);
        radio_alipay = (RadioButton) findViewById(R.id.radio_alipay);
        radio_cash = (RadioButton) findViewById(R.id.radio_cash);
        ((TextView) findViewById(R.id.tvTitleCenter)).setText("订单付款");
        tvzl = (TextView) findViewById(R.id.print_order_zl);
        etSS = (EditText) findViewById(R.id.print_order_ss);
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
                if (tvzl != null)
                    tvzl.setText("找零:" + Utils.moneyFormat(ss_money - payment_money));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setEditFocus(false);
        etSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pay_type == 1) {
                    setEditFocus(true);
                } else {
                    setEditFocus(false);
                }
            }
        });
        data = (SaleDetailBean) getIntent().getSerializableExtra(this.EXTRA_PAYDETAIL);
        if (data != null) {
            ordercode = data.getOrderCode();
            payment_money = Double.parseDouble(data.getPayableAmount());
            tv_top.setText("¥" + data.getPayableAmount());
            tv_bottom.setText("数量：" + data.getProductCount() + "   折扣：" + data.getGetDiscountPercent() + "   优惠：" + data.getDiscount());
        }
    }

    private void setEditFocus(boolean isEdit) {
        if (etSS != null) {
            if (isEdit) {
                etSS.setCursorVisible(true);
                etSS.setFocusable(true);
                etSS.setFocusableInTouchMode(true);
                etSS.requestFocus();
            } else {
                etSS.setCursorVisible(false);
                etSS.setFocusable(false);
                etSS.setFocusableInTouchMode(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTLeft:
                finish();
                break;
            case R.id.btn_pay:
                goPay(pay_type);
                break;
            case R.id.ll_wechat:
                radio_wechat.setChecked(true);
                radio_cash.setChecked(false);
                radio_alipay.setChecked(false);
                pay_type = 2;
                setEditFocus(false);
                break;
            case R.id.ll_alipay:
                radio_wechat.setChecked(false);
                radio_cash.setChecked(false);
                radio_alipay.setChecked(true);
                pay_type = 3;
                setEditFocus(false);
                break;
            case R.id.ll_cash:
                radio_wechat.setChecked(false);
                radio_cash.setChecked(true);
                radio_alipay.setChecked(false);
                pay_type = 1;
                setEditFocus(true);
                break;
        }
    }

    private void goPay(int type) {
//        switch (type) {
//            case 1:
//
//                break;
//            case 2:
//                break;
//            case 3:
//                break;
//        }
//        if (type == 1) {
//            if (ss_money <= 0) {
//                ViewHub.showLongToast(mContext, "请输入现金");
//                return;
//            } else if ((ss_money - payment_money) < 0) {
//                ViewHub.showLongToast(mContext, "应支付金额还差"+Utils.moneyFormat(payment_money-ss_money));
//                return;
//            }
//        }
        task = new Task();
        task.setType(pay_type);
        task.execute();
    }

    private int check_count = 1;

    private class CheckTask extends AsyncTask<Object, Void, Object> {


        @Override
        protected void onPreExecute() {
            if (mloadingDialog != null)
                mloadingDialog.start("检查订单第" + check_count + "次中...");

        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (data != null) {
                    PayBean bean = KdbAPI.checkOrder(mContext, data.getOrderCode());
                    return bean;
                } else {
                    return "error:没订单号";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            try {
                if (result instanceof String && ((String) result).startsWith("error:")) {
                    loadFinish();

                    ViewHub.showOkDialog(mContext, "付款失败", "用户付款失败或二维码失效，如已付款，请查看订单状态是否已同步！", "查看详情", "关闭", new ViewHub.OkDialogListener() {
                        @Override
                        public void onPositiveClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(mContext, SaleDetailActivity.class);
                            intent.putExtra("orderCode", ordercode);
                            startActivity(intent);

                        }

                        @Override
                        public void onNegativeClick(DialogInterface dialog, int which) {
                            // printReceiptClicked();
                            // startprint();
                        }
                    });
                    // ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
                } else {
                    if (result instanceof PayBean) {
                        PayBean payBean = (PayBean) result;
                        if (payBean != null) {
                            if (payBean.getStatu().equals("已完成")) {
                                loadFinish();
                                ViewHub.showOkDialog(mContext, "付款成功", "已付款成功，是否打印小票？", "打印", "不了", new ViewHub.OkDialogListener() {
                                    @Override
                                    public void onPositiveClick(DialogInterface dialog, int which) {

                                        printReceiptClicked();

                                    }

                                    @Override
                                    public void onNegativeClick(DialogInterface dialog, int which) {
                                        BWApplication.getInstance().reBackFirst();
                                    }
                                });
                            } else if (payBean.getStatu().equals("已取消")) {
                                loadFinish();
                                ViewHub.showOkDialog(mContext, "付款已取消", "用户付款失败或二维码失效，如已付款，请查看订单状态是否已同步！", "查看详情", "关闭", new ViewHub.OkDialogListener() {
                                    @Override
                                    public void onPositiveClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(mContext, SaleDetailActivity.class);
                                        intent.putExtra("orderCode", ordercode);
                                        startActivity(intent);

                                    }

                                    @Override
                                    public void onNegativeClick(DialogInterface dialog, int which) {
                                        // printReceiptClicked();
                                        // startprint();
                                    }
                                });
                            } else if (payBean.getStatu().equals("待支付")) {
                                if (check_count == 5) {
                                    loadFinish();
                                    ViewHub.showOkDialog(mContext, "付款待支付", "用户付款待支付或二维码失效，如已付款，请查看订单状态是否已同步！", "查看详情", "关闭", new ViewHub.OkDialogListener() {
                                        @Override
                                        public void onPositiveClick(DialogInterface dialog, int which) {

                                            Intent intent = new Intent(mContext, SaleDetailActivity.class);
                                            intent.putExtra("orderCode", ordercode);
                                            startActivity(intent);

                                        }

                                        @Override
                                        public void onNegativeClick(DialogInterface dialog, int which) {
                                            // printReceiptClicked();
                                            // startprint();
                                        }
                                    });
                                } else {
                                    check_count++;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            checkTask = new CheckTask();
                                            checkTask.execute();
                                        }
                                    }, 3000);
                                }

                            }
                        } else {
                            loadFinish();
                        }
                    } else {
                        loadFinish();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                loadFinish();
            }
        }
    }

    private void loadFinish() {
        if (mloadingDialog != null) {
            mloadingDialog.stop();
        }
    }

    public class Task extends AsyncTask<Void, Void, Object> {
        int type;

        public void setType(int type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mloadingDialog != null) {
                if (this.type == 1) {
                    mloadingDialog.start("付款中....");
                } else if (this.type == 2 || this.type == 3) {
                    mloadingDialog.start("获取付款码中....");
                }

            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if (mloadingDialog != null)
                mloadingDialog.stop();
            try {
                if (result instanceof String && ((String) result).startsWith("error:")) {
                    ViewHub.showOkDialog(mContext, "提示",((String) result).replace("error:", ""), "关闭");
//                    ViewHub.showOkDialog(mContext, "付款失败", "用户付款失败或二维码失效，如已付款，请查看订单状态是否已同步！", "查看详情", "关闭", new ViewHub.OkDialogListener() {
//                        @Override
//                        public void onPositiveClick(DialogInterface dialog, int which) {
//
//                            Intent intent = new Intent(mContext, SaleDetailActivity.class);
//                            intent.putExtra("orderCode", ordercode);
//                            startActivity(intent);
//
//                        }
//
//                        @Override
//                        public void onNegativeClick(DialogInterface dialog, int which) {
//                            // printReceiptClicked();
//                            // startprint();
//                        }
//                    });
                    //ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
                } else {
                    switch (type) {
                        case 1:
                            check_count = 1;
                            checkTask = new CheckTask();
                            checkTask.execute();
                            break;
                        case 2:
                        case 3:
                            CodeBean codeBean = (CodeBean) result;
                            String code = "";
                            if (codeBean != null) {
                                code = codeBean.getQrPayCode();
                            }
                            PopCodeMenu.getInstance(mContext, code).setCode(code).setmLister(new PopCodeMenu.PopLister() {
                                @Override
                                public void onFinish() {
                                    check_count = 1;
                                    checkTask = new CheckTask();
                                    checkTask.execute();
                                }
                            }).show();
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                CodeBean bean = OtherAPI.goCodePay4ShopTrade(mContext, data.getOrderCode(), data.getPayableAmount(), type);
                return bean;
            } catch (Exception ex) {
                ex.printStackTrace();
                return ex.getMessage() == null ? "error:空" : "error:" + ex.getMessage();
            }
        }

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
            esc.addText(SpManager.getShopName(mContext) + "\n"); // 打印文字
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
                    ViewHub.showLongToast(mContext, "蓝牙异常");
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
                ViewHub.showOkDialog(mContext, "蓝牙打印机未连接", "连接打印机？", "确定", "取消", new ViewHub.OkDialogListener() {
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
        if (task != null)
            task.cancel(true);
    }

    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(this, GpPrintService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

}
