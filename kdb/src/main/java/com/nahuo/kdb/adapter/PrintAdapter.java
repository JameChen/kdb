package com.nahuo.kdb.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
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
import com.nahuo.kdb.activity.PrintActivity;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.dialog.PrintDialog;
import com.nahuo.kdb.model.PrintBean;

/**
 * Created by jame on 2017/10/30.
 */

public class PrintAdapter extends MyBaseAdapter<PrintBean> implements PrintDialog.PopDialogListener {
    Activity activity;
    PrintListener listener;

    public void setListener(PrintListener listener) {
        this.listener = listener;
    }

    public PrintAdapter(Activity context) {

        super(context);
        activity=context;
    }
    public void reMoveItem(PrintBean printBean){
        if (!ListUtils.isEmpty(mdata)){
            for (int i=0;i<mdata.size();i++) {
                if (printBean.getTime().equals(mdata.get(i).getTime())){
                    mdata.remove(i);
                }
            }
            notifyDataSetChanged();
            if (listener!=null)
                listener.onFinish(mdata.size());
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_print_list, parent, false);
            holder = new ViewHolder();
            holder.item_text_color_size = (TextView) convertView.findViewById(R.id.item_text_color_size);
            holder.item_text_code = (TextView) convertView.findViewById(R.id.item_text_code);
            holder.item_text_1 = (TextView) convertView.findViewById(R.id.item_text_1);
            holder.item_text_2 = (TextView) convertView.findViewById(R.id.item_text_2);
            holder.item_text_3 = (TextView) convertView.findViewById(R.id.item_text_3);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tv_code = (TextView) convertView.findViewById(R.id.tv_code);
            holder.item_cover = (ImageView) convertView.findViewById(R.id.item_cover);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PrintBean bean = mdata.get(position);
        holder.item_text_color_size.setText( bean.getSize());
        //holder.item_text_code.setText(bean.getItemcode()+"\n"+bean.getDMCode());
        holder.item_text_1.setText(bean.getCode() );
        holder.item_text_2.setText(bean.getCategroy() + "");
        holder.item_text_3.setText(bean.getColor() + "");
        holder.tv_price.setText(bean.getRetailPrice()+"");
        holder.tv_code.setText(bean.getSku()+"");
        // 取消前面的下载任务，要在新下载任务启动之前先取消
        if (holder.task != null) {
            holder.task.cancel(true);
        }
        if (!TextUtils.isEmpty(bean.getScan())) {
            holder.item_cover.setImageBitmap(encodeAsBitmap(bean.getScan()));
            LoadImage task = new LoadImage(holder.item_cover);
            holder.task = task;
            task.execute(bean.getScan());
        }else {
            holder.item_cover.setImageResource(R.drawable.empty_photo);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintBean bean = mdata.get(position);
                PrintDialog.getInstance(activity).setTitle("是否打印该款式").setAction(PrintActivity.Style.ITEM_PRINT).setList(bean).setPositive(PrintAdapter.this).showDialog();
            }
        });
        return convertView;
    }
    /**
     * 通过异步实现图片的下载
     *
     */
    public class LoadImage extends AsyncTask<String, Integer,Bitmap> {

        ImageView iv;

    public LoadImage(ImageView iv) {// 接收传递过来的ImageView控件
            this.iv = iv;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                // 调用HttpUtils的getBitmapByHttp()方法联网下载图片
                Bitmap bitmap =encodeAsBitmap(params[0]);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // 下载完成后得到Bitmap,给ImageView设置值，判断当前任务是否被取消，如果当前任务被取消，就不再设置图片，
            //避免异步加载时图片连闪和错位问题
            if (result != null && !isCancelled()) {
                if (iv!=null)
                iv.setImageBitmap(result);
            }
        }
    }
    Bitmap encodeAsBitmap(String str) {
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 100, 100);
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

    @Override
    public void onPopDialogButtonClick(PrintActivity.Style action, PrintBean bean) {
        if (action==PrintActivity.Style.ITEM_PRINT){
            if (listener!=null)
                listener.onImagePrintClick(bean);
        }
    }
    public interface PrintListener {
        void onImagePrintClick(PrintBean bean);
        void onFinish(int count);
    }
    private static class ViewHolder {
        private LoadImage task;
        private TextView item_text_color_size, item_text_code, item_text_1, item_text_2, item_text_3
                ,tv_price,tv_code;
        private ImageView item_cover;
    }
}
