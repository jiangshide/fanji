package com.fanji.android.dialog

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import com.fanji.android.BuildConfig
import com.fanji.android.HUAEI_ADS_PROTOCOL
import com.fanji.android.R
import com.fanji.android.permission.FJPermission
import com.fanji.android.permission.OnPermissionCallback
import com.fanji.android.ui.FJButton
import com.fanji.android.ui.FJDialog
import com.fanji.android.ui.base.WebActivity
import com.fanji.android.util.SPUtil

/**
 * @author: jiangshide
 * @date: 2023/9/8
 * @email: 18311271399@163.com
 * @description:
 */
object CurDialog : OnPermissionCallback {

    fun follow(context: Context) {
        FJDialog.create(context).setContent("确定取消关注？").setLeftTxt("取消")
            .setCancelListener { }.setRightTxt("不再关注").setListener { _, _ ->
            }.show()
    }

    fun share(context: Context) {
        FJDialog.createView(context, R.layout.dialog_share) {
            val close = it.findViewById<ImageView>(R.id.close)
            close.setOnClickListener {
                FJDialog.cancelDialog()
            }
            val weixin = it.findViewById<ImageView>(R.id.weixin)
            val friendCircle = it.findViewById<ImageView>(R.id.friendCircle)
            val qq = it.findViewById<ImageView>(R.id.qq)
            val weibo = it.findViewById<ImageView>(R.id.weibo)
            val cancelShare = it.findViewById<FJButton>(R.id.cancelShare)
            cancelShare.setOnClickListener {
                FJDialog.cancelDialog()
            }

        }.setGravity(Gravity.BOTTOM).show()
    }

    fun permissions(context: Context) {
        FJDialog.createView(context, R.layout.dialog_permissions) {
            val camera = it.findViewById<FJButton>(R.id.camera)
            camera.setOnClickListener {

            }
            val audio = it.findViewById<FJButton>(R.id.audio)
            audio.setOnClickListener {

            }
            val album = it.findViewById<FJButton>(R.id.album)
            album.setOnClickListener {

            }
        }.show()
    }

    fun permission(context: Context, permission: String) {
        FJPermission.with(context).permission(arrayListOf(permission)).request(this)
    }

    override fun onGranted(permissions: List<String?>?, all: Boolean) {

    }

    fun protocolDialog(context: Context) {
        FJDialog.cancelDialog()
        val huaweiAdsProtocol = context.getString(R.string.summary_of_agreement)
        val dialog = FJDialog.create(context)
//        dialog?.setAnim(R.anim.alpha_to_one)
        dialog?.setTitles("用户协议提示!")?.setContent(huaweiAdsProtocol)?.setLeftTxt("不同意")
            ?.setRightTxt(
                "同意"
            )?.setAnim(com.fanji.android.ui.R.anim.alpha_to_one)
            ?.setListener { isCancel, editMessage ->
                if (isCancel) {
//                context.finish()
                } else {
                    SPUtil.putBoolean(HUAEI_ADS_PROTOCOL, true)
                }
                FJDialog.cancelDialog()
            }?.setReturn(false)?.setOutsideClose(false)?.show()

        val textView = dialog.mContentView
        textView.movementMethod = ScrollingMovementMethod
            .getInstance()
        val spanPrivacyInfoText = SpannableStringBuilder(huaweiAdsProtocol)

        val adsAndPrivacyTouchHere: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                WebActivity.openUrl(
                    context,
                    BuildConfig.PRIVACY_AGREEMENT,
                    context.getString(R.string.user_protocol)
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ds.linkColor
                ds.isUnderlineText = false
            }
        }
        val personalizedAdsTouchHere: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                WebActivity.openUrl(
                    context,
                    BuildConfig.USE_AGREEMENT,
                    context.getString(R.string.protect_protocol)
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ds.linkColor
                ds.isUnderlineText = false
            }
        }
        val privacyStart = huaweiAdsProtocol?.indexOf("《")
        val privacyEnd = huaweiAdsProtocol?.indexOf("》")?.plus(1)
        val colorPrivacy = ForegroundColorSpan(Color.parseColor("#1296db"))
        val colorPersonalize = ForegroundColorSpan(Color.parseColor("#1296db"))
        spanPrivacyInfoText.setSpan(
            adsAndPrivacyTouchHere, privacyStart!!, privacyEnd!!,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanPrivacyInfoText.setSpan(
            colorPrivacy, privacyStart, privacyEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
//        val personalizedStart = huaweiAdsProtocol?.lastIndexOf("、")-1
//        val personalizedEnd = huaweiAdsProtocol?.length

        val personalizedStart = huaweiAdsProtocol?.indexOf("《", privacyStart + 1)
        val personalizedEnd = huaweiAdsProtocol?.indexOf("》", privacyEnd + 1)!! + 1

        spanPrivacyInfoText.setSpan(
            personalizedAdsTouchHere, personalizedStart!!, personalizedEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanPrivacyInfoText.setSpan(
            colorPersonalize, personalizedStart, personalizedEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spanPrivacyInfoText
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}