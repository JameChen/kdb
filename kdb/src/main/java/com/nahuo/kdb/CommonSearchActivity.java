package com.nahuo.kdb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nahuo.kdb.adapter.BaseSearchAdapter;
import com.nahuo.kdb.adapter.SearchLogAdapter;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.eventbus.BusEvent;
import com.nahuo.kdb.eventbus.EventBusId;
import com.nahuo.library.controls.LightAlertDialog;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.FunctionHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

public class CommonSearchActivity extends BaseNoTitleActivity implements View.OnClickListener,AdapterView.OnItemClickListener {

    private static final String TAG = "CommonSearchActivity";

    public static final String PREF_KEY_GOODS_SEARCH_LOG = "pref_key_goods_search_log";//商品搜索记录
    private CommonSearchActivity mContent = this;
    private EditText mEtSearch;
    private LinearLayout mSearchResultNullContainer;
//    private PullToRefreshListViewEx mLvSearchResult;
    private TextView mTvSearchResultNulTip, mTvSearchRecommend;
    private ListView mLvHistory;
    private View mLlSearch, mLlSearchResult;


    private String[] mHotWords;
    private SearchLogAdapter mLogAdapter;
    private BaseSearchAdapter mGoodsAdapter;
    private int mCurKeywordPageIndex = 0;
    private LoadingDialog mLoadingDialog;
    private int mPageIndex = 1;
    private int supplierId;
    /**
     * 搜索类型
     */
    public final static String EXTRA_SEARCH_TYPE = "extra_search_type";

    private ScaleAnimation animShow = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

    private CommonListActivity.ListType mSearchType;

    public static void launch(Context context, CommonListActivity.ListType searchType){
        Intent intent = new Intent(context, CommonSearchActivity.class);
        intent.putExtra(EXTRA_SEARCH_TYPE, searchType);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        animShow.setDuration(400);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_search);
        mSearchType = (CommonListActivity.ListType) getIntent().getSerializableExtra(EXTRA_SEARCH_TYPE);

        initView();

        initData();
    }


    private void initData() {
        mLogAdapter = new SearchLogAdapter(this);
        mLvHistory.setAdapter(mLogAdapter);
        mEtSearch.setHint(R.string.input_good_name);
        getGoodsSearchLogs();
    }
    public void getGoodsSearchLogs() {
        String logs = SpManager.getString(mContent, PREF_KEY_GOODS_SEARCH_LOG, "");
        if (!TextUtils.isEmpty(logs)) {
            String[] logss = logs.split(";");
            if (logss != null && logss.length > 0) {
                List list = Arrays.asList(logss);
                List arrayList = new ArrayList(list);
                onSearchLogsLoaded(arrayList);
            }
        }
    }

    private void initView() {
        mLlSearch = findViewById(R.id.ll_search);
        mLlSearchResult = findViewById(R.id.ll_search_result);
        mEtSearch = (EditText) findViewById(R.id.et_search);
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = mEtSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)) {
                        mPageIndex = 1;
                        search(text, mPageIndex);
                    } else {
                        ViewHub.showShortToast(getApplicationContext(), "请输入关键字搜索");
                    }
                }
                return false;
            }
        });
        mEtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!mLlSearch.isShown()) {
                    mLlSearch.setVisibility(View.VISIBLE);
                    mLlSearchResult.setVisibility(View.GONE);
                }
                return false;
            }
        });

        mSearchResultNullContainer = (LinearLayout) findViewById(R.id.ll_search_null);
        mTvSearchResultNulTip = (TextView) findViewById(R.id.tv_result_null_tip);
        mTvSearchRecommend = (TextView) findViewById(R.id.tv_recommend);
        mTvSearchRecommend.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        mTvSearchRecommend.setOnClickListener(this);
        mLvHistory = (ListView) findViewById(R.id.lv_history);

        Button footer = new Button(this);
        footer.setText("清空搜索记录");
        footer.setBackgroundResource(R.drawable.listview_item_bg);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        footer.setLayoutParams(params);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = LightAlertDialog.Builder.create(CommonSearchActivity.this);
                builder.setTitle("提示").setMessage("您确定要清空搜索记录吗？")
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cleanGoodsSearchLogs();
                            }
                        }).show();
            }
        });
        mLvHistory.addFooterView(footer);
        mLvHistory.setOnItemClickListener(this);
    }

    public void cleanGoodsSearchLogs() {
        cleanSearchLogs(PREF_KEY_GOODS_SEARCH_LOG);
    }

    private void cleanSearchLogs(String prefKey) {
        SpManager.setString(mContent, prefKey, "");
        mContent.onCleanSearchLogs();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (back2SearchPage()) {
                    return;
                }
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (back2SearchPage()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean back2SearchPage() {
        if (!mLlSearch.isShown()) {
            mLlSearch.setVisibility(View.VISIBLE);
            mLlSearchResult.setVisibility(View.GONE);
            findViewById(R.id.tv_search_log).setVisibility(mLogAdapter.getCount() > 0 ? View.VISIBLE : View.GONE);
            mLvHistory.setVisibility(mLogAdapter.getCount() > 0 ? View.VISIBLE : View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getAdapter().getItem(position).toString();
        mPageIndex = 1;
        search(text, mPageIndex);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void search(String search, int index) {
        int id = 0;
        switch(mSearchType){
            case 入库:
                id= EventBusId.SEARCH_BACK_入库;
                break;
            case 库存:
                id = EventBusId.SEARCH_库存;
                break;
            case 销售:
                id = EventBusId.SEARCH_销售;
                break;
        }
        if (id>0) {
            EventBus.getDefault().post(BusEvent.getEvent(id, search));
            addWord2SearchLogs(PREF_KEY_GOODS_SEARCH_LOG, search);
            FunctionHelper.hideSoftInput(this);
        }
        finish();
    }

    private void addWord2SearchLogs(String prefKey, String word) {
        String logs = SpManager.getString(mContent, prefKey, "");
        if (!logs.contains(word + ";")) {
            logs = word + ";" + logs;
            SpManager.setString(mContent, prefKey, logs);
                onAddSearchWord(word);
        }
    }

    public void onAddSearchWord(String word) {
        if (mLogAdapter != null && !TextUtils.isEmpty(word)) {
            mLogAdapter.getLogs().add(0, word);
            mLogAdapter.notifyDataSetChanged();
        }

    }
    public void onSearchLogsLoaded(List<String> logs) {
        if (logs != null) {
            mLogAdapter.setData(logs);
            mLogAdapter.notifyDataSetChanged();
            findViewById(R.id.tv_search_log).setVisibility(mLogAdapter.getCount() > 0 ? View.VISIBLE : View.GONE);
            mLvHistory.setVisibility(mLogAdapter.getCount() > 0 ? View.VISIBLE : View.GONE);
        }
    }

    public void onCleanSearchLogs() {
        if (mLogAdapter != null) {
            mLogAdapter.getLogs().clear();
            mLogAdapter.notifyDataSetChanged();
            findViewById(R.id.tv_search_log).setVisibility(mLogAdapter.getCount() > 0 ? View.VISIBLE : View.GONE);
            mLvHistory.setVisibility(mLogAdapter.getCount() > 0 ? View.VISIBLE : View.GONE);
        }
    }

}
