package dev.cdrck.mdgtictoe.main
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.RelativeLayout
import android.widget.TextView
import dev.cdrck.mdgtictoe.util.AFUtil
import timber.log.Timber
import java.lang.reflect.InvocationTargetException

class WebActivity : Activity() {
    private var webView: WebView? = null
    private var loadUrl: String? = null
    private var mUploadCallBack: ValueCallback<Uri?>? = null
    private var mUploadCallBackAboveL: ValueCallback<Array<Uri>>? = null
    private val REQUEST_CODE_FILE_CHOOSER = 888
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra("url")
        if (TextUtils.isEmpty(url)) {
            finish()
        }
        loadUrl = url
        val relativeLayout = RelativeLayout(this)
        relativeLayout.setLayoutParams(
            RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        webView = WebView(this)
        setSetting()
        webView!!.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                return try {
                    webView!!.loadUrl(uri.toString())
                    true
                } catch (e: Exception) {
                    true
                }
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                if (TextUtils.equals(failingUrl, loadUrl)) {
                    view.post { finish() }
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                val wgPackage = ("javascript:window.WgPackage = {name:'" + packageName + "', version:'"
                        + getAppVersionName(this@WebActivity) + "'}")
                webView!!.evaluateJavascript(wgPackage) {

                }
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                val wgPackage = ("javascript:window.WgPackage = {name:'" + packageName + "', version:'"
                        + getAppVersionName(this@WebActivity) + "'}")
                webView!!.evaluateJavascript(wgPackage) {

                }
            }
        })
        webView!!.addJavascriptInterface(JsInterface(), "jsBridge")
        webView!!.getSettings().javaScriptEnabled = true
        webView!!.getSettings().javaScriptCanOpenWindowsAutomatically = true
        webView!!.layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        webView!!.loadUrl(loadUrl!!)

        val layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(10, 20, 20, 10)

        relativeLayout.addView(webView)
        setContentView(relativeLayout)
    }

    private fun setSetting() {
        val setting = webView!!.getSettings()
        setting.javaScriptEnabled = true
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

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 16) {
            setting.mediaPlaybackRequiresUserGesture = false
        }
        setting.setSupportZoom(false)
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
        webView!!.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val intent = Intent()
            intent.setAction(Intent.ACTION_VIEW)
            val uri = Uri.parse(url)
            intent.setData(uri)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        webView!!.setWebChromeClient(object : WebChromeClient() {

            fun openFileChooser(uploadMsg: ValueCallback<Uri?>?, acceptType: String?) {
                mUploadCallBack = uploadMsg
                openFileChooseProcess()
            }

            fun openFileChooser(uploadMsgs: ValueCallback<Uri?>?) {
                mUploadCallBack = uploadMsgs
                openFileChooseProcess()
            }

            fun openFileChooser(uploadMsg: ValueCallback<Uri?>?, acceptType: String?, capture: String?) {
                mUploadCallBack = uploadMsg
                openFileChooseProcess()
            }

            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                mUploadCallBackAboveL = filePathCallback
                openFileChooseProcess()
                return true
            }
        })
    }

    fun getAppVersionName(context: Context): String {
        var appVersionName = ""
        try {
            val packageInfo = context.applicationContext.packageManager
                .getPackageInfo(context.packageName, 0)
            appVersionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(ContentValues.TAG, e.cause.toString())
        }
        return appVersionName
    }

    private fun openFileChooseProcess() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.setType("image/*")
        startActivityForResult(Intent.createChooser(i, "Select Picture"), REQUEST_CODE_FILE_CHOOSER)
    }

    inner class JsInterface {
        @JavascriptInterface
        fun postMessage(name: String, data: String) {
            Log.e(ContentValues.TAG, "name = $name    data = $data")
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(data)) {
                return
            }
            AFUtil.event(this@WebActivity, name, data)
        }
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILE_CHOOSER) {
            val result = if (data == null || resultCode != RESULT_OK) null else data.data
            if (result != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (mUploadCallBackAboveL != null) {
                        mUploadCallBackAboveL!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                        mUploadCallBackAboveL = null
                        return
                    }
                } else if (mUploadCallBack != null) {
                    mUploadCallBack!!.onReceiveValue(result)
                    mUploadCallBack = null
                    return
                }
            }
            clearUploadMessage()
            return
        } else if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (webView == null) {
                    return
                }
                webView!!.evaluateJavascript("javascript:window.closeGame()") { }
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
