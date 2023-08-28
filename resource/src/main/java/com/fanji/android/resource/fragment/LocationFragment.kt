package com.fanji.android.resource.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.fanji.android.resource.R
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.resource.location.FJLocation
import com.fanji.android.resource.location.listener.IPoiSearchListener
import com.fanji.android.ui.FJButton
import com.fanji.android.ui.FJEditText
import com.fanji.android.ui.FJRecycleView
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.util.LogUtil
import com.fanji.widget.adapter.KAdapter
import com.fanji.widget.adapter.create

/**
 * created by jiangshide on 5/4/21.
 * email:18311271399@163.com
 */
class LocationFragment(private val onLocationListener: OnLocationListener) : BaseFragment(),
    IPoiSearchListener {

    private var adapter: KAdapter<PoiItem>? = null

    private lateinit var locationEdit: FJEditText
    private lateinit var locationCancel: FJButton
    private lateinit var locationRecycleView: FJRecycleView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return view(R.layout.location_fragment, true, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationEdit = view.findViewById(R.id.locationEdit)
        locationCancel = view.findViewById(R.id.locationCancel)
        locationRecycleView = view.findViewById(R.id.locationRecycleView)

        locationEdit.setListener { s, input ->
            FJLocation.getInstance()
                .searchPOI(activity?.applicationContext, input, "", this)
        }
        locationCancel.setOnClickListener {
            pop()
        }
        FJLocation.getInstance()
            .searchNearPOI(activity, page, 100, this)
        loading()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        page = 0
        FJLocation.getInstance()
            .searchNearPOI(activity, page, 20, this)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        page++
        FJLocation.getInstance()
            .searchNearPOI(activity, page, 20, this)
    }

    private fun showView(data: ArrayList<PoiItem>) {
        if (adapter != null) {
            adapter?.add(data)
            return
        }
        adapter = locationRecycleView?.create(data, R.layout.location_fragment_item, {
            val locationItemName: TextView = this.findViewById(R.id.locationItemName)
            val locationItemDes: TextView = this.findViewById(R.id.locationItemDes)
            locationItemName?.text = it.title
            locationItemDes?.text = it.snippet
        }, {
            onLocationListener?.onPoiItem(this)
            pop()
        })

    }

    override fun onPoiResult(poiResult: PoiResult?) {
        refreshFinish(true)
        hiddenTips()
        showView(poiResult!!.pois)
        LogUtil.e("size:", poiResult.pois.size)
    }

    override fun onError(errors: FJLocation.Errors?) {
        refreshFinish(true)
        hiddenTips()
        LogUtil.e("errors:", errors)
        if (!isRefresh) {
            page--
        }
        if (adapter == null || adapter?.datas() == null || adapter?.datas()?.size == 0) {
            tips()
        }
    }
}

interface OnLocationListener {
    fun onPoiItem(poiItem: PoiItem)
}