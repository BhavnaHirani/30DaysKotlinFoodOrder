package com.bh.a30dayskotlinfoodorder.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class GPSTracker : Service {
    private var mContext: Context? = null

    //Flag for GPS status
    var isGPSEnabled = false

    //Flag for network status
    var isNetworkEnabled = false

    //Flag for GPS status
    var canGetLocation = false
    private var location: Location? = null
    private var latitude = 0.0
    private var longitude = 0.0

    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null
    var activity: Activity? = null
    private var mLocationListener: LocationListener? = null

    constructor(context: Context?, activity: Activity?, pLocationListener: LocationListener?) {
        mContext = context
        this.activity = activity
        mLocationListener = pLocationListener
        try {
            getLocation()
        } catch (ex: Exception) {
            MyApplication.instance.logStackTrace(ex)
        }
    }

    @SuppressLint("MissingPermission")
    @Throws(Exception::class)
    fun getLocation(): Location? {
        try {
            canGetLocation = false
            locationManager = mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            //Getting GPS status
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            //Getting network status
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                //No network provider is enabled
            } else {
                canGetLocation = true
                if (isNetworkEnabled) {
                    MyApplication.instance.log(TAG, "isNetworkEnabled")
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                        mLocationListener
                    )
                    if (locationManager != null) {
                        location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        mLocationListener!!.onLocationChanged(location)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }
            }

            //If GPS enabled, get latitude/longitude using GPS Services
            if (isGPSEnabled) {
                if (ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity!!,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),50)
                } else {

                    MyApplication.instance.log(TAG, "isGPSEnabled")

                    if (locationManager != null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                            mLocationListener
                        )
                        location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        mLocationListener!!.onLocationChanged(location)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return location
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     */
    fun stopUsingGPS() {
        if (locationManager != null && mLocationListener != null) locationManager!!.removeUpdates(
            mLocationListener
        )
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }
        // return latitude
        return latitude
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }
        return longitude
    }

    /**
     * Function to check GPS/Wi-Fi enabled
     *
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     */
    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings")
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")
        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext!!.startActivity(intent)
        }

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel") { dialog, which ->
            //dialog.cancel();
        }
        //Showing Alert Message
        alertDialog.show()
    }

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    fun isBetterLocation(location: Location?, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null || location == null) {
            //A new location is always better than no location
            return true
        }

        val TIME = 1000 * 60 * 2
        // Check whether the new location fix is newer or older
        val timeDelta = location.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > TIME
        val isSignificantlyOlder = timeDelta < -TIME
        val isNewer = timeDelta > 0

        //If it's been more than two minutes since the current location,use the new location because the user has likely moved
        if (isSignificantlyNewer) {
            return true
            //If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false
        }

        //DISTANCE BASE;
        val meter = 10.0
        if (distance(location, currentBestLocation) >= meter) {
            return true
        }

        //Accurate BASE;
        //Check whether the new location fix is more or less accurate
        val accuracyDelta = (location.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Check if the old and new location are from the same provider
        val isFromSameProvider = isSameProvider(location.provider, currentBestLocation.provider)

        //Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true
        }
        return false
    }

    private fun isSameProvider(
        provider1: String?,
        provider2: String?
    ): Boolean {
        return if (provider1 == null) {
            provider2 == null
        } else provider1 == provider2
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    private fun distance(location1: Location, location2: Location): Double {

        val R = 6371 // Radius of the earth
        val latDistance = Math.toRadians(location2.latitude - location1.latitude)
        val lonDistance = Math.toRadians(location2.longitude - location1.longitude)
        val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + (Math.cos(Math.toRadians(location1.latitude)) * Math.cos(Math.toRadians(location2.latitude))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        var distance = R * c * 1000 // convert to meters
        val height = location1.altitude - location2.altitude
        distance = Math.pow(distance, 2.0) + Math.pow(height, 2.0)
        return Math.sqrt(distance)
    }

    companion object {
        const val TAG = "@GPS"

        //The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 0 // 10 meters

        //The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES: Long = 0 // 30 minute
    }
}