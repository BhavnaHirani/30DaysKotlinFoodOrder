package com.bh.a30dayskotlinfoodorder.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bh.a30dayskotlinfoodorder.R
import com.bh.a30dayskotlinfoodorder.listener.onPermisionChecked
import com.bh.a30dayskotlinfoodorder.utils.GPSTracker
import com.bh.a30dayskotlinfoodorder.utils.MyApplication
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
abstract class BaseActivity : AppCompatActivity(){

    open var mContext: Context? = null
    private val EMPTY_VALUE = -1
    private var gpsTracker: GPSTracker? = null
    private var location: Location? = null
    private var permisionListetener: onPermisionChecked? = null
    private var isAppPermisssionDone:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        mContext = this
    }

    override fun onResume() {
        super.onResume()

        gpsTracker = GPSTracker(this@BaseActivity, this@BaseActivity, object : LocationListener {

            override fun onLocationChanged(currentlocation: Location) {
                try {
                    location = if (gpsTracker != null && location != null && gpsTracker!!.isBetterLocation(location, currentlocation)) {
                        currentlocation
                    } else {
                        currentlocation
                    }
                } catch (ex: java.lang.Exception) {
                    MyApplication.instance.logStackTrace(ex)
                }
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        })

        if (!gpsTracker!!.canGetLocation()) {
            gpsTracker!!.showSettingsAlert()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (gpsTracker != null) {
            gpsTracker!!.stopUsingGPS()
        }
    }

    fun askForAppPermission(onPermisionChecked: onPermisionChecked) {
        checkRunTimePermission(arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), onPermisionChecked)
    }

    open fun checkRunTimePermission(callingPermission: Array<String>,permisionListetener: onPermisionChecked) {

        this.permisionListetener = permisionListetener

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val list = ArrayList<String>()

            for (i in callingPermission.indices) {

                if (ContextCompat.checkSelfPermission(this,callingPermission[i]) == PackageManager.PERMISSION_DENIED) {
                    list.add(callingPermission[i])
                }
            }

            if (list.isNotEmpty()) {
                var sList: Array<String?>? = arrayOfNulls(list.size)
                sList = list.toArray(sList)
                requestPermissions(sList, 101)
            } else {
                isAppPermisssionDone = true
                permisionListetener.onChecked(isAppPermisssionDone)
            }
        }
        else{
            isAppPermisssionDone = true
            permisionListetener.onChecked(isAppPermisssionDone)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            var f = false
            for (i in grantResults.indices) {
                f = grantResults[i] == PackageManager.PERMISSION_GRANTED
            }
            isAppPermisssionDone = f
            permisionListetener?.onChecked(isAppPermisssionDone)

        } catch (ex: Exception) {
            MyApplication.instance.logStackTrace(ex)
        }
    }

    fun openPageWithFinish(param: Class<*>?) {
        this.startActivity(Intent(this, param))
        finish()
    }
}

