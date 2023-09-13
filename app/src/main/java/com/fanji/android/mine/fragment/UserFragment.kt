package com.fanji.android.mine.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.databinding.FragmentUserBinding
import com.fanji.android.doc.PdfFragment
import com.fanji.android.files.FileListener
import com.fanji.android.img.FJImg
import com.fanji.android.mine.MENU
import com.fanji.android.mine.fragment.user.MyAlbumFragment
import com.fanji.android.mine.fragment.user.MyChannelFragment
import com.fanji.android.mine.fragment.user.MyDocFragment
import com.fanji.android.mine.fragment.user.MyFeedFragment
import com.fanji.android.mine.fragment.user.MyMusicFragment
import com.fanji.android.mine.fragment.user.MyVideoFragment
import com.fanji.android.net.HTTP_OK
import com.fanji.android.resource.Resource
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.resource.vm.user.UserVM
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.FJTextView
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator
import com.fanji.android.util.FJEvent
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.FileData
import com.google.android.material.appbar.AppBarLayout

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class UserFragment(private val id: Long? = Resource.uid) : BaseFragment<FragmentUserBinding>(),
    AppBarLayout.OnOffsetChangedListener, FileListener {

    private var user: UserVM? = create(UserVM::class.java)
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentUserBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userViewPager.adapter = binding.userViewPager.create(childFragmentManager)
            .setTitles(
                "动态", "相册", "频道", "乐听", "乐视", "乐读"
            )
            .setFragment(
                MyFeedFragment(),
                MyAlbumFragment(),
                MyChannelFragment(),
                MyMusicFragment(),
                MyVideoFragment(),
                MyDocFragment()
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
            .initTabs(activity, binding.userTab, binding.userViewPager, true)
        binding.mineTopMore.setOnClickListener {
            FJEvent.get().with(MENU).post(1)
        }

        binding.appBarLayout.addOnOffsetChangedListener(this)

        user!!.profileUid.observe(viewLifecycleOwner, Observer {
            if (it.code == HTTP_OK) {
                setProfile(it.data)
            }
        })

        setProfile(Resource.user)
        binding.userL.userIcon.setOnClickListener {
//            FJFiles.openFile(requireContext(), this)
//            push(PdfFragment())
        }
    }

    private fun setProfile(user: User?) {
        if (user == null) return
        setTitle(user?.nick)
        FJImg.loadImagBlur(user?.icon, binding.mineBgImg, 20, 1)
        binding.userL.userNick.text = FJTextView.subStr(user?.nick, 10)
        user?.setSex(binding.userL.mineSexAgeAddr)
        FJImg.loadImageCircle(
            user?.icon,
            binding.userL.userIcon,
            com.fanji.android.resource.R.mipmap.default_user
        )
        FJImg.loadImageCircle(
            user?.icon,
            binding.mineTopIcon,
            com.fanji.android.resource.R.mipmap.default_user
        )
        val mark = getString(R.string.mark)
        binding.userL.userInvitationCode.text = "$mark:${user?.id}"
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val size = verticalOffset * -1 / 308f
        binding.mineTopTitleL.alpha = size
        binding.mineTopFollow.alpha = size
//        binding.userL.alpha = 1 - size
        if (size == 0.0f) {
            binding.mineTopTitleL.visibility = View.GONE
            binding.mineTopFollow.visibility = View.GONE
        } else {
            binding.mineTopTitleL.visibility = View.VISIBLE
            if (id != Resource.uid) {
                binding.mineTopFollow.visibility = View.VISIBLE
            } else {
                binding.mineTopFollow.visibility = View.GONE
            }
        }
    }

    override fun onFiles(files: List<FileData>) {
        LogUtil.e("----------jsd---", "----files:", files)
    }
}