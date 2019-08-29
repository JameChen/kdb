package com.nahuo.kdb.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.nahuo.kdb.R;

public class ReservationActivity extends FragmentActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    ReFragmentPagerAdapter reFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        initView();
    }

    private void initView() {
        findViewById(R.id.titlebar_btnLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tv_title)).setText("预约单");
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        reFragmentPagerAdapter = new ReFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(reFragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

    }

    public class ReFragmentPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[]{"所有的预约", "今天的预约", "明天的预约"};
        final int PAGE_COUNT = tabTitles.length;
        private Context context;

        public ReFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            return ReservationFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
