package com.fanji.android.resource.bottomsheet

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.viewbinding.ViewBinding
import com.fanji.android.resource.R
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.ui.FJImageView
import com.fanji.android.util.ScreenUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * created by jiangshide on 5/22/21.
 * email:18311271399@163.com
 */
class BottomSheetFragment : Fragment(), SceneContainer, BackPressedConcerned {

    var callback: BottomSheetCallback? = null

    /**
     * onStart 后设置此 drawable 给 container
     */
    private var backgroundDrawable: Drawable? = null

    private var backgroundElevation: Float = 0F

    override fun getInitBehaviorState(): Int =
        arguments?.getInt(INIT_BEHAVIOR_STATE)?.takeIf { it > 0 }
            ?: BottomSheetBehavior.STATE_COLLAPSED

    override fun getInitSceneClass(): String = arguments?.getString(INIT_SCENE_NAME)!!
    override fun getInitSceneArguments(): Bundle =
        arguments?.getBundle(INIT_SCENE_ARGUMENTS) ?: Bundle()

    override fun getFullscreenEnable(): Boolean = arguments?.getBoolean(ENABLE_FULL_SCREEN) ?: false
    override fun getDimBackgroundEnable(): Boolean =
        arguments?.getBoolean(ENABLE_DIM_BACKGROUND, true) ?: true

    override fun getIgnoreRecreateEnable(): Boolean =
        arguments?.getBoolean(SET_IGNORE_RECREATE) ?: false

    override fun getTouchOutsideCancelEnable(): Boolean =
        arguments?.getBoolean(SET_TOUCH_OUTSIDE_CANCEL) ?: false

    override fun getOutsideInteractive(): Boolean =
        arguments?.getBoolean(SET_TOUCH_OUTSIDE_INTERACTIVE) ?: !getDimBackgroundEnable()

    override fun getInitBackgroundColor(): Int =
        arguments?.getInt(INIT_BACKGROUND_COLOR)?.takeIf { it > 0 }
            ?: com.fanji.android.ui.R.color.black

    override fun getCancelable(): Boolean = arguments?.getBoolean(SET_CANCELABLE, true) ?: true
    override fun getCornerEnable(): Boolean = arguments?.getBoolean(ENABLE_CORNER) ?: false
    override fun getDragEnable(): Boolean = arguments?.getBoolean(ENABLE_DRAG) ?: false
    override fun getShowDrag(): Boolean = arguments?.getBoolean(SHOW_DRAG) ?: false
    override fun getInitPeekHeight(): Int? = arguments?.getInt(INIT_PEEK_HEIGHT)?.takeIf { it > 0 }
    override fun getSecondPeekHeight(): Int? =
        arguments?.getInt(SECOND_PEEK_HEIGHT)?.takeIf { it > 0 }

    private lateinit var drag: FJImageView
    private lateinit var background_dim: View
    private lateinit var container: ConstraintLayout
    private lateinit var fragment: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(
            R.layout.bottomsheet_dialog_bottom_sheet_fragment_container,
            container,
            false
        )

//    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = Fra

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drag = view.findViewById(R.id.drag)
        background_dim = view.findViewById(R.id.background_dim)
        container = view.findViewById(R.id.container)
        fragment = view.findViewById(R.id.fragment)
        val context = context ?: return
        val initFragment = makeFragment(context, getInitSceneClass(), getInitSceneArguments())

        childFragmentManager.beginTransaction()
            .replace(R.id.fragment, initFragment, initFragment.tag)
            .commit()

        if (getShowDrag()) {
            drag.visibility = View.VISIBLE
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                drag.imageTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            com.fanji.android.ui.R.color.black
                        )
                    )
            }
        } else {
            drag.visibility = View.GONE
        }

        requireBottomSheetBehavior()?.isDraggable = getDragEnable()

        setContainerBackground(getCornerEnable(), getInitBackgroundColor())

        animateShowPanel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 部分业务想要忽略 Fragment 重建
        if (getIgnoreRecreateEnable() && savedInstanceState != null) {
//            popSelf()
        }
    }

//    override fun provideStatusBarColor(): Int {
////        val context = context ?: return super.provideStatusBarColor()
////        val color = ContextCompat.getColor(context, R.color.blackLight)
//        return if (getDimBackgroundEnable()) {
//            ColorUtils.setAlphaComponent(color, 50)
//        } else {
//            Color.TRANSPARENT
//        }
//    }

    override fun onBackPressed(): Boolean {
        popBack()
        return true
    }

//    override fun isPhantom(): Boolean {
//        return arguments?.getBoolean(ENABLE_ZH_BOTTOM_SHEET_FRAGMENT_PHANTOM) ?: super.isPhantom()
//    }

    override fun popBack() {
        val consumedByChild = childFragmentManager.fragments
            .lastOrNull()
            ?.let { it as? BackPressedConcerned }
            ?.let(BackPressedConcerned::onBackPressed)

        // 当前 child fragment 消耗了 back event, return
        if (consumedByChild == true) return

        if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
            val last = childFragmentManager.fragments.last()
            childFragmentManager.beginTransaction().show(last).commit()
            TransitionManager.beginDelayedTransition(view?.parent as ViewGroup, ChangeBounds())
        } else {
            dismiss()
        }
    }

    private var popSelf = false

    override fun dismiss() {
        popSelf = true
        if (getDimBackgroundEnable()) {
            ObjectAnimator.ofFloat(background_dim, View.ALPHA, 0.2F, 0F)
                .setDuration(400)
                .start()
        }

        /**
         * 关闭 dialog 动画还得靠 hideable
         * 实现 cancelable 可能将此标志位置为 false
         * 这边使用时强行设置回来
         */
        requireBottomSheetBehavior()?.isHideable = true
        requireBottomSheetBehavior()?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun close() = dismiss()

    override fun popScene(fromScene: Scene, targetSceneName: String, param: Bundle) {
        val context = context ?: return
        val target = makeFragment(context, targetSceneName, param)

        val transaction = childFragmentManager.beginTransaction()

        for (view in fromScene.getSharedElement()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                transaction.addSharedElement(view, view.transitionName)
            }
        }

        val current = childFragmentManager.fragments.last()
        transaction.hide(current)
        transaction.add(R.id.fragment, target, targetSceneName)
            .addToBackStack(targetSceneName)
            .commit()

        TransitionManager.beginDelayedTransition(view as ViewGroup, ChangeBounds())
    }

    override fun getCurrentScene(): Scene? {
        // 先做一道检查 host 为空时直接返回
        if (host == null) {
            return null
        }
        try {
            // 再做一次 try catch 保证不会出错
            // ignore getChildFragmentManager IllegalStateException
            return childFragmentManager.fragments.lastOrNull() as? Scene
        } catch (e: Exception) {
        }
        return null
    }

    override fun getCurrentBehaviorState(): Int {
        return requireBottomSheetBehavior()?.state ?: super.getCurrentBehaviorState()
    }

    override fun enableFullscreen() {
        container?.layoutParams = container?.layoutParams?.apply {
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        fragment?.layoutParams = fragment?.layoutParams?.apply {
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        animateLayoutChanged()
    }

    override fun disableFullScreen() {
        container?.layoutParams = container?.layoutParams?.apply {
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        fragment?.layoutParams = fragment?.layoutParams?.apply {
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        animateLayoutChanged()
    }

    override fun setBackgroundElevation(elevation: Float) {
        val container = container
        backgroundElevation = elevation

        if (container != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                container.elevation = elevation
            }
        }
    }

    override fun setBackgroundDrawable(drawable: Drawable) {
        val container = container
        backgroundDrawable = drawable

        if (container != null) {
            container.background = drawable
        }
    }

    override fun setBackgroundColor(@ColorInt color: Int) {
        val drawable = GradientDrawableBuilder()
            .setFillColor(color)
            .create()

        backgroundDrawable = drawable

        val container = container
        if (container != null) {
            container.background = drawable
        }
    }

    override fun setContainerBackground(enableCorner: Boolean, @ColorRes color: Int) {
        val drawable = GradientDrawableBuilder()
            .setFillColor(ContextCompat.getColor(requireContext(), color))
            .create()

        backgroundDrawable = drawable

        val container = container
        if (container != null) {
            container.background = drawable
        }
    }

    override fun setBehaviorState(@BottomSheetBehavior.State behaviorState: Int) {
        super.setBehaviorState(behaviorState)
        requireBottomSheetBehavior()?.state = behaviorState
    }

    override fun animateLayoutChanged() {
        TransitionManager.beginDelayedTransition(view as ViewGroup, ChangeBounds())
    }

    private fun animateShowPanel() {
        val behavior = requireBottomSheetBehavior() ?: return

        if (getFullscreenEnable()) {
            enableFullscreen()
        }

        if (getCornerEnable()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                container.clipToOutline = true
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                container.outlineProvider = cornerProvider
            }
        }
        container.background = backgroundDrawable
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            container.elevation = backgroundElevation
        }

        behavior.addBottomSheetCallback(behaviorStateChangeListener)

        getInitPeekHeight()?.run(behavior::setPeekHeight)

        background_dim.isClickable = !getOutsideInteractive()

        if (getTouchOutsideCancelEnable()) {
            background_dim.setOnClickListener {
                callback?.onCancel()
                dismiss()
            }
        }

        if (!getDimBackgroundEnable()) {
            background_dim.alpha = 0F
        }

        // 自己做弹出动画, 把面板先隐藏掉再弹出
        behavior.isHideable = true
        behavior.state = BottomSheetBehavior.STATE_HIDDEN

        // 随便 delay 下再把咱们的弹窗起来
//        safetyHandler.postDelayed({
//            behavior.state = getInitBehaviorState()
//            behavior.isHideable = getCancelable()
//        }, 100)

        if (getDimBackgroundEnable()) {
            ObjectAnimator.ofFloat(background_dim, View.ALPHA, 0F, 0.2F)
                .setDuration(400)
                .start()
        }
    }

    private val behaviorStateChangeListener = object : BottomSheetBehavior.BottomSheetCallback() {
        /**
         * 这个参数有点丑， 不过我们要手动 hide 再把 layout show 出来也没办法
         * 这个 behavior listener 监听状态修改 drag 图标
         * 这个变量用于忽略做动画时产生的 state change, 避免 drag 图标状态异常
         */
        private var initStateChange = false

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }

        @SuppressLint("WrongConstant")
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            callback?.onBehaviorStateChange(bottomSheet, newState)
            getCurrentScene()?.onContainerBehaviorStateChange(bottomSheet, newState)

            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    if (initStateChange) {
                        drag?.setImageResource(R.drawable.bottomsheet_ic_action_sheet_element_handle_down)
                        drag?.rotation = 180F
                    }
                    initStateChange = true
                }

                BottomSheetBehavior.STATE_EXPANDED -> {
                    if (initStateChange) {
                        drag?.setImageResource(R.drawable.bottomsheet_ic_action_sheet_element_handle_down)
                        drag?.rotation = 0F
                    }
                    initStateChange = true
                }

                BottomSheetBehavior.STATE_DRAGGING -> {
                    if (!getDragEnable()) {
                        requireBottomSheetBehavior()?.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    val bottomSheetBehavior = requireBottomSheetBehavior() ?: return
                    // 当 second peek height有值 并且不等于 behavior.peekHeight 当前值时
                    getSecondPeekHeight()?.takeIf { it != bottomSheetBehavior.peekHeight }
                        ?.run(bottomSheetBehavior::setPeekHeight)
                }

                BottomSheetBehavior.STATE_HIDDEN -> {
//                    popSelf()
                    callback?.onDismiss()
                }
            }
        }
    }

    private fun requireBottomSheetBehavior(): BottomSheetBehavior<View>? =
        container?.let { BottomSheetBehavior.from(it) }

    private fun makeFragment(context: Context, targetSceneName: String, param: Bundle): Fragment {
        val scene =
            childFragmentManager.fragmentFactory.instantiate(context.classLoader, targetSceneName)
        scene.arguments = param
        return scene
    }

//    override fun isImmersive(): Boolean = true

    private val cornerProvider =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    val view = view ?: return
                    val cornerRadius = ScreenUtil.dip2px(context, 16F).toFloat()
                    outline?.setRoundRect(
                        0,
                        0,
                        view.width,
                        (view.height + cornerRadius).toInt(),
                        cornerRadius
                    )
                }
            }
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }
}