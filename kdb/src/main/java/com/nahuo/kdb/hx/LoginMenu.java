package com.nahuo.kdb.hx;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nahuo.kdb.R;


/**
 * Created by jame on 2017/4/12.
 */

public class LoginMenu extends Dialog implements View.OnClickListener{
    private View mRootView;
    private int h;
    private int w;
    private Context context;
    private static LoginMenu dialog = null;
    private LinearLayout      mContentViewBg;
    private TextView          mTvTitle, mTvMessage;
    private Button            mBtnCancel, mBtnOK;
    private LinearLayout      mContentView;
    private PopDialogListener mPositivePopDialogListener;
    private PopDialogListener mNegativePopDialogListener;
    public static final int   BUTTON_POSITIVIE = 1;
    public static final int   BUTTON_NEGATIVE  = 0;
    public String title,mess;
    public static LoginMenu getInstance(Activity context,String title,String mess) {
        if (dialog == null) {
            synchronized (LoginMenu.class) {
                if (dialog == null) {
                    dialog = new LoginMenu(context,title,mess);
                }
            }
        }
        return dialog;
    }

    public LoginMenu(Activity context,String title,String mess) {
        super(context, R.style.popDialog);
        this.context = context;
        this.title=title;
        this.mess=mess;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {

        h = context.getResources().getDisplayMetrics().heightPixels;
        w = context.getResources().getDisplayMetrics().widthPixels;
        mRootView = LayoutInflater.from(context).inflate(R.layout.light_popwindow_dialog1, null);
        setContentView(mRootView);
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

        setOnKeyListener(keylistener);
        setCancelable(true);
        mTvTitle = (TextView)mRootView.findViewById(R.id.tv_title);
        mTvTitle.setText(""+title);
        mContentView = (LinearLayout)mRootView.findViewById(R.id.ll_content);
        mTvMessage = (TextView)mRootView.findViewById(R.id.tv_message);
        mTvMessage.setText(""+mess);
        mBtnCancel = (Button)mRootView.findViewById(R.id.btn_cancle);
        mBtnOK = (Button)mRootView.findViewById(R.id.btn_ok);
        mBtnCancel.setOnClickListener(this);
        mBtnOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            if (mPositivePopDialogListener != null) {
                mPositivePopDialogListener.onPopDialogButtonClick(BUTTON_POSITIVIE);
            }
            dismiss();
            dialog=null;
        } else if (id == R.id.btn_cancle) {
            if (mNegativePopDialogListener != null) {
                mNegativePopDialogListener.onPopDialogButtonClick(BUTTON_NEGATIVE);
            }
            dismiss();
            dialog=null;
        }
    }


    public LoginMenu setNegative( PopDialogListener listener) {
        mNegativePopDialogListener = listener;
        return this;
    }

    public LoginMenu setPositive( PopDialogListener listener) {
        mPositivePopDialogListener = listener;
        return this;
    }

    public void addContentView(View child) {
        mContentView.addView(child);
    }

    public interface PopDialogListener {
         void onPopDialogButtonClick(int which);
    }


}
