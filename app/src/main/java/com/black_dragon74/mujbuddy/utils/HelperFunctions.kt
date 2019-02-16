package com.black_dragon74.mujbuddy.utils

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.view.View
import android.widget.Toast
import com.black_dragon74.mujbuddy.LoginActivity
import android.view.inputmethod.InputMethodManager


class HelperFunctions(val context: Context) {
    // The shared pref instance
    private val sharedPref = context.getSharedPreferences(SHARED_PREF, 0)

    //
    //  General helper functions
    //
    // Shows a toast message for a small duaration of time on the provided context
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Hides the keyboard
    fun endEditing(view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //
    //  Login helper functions
    //
    fun isUserLoggedIn(): Boolean {
        return sharedPref.getBoolean(LOGIN_STATE, false)
    }

    fun getToken(): String? {
        return sharedPref.getString(ACCESS_TOKEN, null)
    }

    fun doLogout() {
        sharedPref.edit().clear().apply()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(context, intent, null)
    }

    //
    //  Dashboard related functions
    //
    fun updateDashInDB(data: String) {
        sharedPref.edit().remove(USER_DATA).apply()
        sharedPref.edit().putString(USER_DATA, data).apply()
    }

    fun getDashFromDB(): String? {
        return sharedPref.getString(USER_DATA, null)
    }
}