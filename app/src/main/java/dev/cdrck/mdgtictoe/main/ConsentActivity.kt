package dev.cdrck.mdgtictoe.main

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import dev.cdrck.mdgtictoe.R
import dev.cdrck.mdgtictoe.util.AFUtil
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.InvocationTargetException

class ConsentActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private var loadUrl = "file:///android_asset/userconsent.html"
    private var mUploadCallBack: ValueCallback<Uri?>? = null
    private var mUploadCallBackAboveL: ValueCallback<Array<Uri>>? = null
    private val requestCodeFileChooser = 888

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consent)
        if (TextUtils.isEmpty(loadUrl)) {
            finish()
        }

        webView = WebView(this)
        setSetting()
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                if (TextUtils.equals(failingUrl, loadUrl)) {
                    view.post { finish() }
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                val wgPackage = ("javascript:window.WgPackage = {name:'" + packageName + "', version:'"
                        + getAppVersionName(this@ConsentActivity) + "'}")
                webView.evaluateJavascript(wgPackage) { }
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                val wgPackage = ("javascript:window.WgPackage = {name:'" + packageName + "', version:'"
                        + getAppVersionName(this@ConsentActivity) + "'}")
                webView.evaluateJavascript(wgPackage) { }
            }
        }
        webView.addJavascriptInterface(JsInterface(), "jsBridge")
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true

        webView.loadUrl(loadUrl)
        setContentView(webView)
        AFUtil.init(this)
    }

    fun getAppVersionName(context: Context): String {
        var appVersionName = ""
        try {
            val packageInfo = context.applicationContext.packageManager
                .getPackageInfo(context.packageName, 0)
            appVersionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, e.cause.toString());
        }
        return appVersionName
    }

    private fun setSetting() {
        val setting = webView.getSettings()
        setting.javaScriptCanOpenWindowsAutomatically = true
        setting.setSupportMultipleWindows(true)
        setting.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        setting.domStorageEnabled = true
        setting.cacheMode = WebSettings.LOAD_DEFAULT
        setting.allowContentAccess = true
        setting.databaseEnabled = true
        setting.setGeolocationEnabled(true)
        setting.useWideViewPort = true
        setting.userAgentString = setting.userAgentString.replace("; wv".toRegex(), "")

        val sdkInt = Build.VERSION.SDK_INT
        if (sdkInt > 16) {
            setting.mediaPlaybackRequiresUserGesture = false
        }
        setting.setSupportZoom(false) // 支持缩放
        EventBus.getDefault().post("")
        try {
            val clazz: Class<*> = setting.javaClass
            val method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", Boolean::class.javaPrimitiveType)
            method.invoke(setting, true)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val uri = Uri.parse(url)
            intent.data = uri
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        webView.webChromeClient = object : WebChromeClient() {
            fun openFileChooser(uploadMsg: ValueCallback<Uri?>, acceptType: String) {
                this@ConsentActivity.mUploadCallBack = uploadMsg
                openFileChooseProcess()
            }

            fun openFileChooser(uploadMsgs: ValueCallback<Uri?>) {
                this@ConsentActivity.mUploadCallBack = uploadMsgs
                openFileChooseProcess()
            }

            fun openFileChooser(uploadMsg: ValueCallback<Uri?>, acceptType: String, capture: String) {
                this@ConsentActivity.mUploadCallBack = uploadMsg
                openFileChooseProcess()
            }

            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                this@ConsentActivity.mUploadCallBackAboveL = filePathCallback
                openFileChooseProcess()
                return true
            }
        }
    }

    private fun openFileChooseProcess() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCodeFileChooser)
    }
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    inner class JsInterface {
        @JavascriptInterface
        fun postMessage(name: String, data: String) {
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(data)) {
                return
            }
            AFUtil.event(this@ConsentActivity, name, data)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeFileChooser) {
            val result = if (data == null || resultCode != RESULT_OK) null else data.data
            if (result != null) {
                if (mUploadCallBackAboveL != null) {
                    mUploadCallBackAboveL!!.onReceiveValue(
                        WebChromeClient.FileChooserParams.parseResult(
                            resultCode,
                            data
                        )
                    )
                    mUploadCallBackAboveL = null
                    return
                }
            }
            clearUploadMessage()
            return
        } else if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                webView.evaluateJavascript("javascript:window.closeGame()") { }
            }
        }
    }

    private fun clearUploadMessage() {
        if (mUploadCallBackAboveL != null) {
            mUploadCallBackAboveL!!.onReceiveValue(null)
            mUploadCallBackAboveL = null
        }
        if (mUploadCallBack != null) {
            mUploadCallBack!!.onReceiveValue(null)
            mUploadCallBack = null
        }
    }
}
