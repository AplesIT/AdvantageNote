

package com.production.advangenote.models.listeners;

import android.view.View;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;


public abstract class AbsListViewScrollDetector extends OnScrollListener {

  private int mLastScrollY;
  private int mPreviousFirstVisibleItem;
  private AbsListView mListView;
  private int mScrollThreshold;


  public abstract void onScrollUp();

  public abstract void onScrollDown();


  @Override
  public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
  }

  @Override
  public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
    if (isSameRow(dy)) {
      int newScrollY = getTopItemScrollY();
      boolean isSignificantDelta = Math.abs(mLastScrollY - newScrollY) > mScrollThreshold;
      if (isSignificantDelta) {
        if (mLastScrollY > newScrollY) {
          onScrollUp();
        } else {
          onScrollDown();
        }
      }
      mLastScrollY = newScrollY;
    } else {
      if (dy > mPreviousFirstVisibleItem) {
        onScrollUp();
      } else {
        onScrollDown();
      }

      mLastScrollY = getTopItemScrollY();
      mPreviousFirstVisibleItem = dy;
    }
  }


  public void setScrollThreshold(int scrollThreshold) {
    mScrollThreshold = scrollThreshold;
  }


  public void setListView(@NonNull AbsListView listView) {
    mListView = listView;
  }


  private boolean isSameRow(int firstVisibleItem) {
    return firstVisibleItem == mPreviousFirstVisibleItem;
  }


  private int getTopItemScrollY() {
    if (mListView == null || mListView.getChildAt(0) == null) {
      return 0;
    }
    View topChild = mListView.getChildAt(0);
    return topChild.getTop();
  }
}
