package com.nahuo.kdb.controls;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.nahuo.kdb.R;
import com.nahuo.kdb.utils.ScreenUtils;

/**
 * Created by jame on 2017/4/12.
 */

public class PopCodeMenu extends Dialog {
    private View mRootView;
    private View iv_del;
    private int h;
    private int w;
    private static PopCodeMenu dialog = null;
    private String code = "";
    private TextView tv_finish;
    private PopLister mLister;

    public PopCodeMenu setmLister(PopLister mLister) {
        this.mLister = mLister;
        return this;
    }

    public PopCodeMenu setCode(String code) {
        this.code = code;
        return  this;
    }
    public interface PopLister{
        void onFinish();
    }
    private Context context;

    public static PopCodeMenu getInstance(Context context, String code) {
        if (dialog == null) {
            synchronized (PopCodeMenu.class) {
                if (dialog == null) {
                    dialog = new PopCodeMenu(context, code);
                }
            }
        }
        return dialog;
    }

    public PopCodeMenu(Context context, String code) {
        super(context, R.style.popDialog);
        this.context = context;
        this.code = code;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    ImageView imageView;
    Task task;

    private void initViews() {

        h = context.getResources().getDisplayMetrics().heightPixels;
        w = context.getResources().getDisplayMetrics().widthPixels;
        mRootView = LayoutInflater.from(context).inflate(R.layout.dialog_code, null);
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(w, h);
        setContentView(mRootView, layoutParams);
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
        imageView = (ImageView) mRootView.findViewById(R.id.pic);
        tv_finish=(TextView)mRootView.findViewById(R.id.tv_finish);
        tv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLister!=null)
                    mLister.onFinish();
                dialog = null;
                dismiss();
            }
        });
        task = new Task();
        task.setImageView(imageView);
        task.execute(code);
        iv_del = mRootView.findViewById(R.id.iv_del);
        iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = null;
                dismiss();
            }
        });

    }

    class Task extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return encodeAsBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    Bitmap encodeAsBitmap(String str) {
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
           int width =ScreenUtils.getScreenWidth(context)*4/5;
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, width, width);
            // 使用 ZXing Android Embedded 要写的代码
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException iae) { // ?
            return null;
        }
        return bitmap;
    }
//    public void show() {
////        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
////        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
////
//////        this.setWidth(w * 4 / 5);
//////        this.setHeight(h * 2 / 3);
////        this.setContentView(mRootView);
////        this.setFocusable(false);
////        this.setOutsideTouchable(false);
//////        ColorDrawable dw = new ColorDrawable(0xb0000000);
//////        this.setBackgroundDrawable(dw);
////        setBackgroundDrawable(new BitmapDrawable());
////        showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
////        mContentViewBg.setVisibility(View.VISIBLE);
////        mContentViewBg.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.bottom_menu_appear));
//
//    }


}
