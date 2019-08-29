package com.nahuo.kdb.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.model.PackageDetailBean;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jame on 2018/11/27.
 */

public class PackageDetailAdapter extends BaseQuickAdapter<PackageDetailBean.ItemsBean, BaseViewHolder> {
    private Context context;
    private EditChangQty editChangQty;

    public void setEditChangQty(EditChangQty editChangQty) {
        this.editChangQty = editChangQty;
    }

    public PackageDetailAdapter(Context context) {
        super(R.layout.item_package_detail, null);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, PackageDetailBean.ItemsBean item) {
        if (item != null) {
            ImageView iv_icon = helper.getView(R.id.iv_icon);
            String path = ImageUrlExtends.getImageUrl(item.getCover(), 13);
            if (TextUtils.isEmpty(path)) {
                iv_icon.setImageResource(R.drawable.empty_photo);
            } else {
                Picasso.with(context).load(path).placeholder(R.drawable.empty_photo).into(iv_icon);
            }
            helper.setText(R.id.content, item.getName() + "\n款号" + item.getSku());
            RecyclerView recyclerView = helper.getView(R.id.recyclerView);
            PackageDetailSubAdapter adapter = new PackageDetailSubAdapter(context);
            adapter.setEditChangQty(new EditChangQty() {
                @Override
                public void OnEditQty() {
                    if (editChangQty != null)
                        editChangQty.OnEditQty();
                   // notifyDataSetChanged();
                }
            });
            if (!ListUtils.isEmpty(item.getProducts())) {
                adapter.setMyData(item.getProducts());
            } else {
                adapter.setMyData(null);
            }
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
    }

    private int selectedEditTextPosition = -1, currentId = -1;

    public interface EditChangQty {
        void OnEditQty();
    }

    ;

    public class PackageDetailSubAdapter extends BaseQuickAdapter<PackageDetailBean.ItemsBean.ProductsBean, BaseViewHolder> implements View.OnTouchListener, View.OnFocusChangeListener {
        private Context context;
        private List<PackageDetailBean.ItemsBean.ProductsBean> data;
        private EditChangQty editChangQty;

        public void setEditChangQty(EditChangQty editChangQty) {
            this.editChangQty = editChangQty;
        }

        public class SubBean {
            int Id = -1;
            int pos = -1;

            public int getId() {
                return Id;
            }

            public void setId(int id) {
                Id = id;
            }

            public int getPos() {
                return pos;
            }

            public void setPos(int pos) {
                this.pos = pos;
            }
        }

        public void setMyData(List<PackageDetailBean.ItemsBean.ProductsBean> data) {
            this.data = data;
            setNewData(data);
        }

        public PackageDetailSubAdapter(Context context) {
            super(R.layout.item_paclage_detail_sub, null);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder helper, PackageDetailBean.ItemsBean.ProductsBean item) {
            if (item != null) {
                int position = helper.getAdapterPosition();
                //Log.e("yutt",position+"");
                helper.setText(R.id.tv_color, item.getColor());
                helper.setText(R.id.tv_size, item.getSize());
                helper.setText(R.id.tv_ship_qty, item.getShipQty() + "");
                helper.setText(R.id.et_arrival_qty, item.getArrivalQty() + "");
                EditText editView = helper.getView(R.id.et_arrival_qty);
                editView.setOnTouchListener(this);
                editView.setOnFocusChangeListener(this);
                SubBean subBean = new SubBean();
                subBean.setId(item.getSourceID());
                subBean.setPos(position);
                editView.setTag(subBean);
                //editView.setText(item.getArrivalQty()+"");
                editView.setSelection(editView.length());
                if (selectedEditTextPosition != -1 && position == selectedEditTextPosition && currentId != -1 && currentId == subBean.getId()) { // 保证每个时刻只有一个EditText能获取到焦点
                    editView.requestFocus();
                } else {
                    editView.clearFocus();
                }
            }
        }


        private class MyTextWatcher implements TextWatcher {
            EditText editText;
            public MyTextWatcher(EditText editText){
                this.editText=editText;
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectedEditTextPosition != -1) {
                    String text = s.toString();
                    if (!ListUtils.isEmpty(data)) {
                        PackageDetailBean.ItemsBean.ProductsBean bean = data.get(selectedEditTextPosition);
                        if (TextUtils.isEmpty(text)) {
                            bean.setArrivalQty(0);
                        } else {
                            if (Integer.parseInt(text) > bean.getShipQty()) {
                                bean.setArrivalQty(bean.getShipQty());
                                this.editText.setText(String.valueOf(bean.getShipQty()));
                                this.editText.setSelection(String.valueOf(bean.getShipQty()).toString().length());
                                ViewHub.showShortToast(mContext, "实到数不能大于可发货数");
                            } else {
                                bean.setArrivalQty(Integer.parseInt(text));
                            }
                        }

                    }
                    if (editChangQty != null)
                        editChangQty.OnEditQty();
//                   ExtendPropertyListBean bean = pareList.get(selectedEditTextPosition);
//                   bean.setValue(text);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                EditText editText = (EditText) v;
                SubBean bean = (SubBean) editText.getTag();
                if (bean != null) {
                    selectedEditTextPosition = bean.getPos();
                    currentId = bean.getId();
                }
            }
            return false;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
             EditText editText = (EditText) v;
            if (hasFocus) {
                editText.addTextChangedListener(new MyTextWatcher(editText));
            } else {
                editText.removeTextChangedListener(new MyTextWatcher(editText));
            }
        }
    }
}
