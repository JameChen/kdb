package com.nahuo.kdb;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.baidu.mobstat.StatService;
import com.nahuo.kdb.R;
import com.nahuo.kdb.adapter.GuideViewPagerAdapter;

public class GuideActivity extends Activity implements OnClickListener {

	private GuideActivity vThis = this;

	private ViewPager viewPager;
	private List<View> pageViews;
	private List<View> pageFooters;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		initViewPager();
		InitPageFooterView();
	}

	/**
	 * 加载分页视图
	 * */
	private void initViewPager() {
		View guideView1 = LayoutInflater.from(vThis).inflate(
				R.layout.layout_guide_item_1, null);
		View guideView2 = LayoutInflater.from(vThis).inflate(
				R.layout.layout_guide_item_2, null);
		View guideViewEnd = LayoutInflater.from(vThis).inflate(
				R.layout.layout_guide_item_end, null);
		Button btnForward = (Button) guideViewEnd
				.findViewById(R.id.guide_btnForward);
		btnForward.setOnClickListener(this);
		pageViews = new ArrayList<View>();
		pageViews.add(guideView1);
		pageViews.add(guideView2);
		pageViews.add(guideViewEnd);

		viewPager = (ViewPager) findViewById(R.id.guide_viewPager);
		GuideViewPagerAdapter adapter = new GuideViewPagerAdapter(pageViews);
		viewPager.setAdapter(adapter);

		viewPager.setOnPageChangeListener(onPageChangeListener);
	}

	/**
	 * 加载分页页脚视图
	 */
	private void InitPageFooterView() {
		pageFooters = new ArrayList<View>();
		pageFooters.add(this.findViewById(R.id.guide_viewPager_footer1));
		pageFooters.add(this.findViewById(R.id.guide_viewPager_footer2));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.guide_btnForward:
//			Intent intent = new Intent(this, WXEntryActivity.class);
//			intent.putExtra(WXEntryActivity.EXTRA_TYPE, WXEntryActivity.Type.LOGIN);
//			startActivity(intent);
//			finish();
			break;
		}
	}

	/**
	 * 切换页时，更换页脚小圈圈
	 * */
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < pageFooters.size(); i++) {
				if (i == arg0) {
					((View) pageFooters.get(i))
							.setBackgroundResource(R.drawable.viewpager_footer_focused);
				} else {
					((View) pageFooters.get(i))
							.setBackgroundResource(R.drawable.viewpager_footer_unfocused);
				}
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);
	}
}
