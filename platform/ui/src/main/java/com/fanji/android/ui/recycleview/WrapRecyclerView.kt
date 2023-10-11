package com.fanji.android.ui.recycleview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fanji.android.ui.recycleview.adapter.WrapRecyclerAdapter

/**
 * @author: jiangshide
 * @date: 2023/10/10
 * @email: 18311271399@163.com
 * @description:
 */
open class WrapRecyclerView : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    /**
     * 包裹了头部和底部的adapter
     */
    private var wrapRecyclerAdapter: WrapRecyclerAdapter? = null

    /**
     * 列表对应的list
     */
    private var mAdapter: Adapter<ViewHolder>? = null

    /**
     * 增加一些通用功能
     * 空列表数据应该显示emptyView
     * 正在加载数据页面
     */
    private var emptyView: View? = null
    private var loadView: View? = null


    private var mDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            if (mAdapter == null) {
                return
            }
            //观察者，列表adapter更新，包裹的也需要更新
            if (wrapRecyclerAdapter != adapter) {
                wrapRecyclerAdapter?.notifyDataSetChanged()
            }
            dataChanged()

        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            if (mAdapter == null) {
                return
            }
            if (wrapRecyclerAdapter != adapter) {
                wrapRecyclerAdapter?.notifyItemRangeRemoved(positionStart, itemCount)
            }
            dataChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            if (mAdapter == null) {
                return
            }
            if (wrapRecyclerAdapter != adapter) {
                wrapRecyclerAdapter?.notifyItemChanged(positionStart, itemCount)
            }
            dataChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            if (mAdapter == null) {
                return
            }
            if (wrapRecyclerAdapter != adapter) {
                wrapRecyclerAdapter?.notifyItemRangeChanged(positionStart, itemCount, payload)
            }
            dataChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            if (mAdapter == null) {
                return
            }
            if (wrapRecyclerAdapter != adapter) {
                wrapRecyclerAdapter?.notifyItemRangeInserted(positionStart, itemCount)
            }
            dataChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            if (mAdapter == null) {
                return
            }
            if (wrapRecyclerAdapter != adapter) {
                wrapRecyclerAdapter?.notifyItemRangeRemoved(fromPosition, toPosition)
            }
            dataChanged()
        }
    }


    private fun dataChanged() {
        if (adapter?.itemCount == 0) {
            emptyView?.visibility = VISIBLE
        }
    }

    fun addEmptyView(view: View) {
        this.emptyView = view
    }

    fun addLoadingView(loadingView: View) {
        this.loadView = loadingView
        loadingView.visibility = VISIBLE
    }


    override fun setAdapter(adapter: Adapter<*>?) {
        //防止多次设置Adapter
        if (mAdapter != null) {
            mAdapter!!.unregisterAdapterDataObserver(mDataObserver)
            mAdapter = null
        }
        this.mAdapter = adapter as Adapter<ViewHolder>?

        wrapRecyclerAdapter = if (adapter is WrapRecyclerAdapter) {
            adapter
        } else {
            WrapRecyclerAdapter(adapter as Adapter<ViewHolder>)
        }
        super.setAdapter(wrapRecyclerAdapter)
        //注册一个观察者
        mAdapter!!.registerAdapterDataObserver(mDataObserver)
        //解决GridLayoutManager添加头部和底部不占据一行问题
        wrapRecyclerAdapter!!.adjustSpanSize(this)

        //加载数据页面
        if (loadView != null && loadView!!.visibility == View.VISIBLE) {
            loadView?.visibility = GONE
        }
    }

    /**
     * 添加头部
     */
    fun addHeaderView(view: View) {
        wrapRecyclerAdapter?.addHeaderView(view)
    }

    /**
     * 添加底部
     */
    fun addFooterView(view: View) {
        wrapRecyclerAdapter?.addFooterView(view)
    }

    fun removeHeaderView(view: View) {
        wrapRecyclerAdapter?.removeHeaderView(view)
    }

    fun removeFooterView(view: View) {
        wrapRecyclerAdapter?.removeFooterView(view)
    }
}