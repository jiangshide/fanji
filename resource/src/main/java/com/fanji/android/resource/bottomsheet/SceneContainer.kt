package com.fanji.android.resource.bottomsheet

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import com.fanji.android.ui.R
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * created by jiangshide on 5/22/21.
 * email:18311271399@163.com
 */
interface SceneContainer  {
    /**
     * Scene 回退
     * popScene 后返回前一个 Scene
     */
    fun popBack()

    /**
     * 跳转到新场景
     */
    @Deprecated("use popScene(fromScene: Scene, Class<out Fragment>, param: Bundle) instead", ReplaceWith("popScene"))
    fun popScene(fromScene: Scene, targetSceneName: String, param: Bundle) = Unit

    /**
     * 跳转到新场景
     */
    fun popScene(fromScene: Scene, target: Class<out Fragment>, param: Bundle) = popScene(fromScene, target.name, param)

    /**
     * 获取当前展示的 Scene
     * 因为 SceneContainer 实现里允许添加未实现 Scene 接口的 Fragment
     * 所以 get 当前展示的  Scene 有可能是 nullable 的
     */
    fun getCurrentScene(): Scene? = null

    /**
     *  获取当前 BehaviorState
     *  提供默认实现，使用 getInitBehaviorState
     */
    fun getCurrentBehaviorState(): Int = getInitBehaviorState()

    fun enableFullscreen()

    fun disableFullScreen()

    /**
     * 设置背景颜色 && corner
     *
     * 实际只是给 backgroundDrawable 赋值
     * drawable 会在 onStart 时设置给 background
     */
    @Deprecated("Use setBackgroundColor() instead")
    fun setContainerBackground(enableCorner: Boolean, @ColorRes color: Int)

    /**
     * 设置背景颜色
     * corner 使用 getCornerEnable() 返回值
     *
     * 实际只是给 backgroundDrawable 赋值
     * drawable 会在 onStart 时设置给 background
     */
    fun setBackgroundColor(@ColorInt color:Int) = Unit

    /**
     * 设置背景 Drawable
     * 忽略 corner
     *
     * 实际只是给 backgroundDrawable 赋值
     * drawable 会在 onStart 时设置给 background
     */
    fun setBackgroundDrawable(drawable: Drawable) = Unit

    /**
     * 目前只为 Fragment
     */
    fun setBackgroundElevation(elevation: Float) = Unit

    /**
     * 控制 Container BottomSheetBehavior State
     */
    fun setBehaviorState(@BottomSheetBehavior.State behaviorState:Int) = Unit

    /**
     * @see dismiss
     */
    @Deprecated("Use dismiss() instead")
    fun close()

    /**
     * 关闭弹窗
     */
    fun dismiss(): Unit

    /**
     * 手动调用 [android.transition.TransitionManager.beginDelayedTransition]
     * 面板切换时有些高度变化的动画, 可以通过手动调用这个实现
     */
    fun animateLayoutChanged()

    fun getInitBehaviorState(): Int = BottomSheetBehavior.STATE_COLLAPSED
    fun getInitSceneClass(): String = ""
    fun getInitSceneArguments(): Bundle = Bundle()
    fun getFullscreenEnable(): Boolean = false
    fun getDimBackgroundEnable(): Boolean = true
    fun getIgnoreRecreateEnable(): Boolean = false
    fun getTouchOutsideCancelEnable(): Boolean = true
    fun getOutsideInteractive(): Boolean = !getDimBackgroundEnable()
    fun getInitBackgroundColor(): Int = R.color.black
    fun getCancelable(): Boolean = true
    fun getCornerEnable(): Boolean = false
    fun getDragEnable(): Boolean = false
    fun getShowDrag(): Boolean = false
    fun getInitPeekHeight(): Int? = null
    fun getSecondPeekHeight(): Int? = null

}

interface Scene {
    /**
     * 获取 SharedElement
     * [SceneContainer.popScene] 时通过此方法获取共享元素
     */
    fun getSharedElement(): Set<View>

    /**
     * 接收容器状态变化
     * 只有当前展示的 && 实现了 Scene 接口的 Fragment 才会收到事件
     *
     * 另外可以通过 sceneContainer.getCurrentBehaviorState() 获取状态
     */
    fun onContainerBehaviorStateChange(bottomSheet: View, @BottomSheetBehavior.State state: Int) = Unit
}