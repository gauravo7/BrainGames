package com.o7solutions.braingames.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

open class NetworkChangeReceiver : BroadcastReceiver() {

    companion object {
        var networkStateListener: NetworkStateListener? = null
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            if (isNetworkAvailable(context)) {
                networkStateListener?.onNetworkAvailable()
                NetworkDialogHelper.dismissDialog()
            } else {

                networkStateListener?.onNetworkLost()
                NetworkDialogHelper.showNoInternetDialog(context)
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    interface NetworkStateListener {
        fun onNetworkAvailable()
        fun onNetworkLost()
    }


}