package com.jkwar.code.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.OverScroller;

/**
 * @author paihaozhan
 * TwoPager
 */
public class Sample01TwoPager extends ViewGroup {
  private float downX;
  //滑动距离
  private float downScrollX;
  //最大滑动速度，最小滑动速度
  private float minVelocity, maxVelocity;
  //滑动
  private OverScroller mScroller;
  //是否滑动
  private boolean scrolling;
  //移动速度
  private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
  private ViewConfiguration viewConfiguration;

  public Sample01TwoPager(Context context, AttributeSet attrs) {
    super(context, attrs);
    mScroller = new OverScroller(context);
    viewConfiguration = ViewConfiguration.get(context);
    minVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    maxVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
  }

  //测量
  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    //测量布局（宽高与父布局一致）
    measureChildren(widthMeasureSpec, heightMeasureSpec);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  //布局
  @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int childLeft = 0;
    int childTop = 0;
    int childRight = getWidth();
    int childBottom = getHeight();
    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      child.layout(childLeft, childTop, childRight, childBottom);
      childLeft += getWidth();
      childRight += getWidth();
    }
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
    boolean result = false;
    if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
      mVelocityTracker.clear();
    }
    mVelocityTracker.addMovement(ev);
    switch (ev.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
        scrolling = false;
        downX = ev.getX();
        downScrollX = getScrollX();
        break;
      case MotionEvent.ACTION_MOVE:
        float offsetX = downX - ev.getX();
        if (!scrolling) {
          if (Math.abs(offsetX) > viewConfiguration.getScaledDoubleTapSlop()) {
            scrolling = true;
            result = true;
            getParent().requestDisallowInterceptTouchEvent(true);
          }
        }
        break;
    }
    return result;
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
      mVelocityTracker.clear();
    }
    mVelocityTracker.addMovement(event);
    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
        downX = event.getX();
        downScrollX = getScrollX();
        break;
      case MotionEvent.ACTION_MOVE:
        float offsetX = downX - event.getX() + downScrollX;
        //如果移动距离大于父view的宽度，就位移了一页
        if (offsetX > getWidth()) {
          offsetX = getWidth();
        } else if (offsetX < 0) {
          offsetX = 0;
        }
        scrollTo((int) offsetX, 0);
        break;
      case MotionEvent.ACTION_UP:
        //计算实时速度
        mVelocityTracker.computeCurrentVelocity(1000, maxVelocity);
        //x轴的速度
        float vx = mVelocityTracker.getXVelocity();
        //滑动的距离
        int scrollX = getScrollX();
        int scrollDistance;
        //如果滑动速度大于最小速度，并且滑动速度大于0就说明说明向左移动，否则就是向右移动
        if (Math.abs(vx) > minVelocity) {
          scrollDistance = vx > 0 ? -scrollX : (getWidth() - scrollX);
        } else {
          //滑动超过子View一半的距离,就滑动到下一页（ 要移动的距离 = 子view的宽度 - 当前滚动到的位置），
          //否则就回滚（ 回滚的距离 = -当前滚动到的位置 ）
          scrollDistance = (scrollX > getWidth() / 2) ? (getWidth() - scrollX) : -scrollX;
        }
        mScroller.startScroll(getScrollX(), 0, scrollDistance, 0);
        postInvalidateOnAnimation();
        break;
    }
    return true;
  }

  @Override public void computeScroll() {
    if (mScroller.computeScrollOffset()) {
      scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
      postInvalidateOnAnimation();
    }
  }
}
