package com.nahuo.kdb.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.nahuo.kdb.R;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.model.AuthBean;

import java.util.ArrayList;
import java.util.List;

import ch.ielse.view.SwitchView;

/**
 * Created by jame on 2018/10/8.
 */

public class AddThAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    private Context context;
    private List<MultiItemEntity> data;

    public void setMyData(List<MultiItemEntity> data) {
        this.data = data;
        super.setNewData(this.data);
    }

    public AddThAdapter(List<MultiItemEntity> data, Context context) {
        super(data);
        this.context = context;
        addItemType(AuthBean.TYPE_LEVEL_0, R.layout.add_item_lv1);
        addItemType(AuthBean.TYPE_LEVEL_1, R.layout.add_item_lv1);
    }

    public List<String> getIsSelect() {
        List<String> ids = new ArrayList<>();
        if (!ListUtils.isEmpty(data)) {
            for (MultiItemEntity itemEntity : data) {
                if (itemEntity instanceof AuthBean.AuthTypeListBean) {
                    AuthBean.AuthTypeListBean authBean = (AuthBean.AuthTypeListBean) itemEntity;
                    if (authBean != null) {
                        if (authBean.isIsSelect())
                            ids.add(authBean.getTypeID() + "");
                        if (!ListUtils.isEmpty(authBean.getAuthorityList())) {
                            for (AuthBean.AuthTypeListBean.AuthorityListBean xBean : authBean.getAuthorityList()) {
                                if (xBean.isIsAuthorised()) {
                                    ids.add(xBean.getAuthorityID() + "");
                                }
                            }
                        }
                    }
                }
            }
        }
        return ids;
    }

    private void setItemIsSelect(AuthBean.AuthTypeListBean.AuthorityListBean authorityListBean) {
        if (!ListUtils.isEmpty(data)) {
            for (MultiItemEntity itemEntity : data) {
                if (itemEntity instanceof AuthBean.AuthTypeListBean) {
                    AuthBean.AuthTypeListBean authBean = (AuthBean.AuthTypeListBean) itemEntity;
                    if (authBean.getTypeID() == authorityListBean.getParentId()) {
                        if (!ListUtils.isEmpty(authBean.getAuthorityList())) {
                            boolean flag = true;
                            for (AuthBean.AuthTypeListBean.AuthorityListBean xBean : authBean.getAuthorityList()) {
                                if (xBean.isIsAuthorised() == false) {
                                    flag = false;
                                    break;
                                }
                            }
                            authBean.setIsSelect(flag);
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    private void setAllIsSelect(AuthBean.AuthTypeListBean authBean) {
        if (!ListUtils.isEmpty(data)) {
            for (MultiItemEntity itemEntity : data) {
                if (itemEntity instanceof AuthBean.AuthTypeListBean) {
                    AuthBean.AuthTypeListBean bean = (AuthBean.AuthTypeListBean) itemEntity;
                    if (bean.getTypeID() == authBean.getTypeID()) {
                        if (!ListUtils.isEmpty(authBean.getAuthorityList())) {
                            for (AuthBean.AuthTypeListBean.AuthorityListBean xBean : authBean.getAuthorityList()) {
                                xBean.setIsAuthorised(authBean.isIsSelect());
                            }
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        View view = helper.getConvertView();

        switch (helper.getItemViewType()) {
            case AuthBean.TYPE_LEVEL_0:
                final AuthBean.AuthTypeListBean authBean = (AuthBean.AuthTypeListBean) item;
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_gray));
                if (authBean != null) {
                    helper.setText(R.id.name, authBean.getTypeName());
                    helper.setGone(R.id.switch_view, authBean.isShow());
                    SwitchView switchView = helper.getView(R.id.switch_view);
                    switchView.setOpened(authBean.isIsSelect());
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (authBean.isShow()) {
                                authBean.setIsSelect(!authBean.isIsSelect());
                                setAllIsSelect(authBean);
                            }
                        }
                    });
                    switchView.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                        @Override
                        public void toggleToOn(SwitchView view) {
                            authBean.setIsSelect(true);
                            setAllIsSelect(authBean);
                        }

                        @Override
                        public void toggleToOff(SwitchView view) {
                            authBean.setIsSelect(false);
                            setAllIsSelect(authBean);
                        }
                    });
                }
                break;
            case AuthBean.TYPE_LEVEL_1:
                final AuthBean.AuthTypeListBean.AuthorityListBean authorityListBean = (AuthBean.AuthTypeListBean.AuthorityListBean) item;
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                if (authorityListBean != null) {
                    helper.setText(R.id.name, authorityListBean.getAuthorityName());
                    helper.setText(R.id.name, authorityListBean.getAuthorityName());
                    helper.setGone(R.id.switch_view, true);
                    SwitchView switchView = helper.getView(R.id.switch_view);
                    switchView.setOpened(authorityListBean.isIsAuthorised());
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            authorityListBean.setIsAuthorised(!authorityListBean.isIsAuthorised());
                            setItemIsSelect(authorityListBean);
                        }
                    });
                    switchView.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                        @Override
                        public void toggleToOn(SwitchView view) {
                            authorityListBean.setIsAuthorised(true);
                            setItemIsSelect(authorityListBean);
                        }

                        @Override
                        public void toggleToOff(SwitchView view) {
                            authorityListBean.setIsAuthorised(false);
                            setItemIsSelect(authorityListBean);
                        }
                    });
                }
                break;
        }
    }
}
