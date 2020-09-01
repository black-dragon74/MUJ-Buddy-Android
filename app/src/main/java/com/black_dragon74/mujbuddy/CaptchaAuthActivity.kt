package com.black_dragon74.mujbuddy

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.black_dragon74.mujbuddy.models.CaptchaModel
import com.black_dragon74.mujbuddy.models.CaptchaResponseModel
import com.black_dragon74.mujbuddy.utils.API_URL
import com.black_dragon74.mujbuddy.utils.HelperFunctions
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_captcha_auth.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.net.URLEncoder

class CaptchaAuthActivity : AppCompatActivity() {
    // Vars to be used throughout this class
    private var helperFunctions: HelperFunctions? = null
    private lateinit var pd: ProgressDialog
    private var userID: String? = null
    private var password: String? = null
    private var sessionID: String? = null

    // Override onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_captcha_auth)

        // Check if the userID and Password is supplied in the intent
        if (intent.getStringExtra(LoginActivity.USER_ID).isNullOrEmpty() || intent.getStringExtra(LoginActivity.PASSWORD).isNullOrEmpty()) {
            val filter = Intent("com.mujbuddy.AUTH_CANCELLED")
            sendBroadcast(filter)
            finish()
        }
        else {
            userID = intent.getStringExtra(LoginActivity.USER_ID)
            password = intent.getStringExtra(LoginActivity.PASSWORD)
        }

        // First and foremost, init class vars
        helperFunctions = HelperFunctions(this)
        pd = ProgressDialog(this, R.style.DarkProgressDialog)
        pd.setCanceledOnTouchOutside(false)

        // Setup the button listeners
        captchaRefresh.setOnClickListener {
            fetchAndLoadCaptcha()
        }

        captchaSubmit.setOnClickListener {
            val rawCaptcha = captchaTF.text.toString()
            if (rawCaptcha.isEmpty()) {
                helperFunctions?.showToast(this, "Please fill the captcha first")
                return@setOnClickListener
            }

            handleCaptchaAuth(sessionID ?: "null", userID ?: "null", password ?: "null", rawCaptcha)
        }

        // Now we need to fetch and load the captcha
        fetchAndLoadCaptcha()
    }

    override fun onBackPressed() {
        val intent = Intent("com.mujbuddy.AUTH_CANCELLED")
        sendBroadcast(intent)

        super.onBackPressed()
    }

    // Gets and sets the captcha image from the server
    private fun fetchAndLoadCaptcha() {
        pd.setMessage("Loading captcha")
        pd.show()

        // Init GSON and OkHTTP
        val client = OkHttpClient()
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

        // Create and enqueue the request
        val request = Request.Builder().url("${API_URL}captcha").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MUJ_BUDDY", "Failed to get the captcha response")
                runOnUiThread {
                    pd.dismiss()
                    helperFunctions?.showToast(this@CaptchaAuthActivity, "Failed to get captcha from the server")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body?.string()
                try {
                    val parsed: CaptchaModel = gson.fromJson(respBody, CaptchaModel::class.java)

                    // Decode the base64 to image
                    val imageBytes = Base64.decode(parsed.encodedImage, Base64.DEFAULT)
                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    // Set the sessionID to the current one
                    sessionID = parsed.sessionid

                    // Set the image bitmap to the decoded one
                    runOnUiThread {
                        pd.dismiss()
                        captchaImageView.setImageBitmap(decodedImage)
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        pd.dismiss()
                        helperFunctions?.showToast(this@CaptchaAuthActivity, "Failed to parse the API response")
                    }
                }
            }
        })
    }

    // Handles the auth handshake
    private fun handleCaptchaAuth(sessionID: String, username: String, password: String, captcha: String) {
        // Init Gson and OkHTTP
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        val client = OkHttpClient()

        // Form the URL
        val authURL = "${API_URL}captcha_auth?sessionid=${sessionID}&username=${username}&password=${URLEncoder.encode(password)}&captcha=${captcha}"
        Log.e("MUJ_BUDDY", authURL)

        // Spin the progress bar
        pd.setMessage("Logging in...")
        pd.show()

        // Send the request to the server and wait for the response
        val authRequest = Request.Builder().url(authURL).build()
        client.newCall(authRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MUJ_BUDDY", "Failed to send the auth request")
                pd.dismiss()
                helperFunctions?.showToast(this@CaptchaAuthActivity, "Failed to send the request to the API")
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body?.string()
                try {
                    val parsed = gson.fromJson(respBody, CaptchaResponseModel::class.java)

                    // Now if the captcha failed, try again
                    if (parsed.captchaFailed) {
                        runOnUiThread {
                            pd.dismiss()
                            helperFunctions?.showToast(this@CaptchaAuthActivity, "Invalid captcha, try again.")
                            captchaTF.text = null
                            captchaTF.requestFocus()
                        }
                        return
                    }

                    // If the credentials failed, well,
                    if (parsed.credentialsError) {
                        runOnUiThread {
                            pd.dismiss()
                        }
                        Log.e("MUJ_BUDDY", respBody ?: "")
                        setResult(5644, intent)
                        finish()
                    }

                    // Else, horray!
                    if (parsed.loginSucceeded) {
                        runOnUiThread {
                            pd.dismiss()
                        }
                        intent.putExtra("cookie", parsed.sessionid)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        pd.dismiss()
                        helperFunctions?.showToast(this@CaptchaAuthActivity, "Unable to parsed the API response")
                    }
                    Log.e("MUJ_BUDDY", "Unable to parsed the API response")
                }
            }
        })
    }
}