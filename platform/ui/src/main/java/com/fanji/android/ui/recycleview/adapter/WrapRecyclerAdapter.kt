package com.fanji.android.ui.recycleview.adapter

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author: jiangshide
 * @date: 2023/10/10
 * @email: 18311271399@163.com
 * @description:
 */
class WrapRecyclerAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * 用来存放头部和底部的集合
     */
    private var headerView: SparseArray<View>
    private var footerView: SparseArray<View>

    //头部类型开始位置，用于viewType
    private var BASE_ITEM_TYPE_HEADER = 1000000

    //底部类型开始位置，用于viewType
    private var BASE_ITEM_TYPE_FOOTER = 2000000

    /**
     * 列表的adapter
     */
    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>

    init {
        this.adapter = adapter
        headerView = SparseArray()
        footerView = SparseArray()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (isHeaderViewType(viewType)) {
            val headerView = headerView.get(viewType)
            return createHeaderFooterViewHolder(headerView)
        }
        if (isFooterViewType(viewType)) {
            val footerView = footerView.get(viewType)
            return createHeaderFooterViewHolder(footerView)
        }
        return adapter.onCreateViewHolder(parent, viewType)
    }

    /**
     * 创建头部或底部的ViewHolder
     */
    private fun createHeaderFooterViewHolder(view: View): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //如果是头部或者底部item，不进行布局绑定
        if (isHeaderViewPosition(position) || isFooterViewPosition(position)) {
            return
        }
        val itemAdapterPosition = position - headerView.size()
        adapter.onBindViewHolder(holder, itemAdapterPosition)
    }

    override fun getItemViewType(position: Int): Int {
        if (isHeaderViewPosition(position)) {
            //如果是headerView
            return headerView.keyAt(position)
        }
        if (isFooterViewPosition(position)) {
            //如果是footerView
            val footerPosition = position - headerView.size() - adapter.itemCount
            return footerView.keyAt(footerPosition)
        }
        //则为普通adapter的ViewType
        val adapterPosition = position - headerView.size()
        return adapter.getItemViewType(adapterPosition)
    }

    override fun getItemCount(): Int {
        //条目为三者数量之和
        return adapter.itemCount + headerView.size() + footerView.size()
    }

    fun addHeaderView(view: View) {
        val position = headerView.indexOfValue(view)
        if (position < 0) {
            headerView.put(BASE_ITEM_TYPE_HEADER++, view)
        }
        notifyDataSetChanged()
    }

    fun removeHeaderView(view: View) {
        val position = headerView.indexOfValue(view)
        if (position < 0) {
            return
        }
        headerView.removeAt(position)
        notifyDataSetChanged()
    }

    fun getAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        return adapter
    }

    fun addFooterView(view: View) {
        val position = footerView.indexOfValue(view)
        if (position < 0) {
            footerView.put(BASE_ITEM_TYPE_FOOTER++, view)
        }
        notifyDataSetChanged()
    }


    fun removeFooterView(view: View) {
        val position = footerView.indexOfValue(view)
        if (position < 0) {
            return
        }
        footerView.removeAt(position)
        notifyDataSetChanged()
    }

    private fun isHeaderViewType(viewType: Int): Boolean {
        val position = headerView.indexOfKey(viewType)
        return position >= 0
    }

    private fun isFooterViewType(viewType: Int): Boolean {
        val position = footerView.indexOfKey(viewType)
        return position >= 0
    }


    private fun isHeaderViewPosition(position: Int): Boolean {
        return position < headerView.size()
    }

    private fun isFooterViewPosition(position: Int): Boolean {
        return position >= headerView.size() + adapter.itemCount
    }


    /**
     * 解决GridLayoutManager添加头部和底部不占用一行的问题
     */
    fun adjustSpanSize(recyclerView: RecyclerView) {
        if (recyclerView.layoutManager is GridLayoutManager) {
            val layoutManager = recyclerView.layoutManager as GridLayoutManager
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val isHeaderOrFooter =
                        isHeaderViewPosition(position) || isFooterViewPosition(position)
                    return if (isHeaderOrFooter) {
                        layoutManager.spanCount
                    } else {
                        1
                    }
                }

            }
        }
    }
}