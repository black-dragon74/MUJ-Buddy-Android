package com.black_dragon74.mujbuddy

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.widget.Toast
import com.black_dragon74.mujbuddy.models.LoginResponseModel
import com.black_dragon74.mujbuddy.utils.*
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    companion object {
        val USER_ID= "userid"
        val PASSWORD = "password"
        val REQ_CODE = 9236
    }
    // Global vars
    private lateinit var pd: ProgressDialog
    private var parentLogin: Boolean = false
    private var rawUserID: String? = null
    private  var rawPassword: String? = null

    // Coz we need to do the operations in this contentxt, I've created an anonymous instance of the Receiver
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val helper = HelperFunctions(context!!)
            pd.dismiss()
            helper.showToast(context, "Login cancelled by the user")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val helper = HelperFunctions(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Register the broadcast receiver
        val filter = IntentFilter("com.mujbuddy.AUTH_CANCELLED")
        registerReceiver(receiver, filter)

        // If user is logged in and credentials are there, send directly to the main activity
        if (helper.isUserLoggedIn() && helper.getUserCredentials() != null && !intent.getBooleanExtra("reauth", false)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set an onclick listener to the login button
        loginSubmitBtn.setOnClickListener {
            // Hide the keyboard
            helper.endEditing(it)

            // Assign the values
            rawUserID = loginUserIDTF.text.toString()
            rawPassword = loginPasswordTF.text.toString()

            // Show a progress bar
            pd = ProgressDialog(this, R.style.DarkProgressDialog)
            pd.setMessage("Logging in..")
            pd.setCanceledOnTouchOutside(false)
            pd.show()

            if (rawUserID.isNullOrEmpty() || rawPassword.isNullOrEmpty()) {
                // Show a snackbar and exit
                pd.dismiss()
                helper.showToast(this, "Empty UserID or Password.")
                return@setOnClickListener
            }

            // Now we need to open the webauth activity and also send a few params along
            val authIntent = Intent(this, WebauthActivity::class.java)
            authIntent.putExtra(USER_ID, rawUserID)
            authIntent.putExtra(PASSWORD, rawPassword)
            startActivityForResult(authIntent, REQ_CODE)
        }

        // If the launching intent for this activity has the reauth in it, fill in the details
        val launchingIntent = intent
        if (launchingIntent.getBooleanExtra("reauth", false)) {
            if (helper.isUserLoggedIn()) {
                val currentUser = helper.getUserCredentials()
                loginUserIDTF.setText(currentUser!!.username)
                loginPasswordTF.setText(currentUser!!.usertype)
                helper.showToast(this, "Session expired. Logging in again...")
                loginSubmitBtn.performClick()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val cookie = data?.getStringExtra("cookie");

                if (cookie.isNullOrEmpty()) {
                    // Failed
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Login Failed.")
                    builder.setMessage("The login to portal was successful but auth handshake failed. ")
                    builder.setNegativeButton("Okay") {dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
                else {
                    // Success
                    val sharedPref = getSharedPreferences(SHARED_PREF, 0)
                    sharedPref.edit().putBoolean(LOGIN_STATE, true).apply()
                    sharedPref.edit().putString("reg_no", rawUserID).apply()
                    sharedPref.edit().putString(SESSION_ID, cookie).apply()
                    sharedPref.edit().putString(USER_PASS, rawPassword).apply()

                    // Now is the time to dismiss this activity and present the dashboard
                    pd.dismiss()
                    val dashIntent = Intent(this, MainActivity::class.java)
                    dashIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(dashIntent)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the receiver
        unregisterReceiver(receiver)
    }
}
