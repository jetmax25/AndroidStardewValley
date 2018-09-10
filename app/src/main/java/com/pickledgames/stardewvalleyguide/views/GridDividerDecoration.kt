package com.pickledgames.stardewvalleyguide.views

import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

// https://gist.github.com/jlorett/42daeda5f511ddadb887
class GridDividerDecoration(
        private val offset: Int = 0,
        private val spanCount: Int = 1
) : RecyclerView.ItemDecoration() {

    private var lastItemInFirstLane = -1

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val params = view.layoutParams as RecyclerView.LayoutParams
        val position = params.viewAdapterPosition
        val spanSize: Int
        val index: Int
        if (params is GridLayoutManager.LayoutParams) {
            spanSize = params.spanSize
            index = params.spanIndex
        } else {
            spanSize = 1
            index = position % spanCount
        }
        // invalid value
        if (spanSize < 1 || index < 0) return

        if (spanSize == spanCount) { // full span
            outRect.left = offset
            outRect.right = offset
        } else {
            if (index == 0) {  // left one
                outRect.left = offset
            }
            // spanCount >= 1
            if (index == spanCount - 1) { // right one
                outRect.right = offset
            }
            if (outRect.left == 0) {
                outRect.left = offset / 2
            }
            if (outRect.right == 0) {
                outRect.right = offset / 2
            }
        }
        // set top to all in first lane
        if (position < spanCount && spanSize <= spanCount) {
            if (lastItemInFirstLane < 0) { // lay out at first time
                lastItemInFirstLane = if (position + spanSize == spanCount) position else lastItemInFirstLane
                outRect.top = offset
            } else if (position <= lastItemInFirstLane) { // scroll to first lane again
                outRect.top = offset
            }
        }
        outRect.bottom = offset
    }
}
