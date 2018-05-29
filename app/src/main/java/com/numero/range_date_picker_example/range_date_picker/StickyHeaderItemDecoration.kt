package com.numero.range_date_picker_example.range_date_picker

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class StickyHeaderItemDecoration(private var stickyHeader: IStickyHeader) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val topChild = parent.getChildAt(0) ?: return

        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return
        }

        val prevHeaderPosition = stickyHeader.getHeaderPositionForItem(topChildPosition)
        if (prevHeaderPosition == RecyclerView.NO_POSITION) {
            return
        }

        val currentHeader = getHeaderViewForItem(topChildPosition, parent)
        fixLayoutSize(parent, currentHeader)
        val contactPoint = currentHeader.bottom

        val childInContact = getChildInContact(parent, contactPoint) ?: return
        if (stickyHeader.isHeader(parent.getChildAdapterPosition(childInContact))) {
            // 既存のHeaderを押し上げる
            moveHeader(c, currentHeader, childInContact)
            return
        }

        // Headerの描画
        drawHeader(c, currentHeader)
    }

    private fun getHeaderViewForItem(itemPosition: Int, parent: RecyclerView): View {
        val headerPosition = stickyHeader.getHeaderPositionForItem(itemPosition)
        val layoutResId = stickyHeader.getHeaderLayout(headerPosition)

        val header = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        stickyHeader.bindHeaderData(header, headerPosition)
        return header
    }

    private fun drawHeader(c: Canvas, header: View) {
        c.apply {
            save()
            translate(0F, 0F)
            header.draw(this)
            restore()
        }
    }

    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.apply {
            save()
            translate(0F, (nextHeader.top - currentHeader.height).toFloat())
            currentHeader.draw(this)
            restore()
        }
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.bottom > contactPoint) {
                if (child.top <= contactPoint) {
                    return child
                }
            }
        }
        return null
    }

    private fun fixLayoutSize(parent: ViewGroup, view: View) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.paddingTop + parent.paddingBottom, view.layoutParams.height)

        view.measure(childWidthSpec, childHeightSpec)

        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    interface IStickyHeader {

        fun getHeaderPositionForItem(position: Int): Int

        fun getHeaderLayout(headerPosition: Int): Int

        fun bindHeaderData(header: View, headerPosition: Int)

        fun isHeader(itemPosition: Int): Boolean
    }
}