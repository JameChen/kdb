package com.nahuo.kdb;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nahuo.kdb.api.AccountAPI;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.library.controls.LoadingDialog;

;

public class ShopSetActivity extends Activity implements View.OnClickListener {

    private Context mContext = this;
    private LoadingDialog mDialog;
    private EditText txtShopName;
    private TextView tv_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop_set);
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        initTitleBar();
    }

    int Type;

    private void initTitleBar() {

        Type = getIntent().getIntExtra("Type", -1);
        mDialog = new LoadingDialog(this);
        tv_name = (TextView) findViewById(R.id.tv_name);
        txtShopName = (EditText) findViewById(R.id.shopset_shopname);
        Button btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setOnClickListener(this);
        switch (Type) {
            case 1:
                setTitle("店铺设置");
                tv_name.setText("店铺名称：");
                txtShopName.setInputType(InputType.TYPE_CLASS_TEXT);
                txtShopName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
                if (!TextUtils.isEmpty(SpManager.getShopName(mContext))) {
                    txtShopName.setText(SpManager.getShopName(mContext));
                }
                break;
            case 2:
                setTitle("店铺签名设置");
                tv_name.setText("店铺签名：");
                if (!TextUtils.isEmpty(SpManager.getShopSignature(mContext))) {
                    txtShopName.setText(SpManager.getShopSignature(mContext));
                }
                break;
            case 3:
                setTitle("联系电话设置");
                tv_name.setText("联系电话：");
                txtShopName.setInputType(InputType.TYPE_CLASS_PHONE);
                txtShopName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                if (!TextUtils.isEmpty(SpManager.getShopMobile(mContext))) {
                    txtShopName.setText(SpManager.getShopMobile(mContext));
                }
                break;
        }
        txtShopName.setSelection(txtShopName.getText().toString().trim().length());
        showRight();
    }

    private void showRight() {
        TextView btnRight = (TextView) findViewById(R.id.tv_right);
        btnRight.setText("保存");
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setOnClickListener(this);
    }

    private void setTitle(String text) {
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right:
                if (txtShopName.getText().toString().length() > 0) {
                    new Task(Type).execute();
                } else {
                    ViewHub.showLongToast(mContext, "请输入店铺名称");
                }
                break;
            case R.id.titlebar_btnLeft:
                finish();
                break;
        }
    }

    private class Task extends AsyncTask<Object, Void, Object> {

        String shopname;
        int type;

        private Task(int Type) {
            this.type = Type;
            shopname = txtShopName.getText().toString();
        }

        @Override
        protected void onPreExecute() {
            mDialog.start("保存中...");
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (type == 1) {
                    AccountAPI.SaveShopInfo(mContext, SpManager.getSignature(mContext), SpManager.getShopMobile(mContext), shopname);
                } else if (type == 2) {
                    AccountAPI.SaveShopInfo(mContext, shopname, SpManager.getShopMobile(mContext), SpManager.getShopName(mContext));
                } else if (type == 3) {
                    AccountAPI.SaveShopInfo(mContext, SpManager.getSignature(mContext), shopname, SpManager.getShopName(mContext));
                }
                return "OK";
            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if (mDialog.isShowing()) {
                mDialog.stop();
            }
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
            } else {
                ViewHub.showLongToast(mContext, "保存成功");
                if (type == 1) {
                    SpManager.setShopName(mContext, shopname);
                } else if (type == 2) {
                    SpManager.setShopSignature(mContext, shopname);
                } else if (type == 3) {
                    SpManager.setShopMobile(mContext, shopname);
                }
                finish();
            }
        }

    }
}
