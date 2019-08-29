package com.nahuo.kdb.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;


public class EditInventoryDialog extends Dialog implements View.OnClickListener {

    private Activity mActivity;
    private View mRootView;
    private LinearLayout mContentViewBg;
    private TextView mTvTitle;TextView mTvMessage;
    private Button mBtnCancel, mBtnOK;
    private PopDialogListener mPositivePopDialogListener;
    private PopDialogListener mNegativePopDialogListener;
    private LinearLayout mContentView;
    private boolean isShowDismiss = true;
    static EditInventoryDialog dialog;
    public static final int BUTTON_POSITIVIE = 1;
    public static final int BUTTON_NEGATIVE = 0;
    String content = "", left = "", right;
    private EditText et_txt;
    private String useName="";

    public EditInventoryDialog setUseName(String useName) {
        this.useName = useName;
        return this;
    }

    public  enum  DialogType {
        D_ADD, D_EDIT
    }

    public void setIsShowDismiss(boolean showDismiss) {
        isShowDismiss = showDismiss;
        mBtnCancel.setVisibility(isShowDismiss ? View.VISIBLE : View.GONE);
    }

    public static EditInventoryDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new EditInventoryDialog(activity);
        }
        return dialog;
    }

    public EditInventoryDialog(Activity activity) {
        super(activity, R.style.popDialog);
        this.mActivity = activity;
        initViews();
    }

    public View getmRootView() {
        return mRootView;
    }


    private void initViews() {
        mRootView = mActivity.getLayoutInflater().inflate(R.layout.light_popwindow_dialog_inventory_edit, null);
        mContentViewBg = (LinearLayout) mRootView.findViewById(R.id.contentView);
        mTvTitle = (TextView) mContentViewBg.findViewById(R.id.tv_title);
        mContentView = (LinearLayout) mRootView.findViewById(R.id.ll_content);
        mTvMessage = (TextView) mContentViewBg.findViewById(R.id.tv_message);
        mBtnCancel = (Button) mContentViewBg.findViewById(R.id.btn_cancle);
        mBtnOK = (Button) mContentViewBg.findViewById(R.id.btn_ok);
        et_txt= (EditText) mContentView.findViewById(R.id.et_txt);
        mBtnCancel.setOnClickListener(this);
        mBtnOK.setOnClickListener(this);
        if (mBtnOK != null)
            mBtnOK.setText("确定");
        if (mBtnCancel != null)
            mBtnCancel.setText("取消");

        setCanceledOnTouchOutside(false);
        OnKeyListener keylistener = new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        this.setContentView(mRootView);
        setOnKeyListener(keylistener);
        setCancelable(true);
//        mRootView.setOnTouchListener(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int height = mContentViewBg.getTop();
//                int bottom = mContentViewBg.getBottom();
//
//                int y = (int)event.getY();
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (y < height || y > bottom) {
//                        dismiss();
//                    }
//                }
//                return true;
//            }
//        });

       /* this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);*/

//        this.setFocusable(true);
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        this.setBackgroundDrawable(dw);
//        setAnimationStyle(R.style.LightPopDialogAnim);
    }

    public void showDialog() {
//        if (!TextUtils.isEmpty(content)) {
//            if (tv_title != null)
//                tv_title.setText(content);
//        }   if (mTvMessage!=null)
//        if (type== DialogType.D_EDIT){
//            if (et_use_name!=null) {
//                et_use_name.setText(useName);
//                et_use_name.setCursorVisible(false);
//                et_use_name.setFocusable(false);
//                et_use_name.setFocusableInTouchMode(false);
//            }
//        }
        if (dialog!=null)
        dialog.show();
    }
//    public void show() {
//        DisplayMetrics dm = new DisplayMetrics();
//        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//        // int screenWidth = dm.widthPixels;
//        int screenHeight = dm.heightPixels;
//        int top = mContentViewBg.getTop();
//        int bottom = mContentViewBg.getBottom();
//        showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER, 0, screenHeight / 2 - (bottom - top) / 2);
//    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            String userName="",password="";
            if (et_txt!=null)
                userName=et_txt.getText().toString().trim();
            if (TextUtils.isEmpty(userName)){
                ViewHub.showLongToast(mActivity,"备注不能空");
                return;
            }
            if (mPositivePopDialogListener != null) {
                mPositivePopDialogListener.onPopDialogButtonClick(BUTTON_POSITIVIE,userName);
            }
            dialog = null;
            dismiss();
        } else if (id == R.id.btn_cancle) {
            if (mPositivePopDialogListener != null) {
                mPositivePopDialogListener.onPopDialogButtonClick(BUTTON_NEGATIVE,"");
            }
            dialog = null;
            dismiss();
        }
    }

    DialogType type;

    public EditInventoryDialog setDialogType (DialogType type) {
        this.type = type;
        return this;
    }

    public EditInventoryDialog setContent(String content) {
        this.content = content;
        return this;
    }

    public EditInventoryDialog setLeftStr(String left) {
        this.left = left;
        return this;
    }

    public EditInventoryDialog setRightStr(String right) {
        this.right = right;
        return this;
    }

    public EditInventoryDialog setIcon(int resId) {
        mTvTitle.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        return this;
    }

    public EditInventoryDialog setIcon(Drawable icon) {
        mTvTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        return this;
    }

    public EditInventoryDialog setMessage(CharSequence message) {
        mTvMessage.setText(message);
        return this;
    }

    public EditInventoryDialog setMessage(int resId) {
        mTvMessage.setText(resId);
        return this;
    }

    public EditInventoryDialog setNegative(CharSequence text, PopDialogListener listener) {
        mBtnCancel.setText(text);
        mNegativePopDialogListener = listener;
        return this;
    }

    public EditInventoryDialog setNegative(int resId, PopDialogListener listener) {
        mBtnCancel.setText(resId);
        mNegativePopDialogListener = listener;
        return this;
    }

    public EditInventoryDialog setPositive(PopDialogListener listener) {
        mPositivePopDialogListener = listener;
        return this;
    }


    public void addContentView(View child) {
        mContentView.addView(child);
    }

    public interface PopDialogListener {
         void onPopDialogButtonClick(int ok_cancel,
                                     String txt);
    }

}
