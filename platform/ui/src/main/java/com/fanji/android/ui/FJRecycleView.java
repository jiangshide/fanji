package com.fanji.android.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.fanji.android.ui.recycleview.Mode;
import com.fanji.android.ui.recycleview.ViewPagerLayoutManager;
import com.fanji.android.ui.recycleview.listener.OnViewPagerListener;
import com.fanji.android.ui.adapter.KAdapter;

import java.util.Collections;

/**
 * created by jiangshide on 2019-09-22.
 * email:18311271399@163.com
 */
public class FJRecycleView extends RecyclerView {

    private int mGridSize = 3;
    public ViewPagerLayoutManager viewPagerLayoutManager = null;

    public FJRecycleView(@NonNull Context context) {
        super(context);
    }

    public FJRecycleView(@NonNull Context context,
                         @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(OVER_SCROLL_NEVER);
        setMode(Mode.LINE);
    }

    public FJRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setMode(Mode.LINE);
    }

    public FJRecycleView setGridSize(int gridSize) {
        this.mGridSize = gridSize;
        return this;
    }

    public FJRecycleView setMode(@Mode int mode) {
        return setMode(mode, false, null);
    }

    public FJRecycleView setMode(@Mode int mode, boolean isHorizontal, OnViewPagerListener listener) {
        setLayoutManager(mode == Mode.VIEWPAGER ? viewPagerLayoutManager = new ViewPagerLayoutManager(getContext(),
                isHorizontal ? OrientationHelper.HORIZONTAL
                        : OrientationHelper.VERTICAL).setOnViewPagerListener(listener)
                : mode == Mode.GRID
                ? new GridLayoutManager(getContext(), mGridSize)
                : new LinearLayoutManager(getContext()));
        setItemAnimator(new DefaultItemAnimator());
        return this;
    }

    public FJRecycleView setDrag(KAdapter<?> adapter) {
        return setDrag(adapter, true);
    }

    public FJRecycleView setDrag(KAdapter<?> adapter, boolean isDrag) {
        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                        @NonNull ViewHolder viewHolder) {
                int dragFlags;
                int swipeFlags;
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    dragFlags = ItemTouchHelper.UP
                            | ItemTouchHelper.DOWN
                            | ItemTouchHelper.LEFT
                            | ItemTouchHelper.RIGHT;
                    swipeFlags = 0;
                } else {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    swipeFlags = 0;
                }
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder,
                                  @NonNull ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (adapter != null && adapter.datas() != null) {
                    if (fromPosition < toPosition) {
                        for (int i = fromPosition; i < fromPosition; i++) {
                            Collections.swap(adapter.datas(), i, i + 1);
                        }
                    } else {
                        for (int i = fromPosition; i > toPosition; i--) {
                            Collections.swap(adapter.datas(), i, i - 1);
                        }
                    }
                    adapter.notifyItemMoved(fromPosition, toPosition);
                }
                return true;
            }

            @Override
            public void onSwiped(@NonNull ViewHolder viewHolder, int direction) {

            }
        }).attachToRecyclerView(isDrag ? this : null);
        return this;
    }

    //@Override protected void onMeasure(int widthSpec, int heightSpec) {
    //  if (mMaxHeight > 0) {
    //    heightSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
    //  }
    //  super.onMeasure(widthSpec, heightSpec);
    //}

    public void setHeight(int height) {
        this.mMaxHeight = height;
        requestLayout();
    }

    public int mMaxHeight = 0;
}
