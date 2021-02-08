package com.example.password_manager.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager

class AutofillAlertWindowService: Service() {
    private lateinit var windowManager: WindowManager
    private var floatyView: View? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showAutofillAlertWindow()
    }

    private fun showAutofillAlertWindow(){
        val params: WindowManager.LayoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)

        params.gravity = Gravity.BOTTOM
        params.packageName = packageName

        floatyView = LayoutInflater.from(applicationContext).inflate(com.example.password_manager.R.layout.autofill_successful, null)
        floatyView?.let {
            windowManager.addView(floatyView, params)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        floatyView?.let {
            windowManager.removeView(it)
            floatyView = null
        }
    }
}