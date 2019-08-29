package com.nahuo.kdb;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.nahuo.kdb.activity.AuthorityActivity;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.provider.UserInfoProvider;
import com.nahuo.kdb.task.CheckUpdateTask;
import com.nahuo.library.controls.LightPopDialog;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.service.autoupdate.AppUpdate;
import com.nahuo.service.autoupdate.AppUpdateService;

import static com.nahuo.kdb.R.id.me_phone;
import static com.nahuo.kdb.R.id.me_sign;

/**
 * @description 我的账号
 * @created 2014-8-15 上午11:01:44
 */
public class MeSettingActivity extends BaseActivity2 implements OnClickListener {

    private Context mContext = this;
    private static final String TAG = "MeSettingActivity";
    private MeSettingActivity vThis = this;
    private AppUpdate mAppUpdate;
    private LoadingDialog loadingDialog;
    private TextView mTvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);
        setTitle("设置");
        mAppUpdate = AppUpdateService.getAppUpdate(this);
        initView();


    }

    // 初始化数据
    private void initView() {
        loadingDialog = new LoadingDialog(this);
        initItem(R.id.item_app_authority, "权限设置");
        initItem(R.id.item_app_update, "版本更新");
        setItemRightText(R.id.item_app_update, "当前版本：" + FunctionHelper.GetAppVersion(vThis));
        initItem(R.id.item_shopset, "店铺名称");
        initItem(me_sign, "店铺签名");
        initItem(R.id.me_phone, "联系电话");
        initItem(R.id.me_checkout, "退出");
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(vThis, ShopSetActivity.class);
        switch (v.getId()) {
            case R.id.item_app_authority:
                startActivity(new Intent(vThis, AuthorityActivity.class));
                break;
            case R.id.titlebar_btnLeft:// 返回
                finish();
                break;

            case R.id.me_checkout:// 退出登录
                ViewHub.showLightPopDialog(this, getString(R.string.dialog_title),
                        getString(R.string.shopset_exit_confirm), "取消", "退出登录", new LightPopDialog.PopDialogListener() {
                            @Override
                            public void onPopDialogButtonClick(int which) {
                                UserInfoProvider.exitApp(mContext);
                                finish();
                            }
                        });
                break;
            case R.id.item_app_update:// 版本更新
                new CheckUpdateTask(this, mAppUpdate, true, true).execute();
                break;
            case R.id.item_shopset:// 店铺设置

                intent.putExtra("Type", 1);
                startActivity(intent);
                break;
            case R.id.me_sign:// 店签名
                intent.putExtra("Type", 2);
                startActivity(intent);
                break;
            case R.id.me_phone:// 联系电话
                intent.putExtra("Type", 3);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    @Override
    public void finish() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        super.finish();
    }

    private void toOtherActivity(Class<?> cls) {
        Intent intent = new Intent(vThis, cls);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
        mAppUpdate.callOnPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SpManager.getShopName(vThis).length() > 0) {
            setItemRightText(R.id.item_shopset, SpManager.getShopName(vThis));
        } else {
            setItemRightText(R.id.item_shopset, "待设置");
        }
        if (SpManager.getShopSignature(vThis).length() > 0) {
            setItemRightText(me_sign, SpManager.getShopSignature(vThis));
        } else {
            setItemRightText(me_sign, "待设置");
        }
        if (SpManager.getShopMobile(vThis).length() > 0) {
            setItemRightText(me_phone, SpManager.getShopMobile(vThis));
        } else {
            setItemRightText(me_phone, "待设置");
        }
        StatService.onResume(this);
        mAppUpdate.callOnResume();
    }

    private void initItem(int viewId, String text) {
        View v = findViewById(viewId);
        v.setOnClickListener(this);
        TextView tv = (TextView) v.findViewById(R.id.tv_left_text);
        ImageView ivLeftIcon = (ImageView) v.findViewById(R.id.iv_left_icon);

        tv.setText(text);
        ivLeftIcon.setVisibility(View.GONE);

    }

    private void setItemRightText(int viewId, String text) {
        View v = findViewById(viewId);
        TextView tv = (TextView) v.findViewById(R.id.tv_right_text);
        tv.setText(text);
    }

    private void setItemRightText(int viewId, Spanned spanned) {
        View v = findViewById(viewId);
        TextView tv = (TextView) v.findViewById(R.id.tv_right_text);
        tv.setText(spanned);
    }

}
