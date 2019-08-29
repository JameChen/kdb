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
import com.nahuo.kdb.controls.DropDownView;
import com.nahuo.kdb.model.SelectBean;

import java.util.ArrayList;


public class AddEditMemDialog extends Dialog implements View.OnClickListener {

    private Activity mActivity;
    private View mRootView;
    private LinearLayout mContentViewBg;
    private TextView mTvTitle;
    TextView mTvMessage;
    private Button mBtnCancel, mBtnOK;
    private PopDialogListener mPositivePopDialogListener;
    private PopDialogListener mNegativePopDialogListener;
    private LinearLayout mContentView;
    private boolean isShowDismiss = true;
    static AddEditMemDialog dialog;
    public static final int BUTTON_POSITIVIE = 1;
    public static final int BUTTON_NEGATIVE = 0;
    String content = "", left = "", right;
    private EditText et_use_name, et_pass_word, et_alias_name;
    private String useName = "", groupName = "";
    private View layout_role;
    private DropDownView tv_sort_role;
    private ArrayList<SelectBean> sList;
    private String alias_name = "";
    private int groupID;
    private int defautGroupId;

    public AddEditMemDialog setDefautGroupId(int defautGroupId) {
        this.defautGroupId = defautGroupId;
        return this;
    }

    public AddEditMemDialog setsList(ArrayList<SelectBean> sList) {
        this.sList = sList;
        return this;
    }

    public AddEditMemDialog setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public AddEditMemDialog setUseName(String useName) {
        this.useName = useName;
        return this;
    }

    public AddEditMemDialog setAliasName(String alias_name) {
        this.alias_name = alias_name;
        return this;
    }

    public enum DialogType {
        D_ADD, D_EDIT
    }

    public void setIsShowDismiss(boolean showDismiss) {
        isShowDismiss = showDismiss;
        mBtnCancel.setVisibility(isShowDismiss ? View.VISIBLE : View.GONE);
    }

    public static AddEditMemDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new AddEditMemDialog(activity);
        }
        return dialog;
    }

    public AddEditMemDialog(Activity activity) {
        super(activity, R.style.popDialog);
        this.mActivity = activity;
        initViews();
    }

    public View getmRootView() {
        return mRootView;
    }


    private void initViews() {
        mRootView = mActivity.getLayoutInflater().inflate(R.layout.light_popwindow_dialog_add_edit, null);
        mContentViewBg = (LinearLayout) mRootView.findViewById(R.id.contentView);
        mTvTitle = (TextView) mContentViewBg.findViewById(R.id.tv_title);
        mContentView = (LinearLayout) mRootView.findViewById(R.id.ll_content);
        mTvMessage = (TextView) mContentViewBg.findViewById(R.id.tv_message);
        mBtnCancel = (Button) mContentViewBg.findViewById(R.id.btn_cancle);
        mBtnOK = (Button) mContentViewBg.findViewById(R.id.btn_ok);
        et_use_name = (EditText) mContentView.findViewById(R.id.et_user_name);
        et_pass_word = (EditText) mContentView.findViewById(R.id.et_user_password);
        et_alias_name = (EditText) mContentView.findViewById(R.id.et_alias_name);
        mBtnCancel.setOnClickListener(this);
        mBtnOK.setOnClickListener(this);
        layout_role = mContentView.findViewById(R.id.layout_role);
        tv_sort_role = (DropDownView) mContentView.findViewById(R.id.tv_sort_role);
        tv_sort_role.setNUSELETED_SHOW_NAME("请选择");
        tv_sort_role.setOnItemClickListener(new DropDownView.OnItemClickListener() {
            @Override
            public void onItemClick(SelectBean map, int pos, int realPos, int Type) {
                if (map == null) {
                    groupID = defautGroupId;
                } else {
                    groupID = map.getID();
                }
            }
        });
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
        groupID = defautGroupId;
        if (type == DialogType.D_EDIT) {
            if (et_use_name != null) {
                et_use_name.setText(useName);
                et_use_name.setCursorVisible(false);
                et_use_name.setFocusable(false);
                et_use_name.setFocusableInTouchMode(false);
            }
            if (et_alias_name != null) {
                et_alias_name.setText(alias_name);
                et_alias_name.setSelection(alias_name.length());
            }
            if (tv_sort_role != null) {
                if (!TextUtils.isEmpty(groupName)) {
                    tv_sort_role.setText(groupName);
                } else {
                    tv_sort_role.setText("请选择");
                }
                tv_sort_role.setupDataList(sList);
            }
            if (layout_role != null)
                layout_role.setVisibility(View.VISIBLE);
        } else {
            if (layout_role != null)
                layout_role.setVisibility(View.GONE);
        }
        if (dialog != null)
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
            String userName = "", password = "", aName = "";
            if (et_use_name != null)
                userName = et_use_name.getText().toString().trim();
            if (et_pass_word != null)
                password = et_pass_word.getText().toString().trim();
            if (et_alias_name != null)
                aName = et_alias_name.getText().toString().trim();
            if (TextUtils.isEmpty(userName)) {
                ViewHub.showLongToast(mActivity, "账号名不能空");
                return;
            }
            if (TextUtils.isEmpty(password) && type != DialogType.D_EDIT) {
                ViewHub.showLongToast(mActivity, "密码不能空");
                return;
            }
            if (mPositivePopDialogListener != null) {
                mPositivePopDialogListener.onPopDialogButtonClick(BUTTON_POSITIVIE, type, userName, password, aName, groupID);
            }
            dialog = null;
            dismiss();
        } else if (id == R.id.btn_cancle) {
            if (mPositivePopDialogListener != null) {
                mPositivePopDialogListener.onPopDialogButtonClick(BUTTON_NEGATIVE, type, "", "", "", 0);
            }
            dialog = null;
            dismiss();
        }
    }

    DialogType type;

    public AddEditMemDialog setDialogType(DialogType type) {
        this.type = type;
        return this;
    }

    public AddEditMemDialog setContent(String content) {
        this.content = content;
        return this;
    }

    public AddEditMemDialog setLeftStr(String left) {
        this.left = left;
        return this;
    }

    public AddEditMemDialog setRightStr(String right) {
        this.right = right;
        return this;
    }

    public AddEditMemDialog setIcon(int resId) {
        mTvTitle.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        return this;
    }

    public AddEditMemDialog setIcon(Drawable icon) {
        mTvTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        return this;
    }

    public AddEditMemDialog setMessage(CharSequence message) {
        mTvMessage.setText(message);
        return this;
    }

    public AddEditMemDialog setMessage(int resId) {
        mTvMessage.setText(resId);
        return this;
    }

    public AddEditMemDialog setNegative(CharSequence text, PopDialogListener listener) {
        mBtnCancel.setText(text);
        mNegativePopDialogListener = listener;
        return this;
    }

    public AddEditMemDialog setNegative(int resId, PopDialogListener listener) {
        mBtnCancel.setText(resId);
        mNegativePopDialogListener = listener;
        return this;
    }

    public AddEditMemDialog setPositive(PopDialogListener listener) {
        mPositivePopDialogListener = listener;
        return this;
    }


    public void addContentView(View child) {
        mContentView.addView(child);
    }

    public interface PopDialogListener {
        void onPopDialogButtonClick(int ok_cancel, DialogType type, String userName, String password, String alias_name, int groupID);
    }

}
