package com.black_dragon74.mujbuddy.fragments

import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.black_dragon74.mujbuddy.MessMenuActivity
import com.black_dragon74.mujbuddy.R
import com.black_dragon74.mujbuddy.adapters.MessMenuAdapter
import com.black_dragon74.mujbuddy.interfaces.MessMenuInterface
import com.black_dragon74.mujbuddy.receivers.MessMenuReceiver


class BreakfastFragment : Fragment(), MessMenuInterface {

    private val receiver = MessMenuReceiver()

    private var items: View? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            receiver.delegate = this

            val filter = IntentFilter("com.mujbuddy.MESS_MENU_FETCHED")
            context?.registerReceiver(receiver, filter)
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            // Inflate the layout for this fragment
            items = inflater.inflate(R.layout.fragment_breakfast, container, false)

            getMenuDetails(items!!)

            return items
        }

        private fun getMenuDetails(items: View) {
        val menu = items.findViewById<RecyclerView>(R.id.breakfast_recycler_view)
        menu.layoutManager = LinearLayoutManager(context)
        val data = MessMenuActivity.MessMenuData?.breakfast ?: arrayOf("Breakfast menu not available")
        menu.adapter = MessMenuAdapter(data)
    }

    override fun onDestroy() {
        super.onDestroy()

        context?.unregisterReceiver(receiver)
    }

    override fun onMessMenuFetched() {
        getMenuDetails(items!!)
    }
}
