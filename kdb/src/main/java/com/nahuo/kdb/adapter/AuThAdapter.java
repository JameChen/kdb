package com.nahuo.kdb.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.nahuo.kdb.R;
import com.nahuo.kdb.activity.AddMemberActivity;
import com.nahuo.kdb.dialog.AddEditMemDialog;
import com.nahuo.kdb.dialog.CommDialog;
import com.nahuo.kdb.model.MemberBean;
import com.nahuo.kdb.model.SelectBean;

import java.util.ArrayList;
import java.util.List;

import static com.nahuo.kdb.model.MemberBean.TYPE_LEVEL_0;
import static com.nahuo.kdb.model.MemberBean.TYPE_LEVEL_1;

/**
 * Created by jame on 2018/9/29.
 */

public class AuThAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    private Context context;
    private List<MultiItemEntity> data;
    DelLister delLister;

    public void setsList(ArrayList<SelectBean> sList) {
        this.sList = sList;
    }

    private ArrayList<SelectBean> sList;
    public void setDelLister(DelLister delLister) {
        this.delLister = delLister;
    }

    public void setMyData(List<MultiItemEntity> data) {
        this.data = data;
        super.setNewData(this.data);
    }

    public AuThAdapter(List<MultiItemEntity> data, Context context) {
        super(data);
        this.context = context;
        addItemType(TYPE_LEVEL_0, R.layout.item_auth_lv0_layout);
        addItemType(TYPE_LEVEL_1, R.layout.item_auth_lv0_layout);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final MultiItemEntity item) {
         switch (helper.getItemViewType()) {
            case TYPE_LEVEL_0:
                final MemberBean parent = (MemberBean) item;
                if (parent.isExpanded()) {
                    helper.setImageResource(R.id.icon_expand, R.drawable.oder_shang);
                } else {
                    helper.setImageResource(R.id.icon_expand, R.drawable.order_xia);
                }
                helper.setText(R.id.tv_member, parent.getGroupName());
                if (parent.isIsAddMember()) {
                    helper.setGone(R.id.tv_add, true);
                } else {
                    helper.setGone(R.id.tv_add, false);
                }
                if (parent.isIsEditGroup()) {
                    helper.setGone(R.id.tv_edit, true);
                } else {
                    helper.setGone(R.id.tv_edit, false);
                }
                if (parent.isIsDelGroup()) {
                    helper.setGone(R.id.tv_del, true);
                } else {
                    helper.setGone(R.id.tv_del, false);
                }
                helper.getView(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommDialog.getInstance((Activity) context).setcTitle("确定要删除该角色吗？")
                                .setLeftStr("取消").setRightStr("确定") .setMessage("删除成功后，该角色下的所有人员也会被同时删除？").setPositive(new CommDialog.PopDialogListener() {
                            @Override
                            public void onPopDialogButtonClick(int ok_cancel, CommDialog.DialogType type) {
                                if (ok_cancel == CommDialog.BUTTON_POSITIVIE) {
                                    if (delLister != null)
                                        delLister.del(parent);
                                }
                            }
                        }).showDialog();
                    }
                });
                helper.getView(R.id.tv_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddEditMemDialog.getInstance((Activity) context).setDialogType(AddEditMemDialog.DialogType.D_ADD)
                                .setPositive(new AddEditMemDialog.PopDialogListener() {
                                    @Override
                                    public void onPopDialogButtonClick(int ok_cancel, AddEditMemDialog.DialogType type, String userName, String password,String alias_name,int groupID) {
                                        if (ok_cancel == CommDialog.BUTTON_POSITIVIE) {
                                            if (delLister != null)
                                                delLister.addOredit(parent, type, userName, password,alias_name,groupID);
                                        }
                                    }
                                }).showDialog();
                    }
                });
                helper.getView(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, AddMemberActivity.class);
                        intent.putExtra(AddMemberActivity.EXTRA_TITLE, "编辑角色");
                        intent.putExtra(AddMemberActivity.EXTRA_GROUP, parent.getGroupID());
                        context.startActivity(intent);
                    }
                });
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = helper.getAdapterPosition();
                        if (parent.isExpanded()) {
                            collapse(pos);
                        } else {
                            expand(pos);
                        }
                    }
                });
                break;
            case TYPE_LEVEL_1:
                final MemberBean.MemberListBean sub = (MemberBean.MemberListBean) item;
                TextView tv_member = helper.getView(R.id.tv_member);
                tv_member.setText(sub.getMemberName());
                tv_member.setTextColor(ContextCompat.getColor(context, R.color.txt_gray));
                tv_member.setTextSize(14);
                helper.setGone(R.id.tv_add, false);
                if (sub.isIsEditMember()) {
                    helper.setGone(R.id.tv_edit, true);
                } else {
                    helper.setGone(R.id.tv_edit, false);
                }
                if (sub.isIsDelMember()) {
                    helper.setGone(R.id.tv_del, true);
                } else {
                    helper.setGone(R.id.tv_del, false);
                }
                helper.getView(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommDialog.getInstance((Activity) context).setcTitle("确定要删除该成员吗？")
                                .setLeftStr("取消").setRightStr("确定") .setMessage("确定要删除该成员吗？").setPositive(new CommDialog.PopDialogListener() {
                            @Override
                            public void onPopDialogButtonClick(int ok_cancel, CommDialog.DialogType type) {
                                if (ok_cancel == CommDialog.BUTTON_POSITIVIE) {
                                    if (delLister != null)
                                        delLister.del(sub);
                                }
                            }
                        }).showDialog();
                    }
                });
                helper.getView(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = sub.getMemberName();
                        String Alias=sub.getAlias();
                        String groupName=sub.getGroupName();
                        int groupId=sub.getGroupID();
                        AddEditMemDialog.getInstance((Activity) context).setDefautGroupId(groupId).setGroupName(groupName).setsList(sList).setAliasName(Alias).setUseName(name).setDialogType(AddEditMemDialog.DialogType.D_EDIT)
                                .setPositive(new AddEditMemDialog.PopDialogListener() {
                                    @Override
                                    public void onPopDialogButtonClick(int ok_cancel, AddEditMemDialog.DialogType type, String userName, String password,String alias_name,int groupID) {
                                        if (ok_cancel == CommDialog.BUTTON_POSITIVIE) {
                                            if (delLister != null)
                                                delLister.addOredit(sub, type, userName, password,alias_name,groupID);
                                        }
                                    }
                                }).showDialog();
                    }
                });
                break;
        }
    }

    public interface DelLister {
        void del(MultiItemEntity item);

        void addOredit(MultiItemEntity item, AddEditMemDialog.DialogType type, String userName, String password,String alias_name,int groupID);
    }
}
