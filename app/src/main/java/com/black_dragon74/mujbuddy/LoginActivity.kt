package com.black_dragon74.mujbuddy

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.black_dragon74.mujbuddy.models.LoginResponseModel
import com.black_dragon74.mujbuddy.utils.*
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    // Global vars
    private val encryptor = Encryptor()

    override fun onCreate(savedInstanceState: Bundle?) {
        val helper = HelperFunctions(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // If user is logged in and token is there, send directly to the main activity
        if (helper.isUserLoggedIn() && helper.getToken() != null) {
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

            // Else we will now encrypt the user ID and password and get the token from the API
            val eUID = encryptor.encrypt(rawUserID.toString())
            val ePass = encryptor.encrypt(rawPassword.toString())

            // Call in the OKHTTP function and send the request
            val client = OkHttpClient()
            val request = Request.Builder().url("https://dragon.strangebits.co.in/auth?userid=$eUID&password=$ePass").build()
            client.newCall(request).enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread{
                        pd.dismiss()
                        helper.showToast(this@LoginActivity, "Failed to send the request.")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val bodyResp = response.body()?.string()
                    runOnUiThread{
                        pd.dismiss()
                        val gson = GsonBuilder().create()
                        val d = gson.fromJson(bodyResp, LoginResponseModel::class.java)

                        // If there is an error in the response, exit
                        if (d.error != null) {
                            helper.showToast(this@LoginActivity, d.error)
                            return@runOnUiThread
                        }

                        // Else, we got the token
                        // Save in the storage
                        val sharedPref = getSharedPreferences(SHARED_PREF, 0)
                        sharedPref.edit().putBoolean(LOGIN_STATE, true).apply()
                        sharedPref.edit().putString(ACCESS_TOKEN, d.token).apply()

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
