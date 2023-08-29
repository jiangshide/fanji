package com.android.sanskrit.wxapi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fanji.android.util.FJEvent
import com.fanji.android.util.LogUtil
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

class WXEntryActivity : AppCompatActivity(), IWXAPIEventHandler {

    override fun onReq(p0: BaseReq?) {
        LogUtil.e("-------jsd-------wx onReq", p0)
    }

    override fun onResp(resp: BaseResp?) {
        LogUtil.e("-----------jsd----------wx onResp", resp, " | errCode:", resp?.errCode)
        when (resp?.errCode) {
            BaseResp.ErrCode.ERR_OK -> {
                if (resp is SendAuth.Resp) {
                    val code = resp.code
                    LogUtil.e("----jsd----","-----wx------code:"+code)
                    FJEvent.get()
                        .with(LOGIN_WECHAT)
                        .post(code)
                    finish()
                }
            }
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.e("weixin----onCreate------handnlerIntent>>>>>it")
        WXApiManager.handlerIntent(intent, this)
    }
}

const val LOGIN_WECHAT = "loginWeChat"
