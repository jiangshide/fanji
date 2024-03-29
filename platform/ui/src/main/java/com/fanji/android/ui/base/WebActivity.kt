package com.fanji.android.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.fanji.android.ui.databinding.WebViewBinding

/**
 * created by jiangshide on 2020/7/28.
 * email:18311271399@163.com
 *
 * WebView复用池+独立进程优化：https://www.heng666.cn/324.html
 */
open class WebActivity : BaseActivity<WebViewBinding>() {

    companion object {
        private var mTitle: String? = null
        private var mUrl: String? = null
        fun openUrl(context: Context, title: String?, url: String?) {
            mTitle = title
            mUrl = url
            context.startActivity(Intent(context, WebActivity::class.java))
        }
    }

    override fun getViewBinding() = WebViewBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.webViewTopBack.setOnClickListener {
            finish()
        }
        binding.webViewTopTitle.text = mTitle
        initWebSet(this, binding.bridgeWebView)
        binding.bridgeWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.webViewProgressBar?.progress = newProgress
                if (newProgress == 100) {
                    binding.webViewProgressBar?.progress = 0
                    binding.webViewProgressBar?.visibility = View.GONE
                } else {
                    binding.webViewProgressBar?.visibility = View.VISIBLE
                }
            }
        }
        if (!TextUtils.isEmpty(binding.bridgeWebView.title)) {
            title = binding.bridgeWebView.title
            binding.webViewTopTitle.text = title
        }
        binding.bridgeWebView.loadUrl(mUrl!!)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebSet(context: Context, webView: WebView) {
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        webView.layoutParams = params
        val webSettings: WebSettings = webView.settings
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.javaScriptEnabled = true
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可

        //支持插件
        //        webSettings.setPluginsEnabled(true);

        //设置自适应屏幕，两者合用
//        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
//        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = false //隐藏原生的缩放控件
        webSettings.textZoom = 110

        //其他细节操作
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT //关闭webview中缓存
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
        webSettings.defaultTextEncodingName = "utf-8" //设置编码格式
        webSettings.domStorageEnabled = true // 开启 DOM storage API 功能
        webSettings.databaseEnabled = true //开启 database storage API 功能
//        webSettings.setAppCacheEnabled(true) //开启 Application Caches 功能
//        webSettings.setAppCachePath(FileUtil.getWebViewDir()) //设置  Application Caches 缓存目录

        //页面白屏问题
        webView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        webView.setBackgroundResource(com.fanji.android.ui.R.color.white)
    }
}