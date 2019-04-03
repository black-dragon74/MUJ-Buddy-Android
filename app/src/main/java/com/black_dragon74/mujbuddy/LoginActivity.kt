package com.black_dragon74.mujbuddy

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.black_dragon74.mujbuddy.models.LoginResponseModel
import com.black_dragon74.mujbuddy.utils.*
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    // Global vars
    private val encryptor = Encryptor()
    private var parentLogin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val helper = HelperFunctions(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // If the button state is set to on, change the text on the login button accordingly
        parentLoginSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                loginSubmitBtn.text = "Parent Login"
                this.parentLogin = true
            }
            else {
                loginSubmitBtn.text = "Student Login"
                this.parentLogin = false
            }
            return@setOnCheckedChangeListener
        }

        // If user is logged in and credentials are there, send directly to the main activity
        if (helper.isUserLoggedIn() && helper.getUserCredentials() != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Bind the elements and get the values from there
        val rawUserID = loginUserIDTF.text
        val rawPassword = loginPasswordTF.text

        // Set an onclick listener to the login button
        loginSubmitBtn.setOnClickListener {
            // Hide the keyboard
            helper.endEditing(it)

            // Show a progress bar
            val pd = ProgressDialog(this)
            pd.setMessage("Logging in..")
            pd.setCanceledOnTouchOutside(false)
            pd.show()

            if (rawUserID.isEmpty() || rawPassword.isEmpty()) {
                // Show a snackbar and exit
                pd.dismiss()
                helper.showToast(this, "Empty UserID or Password.")
                return@setOnClickListener
            }

            // Call in the OKHTTP function and send the request
            val client = OkHttpClient()
            val usertype = if (parentLogin) "parent" else "student"
            val request = Request.Builder().url(API_URL + "auth?userid=$rawUserID&usertype=$usertype").build()
            client.newCall(request).enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread{
                        pd.dismiss()
                        helper.showToast(this@LoginActivity, "Failed to send the request.")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val bodyResp = response.body()?.string()

                    // If the login fails, return by showing an error message
                    if (bodyResp != null) {
                        val parsedResponse = bodyResp.replace("\n", "")
                        if (parsedResponse == "false") {
                            runOnUiThread {
                                pd.dismiss()
                                helper.showToast(this@LoginActivity, "Login failed. Please try again.")
                            }
                            return
                        }
                    }

                    // Else, we continue the normal process
                    runOnUiThread{
                        pd.dismiss()

                        // Else, we got the token
                        // Save in the storage
                        val sharedPref = getSharedPreferences(SHARED_PREF, 0)
                        sharedPref.edit().putBoolean(LOGIN_STATE, true).apply()
                        sharedPref.edit().putString(USER_ID, rawUserID.toString()).apply()
                        sharedPref.edit().putString(USER_TYPE, usertype).apply()

                        // Call the new activity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            })
        }
    }
}
