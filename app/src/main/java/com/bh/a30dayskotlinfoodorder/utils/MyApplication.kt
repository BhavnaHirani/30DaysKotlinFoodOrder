package com.bh.a30dayskotlinfoodorder.utils

import android.app.Activity
import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.bh.a30dayskotlinfoodorder.R
import com.google.android.material.snackbar.Snackbar
import java.util.*

lateinit var mToast: Toast

class MyApplication : Application(){

    companion object {
        lateinit var instance: MyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getAppVersionName(): String? {
        return try {
            val packageInfo: PackageInfo = instance.getPackageManager().getPackageInfo(
                instance.getPackageName(), 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("Could not get package name: $e")
        }
    }

    fun log(tag: String?, message: String?) {
        Log.d(tag, message)
    }

    fun logStackTrace(exception: java.lang.Exception) {
        exception.printStackTrace()
    }

    fun showToast(message: String?) {
        try {
            if (mToast != null) {
                mToast!!.cancel()
            }
            mToast = Toast.makeText(
                instance, message, Toast.LENGTH_SHORT)
            mToast.setGravity(Gravity.CENTER, 0, 0)
            mToast.show()
        } catch (ex: java.lang.Exception) {
            Toast.makeText(instance, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun getGUID(): String? {
        return UUID.randomUUID().toString()
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showError(lin_root: LinearLayout, errorMsg: String) {
        val snackbar = Snackbar.make(lin_root, errorMsg, Snackbar.LENGTH_LONG)
        val tv = snackbar.view.findViewById(R.id.snackbar_text) as TextView
        tv.text = errorMsg
        snackbar.show()
    }

    fun showError(lin_root: RelativeLayout, errorMsg: String) {
        val snackbar = Snackbar.make(lin_root, errorMsg, Snackbar.LENGTH_LONG)
        val tv = snackbar.view.findViewById(R.id.snackbar_text) as TextView
        tv.text = errorMsg
        snackbar.show()
    }
}