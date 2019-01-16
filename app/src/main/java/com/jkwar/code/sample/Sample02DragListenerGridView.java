package com.jkwar.code.sample;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * @author paihaozhan
 * Sample02DragListenerGridView
 */
public class Sample02DragListenerGridView extends ViewGroup {
  //行数
  private static final int COLUMNS = 2;
  //列数
  private static final int ROWS = 3;
  public static final String TAG = "OnDragListener";
  //拖拽事件
  private OnDragListener mOnDragListener = new DragListener();
  //View 列表
  private List<View> orderedChildren = new ArrayList<>();
  //触摸的View
  private View draggedView;

  public Sample02DragListenerGridView(Context context) {
    super(context);
  }

  public Sample02DragListenerGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setChildrenDrawingOrderEnabled(true);
  }

  public Sample02DragListenerGridView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    for (int i = 0; i < getChildCount(); i++) {
      View childView = getChildAt(i);
      //初始化位置
      orderedChildren.add(childView);
      childView.setOnLongClickListener(new OnLongClickListener() {
        @Override public boolean onLongClick(View v) {
          draggedView = v;
          //跨进程数据，，本地数据，
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            v.startDragAndDrop(null, new DragShadowBuilder(v), v, DRAG_FLAG_OPAQUE);
          } else {
            v.startDrag(null, new DragShadowBuilder(v), v, 0);
          }
          return false;
        }
      });
      childView.setOnDragListener(mOnDragListener);
    }
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
      child.layout(0, 0, childWidth, childHeight);
      child.setTranslationX(childLeft);
      child.setTranslationY(childTop);
    }
  }

  /**
   * 排序算法
   */
  private void sort(View targetView) {
    //拖动的View的坐标
    int draggedIndex = -1;
    int targetIndex = -1;
    for (int i = 0; i < getChildCount(); i++) {
      View child = orderedChildren.get(i);
      if (targetView == child) {
        targetIndex = i;
      } else if (draggedView == child) {
        draggedIndex = i;
      }
    }
    if (targetIndex < draggedIndex) {
      orderedChildren.remove(draggedIndex);
      orderedChildren.add(targetIndex, draggedView);
    } else if (targetIndex > draggedIndex) {
      orderedChildren.remove(draggedIndex);
      orderedChildren.add(targetIndex, draggedView);
    }
    int childLeft;
    int childTop;
    int childWidth = getWidth() / COLUMNS;
    int childHeight = getHeight() / ROWS;
    for (int index = 0; index < getChildCount(); index++) {
      View child = orderedChildren.get(index);
      childLeft = index % 2 * childWidth;
      childTop = index / 2 * childHeight;
      child.animate()
          .translationX(childLeft)
          .translationY(childTop)
          .setDuration(150);
    }
  }

  private class DragListener implements OnDragListener {
    @Override public boolean onDrag(View v, DragEvent event) {
      switch (event.getAction()) {
        //只在应用程序调用startDrag()方法，并且获得了拖拽影子后，View对象的拖拽事件监听器才接收这种事件操作。
        case DragEvent.ACTION_DRAG_STARTED:
          Log.d(TAG, "onDrag: ACTION_DRAG_STARTED");
          if (event.getLocalState() == v) {
            v.setVisibility(View.INVISIBLE);
          }
          break;
        //当拖拽影子刚进入View对象的边框时，View对象的拖拽事件监听器会接收这种事件操作类型。
        case DragEvent.ACTION_DRAG_ENTERED:
          Log.d(TAG, "onDrag: ACTION_DRAG_ENTERED");
          if (event.getLocalState() != v) {
            sort(v);
          }
          break;
        //在View对象收到一个ACTION_DRAG_ENTERED事件之后，并且拖拽影子依然还在这个对象的边框之内时，这个View对象的拖拽事件监听器会接收这种事件操作类型
        case DragEvent.ACTION_DRAG_LOCATION:
          Log.d(TAG, "onDrag: ACTION_DRAG_LOCATION");
          break;
        //View对象收到一个ACTION_DRAG_ENTERED和至少一个ACTION_DRAG_LOCATION事件之后，这个对象的事件监听器会接受这种操作类型。
        case DragEvent.ACTION_DRAG_EXITED:
          Log.d(TAG, "onDrag: ACTION_DRAG_EXITED");
          break;
        //当用户在一个View对象之上释放了拖拽影子，这个对象的拖拽事件监听器就会收到这种操作类型。如果这个监听器在响应ACTION_DRAG_STARTED拖拽事件中返回了true，那么这种操作类型只会发送给一个View对象。如果用户在没有被注册监听器的View对象上释放了拖拽影子，或者用户没有在当前布局的任何部分释放操作影子，这个操作类型就不会被发送。如果View对象成功的处理放下事件，监听器要返回true，否则应该返回false。
        case DragEvent.ACTION_DROP:
          Log.d(TAG, "onDrag: ACTION_DROP");
          break;
        //当系统结束拖拽操作时，View对象拖拽监听器会接收这种事件操作类型。这种操作类型之前不一定是ACTION_DROP事件。如果系统发送了一个ACTION_DROP事件，那么接收ACTION_DRAG_ENDED操作类型不意味着放下操作成功了。监听器必须调用getResult()方法来获得响应ACTION_DROP事件中的返回值。如果ACTION_DROP事件没有被发送，那么getResult()会返回false。
        case DragEvent.ACTION_DRAG_ENDED:
          Log.d(TAG, "onDrag: ACTION_DRAG_ENDED");
          v.setVisibility(View.VISIBLE);
          break;
      }
      return true;
    }
  }
}
