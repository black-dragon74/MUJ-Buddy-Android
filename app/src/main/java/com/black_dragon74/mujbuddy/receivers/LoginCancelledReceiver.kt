package com.black_dragon74.mujbuddy.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.black_dragon74.mujbuddy.interfaces.LoginCancelledInterface

class LoginCancelledReceiver : BroadcastReceiver() {

    var delegate: LoginCancelledInterface? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        delegate?.onLoginCancelled(context!!)
    }
}