package com.fanji.android.ui.anim

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.fanji.android.util.AppUtil

/**
 * created by jiangshide on 2019-09-07.
 * email:18311271399@163.com
 */
object Anim {
    val FAST_OUT_SLOW_IN_INTERPOLATOR: Interpolator = FastOutSlowInInterpolator()
    val FAST_OUT_LINEAR_IN_INTERPOLATOR: Interpolator = FastOutLinearInInterpolator()
    val LINEAR_OUT_SLOW_IN_INTERPOLATOR: Interpolator = LinearOutSlowInInterpolator()
    val DECELERATE_INTERPOLATOR: Interpolator = DecelerateInterpolator()
    val LINEAR_INTERPOLATOR: Interpolator = LinearInterpolator()

    @JvmStatic
    fun shakeView(view: View) {
        val translateAnimation: Animation = TranslateAnimation(-10f, 10f, 0f, 0f)
        translateAnimation.duration = 50 //每次时间
        translateAnimation.repeatCount = 10 //重复次数
        translateAnimation.repeatMode = Animation.REVERSE
        view.startAnimation(translateAnimation)
    }

    fun lerp(startValue: Float, endValue: Float, fraction: Float): Float {
        return startValue + fraction * (endValue - startValue)
    }

    fun lerp(startValue: Int, endValue: Int, fraction: Float): Int {
        return startValue + Math.round(fraction * (endValue - startValue))
    }

    //public static void anim(View view) {
    //  anim(view, R.anim.circulate);
    //}
    @JvmStatic
    fun anim(view: View, anim: Int) {
        val animation = AnimationUtils.loadAnimation(AppUtil.getApplicationContext(), anim)
        val lin = LinearInterpolator() //设置动画匀速运动
        animation.interpolator = lin
        view.startAnimation(animation)
    }

    private val hashMap = HashMap<View, Any>()

    fun scale(view: View?, duration: Long = 800L) {
        ScaleAnimation(
            1f,
            2f,
            1f,
            2f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).run {
            view?.clearAnimation()
            this.duration = duration
            view?.startAnimation(this)
        }
    }

    @JvmOverloads
    fun rotation(view: View?, duration: Long = 500L): ObjectAnimator {
        if (hashMap.containsKey(view)) {
            return (hashMap[view] as ObjectAnimator?)!!
        }
        val objectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0.0f, 360.0f)
        objectAnimator.duration = duration
        objectAnimator.repeatCount = Animation.INFINITE
        objectAnimator.repeatMode = ObjectAnimator.RESTART
        objectAnimator.interpolator = LinearInterpolator()
        hashMap[view!!] = objectAnimator
        return objectAnimator
    }

    internal class AnimationListenerAdapter : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {}
        override fun onAnimationRepeat(animation: Animation) {}
    }
}