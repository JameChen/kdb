package com.nahuo.kdb.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.adapter.AuThAdapter;
import com.nahuo.kdb.api.KdbAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.dialog.AddEditMemDialog;
import com.nahuo.kdb.model.MBean;
import com.nahuo.kdb.model.MemberBean;
import com.nahuo.kdb.model.SelectBean;
import com.nahuo.library.controls.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import static com.nahuo.kdb.activity.AuthorityActivity.Step.ADDMEM;
import static com.nahuo.kdb.activity.AuthorityActivity.Step.DELTEMEM;
import static com.nahuo.kdb.activity.AuthorityActivity.Step.EDITMEM;
import static com.nahuo.kdb.activity.AuthorityActivity.Step.GETLIST;

public class AuthorityActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private LoadingDialog mloadingDialog;
    private AuthorityActivity vThis;
    private TextView tvTitleCenter, tvTRight;
    private AuThAdapter adapter;
    private static final String ERROR_PREFIX = "error:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority);
        BWApplication.getInstance().addActivity(this);
        vThis = this;
        mloadingDialog = new LoadingDialog(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvTitleCenter = (TextView) findViewById(R.id.tvTitleCenter);
        tvTRight = (TextView) findViewById(R.id.tvTRight);
        tvTRight.setText("添加角色");
        tvTRight.setOnClickListener(this);
        findViewById(R.id.tvTLeft).setOnClickListener(this);
        tvTitleCenter.setText("权限设置");
        adapter = new AuThAdapter(null, vThis);
        adapter.setDelLister(new AuThAdapter.DelLister() {
            @Override
            public void del(MultiItemEntity item) {
                new Task(DELTEMEM, item).execute();
            }

            @Override
            public void addOredit(MultiItemEntity item, AddEditMemDialog.DialogType type, String userName, String password
                    , String alias_name, int groupID) {
                if (type == AddEditMemDialog.DialogType.D_ADD) {
                    new Task(ADDMEM, item, userName, password,alias_name,groupID).execute();
                } else if (type == AddEditMemDialog.DialogType.D_EDIT) {
                    new Task(EDITMEM, item, userName, password,alias_name,groupID).execute();
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        new Task(GETLIST).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddMemberActivity.REQUESTCODE) {
            if (resultCode == AddMemberActivity.RESULTCODE) {
                onRefresh();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTLeft:
                finish();
                break;
            case R.id.tvTRight:
                Intent intent = new Intent(vThis, AddMemberActivity.class);
                intent.putExtra(AddMemberActivity.EXTRA_TITLE, "添加角色");
                vThis.startActivityForResult(intent, AddMemberActivity.REQUESTCODE);
                break;
        }
    }

    private void onRefresh() {
        new Task(GETLIST).execute();
    }

    public enum Step {
        GETLIST, DELTEMEM, EDITMEM, ADDMEM
    }

    private void showDialog(String messge) {
        if (mloadingDialog != null) {
            mloadingDialog.start(messge);
        }
    }

    private void stopDialog() {
        if (mloadingDialog != null) {
            mloadingDialog.stop();
        }
    }

    public class Task extends AsyncTask<Void, Void, Object> {
        Step step;
        MultiItemEntity item;
        String userName = "", password = "";
        String alias_name = "";
        int groupID;

        Task(Step step) {
            this.step = step;
        }

        Task(Step step, MultiItemEntity item) {
            this.step = step;
            this.item = item;
        }

        Task(Step step, MultiItemEntity item, String userName, String password, String alias_name, int groupID) {
            this.step = step;
            this.item = item;
            this.userName = userName;
            this.password = password;
            this.alias_name = alias_name;
            this.groupID = groupID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            switch (step) {
                case GETLIST:
                    showDialog("加载数据.....");
                    break;
                case DELTEMEM:
                    showDialog("正在删除.....");
                    break;
                case ADDMEM:
                    showDialog("正在添加.....");
                    break;
                case EDITMEM:
                    showDialog("正在编辑.....");
                    break;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            stopDialog();
            if (result == null || result.equals(""))
                return;
            if (result instanceof String && ((String) result).startsWith(ERROR_PREFIX)) {
                String msg = ((String) result).replace(ERROR_PREFIX, "");
                ViewHub.showLongToast(vThis, msg);
                return;
            }
            switch (step) {
                case ADDMEM:
                    ViewHub.showLongToast(vThis, "添加成功！");
                    onRefresh();
                    break;
                case EDITMEM:
                    ViewHub.showLongToast(vThis, "编辑成功！");
                    onRefresh();
                    break;
                case DELTEMEM:
                    ViewHub.showLongToast(vThis, "删除成功！");
                    onRefresh();
                    break;
                case GETLIST:
                    if (tvTRight != null && SpManager.getIsadd_Group(vThis))
                        tvTRight.setVisibility(View.VISIBLE);
                    if (result instanceof MBean) {
                        MBean xbean= (MBean) result;
                        List<MemberBean> data = xbean.getList();
                        ArrayList<SelectBean> sList = new ArrayList<>();
                        if (!ListUtils.isEmpty(data)) {
                            for (MemberBean bean : data) {
                                if (!bean.getGroupName().equals("管理员")) {
                                    SelectBean selectBean = new SelectBean();
                                    selectBean.setID(bean.getGroupID());
                                    selectBean.setName(bean.getGroupName());
                                    sList.add(selectBean);
                                }
                                if (!ListUtils.isEmpty(bean.getMemberList())){
                                    for (MemberBean.MemberListBean sub:bean.getMemberList()) {
                                        sub.setGroupID(bean.getGroupID());
                                        sub.setGroupName(bean.getGroupName());
                                    }
                                }
                            }
                        }
                        if (adapter != null) {
                            adapter.setsList(sList);
                            ArrayList<MultiItemEntity> res = new ArrayList<>();
                            if (!ListUtils.isEmpty(data)) {
                                res.addAll(data);
                            }
                            adapter.setMyData(res);
                            adapter.expandAll();
                            adapter.notifyDataSetChanged();
                        }
                    }
                    break;
            }
        }

        @Override
        protected Object doInBackground(Void... params) {


            switch (step) {
                case GETLIST:
                    try {
                        MBean bean = KdbAPI.getAuthManageList(vThis);
                        return bean;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ERROR_PREFIX + e.getMessage();
                    }
                case DELTEMEM:
                    if (item instanceof MemberBean) {
                        MemberBean memberBean = (MemberBean) item;
                        try {
                            KdbAPI.deletegroup(vThis, memberBean.getGroupID());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return ERROR_PREFIX + e.getMessage();
                        }
                    } else if (item instanceof MemberBean.MemberListBean) {
                        MemberBean.MemberListBean memberListBean = (MemberBean.MemberListBean) item;
                        try {
                            KdbAPI.deletemember(vThis, memberListBean.getMemberID());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return ERROR_PREFIX + e.getMessage();
                        }
                    }
                    break;
                case ADDMEM:
                    if (item instanceof MemberBean) {
                        MemberBean memberBean = (MemberBean) item;
                        try {
                            KdbAPI.addmember(vThis, userName, password, memberBean.getGroupID(),alias_name);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return ERROR_PREFIX + e.getMessage();
                        }
                    }
                    break;
                case EDITMEM:
                    if (item instanceof MemberBean.MemberListBean) {
                        MemberBean.MemberListBean memberListBean = (MemberBean.MemberListBean) item;
                        try {
                            KdbAPI.editmember(vThis, password, memberListBean.getMemberUserID(),groupID,alias_name);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return ERROR_PREFIX + e.getMessage();
                        }
                    }
                    break;
            }
            return "ok";
        }
    }
}
