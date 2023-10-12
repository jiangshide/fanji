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
import android.view.View
import com.fanji.android.BuildConfig
import com.fanji.android.HUAEI_ADS_PROTOCOL
import com.fanji.android.R
import com.fanji.android.ui.FJDialog
import com.fanji.android.ui.base.WebActivity
import com.fanji.android.util.LogUtil
import com.fanji.android.util.SPUtil

/**
 * @author: jiangshide
 * @date: 2023/9/8
 * @email: 18311271399@163.com
 * @description:
 */
object CurDialog {

    fun share(context: Context) {
        val list = ArrayList<String>()
        list.add("微信")
        list.add("微信朋友圈")
        list.add("复制链接")
        FJDialog.createList(context, list).setOnItemListener { parent, view, position, id ->
            LogUtil.e("----jsd---", "-----position:", position, " | id:", id)
        }.show()
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