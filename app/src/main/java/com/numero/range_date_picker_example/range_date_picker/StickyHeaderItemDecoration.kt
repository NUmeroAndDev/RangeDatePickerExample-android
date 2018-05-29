package com.numero.range_date_picker_example.range_date_picker

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class StickyHeaderItemDecoration(private var mListener: StickyHeaderInterface) : RecyclerView.ItemDecoration() {

    private var currentHeader: View? = null

    // RecyclerViewのセルが表示されたときに呼ばれる
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        // 一番上のビュー
        val topChild = parent.getChildAt(0) ?: return  // RecyclerViewの中身がない

        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return
        }


        val prevHeaderPosition = mListener.getHeaderPositionForItem(topChildPosition)
        if (prevHeaderPosition == -1) {
            return
        }

        // ヘッダービューが表示された
        currentHeader = getHeaderViewForItem(topChildPosition, parent)
        fixLayoutSize(parent, currentHeader!!)
        val contactPoint = currentHeader!!.getBottom()
        // 次のセルを取得
        val childInContact = getChildInContact(parent, contactPoint)
                ?: return  // 次のセルがない

        // ヘッダーの判定
        if (mListener.isHeader(parent.getChildAdapterPosition(childInContact))) {
            // 既存のStickyヘッダーを押し上げる
            moveHeader(c, currentHeader!!, childInContact)
            return
        }

        // Stickyヘッダーの描画
        drawHeader(c, currentHeader!!)

    }

    // dp <=> pixel変換
    fun convertDp2Px(dp: Float, context: Context): Float {
        val metrics = context.getResources().getDisplayMetrics()
        return dp * metrics.density
    }


    // Stickyヘッダービューの取得
    private fun getHeaderViewForItem(itemPosition: Int, parent: RecyclerView): View {
        val headerPosition = mListener.getHeaderPositionForItem(itemPosition)
        val layoutResId = mListener.getHeaderLayout(headerPosition)
        // Stickyヘッダーレイアウトをinflateする
        val header = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        //header.setElevation(header,convertDp2Px(R.dimen.shadow,header.getContext()));
        // Stickyレイアウトにデータバインドする
        mListener.bindHeaderData(header, headerPosition)
        return header
    }

    // Stickyヘッダーを描画する
    private fun drawHeader(c: Canvas, header: View) {
        c.save()
        c.translate(0F, 0F)
        header.draw(c)
        c.restore()
    }

    // Stickyヘッダーを動かす
    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.save()
        c.translate(0F, (nextHeader.getTop() - currentHeader.getHeight()).toFloat())
        currentHeader.draw(c)
        c.restore()
    }

    // 座標から次のRecyclerViewのセル位置を取得
    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? {
        var childInContact: View? = null
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.bottom > contactPoint) {
                if (child.top <= contactPoint) {
                    childInContact = child
                    break
                }
            }
        }
        return childInContact
    }

    // Stickyヘッダーのレイアウトサイズを取得
    private fun fixLayoutSize(parent: ViewGroup, view: View) {

        // RecyclerViewのSpec
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        // headersのSpec
        val childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, view.getLayoutParams().width)
        val childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.paddingTop + parent.paddingBottom, view.getLayoutParams().height)

        view.measure(childWidthSpec, childHeightSpec)

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight())
    }

    // Stickyヘッダーインタフェース
    interface StickyHeaderInterface {

        fun getHeaderPositionForItem(itemPosition: Int): Int

        fun getHeaderLayout(headerPosition: Int): Int

        fun bindHeaderData(header: View, headerPosition: Int)

        fun isHeader(itemPosition: Int): Boolean
    }
}