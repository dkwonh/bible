package com.example.bible

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class ExitDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.exit_ad_dialog)

        val exitView = findViewById<TextView>(R.id.exit_view)
        val cancelView = findViewById<TextView>(R.id.cancel_view)
        val adView = findViewById<AdView>(R.id.nativeAd)

        val request = AdRequest.Builder().build()
        adView.loadAd(request)

        exitView.setOnClickListener { android.os.Process.killProcess(android.os.Process.myPid()) }
        cancelView.setOnClickListener { dismiss() }


    }
}