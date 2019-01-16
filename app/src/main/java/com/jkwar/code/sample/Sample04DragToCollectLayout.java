package com.jkwar.code.sample;

import android.content.ClipData;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jkwar.code.R;

/**
 * @author paihaozhan
 * Sample04DragToCollectLayout
 */
public class Sample04DragToCollectLayout extends RelativeLayout {
  private ImageView googleIv;
  private ImageView baiduIv;
  private LinearLayout collectorLayout;
  private OnDragListener dragListener = new mOnDragListener();

  public Sample04DragToCollectLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    googleIv = findViewById(R.id.googleView);
    baiduIv = findViewById(R.id.baiduView);
    collectorLayout = findViewById(R.id.collectorLayout);
    googleIv.setOnLongClickListener(mOnLongClickListener);
    baiduIv.setOnLongClickListener(mOnLongClickListener);
    collectorLayout.setOnDragListener(dragListener);
  }

  private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
    @Override public boolean onLongClick(View v) {
      ClipData imageData = ClipData.newPlainText("name", v.getContentDescription());
      return ViewCompat.startDragAndDrop(v, imageData, new DragShadowBuilder(v), null,
          0);
    }
  };

  private class mOnDragListener implements View.OnDragListener {
    @Override public boolean onDrag(View v, DragEvent event) {
      switch (event.getAction()) {
        case DragEvent.ACTION_DROP:
          if (v instanceof LinearLayout) {
            TextView textView = new TextView(getContext());
            textView.setText(event.getClipData().getItemAt(0).getText());
            textView.setTextSize(16);
            ((LinearLayout) v).addView(textView);
          }
          break;
      }
      return true;
    }
  }
}
