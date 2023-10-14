package com.fanji.android.mine.fragment.user

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.net.HTTP_OK
import com.fanji.android.net.vm.LiveResult
import com.fanji.android.resource.Resource
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.ui.FJButton
import com.fanji.android.ui.FJImageView
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.ui.vm.FJVM

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class MyAlbumFragment(private val uid: Long? = Resource.uid) :
    BaseFragment<CommonRecyclerviewBinding>(),
    FJVM.VMListener<MutableList<Feed>> {

    private var adapter: KAdapter<Feed>? = null
    private var feed: FeedVM? = create(FeedVM::class.java)
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = CommonRecyclerviewBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter =
            binding.recyclerView.create(
                R.layout.mine_user_album_fragment_item,
                {
                    val albumItemFormat: FJImageView = this.findViewById(R.id.albumItemFormat)
                    val albumItemSize: TextView = this.findViewById(R.id.albumItemSize)
                    val albumItemImg: FJImageView = this.findViewById(R.id.albumItemImg)
                    val albumItemDate: TextView = this.findViewById(R.id.albumItemDate)
                    val albumItemChannel: FJButton = this.findViewById(R.id.albumItemChannel)
                    it.setImg(
                        activity,
                        albumItemFormat,
                        albumItemSize,
                        albumItemImg,
                        it.cover,
                        true
                    )
                    albumItemDate.text = it.date
                    if (!TextUtils.isEmpty(it.channel)) {
                        albumItemChannel.text = "# ${it.channel}"
                        albumItemChannel.visibility = View.VISIBLE
                    } else {
                        albumItemChannel.visibility = View.GONE
                    }
                },
                {},
                manager = layoutManager
            )

        feed?.userBlog(uid = uid, listener = this)
        loading()
    }

    override fun onRes(res: LiveResult<MutableList<Feed>>) {
        finishData(true, true, true)
        page = res.page
        if (res.code == HTTP_OK) {
            adapter?.add(res.data, res.isRefresh)
            finishTips()
            return
        }
        if (adapter == null || adapter?.count() == 0) {
            tips()
        }
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
        feed?.userBlog(uid = uid, listener = this)
        loading()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        feed?.userBlog(uid = uid, listener = this)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        feed?.userBlog(uid = uid, isRefresh = false, page = page, listener = this)
    }
}