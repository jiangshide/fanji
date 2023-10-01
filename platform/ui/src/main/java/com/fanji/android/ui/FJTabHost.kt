package com.fanji.android.ui

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.fanji.android.ui.FJNavigationView.CustomBuilder
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.navigation.NavigationController
import com.fanji.android.ui.navigation.item.BaseTabItem

/**
 * @author: jiangshide
 * @date: 2023/9/30
 * @email: 18311271399@163.com
 * @description:
 */
class FJTabHost : FrameLayout {
    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }

    private var mMainRootView: ConstraintLayout? = null
    private var mainViewPager: FJViewPager? = null
    private var mainTab: FJNavigationView? = null
    private var tabController: NavigationController? = null
    private var customBuilder: CustomBuilder? = null
    private var mFragmentManager: FragmentManager? = null
    private fun init() {
        val view = LayoutInflater.from(context).inflate(R.layout.default_tab, this, true)
        mMainRootView = view.findViewById(R.id.mainRootView)
        mainViewPager = view.findViewById(R.id.mainViewPager)
        mainTab = view.findViewById(R.id.mainTab)
        customBuilder = mainTab?.custom()
    }

    fun setFragmentManager(fragmentManager: FragmentManager?) {
        mFragmentManager = fragmentManager
    }

    fun addRoundItem(drawable: Int, checkedDrawable: Int, title: String?): FJTabHost {
        this.addRoundItem(drawable, checkedDrawable, title, R.color.gray, R.color.white)
        return this
    }

    fun addRoundItem(
        drawable: Int,
        checkedDrawable: Int,
        title: String?,
        selectColor: Int,
        selectedColor: Int
    ): CustomBuilder {
        return customBuilder!!.addItem(
            mainTab!!.newRoundItem(
                drawable,
                checkedDrawable,
                title,
                selectColor,
                selectedColor
            )
        )
    }

    fun addItem(tabItem: BaseTabItem?): FJTabHost {
        customBuilder!!.addItem(tabItem)
        return this
    }

    fun addItem(drawable: Int, checkedDrawable: Int, title: String?): FJTabHost {
        this.addItem(drawable, checkedDrawable, title, R.color.gray, R.color.white)
        return this
    }

    fun addItem(
        drawable: Int,
        checkedDrawable: Int,
        title: String?,
        selectColor: Int,
        selectedColor: Int
    ): CustomBuilder {
        return customBuilder!!.addItem(
            mainTab!!.newItem(
                drawable,
                checkedDrawable,
                title,
                selectColor,
                selectedColor
            )
        )
    }

    fun setData(drawables: IntArray, checkedDrawables: IntArray, titles: Array<String?>) {
        this.setData(drawables, checkedDrawables, titles, R.color.gray, R.color.white)
    }

    fun setData(
        drawables: IntArray,
        checkedDrawables: IntArray,
        titles: Array<String?>,
        selectColor: Int,
        selectedColor: Int
    ) {
        val length = drawables.size
        for (i in 0 until length) {
            addItem(drawables[i], checkedDrawables[i], titles[i], selectColor, selectedColor)
        }
        customBuilder!!.build()
    }

    fun build(): NavigationController? {
        tabController = customBuilder!!.build()
        tabController?.setupWithViewPager(mainViewPager!!)
        return tabController
    }

    fun setFragments(
        listener: ViewPager.OnPageChangeListener,
        vararg fragments: Fragment?
    ): FJTabHost {
        mainViewPager?.adapter =
            mainViewPager!!.create(mFragmentManager).setFragment(fragments.toList())
                .initTabs(context, mainViewPager, true).setListener(listener)
        return this
    }

    fun push(
        fragment: BaseFragment<*>,
        bundle: Bundle?,
        tag: String?,
        enterAnim: Int,
        exitAnim: Int
    ) {
        if (bundle != null) {
            fragment.arguments = bundle
        }
        val fragmentTransaction = mFragmentManager!!.beginTransaction()
        if (enterAnim != -1 || exitAnim != -1) {
            fragmentTransaction.setCustomAnimations(enterAnim, exitAnim)
        }
        fragmentTransaction.add(R.id.content, fragment)
        //        fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.DESTROYED);
        fragmentTransaction.addToBackStack(if (TextUtils.isEmpty(tag)) fragment.javaClass.canonicalName else tag)
        fragmentTransaction.commitAllowingStateLoss()
        //        fragmentTransaction.commitNowAllowingStateLoss();
    }

    fun pop(flag: Int) {
        mFragmentManager?.popBackStackImmediate(null, flag)
    }
}