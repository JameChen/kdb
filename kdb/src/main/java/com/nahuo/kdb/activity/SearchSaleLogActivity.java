package com.nahuo.kdb.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.library.helper.FunctionHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SearchSaleLogActivity extends AppCompatActivity implements View.OnClickListener {
    private String beginTime="",endTime="";
    private int type=-1 ;
    private TextView tvBeginTime,tvEndTime;
    private String name="";
    public static int Resutlt_Code=100;
    public static int REQUEST_CODE=1;
    public static String EXTRA_START_TIME="EXTRA_START_TIME";
    public static String EXTRA_END_TIME="EXTRA_END_TIME";
    public static String EXTRA_STATUS="EXTRA_STATUS";
    private Context mContext;
//    statuID   {
//        全部 = -1
//        待支付 = 1,
//                已完成 = 5,
//                已取消 = 10
//    }
    private RadioButton rb_all,rb_wait,rb_finish,rb_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sale_log);
        BWApplication.addActivity(this);
        mContext=this;
        TextView tvTitle = (TextView) findViewById(R.id.titlebar_tvTitle);
        Button btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
//        ImageView titlebar_icon_right = (ImageView) findViewById(R.id.titlebar_icon_right);
//        titlebar_icon_right.setImageResource(R.drawable.pn_message_left_white);
//        titlebar_icon_right.setOnClickListener(this);
        tvTitle.setText("筛选销售记录");
        btnLeft.setText(R.string.titlebar_btnBack);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setOnClickListener(this);
        tvBeginTime = (TextView)findViewById(R.id.trade_log_search_begintime);
        tvEndTime = (TextView)findViewById(R.id.trade_log_search_endtime);
        tvBeginTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        findViewById(R.id.tv_search).setOnClickListener(this);
        rb_all=(RadioButton) findViewById(R.id.rb_all);
        rb_all.setOnClickListener(this);
        rb_wait=(RadioButton) findViewById(R.id.rb_wait);
        rb_wait.setOnClickListener(this);
        rb_finish=(RadioButton) findViewById(R.id.rb_finish);
        rb_finish.setOnClickListener(this);
        rb_cancel=(RadioButton) findViewById(R.id.rb_cancel);
        rb_cancel.setOnClickListener(this);
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        Date begin = FunctionHelper.GetDateTime(-30);
        Date end = FunctionHelper.GetDateTime(0);
        beginTime = df1.format(begin);
        endTime = df1.format(end);
        tvBeginTime.setText("开始时间:"+beginTime);
        tvEndTime.setText("结束时间:"+endTime);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_all:
                type=-1;
                break;
            case R.id.rb_finish:
                type=5;
                break;
            case R.id.rb_cancel:
                type=10;
                break;
            case R.id.rb_wait:
                type=1;
                break;
            case R.id.tv_search:
                Intent intent = new Intent();
                intent.putExtra(this.EXTRA_START_TIME,beginTime);
                intent.putExtra(this.EXTRA_END_TIME,endTime);
                intent.putExtra(this.EXTRA_STATUS,type);
                setResult(Resutlt_Code,intent);
                finish();
                break;
            case R.id.titlebar_btnLeft:
                finish();
                break;
            case R.id.trade_log_search_begintime: {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String time1 = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth ;

                        if (checkSearchTime(time1,endTime))
                        {
                            beginTime = time1;
                            tvBeginTime.setText("开始时间:" + beginTime);
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            }
            case R.id.trade_log_search_endtime: {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String time2 = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                        if (checkSearchTime(beginTime,time2))
                        {
                            endTime = time2;
                            tvEndTime.setText("结束时间:" + endTime);
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            }
        }


    }
    private boolean checkSearchTime(String time1,String time2)
    {
        try {
            Date begin = FunctionHelper.StringToDate(time1,"yyyy-MM-dd");
            Date end = FunctionHelper.StringToDate(time2,"yyyy-MM-dd");
            long second = begin.getTime() - end.getTime();
            long day = Math.abs(second/1000/60/60/24);
            if (day>365*2)
            {
                ViewHub.showLongToast(mContext,"搜索时间区间不能超过两年");
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
