package dev.cdrck.mdgtictoe.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com. android.volley.toolbox.Volley
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import dev.cdrck.mdgtictoe.main.MainActivity
import dev.cdrck.mdgtictoe.main.WebActivity
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

object AFUtil {
    private const val tag = "AppsFlyerLibUtil"
    private const val afID = "2jAVWqgmQoeQmHCJyVUsRh"
    @JvmStatic
    fun init(context: Context) {
        AppsFlyerLib.getInstance().start(context, afID, object : AppsFlyerRequestListener {
            override fun onSuccess() {
                Timber.tag(tag).e("Launch sent successfully, got 200 response code from server")
            }

            override fun onError(i: Int, s: String) {
                Timber.tag(tag).e("Launch failed to be sent:\nError code: $i\nError description: $s")
            }
        })
        AppsFlyerLib.getInstance().setDebugLog(true)
    }

    @JvmStatic
    fun event(context: Activity, name: String, data: String) {
        val eventValue = mutableMapOf<String, Any>()

        when (name) {
            "UserConsent" -> handleUserConsentEvent(context, data)
            else -> eventValue[name] = data
        }

        AppsFlyerLib.getInstance().logEvent(context, name, eventValue)
        Toast.makeText(context, name, Toast.LENGTH_SHORT).show()
    }

    private fun handleUserConsentEvent(context: Activity, data: String) {
        if (data == "Accepted") {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            context.finish()
        } else {
            context.finishAffinity()
        }
    }
}

