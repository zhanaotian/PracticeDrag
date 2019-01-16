package com.jkwar.code.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jkwar.code.R;

/**
 * @author paihaozhan
 * Sample05DragUpCollectLayout
 */
public class Sample06DragUpCollectLayout extends RelativeLayout {
  private TextView mTextView;
  private ViewDragHelper mViewDragHelper;
  private ViewConfiguration mViewConfiguration;

  public Sample06DragUpCollectLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    mViewDragHelper = ViewDragHelper.create(this, new DragCallback());
    mViewConfiguration = ViewConfiguration.get(context);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    mTextView = findViewById(R.id.textView);
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
    return mViewDragHelper.shouldInterceptTouchEvent(ev);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    mViewDragHelper.processTouchEvent(event);
    return true;
  }

  @Override
  public void computeScroll() {
    if (mViewDragHelper.continueSettling(true)) {
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }

  private class DragCallback extends ViewDragHelper.Callback {
    @Override public boolean tryCaptureView(@NonNull View child, int i) {
      return child == mTextView;
    }

    @Override public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
      return top;
    }

    @Override public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
      if (Math.abs(yvel) > mViewConfiguration.getScaledMinimumFlingVelocity()) {
        if (yvel > 0) {
          //往下
          mViewDragHelper.settleCapturedViewAt(0,
              getHeight() - releasedChild.getHeight());
        } else {
          //往上
          mViewDragHelper.settleCapturedViewAt(0,
              0);
        }
      } else {
        //如果子view的上边距大于子view的高，那就往下
        if (releasedChild.getTop() > (getHeight() - releasedChild.getBottom())) {
          mViewDragHelper.settleCapturedViewAt(0,
              getHeight() - releasedChild.getHeight());
        } else {
          //往上
          mViewDragHelper.settleCapturedViewAt(0,
              0);
        }
      }
      postInvalidateOnAnimation();
    }
  }
}
