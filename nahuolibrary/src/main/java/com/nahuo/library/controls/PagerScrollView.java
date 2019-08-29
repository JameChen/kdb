package com.nahuo.library.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ScrollView;
/**
 * @description 与ViewPager一起使用的ScrollView,防止ScrollView内嵌ViewPager导致ViewPager滑动困难
 * @created 2014-10-17 上午11:06:02
 * @author ZZB
 */
public class PagerScrollView extends ScrollView {

    private GestureDetector mGestureDetector;

    public PagerScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PagerScrollView(Context context) {
        super(context);
        init();
    }

    public PagerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(),
                new YScrollDetector());
        setFadingEdgeLength(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev)
                && mGestureDetector.onTouchEvent(ev);
    }

    private class YScrollDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            
            if (Math.abs(distanceY) >= Math.abs(distanceX)) {
                return true;
            }
            return false;
        }
    }
}

