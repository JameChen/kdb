package com.nahuo.kdb.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.adapter.AddThAdapter;
import com.nahuo.kdb.api.KdbAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.model.AuthBean;
import com.nahuo.library.controls.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

public class AddMemberActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private LoadingDialog mloadingDialog;
    private AddMemberActivity vThis;
    private TextView tvTitleCenter, tvTRight;
    private static final String ERROR_PREFIX = "error:";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_GROUP = "EXTRA_GROUP";
    private String title;
    private int group_id = 0;
    private AddThAdapter addThAdapter;
    private View head = null;
    private EditText et_name;
    public static int  REQUESTCODE=100;
    public static int  RESULTCODE=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        BWApplication.addActivity(this);
        vThis = this;
        mloadingDialog = new LoadingDialog(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        addThAdapter = new AddThAdapter(null, vThis);
        recyclerView.setLayoutManager(new LinearLayoutManager(vThis));
        recyclerView.setAdapter(addThAdapter);
        tvTitleCenter = (TextView) findViewById(R.id.tvTitleCenter);
        tvTRight = (TextView) findViewById(R.id.tvTRight);
        head = LayoutInflater.from(vThis).inflate(R.layout.add_item_head, null);
        et_name = (EditText) head.findViewById(R.id.et_name);
        Intent intent = getIntent();
        if (intent != null) {
            title = getIntent().getStringExtra(this.EXTRA_TITLE);
            group_id = getIntent().getIntExtra(this.EXTRA_GROUP, 0);
        }
        tvTitleCenter.setText(title);
        tvTRight.setText("确定");
        tvTRight.setOnClickListener(this);
        findViewById(R.id.tvTLeft).setOnClickListener(this);
        new Task(Step.GETGROUP).execute();
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

    public enum Step {
        GETGROUP, ADDGROUP, EDITGROUP
    }

    public class Task extends AsyncTask<Void, Void, Object> {
        Step step;

        Task(Step step) {
            this.step = step;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            switch (step) {
                case GETGROUP:
                    showDialog("加载数据.....");
                    break;
                case ADDGROUP:
                    showDialog("添加中.....");
                    break;
                case EDITGROUP:
                    showDialog("编辑中.....");
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
                case GETGROUP:
                    if (tvTRight != null)
                        tvTRight.setVisibility(View.VISIBLE);
                    addThAdapter.removeAllHeaderView();
                    if (head != null)
                        addThAdapter.addHeaderView(head);
                    if (result instanceof AuthBean) {
                        AuthBean authBean = (AuthBean) result;
                        if (authBean != null) {
                            if (group_id > 0) {
                                if (et_name != null)
                                    et_name.setText(authBean.getGroupName());
                            }
                            List<MultiItemEntity> data = new ArrayList<>();
                            if (!ListUtils.isEmpty(authBean.getAuthTypeList())) {
                                for (AuthBean.AuthTypeListBean authTypeListBean : authBean.getAuthTypeList()) {
                                    if (!ListUtils.isEmpty(authTypeListBean.getAuthorityList())) {
                                        for (AuthBean.AuthTypeListBean.AuthorityListBean authorityListBean : authTypeListBean.getAuthorityList()) {
                                            authorityListBean.setParentId(authTypeListBean.getTypeID());
                                        }
                                        authTypeListBean.setSubItems(authTypeListBean.getAuthorityList());
                                    }
                                }
                                data.addAll(authBean.getAuthTypeList());
                                addThAdapter.setMyData(data);
                                addThAdapter.expandAll();
                                addThAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    break;
                case ADDGROUP:
                    ViewHub.showLongToast(vThis, "添加成功");
                    vThis.setResult(RESULTCODE);
                    vThis.finish();
                    break;
                case EDITGROUP:
                    ViewHub.showLongToast(vThis, "编辑成功");
                    vThis.setResult(RESULTCODE);
                    vThis.finish();
                    break;
            }
        }

        @Override
        protected Object doInBackground(Void... params) {


            switch (step) {
                case GETGROUP:
                    try {
                        return KdbAPI.getGroup(vThis, group_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ERROR_PREFIX + e.getMessage();
                    }
                case ADDGROUP:
                    try {
                        KdbAPI.addgroup(vThis, nameStr,authIDs);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ERROR_PREFIX + e.getMessage();
                    }
                    break;
                case EDITGROUP:
                    try {
                        KdbAPI.editgroup(vThis, nameStr,authIDs , group_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ERROR_PREFIX + e.getMessage();
                    }
                    break;
            }
            return "ok";
        }
    }

    private String nameStr = "", authIDs = "";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTLeft:
                finish();
                break;
            case R.id.tvTRight:
                nameStr = et_name.getText().toString().trim();
                if (TextUtils.isEmpty(nameStr)) {
                    ViewHub.showLongToast(vThis, "请填写角色名");
                    return;
                }
                if (addThAdapter != null) {
                    List<String> ids = addThAdapter.getIsSelect();
                    if (ListUtils.isEmpty(ids)) {
                        ViewHub.showLongToast(vThis, "请选择权限");
                        return;
                    } else {
                        authIDs = ids.toString().substring(1, ids.toString().length() - 1);
                    }
                } else {
                    ViewHub.showLongToast(vThis, "请选择权限");
                    return;
                }
                if (group_id > 0) {
                    new Task(Step.EDITGROUP).execute();
                } else {
                    new Task(Step.ADDGROUP).execute();
                }

                break;
        }
    }
}
