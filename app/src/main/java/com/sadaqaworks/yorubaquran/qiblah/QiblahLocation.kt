package com.sadaqaworks.yorubaquran.qiblah

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.sadaqaworks.yorubaquran.qiblah.presentation.QiblahCordinate
import com.sadaqaworks.yorubaquran.util.checkPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.tasks.await

import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class QiblahLocation(
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : QuranLocation(), SensorEventListener {

    private var sensor: Sensor? = null
    private var userLocation: Location? = null


    private lateinit var sensorManager: SensorManager


    private fun locationIsEnabled(): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private val hasCompassSensor: Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)

    @SuppressLint("MissingPermission")
    override fun startListening() {


        val hasCompassSensor: Boolean =
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)

        if (!hasCompassSensor) {
            throw DeviceNotSupported()
        }
        if (!locationIsEnabled()) {
            throw LocationNotEnabled()
        }
        val hasPermission =
            context.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    context.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)


        if (!hasPermission) {

            throw PermissionNotGranted()
        }
        if (!this::sensorManager.isInitialized && sensor == null) {
            sensorManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.getSystemService(SensorManager::class.java) as SensorManager
            } else {
                context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            }
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)

        }
        CoroutineScope(Dispatchers.IO).launch {
            val location = fusedLocationProviderClient.lastLocation.await()
            if (location != null) {
                userLocation = location
                sensorManager.registerListener(
                    this@QiblahLocation,
                    sensor,
                    SensorManager.SENSOR_DELAY_GAME
                )

            } else {
                handleNewLocation()
                Log.d("LOCATION", "location is null")
            }

        }

    }

    @SuppressLint("MissingPermission")
    private fun handleNewLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            val priority = Priority.PRIORITY_HIGH_ACCURACY
            val result = fusedLocationProviderClient.getCurrentLocation(
                priority, CancellationTokenSource().token
            ).await()
            if (result == null) {
                Log.d("LOCATION", "LOCATIOn is null again")

            }
            result?.let {
                Log.d("LOCATION", "LOCATION VALUE $it")
            }

        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        var head = event?.values?.get(0)!!
        val degree = event.values?.get(0)!!
        val destinationLocation = Location("service Provider")
        val qiblahCordinate = QiblahCordinate()
        destinationLocation.latitude = qiblahCordinate.latitude
        destinationLocation.longitude = qiblahCordinate.longitude
        if (userLocation == null) {
            Log.d("LOCATION", "IS NULL")
            return
        }
        var bearTo = userLocation!!.bearingTo(destinationLocation)
        val geomagneticField = GeomagneticField(
            userLocation!!.latitude.toFloat(),
            userLocation!!.longitude.toFloat(),
            userLocation!!.altitude.toFloat(),
            System.currentTimeMillis()
        )
        head -= geomagneticField.declination

        if (bearTo < 0) {
            bearTo += 360
        }
        val isAlign = head - bearTo < 4 && head - bearTo > -4


        val locationUpdates = Pair(isAlign, degree)
        locationChanges?.invoke(locationUpdates)

    }

    override fun stopListening() {

        if (!this::sensorManager.isInitialized) {
            return
        }
        sensorManager.unregisterListener(this)
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}