package com.fanji.android.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.FragmentLocationBinding
import com.fanji.android.location.data.PoiData
import com.fanji.android.location.listener.IPoiSearchListener
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.util.LogUtil

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class LocationFragment(private val onLocationListener: OnLocationListener) :
    BaseFragment<FragmentLocationBinding>(),
    IPoiSearchListener {

    private var adapter: KAdapter<PoiData>? = null
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLocationBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.locationEdit.setListener { s, input ->
            FJLocation.getInstance()
                .searchPOI(activity?.applicationContext, input, "", this)
        }
        binding.locationCancel.setOnClickListener {
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

    private fun showView(data: MutableList<PoiData>) {
        if (adapter != null) {
            adapter?.add(data)
            return
        }
        adapter = binding.locationRecycleView?.create(R.layout.location_fragment_item, {
            val locationItemName: TextView = this.findViewById(R.id.locationItemName)
            val locationItemDes: TextView = this.findViewById(R.id.locationItemDes)
            locationItemName?.text = it.title
            locationItemDes?.text = it.snippet
        }, {
            onLocationListener?.onPoiData(this)
            pop()
        },data)

    }

    override fun onPoiResult(poiResult: List<PoiData>) {
        finishData(true, true, true)
        showView(poiResult.toMutableList())
        LogUtil.e("size:", poiResult.size)
    }

    override fun onError(errors: FJLocation.Errors?) {
        finishData(true, true, true)
        LogUtil.e("errors:", errors)
//        if (!mIsRefresh) {
//            page--
//        }
        if (adapter == null || adapter?.datas() == null || adapter?.datas()?.size == 0) {
            tips()
        }
    }
}

interface OnLocationListener {
    fun onPoiData(poiDate: PoiData)
}