package com.fanji.android.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.DRAWING_CACHE_QUALITY_HIGH
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.Collections

/**
 * created by jiangshide on 2019-12-6.
 * email:18311271399@163.com
 */
class KAdapter<ITEM>(
    private val items: MutableList<ITEM>,
    layoutResId: Int,
    private val bindHolder: View.(ITEM) -> Unit,
    private val itemClick: ITEM.() -> Unit = {}
) : AbstractAdapter<ITEM>(items, layoutResId) {

    private var mHasStableIds = true

    override fun setHasStableIds(hasStableIds: Boolean) {
        this.mHasStableIds = hasStableIds
        super.setHasStableIds(mHasStableIds)
    }

    override fun onItemClick(
        itemView: View,
        position: Int
    ) {
        if (items == null || items.size < position) return
        items[position].itemClick()
    }

    override fun getItemId(position: Int): Long {
//        return super.getItemId(position)
        return position.toLong()
    }

    override fun View.bind(item: ITEM) {
        bindHolder(item)
    }
}

fun <ITEM> RecyclerView.create(
    items: MutableList<ITEM>,
    layoutResId: Int,
    bindHolder: View.(ITEM) -> Unit,
    itemClick: ITEM.() -> Unit = {},
    manager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)
): KAdapter<ITEM> {
    layoutManager = manager
    val pool = RecycledViewPool()
    setRecycledViewPool(pool)
    setItemViewCacheSize(20)
    isDrawingCacheEnabled = true
    drawingCacheQuality = DRAWING_CACHE_QUALITY_HIGH
    return KAdapter(items, layoutResId, bindHolder, itemClick).apply { adapter = this }
}

fun <ITEM> RecyclerView.create(
    items: MutableList<ITEM>,
    layoutResId: Int,
    bindHolder: View.(ITEM) -> Unit,
    itemClick: ITEM.() -> Unit = {},
    manager: RecyclerView.LayoutManager = LinearLayoutManager(this.context),
    position: Int
): KAdapter<ITEM> {
    layoutManager = manager
    return KAdapter(items, layoutResId, bindHolder, itemClick).apply { adapter = this }
}

fun <ITEM> RecyclerView.create(
    items: MutableList<ITEM>,
    layoutResId: Int,
    bindHolder: View.(ITEM) -> Unit,
    itemClick: ITEM.() -> Unit = {},
    itemDecoration: ItemDecoration
): KAdapter<ITEM> {
    if (itemDecorationCount == 0) {
        addItemDecoration(itemDecoration)
    }
    return KAdapter(items, layoutResId, bindHolder, itemClick).apply { adapter = this }
}

fun <ITEM> RecyclerView.create(
    items: MutableList<ITEM>,
    layoutResId: Int,
    bindHolder: View.(ITEM) -> Unit,
    itemClick: ITEM.() -> Unit = {},
    manager: RecyclerView.LayoutManager = LinearLayoutManager(this.context),
    itemDecoration: ItemDecoration
): KAdapter<ITEM> {
    layoutManager = manager
    if (itemDecorationCount == 0) {
        addItemDecoration(itemDecoration)
    }
    return KAdapter(items, layoutResId, bindHolder, itemClick).apply { adapter = this }
}

fun RecyclerView.HORIZONTAL(): LayoutManager {
    val layoutManager = LinearLayoutManager(context)
    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
    return layoutManager
}

fun RecyclerView.VERTICAL(): LayoutManager {
    val layoutManager = LinearLayoutManager(context)
    layoutManager.orientation = LinearLayoutManager.VERTICAL
    return layoutManager
}

abstract class AbstractAdapter<ITEM> constructor(
    private var itemList: MutableList<ITEM>,
    private val layoutResId: Int
) : RecyclerView.Adapter<AbstractAdapter.Holder>() {

    private var mOnItemListener: OnItemListener<ITEM>? = null

    protected abstract fun onItemClick(
        itemView: View,
        position: Int
    )

    protected abstract fun View.bind(item: ITEM)

    override fun getItemCount(): Int = itemList?.size!!

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutResId, parent, false)
        val viewHolder = Holder(view)
        val itemView = viewHolder.itemView
        itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClick(itemView, position)
            }
            if (mOnItemListener != null && itemList != null && position < itemList?.size!!) {
                mOnItemListener?.onItem(position, itemList[position])
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(
        holder: Holder,
        position: Int
    ) {
        if (itemList == null || itemList?.size!! < position) return
        holder.itemView.bind(itemList[position])
    }

    fun setItemListener(listener: OnItemListener<ITEM>?): KAdapter<ITEM> {
        this.mOnItemListener = listener
        return Kadapter@ this as KAdapter<ITEM>
    }

    @Synchronized
    fun update(
        index: Int,
        t: ITEM
    ) {
        if (itemList == null || itemList.size < index) return
        itemList.removeAt(index)
        itemList.add(index, t)
        notifyDataSetChanged()
    }

    @Synchronized
    fun update(
        source: ITEM,
        to: ITEM
    ) {
        if (itemList == null || itemList.size == 0) return
        itemList.forEachIndexed { index, item ->
            if (item!!.equals(source)) {
                itemList.remove(source)
                itemList.add(index, to)
            }
        }
        notifyDataSetChanged()
    }

    fun update(items: List<ITEM>) {
        DiffUtil.calculateDiff(DiffUtilCallback(itemList, items))
            .dispatchUpdatesTo(this)
    }

    fun add(items: List<ITEM>) {
        if (items == null) return
        this.add(0, items)
    }

    fun add(
        index: Int,
        items: List<ITEM>
    ) {
        itemList?.addAll(index, items)
        notifyDataSetChanged()
    }

    fun add(
        index: Int,
        item: ITEM
    ) {
        itemList?.add(index, item)
        notifyDataSetChanged()
    }

    fun add(item: ITEM) {
        itemList?.add(item)
        notifyItemInserted(itemList?.size!!)
    }

    fun add(
        data: MutableList<ITEM>?,
        isRefresh: Boolean
    ) {
        if (isRefresh) {
            itemList?.clear()
        }
        if (data != null) {
            itemList?.addAll(data)
            notifyDataSetChanged()
        }
    }

    fun datas(): MutableList<ITEM> {
        return itemList
    }

    fun clear() {
        itemList?.clear()
        notifyDataSetChanged()
    }

    fun count(): Int {
        return itemList?.size!!
    }

    fun remove(position: Int) {
        if (itemList == null || itemList?.size!! < position) return
        itemList?.removeAt(position)
        notifyItemRemoved(position)
    }

    fun remove(item: ITEM) {
        itemList?.remove(item)
        notifyDataSetChanged()
    }

    fun drag(recyclerView: RecyclerView): KAdapter<ITEM> {
        return this.drag(recyclerView, null)
    }

    fun drag(
        recyclerView: RecyclerView,
        listener: OnMoveListener<ITEM>?
    ): KAdapter<ITEM> {
        return this.drag(recyclerView, listener, listOf())
    }

    fun drag(
        recyclerView: RecyclerView,
        list: List<Int>
    ): KAdapter<ITEM> {
        return this.drag(recyclerView, null, list)
    }

    fun drag(
        recyclerView: RecyclerView,
        listener: OnMoveListener<ITEM>?,
        list: List<Int>
    ): KAdapter<ITEM> {

        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                if (list != null && list.isNotEmpty()) {
                    list?.forEach {
                        if (target.adapterPosition == it) return false
                    }
                } else {
                    if (target.adapterPosition == datas()?.size!! - 1) return false
                }
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                if (fromPosition < toPosition) {
                    for (i in fromPosition until fromPosition) {
                        Collections.swap(datas(), i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(datas(), i, i - 1)
                    }
                }
                notifyItemMoved(fromPosition, toPosition)
                listener?.move(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(
                viewHolder: ViewHolder,
                direction: Int
            ) {
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder
            ): Int {
                list?.forEach {
                    if (viewHolder.adapterPosition == it) return 0
                }
                val dragFlags: Int
                val swipeFlags: Int
                if (recyclerView.layoutManager is GridLayoutManager) {
                    dragFlags = (ItemTouchHelper.UP
                            or ItemTouchHelper.DOWN
                            or ItemTouchHelper.LEFT
                            or ItemTouchHelper.RIGHT)
                    swipeFlags = 0
                } else {
                    dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    swipeFlags = 0
                }
                return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
            }

        }).attachToRecyclerView(recyclerView)
        return Kadapter@ this as KAdapter<ITEM>
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
    interface OnMoveListener<ITEM> {
        fun move(
            fromPosition: Int,
            toPosition: Int
        )
    }

    interface OnItemListener<ITEM> {
        fun onItem(
            position: Int,
            item: ITEM
        )
    }
}

internal class DiffUtilCallback<ITEM>(
    private val oldItems: List<ITEM>,
    private val newItems: List<ITEM>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ) =
        oldItems[oldItemPosition] == newItems[newItemPosition]

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ) =
        oldItems[oldItemPosition] == newItems[newItemPosition]
}