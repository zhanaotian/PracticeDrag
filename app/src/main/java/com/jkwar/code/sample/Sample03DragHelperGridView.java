package com.jkwar.code.sample;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


/**
 * @author paihaozhan
 * Sample03DragHelperGridView
 */
public class Sample03DragHelperGridView extends ViewGroup {
  //行数
  private static final int COLUMNS = 2;
  //列数
  private static final int ROWS = 3;

  private ViewDragHelper mViewDragHelper;

  public Sample03DragHelperGridView(Context context) {
    super(context);
  }

  public Sample03DragHelperGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mViewDragHelper = ViewDragHelper.create(this, new DragCallback());
  }


  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
    int heightSpec = MeasureSpec.getSize(heightMeasureSpec);
    int childWidth = widthSpec / COLUMNS;
    int childHeight = heightSpec / ROWS;
    measureChildren(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));
    setMeasuredDimension(widthSpec, heightSpec);
  }

  @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int count = getChildCount();
    int childLeft;
    int childTop;
    int childWidth = getWidth() / COLUMNS;
    int childHeight = getHeight() / ROWS;
    for (int i = 0; i < count; i++) {
      View child = getChildAt(i);
      childLeft = i % 2 * childWidth;
      childTop = i / 2 * childHeight;
      child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
    }
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
    float capturedLeft;
    float capturedTop;

    @Override public boolean tryCaptureView(@NonNull View view, int i) {
      return true;
    }



    @Override public void onViewDragStateChanged(int state) {
      //抬下
      if (state == ViewDragHelper.STATE_IDLE) {
        View capturedView = mViewDragHelper.getCapturedView();
        if (capturedView != null) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            capturedView.setElevation(capturedView.getElevation() - 1);
          }
        }
      }
    }

    @Override public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
      //抬高
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        capturedChild.setElevation(capturedChild.getElevation() + 1);
      }
      capturedLeft = capturedChild.getLeft();
      capturedTop = capturedChild.getTop();
    }

    @Override public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
      return left;
    }

    @Override public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
      return top;
    }

    @Override public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
      mViewDragHelper.settleCapturedViewAt((int) capturedLeft, (int) capturedTop);
      postInvalidateOnAnimation();
    }
  }
}
