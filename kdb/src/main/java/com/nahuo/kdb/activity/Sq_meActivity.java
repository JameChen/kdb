package com.nahuo.kdb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.common.BlurTransform;
import com.nahuo.kdb.common.Const;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.library.helper.DisplayUtil;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Sq_meActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "Sq_meActivity";
    private Sq_meActivity Vthis = this;
    private CircleImageView mIvAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sq_me);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        findViewById(R.id.titlebar_btnLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tv_title)).setText("我的信息");
        String userName = SpManager.getUserName(BWApplication.getInstance());
        String logo = SpManager.getShopLogo(BWApplication.getInstance()).trim();// SpManager.getUserLogo(NHApplication.getInstance());
        TextView name = (TextView) Vthis.findViewById(R.id.txt_name);
        mIvAvatar = (CircleImageView) Vthis.findViewById(R.id.iv_userhead);
        mIvAvatar.setBorderWidth(DisplayUtil.dip2px(this, 2));
        mIvAvatar.setBorderColor(getResources().getColor(R.color.white));
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
        initItem(R.id.item_my_wallet, "我的钱包", false);
        setItemRightText(R.id.item_my_wallet, "");
        setItemLeftImageView(R.id.item_my_wallet, R.drawable.wodedingdan);
        initItem(R.id.item_my_invoice, "收货单", false);
        setItemRightText(R.id.item_my_invoice, "");
        setItemLeftImageView(R.id.item_my_invoice, R.drawable.wodedingdan);
        initItem(R.id.item_my_receipt, "发货单", false);
        setItemRightText(R.id.item_my_receipt, "");
        setItemLeftImageView(R.id.item_my_receipt, R.drawable.wodedingdan);

    }

    private void initItem(int viewId, String text, boolean b) {
        View v = findViewById(viewId);
        v.setOnClickListener(Vthis);
        TextView tv = (TextView) v.findViewById(R.id.tv_left_text);
        ImageView ivLeftIcon = (ImageView) v.findViewById(R.id.iv_left_icon);
        View bBtmLine = v.findViewById(R.id.view_btm_line);
        if (!b) {
            bBtmLine.setVisibility(View.VISIBLE);
        } else {
            bBtmLine.setVisibility(View.GONE);
        }

        tv.setText(text);
        ivLeftIcon.setVisibility(View.GONE);

    }

    private void setItemRightText(int viewId, String text) {
        View v = findViewById(viewId);
        TextView tv = (TextView) v.findViewById(R.id.tv_right_text);
        tv.setText(text);
    }

    private void setItemLeftImageView(int viewId, int resId) {
        View v = findViewById(viewId);
        ImageView ivLeftIcon = (ImageView) v.findViewById(R.id.iv_left_icon);
        ivLeftIcon.setImageResource(resId);
        ivLeftIcon.setVisibility(View.VISIBLE);
    }

    private void setItemRightText(int viewId, Spanned spanned) {
        View v = findViewById(viewId);
        TextView tv = (TextView) v.findViewById(R.id.tv_right_text);
        tv.setText(spanned);
    }

    private void initLogoBgView(String url) {
        Picasso.with(BWApplication.getInstance())
                .load(url)
                .transform(new BlurTransform(80))
                .placeholder(R.drawable.empty_photo).into((ImageView) Vthis.findViewById(R.id.iv_logobg));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_my_wallet:
                //我的钱包
                break;
            case R.id.item_my_invoice:
                //我的收货单
                break;
            case R.id.item_my_receipt:
                //我的发货单
                break;
        }
    }
}
