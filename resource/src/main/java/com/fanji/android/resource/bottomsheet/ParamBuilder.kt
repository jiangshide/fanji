package com.fanji.android.resource.bottomsheet

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import com.fanji.android.ui.R
import com.google.android.material.bottomsheet.BottomSheetBehavior


internal const val ENABLE_DRAG = "ENABLE_DRAG"
internal const val SHOW_DRAG = "SHOW_DRAG"
internal const val ENABLE_CORNER = "ENABLE_CORNER"
internal const val ENABLE_FULL_SCREEN = "ENABLE_FULL_SCREEN"
internal const val ENABLE_DIM_BACKGROUND = "ENABLE_TRANSPARENT_BG"

internal const val INIT_SCENE_NAME = "INIT_SCENE_NAME"
internal const val INIT_SCENE_ARGUMENTS = "INIT_SCENE_ARGUMENTS"
internal const val INIT_BACKGROUND_COLOR = "INIT_BACKGROUND_COLOR"
internal const val INIT_BEHAVIOR_STATE = "INIT_BEHAVIOR_STATE"

internal const val INIT_PEEK_HEIGHT = "INIT_PEEK_HEIGHT"
internal const val SECOND_PEEK_HEIGHT = "SECOND_PEEK_HEIGHT"

internal const val SET_CANCELABLE = "IS_CANCELABLE"
internal const val SET_TOUCH_OUTSIDE_CANCEL = "SET_TOUCH_OUTSIDE_CANCEL"
internal const val SET_TOUCH_OUTSIDE_INTERACTIVE = "SET_TOUCH_OUTSIDE_INTERACTIVE"

internal const val SET_IGNORE_RECREATE = "SET_IGNORE_RECREATE"
internal const val ENABLE_ZH_BOTTOM_SHEET_FRAGMENT_PHANTOM = "ENABLE_ZH_BOTTOM_SHEET_FRAGMENT_PHANTOM"

/**
 * @param initFragment 初始化 Fragment class name
 */
class ParamBuilder(private val initFragment: Class<out Fragment>) {
    private var enableCorner: Boolean = false
    private var enableDrag: Boolean = false
    private var enableFullScreen: Boolean = false
    private var enableDimBackground: Boolean = true
    private var initBackgroundColor: Int = R.color.black
    private var initPeekHeight: Int = 0
    private var secondPeekHeight: Int = 0
    private var cancelable: Boolean = true
    private var touchOutsideCancel: Boolean = false
    private var initFragmentArguments: Bundle = Bundle()
    private var ignoreRecreate: Boolean = false
    private var initBehaviorState: Int = if (enableFullScreen) {
        BottomSheetBehavior.STATE_EXPANDED
    } else {
        BottomSheetBehavior.STATE_COLLAPSED
    }
    private var showDrag: Boolean = enableDrag
    private var enableFragmentPhantom: Boolean = false
    private var outsideInteractive: Boolean? = null

    @Deprecated("ParamBuilder 构造函数方式不便于维护，已经弃用，请使用 Builder 方式构建参数", ReplaceWith("ParamBuilder(initFragment)"))
    constructor(
            initFragment: Class<out Fragment>,
            enableCorner: Boolean = false,
            enableDrag: Boolean = false,
            enableFullScreen: Boolean = false,
            enableDimBackground: Boolean = true,
            @ColorRes initBackgroundColor: Int = R.color.black,
            initPeekHeight: Int = 0,
            secondPeekHeight: Int = 0,
            cancelable: Boolean = true,
            touchOutsideCancel: Boolean = false,
            initFragmentArguments: Bundle = Bundle(),
            ignoreRecreate: Boolean = false,
            initBehaviorState: Int = if (enableFullScreen) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            },
            showDrag: Boolean = enableDrag
    ) : this(initFragment) {
        this.enableCorner = enableCorner
        this.enableDrag = enableDrag
        this.enableFullScreen = enableFullScreen
        this.enableDimBackground = enableDimBackground
        this.initBackgroundColor = initBackgroundColor
        this.initPeekHeight = initPeekHeight
        this.secondPeekHeight = secondPeekHeight
        this.cancelable = cancelable
        this.touchOutsideCancel = touchOutsideCancel
        this.initFragmentArguments = initFragmentArguments
        this.ignoreRecreate = ignoreRecreate
        this.initBehaviorState = initBehaviorState
        this.showDrag = showDrag
    }

    /**
     * https://jakewharton.com/public-api-challenges-in-kotlin/
     * https://jakewharton.com/public-api-challenges-in-kotlin/#binary-compatibility
     * S.H.I.T
     */
    @Deprecated("ParamBuilder 构造函数方式不便于维护，已经弃用，请使用 Builder 方式构建参数", ReplaceWith("ParamBuilder(initFragment)"))
    constructor(
            initFragment: Class<out Fragment>,
            enableCorner: Boolean = false,
            enableDrag: Boolean = false,
            enableFullScreen: Boolean = false,
            enableDimBackground: Boolean = true,
            @ColorRes initBackgroundColor: Int = R.color.black,
            initPeekHeight: Int = 0,
            secondPeekHeight: Int = 0,
            cancelable: Boolean = true,
            touchOutsideCancel: Boolean = false,
            initFragmentArguments: Bundle = Bundle(),
            ignoreRecreate: Boolean = false,
            initBehaviorState: Int = if (enableFullScreen) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
    ) : this(
            initFragment,
            enableCorner,
            enableDrag,
            enableFullScreen,
            enableDimBackground,
            initBackgroundColor,
            initPeekHeight,
            secondPeekHeight,
            cancelable,
            touchOutsideCancel,
            initFragmentArguments,
            ignoreRecreate,
            initBehaviorState,
            enableFullScreen
    )

    /**
     * @param initPeekHeight 初始化 BottomSheet Peek 高度
     */
    fun setInitPeekHeight(initPeekHeight: Int) = this.apply {
        this.initPeekHeight = initPeekHeight
    }

    /**
     * @param secondPeekHeight 拖动后，更新 peek height
     */
    fun setSecondPeekHeight(secondPeekHeight: Int) = this.apply {
        this.secondPeekHeight = secondPeekHeight
    }

    /**
     * @param initBehaviorState 面板启动时状态
     *
     * [BottomSheetBehavior.STATE_EXPANDED]
     * [BottomSheetBehavior.STATE_COLLAPSED]
     * ...
     */
    fun setInitBehaviorState(initBehaviorState: Int) = this.apply {
        this.initBehaviorState = initBehaviorState
    }

    /**
     * @param initBackgroundColor BottomSheet 背景色
     */
    fun setInitBackgroundColor(@ColorRes initBackgroundColor: Int) = this.apply {
        this.initBackgroundColor = initBackgroundColor
    }

    /**
     * @param enableDrag 启用拖拽
     */
    fun setEnableDrag(enableDrag: Boolean) = this.apply {
        this.enableDrag = enableDrag
    }

    /**
     * @param showDrag  显示顶部拖拽条
     */
    fun setShowDrag(showDrag: Boolean) = this.apply {
        this.showDrag = showDrag
    }

    /**
     * @param cancelable 面板可以被拖拽关闭
     */
    fun setCancelable(cancelable: Boolean) = this.apply {
        this.cancelable = cancelable
    }

    /**
     * @param ignoreRecreate 重建时关闭面板
     */
    fun setIgnoreRecreate(ignoreRecreate: Boolean) = this.apply {
        this.ignoreRecreate = ignoreRecreate
    }

    /**
     * @param enableCorner  top 圆角
     */
    fun setEnableCorner(enableCorner: Boolean) = this.apply {
        this.enableCorner = enableCorner
    }

    /**
     * @param enableFullScreen 全屏
     */
    fun setEnableFullScreen(enableFullScreen: Boolean) = this.apply {
        this.enableFullScreen = enableFullScreen
    }

    /**
     * @param enableDimBackground 启用背景色 默认 true
     */
    fun setEnableDimBackground(enableDimBackground: Boolean) = this.apply {
        this.enableDimBackground = enableDimBackground
    }

    /**
     * @param touchOutsideCancel 点击面板外部区域关闭面板
     */
    fun setTouchOutsideCancel(touchOutsideCancel: Boolean) = this.apply {
        this.touchOutsideCancel = touchOutsideCancel
    }

    /**
     * @param initFragmentArguments 初始化 Fragment bundle 参数
     */
    fun setInitFragmentArguments(initFragmentArguments: Bundle) = this.apply {
        this.initFragmentArguments = initFragmentArguments
    }

    /**
     * 用于忽略 ZhBottomSheetFragment 的 ZA PageShow
     *
     * 仅支持 [ZhBottomSheetFragment]
     */
    fun setEnableZhBottomSheetFragmentPhantom(enablePhantom: Boolean) = this.apply {
        this.enableFragmentPhantom = enablePhantom
    }

    /**
     * 能否与面板之下 UI 交互
     *
     * 不设置的话跟随 enableDimBackground
     * [setEnableDimBackground] == true 不可交互
     * [setEnableDimBackground] == false 可交互
     */
    fun setOutsideInteractive(boolean: Boolean) = this.apply {
        this.outsideInteractive = boolean
    }

    fun buildBundle(): Bundle = Bundle().apply {
        putInt(INIT_PEEK_HEIGHT, initPeekHeight)
        putInt(SECOND_PEEK_HEIGHT, secondPeekHeight)
        putInt(INIT_BEHAVIOR_STATE, initBehaviorState)
        putInt(INIT_BACKGROUND_COLOR, initBackgroundColor)
        putBoolean(ENABLE_DRAG, enableDrag)
        putBoolean(SHOW_DRAG, showDrag)
        putBoolean(SET_CANCELABLE, cancelable)
        putBoolean(SET_IGNORE_RECREATE, ignoreRecreate)
        putBoolean(ENABLE_CORNER, enableCorner)
        putBoolean(ENABLE_FULL_SCREEN, enableFullScreen)
        putBoolean(ENABLE_DIM_BACKGROUND, enableDimBackground)
        putBoolean(SET_TOUCH_OUTSIDE_CANCEL, touchOutsideCancel)
        putString(INIT_SCENE_NAME, initFragment.name)
        putBundle(INIT_SCENE_ARGUMENTS, initFragmentArguments)
        putBoolean(ENABLE_ZH_BOTTOM_SHEET_FRAGMENT_PHANTOM, enableFragmentPhantom)
        putBoolean(SET_TOUCH_OUTSIDE_INTERACTIVE, outsideInteractive ?: !enableDimBackground)
    }
}
