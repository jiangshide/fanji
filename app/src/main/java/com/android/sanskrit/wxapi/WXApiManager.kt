package com.android.sanskrit.wxapi

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.fanji.android.resource.BuildConfig
import com.fanji.android.resource.Resource
import com.fanji.android.resource.vm.user.data.Order
import com.fanji.android.util.ImgUtil
import com.fanji.android.util.LogUtil
//import com.android.resource.BuildConfig
//import com.android.resource.Resource
//import com.android.resource.vm.user.data.Order
//import com.android.utils.ImgUtil
//import com.android.utils.LogUtil
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory


class WXApiManager {

    companion object {
        private var api: IWXAPI? = null

        private const val mTargetScene = Req.WXSceneTimeline
        private const val THUMB_SIZE = 150

        fun regToWX(context: Context) {
            if (api == null) {
                api = WXAPIFactory.createWXAPI(context, BuildConfig.WECHAT_APPID, true)
            }
            api!!.registerApp(BuildConfig.WECHAT_APPID)
        }

        fun sendLoginRequest() {
            // send oauth request
            var req = SendAuth.Req()
            req.scope = "snsapi_userinfo"
            req.state = "sanskrit_login"
            val reqRet = api!!.sendReq(req)
            LogUtil.e("sendLoginRequest------>>>>>req:",req," | reqRet:"+reqRet)
        }

        fun isWxInstall(): Boolean {
            return api!!.isWXAppInstalled
        }

        fun handlerIntent(
            intent: Intent,
            iwxapiEventHandler: IWXAPIEventHandler
        ) {
            api?.handleIntent(intent, iwxapiEventHandler)
            LogUtil.e("handlerIntent------>>>>>intent:",intent)
        }

        fun pay(order: Order) {
            val payReq = PayReq()
            payReq.appId = order.appid
            payReq.partnerId = order.mchid
            payReq.prepayId = order.prepayId
            payReq.packageValue = order.packageValue
            payReq.nonceStr = order.nonceStr
            payReq.timeStamp = "${order.date}"
            payReq.sign = order.sign
            val result = api?.sendReq(payReq)
        }

        fun shareTxt(
            title: String? = "",
            des: String? = "",
            str: String? = "",
            scene: Int = mTargetScene
        ) {
            val wxTextObject = WXTextObject()
            wxTextObject.text = str
            val msg = WXMediaMessage()
            msg.mediaObject = wxTextObject
            msg.title = title
            msg.description = des
            msg.mediaTagName = "the jankey!"
            val req = Req()
            req.transaction = buildTransaction("text")
            req.message = msg
            req.scene = mTargetScene
            req.userOpenId = "${Resource.uid}"
            api?.sendReq(req)
        }

        fun shareImg(
            title: String? = "",
            des: String? = "",
            bitmap: Bitmap,
            scene: Int = mTargetScene
        ) {
            val imgObj = WXImageObject(bitmap)
            val msg = WXMediaMessage()
            msg.mediaObject = imgObj
            val thumbBmp = Bitmap.createScaledBitmap(
                bitmap, THUMB_SIZE, THUMB_SIZE, true
            )
//            bitmap.recycle()
            msg.title = "the test $title"
            msg.description = "the des $des"
            msg.thumbData = ImgUtil.bmpToByteArray(thumbBmp, true)

            val req = Req()
            req.transaction = buildTransaction("img")
            req.message = msg
            req.scene = scene
//            req.userOpenId = "${Resource.uid}"
            api?.sendReq(req)
        }

        fun shareAudio(
            title: String? = "",
            des: String? = "",
            thumbData: Bitmap? = null,
            url: String? = "",
            scene: Int = mTargetScene
        ) {
            val musicObject = WXMusicObject()
            musicObject.musicUrl = url
            val msg = WXMediaMessage()
            msg.mediaObject = musicObject
            msg.title = title
            msg.description = des
//            val bmp = Bitmap.createScaledBitmap(thumbData!!, 80, 80, true)
            val thumbBmp = Bitmap.createScaledBitmap(
                thumbData!!, THUMB_SIZE, THUMB_SIZE, true
            )
            msg.thumbData = ImgUtil.bmpToByteArray(thumbBmp, true)

            val req = SendMessageToWX.Req()
            req.transaction = buildTransaction("music")
            req.message = msg
            req.scene = scene
            req.userOpenId = "$Resource.uid"
            api?.sendReq(req)
        }

        fun shareVideo(
            title: String? = "",
            des: String? = "",
            thumbData: Bitmap? = null,
            url: String? = "",
            scene: Int = mTargetScene
        ) {
            val videoObject = WXVideoObject()
            videoObject.videoUrl = url
            videoObject.videoLowBandUrl = videoObject.videoUrl
            val msg = WXMediaMessage(videoObject)
            msg.title = title
            msg.description = des
//            val bmp = Bitmap.createScaledBitmap(thumbData!!, 80, 80, true)
            val thumbBmp = Bitmap.createScaledBitmap(
                thumbData!!, THUMB_SIZE, THUMB_SIZE, true
            )
            msg.thumbData = ImgUtil.bmpToByteArray(thumbBmp, true)

            val req = SendMessageToWX.Req()
            req.transaction = buildTransaction("video.json")
            req.message = msg
            req.scene = scene
            req.userOpenId = "${Resource.uid}"
            api?.sendReq(req)
        }

        private fun buildTransaction(type: String?): String? {
            return if (type == null) System.currentTimeMillis()
                .toString() else type + System.currentTimeMillis()
        }
    }

}