package com.matome.asmr;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private int mPreviousTotal = 0;
    private boolean mLoading = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();

        int totalCount = recyclerView.getAdapter().getItemCount(); //合計のアイテム数
        int childCount = recyclerView.getChildCount(); // RecyclerViewに表示されてるアイテム数
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        int firstPosition = 0;// RecyclerViewに表示されている一番上のアイテムポジション

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            firstPosition = gridLayoutManager.findFirstVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
        }

        if (mLoading) {
            if (totalItemCount > mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = totalItemCount;
            }
        }
        if (!mLoading && totalCount == childCount + firstPosition) {
            onLoadMore();
            mLoading = true;
        }
    }
    public abstract void onLoadMore();

}