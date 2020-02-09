package com.black_dragon74.mujbuddy

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.*
import com.black_dragon74.mujbuddy.utils.CONF_URL
import com.black_dragon74.mujbuddy.utils.LOGIN_URL
import kotlinx.android.synthetic.main.activity_webauth.*
import okhttp3.Cookie

class WebauthActivity : AppCompatActivity() {

    // These need to be declared globally
    var userid: String? = null
    var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webauth)

        // If the intent has userid and password set, update the variables
        val intent = intent
        userid = intent.getStringExtra(LoginActivity.USER_ID)
        password = intent.getStringExtra(LoginActivity.PASSWORD)

        // Now is the time to load the webview
        webauthView.settings.javaScriptEnabled = true
        webauthView.webViewClient = WebAuthDelegate()
        webauthView.loadUrl(LOGIN_URL)
    }

    override fun onBackPressed() {
        // Now is the time to send the broadcast and de-register the receiver
        val cancelled = Intent("com.mujbuddy.AUTH_CANCELLED")
        sendBroadcast(cancelled)

        super.onBackPressed()
    }

    // Coz Kotlin doesn't have the fancy delegation
    private inner class WebAuthDelegate: WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url)
            return true
        }

        override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
            if (url == CONF_URL) {
                val cookie = CookieManager.getInstance().getCookie(url).substringAfter('=').substringBefore(';')

                intent.putExtra("cookie", cookie)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            return super.shouldInterceptRequest(view, url)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            if (url == LOGIN_URL) {
                // Execute the javascrip to fill in the details
                if (userid != null && password != null) {
                    view?.settings?.javaScriptEnabled = true
                    view?.evaluateJavascript("document.getElementById('txtUserid').value = '$userid'", null)
                    view?.evaluateJavascript("document.getElementById('txtpassword').value = '$password'", null)
                }
            }
        }
    }
}
