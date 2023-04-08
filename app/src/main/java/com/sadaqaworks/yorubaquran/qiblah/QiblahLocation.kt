package com.sadaqaworks.yorubaquran.qiblah

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.location.LocationManager
import android.os.Build
import android.util.Log

import androidx.annotation.RequiresApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.sadaqaworks.yorubaquran.qiblah.presentation.QiblahCordinate
import com.sadaqaworks.yorubaquran.util.checkPermission
import kotlinx.coroutines.delay

class QiblahLocation(
    private val context:Context,
    private val  fusedLocationProviderClient: FusedLocationProviderClient
) : Location(), SensorEventListener{

   private var  sensor:Sensor? = null
    private var userLocation: android.location.Location? =null

    private lateinit var sensorManager: SensorManager
    override val isPermissionEnabled: Boolean
        get() = hasPermission


    override val isDeviceSupported: Boolean
        get() = hasCompassSensor

    override val locationIsEnabled: Boolean
        get() = locationIsEnabled()
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun startListening() {

        if (!isDeviceSupported){
            throw DeviceNotSupported()
        }
        if (!locationIsEnabled){
            throw  LocationNotEnabled()
        }

        if ( !isPermissionEnabled){

            throw  PermissionNotGranted()
        }
        if(!this::sensorManager.isInitialized && sensor == null){
            Log.d("Error","Inin")
            sensorManager = context.getSystemService(SensorManager::class.java) as SensorManager
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            location ->
            userLocation = location
            sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_GAME )
        }

    }

    override fun stopListening() {

        if (!this::sensorManager.isInitialized){
            return
        }
        sensorManager.unregisterListener(this)
    }

    private val hasPermission = context.checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
            context.checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)



    private fun locationIsEnabled(): Boolean {
        val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private val hasCompassSensor:Boolean = context.packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)
    override fun onSensorChanged(event: SensorEvent?) {
        var head = event?.values?.get(0)!!
        val degree = event.values?.get(0)!!
        val destinationLocation = android.location.Location("service Provider")
        val qiblahCordinate = QiblahCordinate()
        destinationLocation.latitude =qiblahCordinate.latitude
        destinationLocation.longitude = qiblahCordinate.longitude
        var bearTo = userLocation!!.bearingTo(destinationLocation)
        val geomagneticField = GeomagneticField(
            userLocation!!.latitude.toFloat(),
            userLocation!!.longitude.toFloat(),
            userLocation!!.altitude.toFloat(),
            System.currentTimeMillis())
        head -= geomagneticField.declination

        if (bearTo < 0){
            bearTo += 360
        }
        val  isAlign = head - bearTo < 4 && head - bearTo > -4


        val locationUpdates = Pair(isAlign, degree)
        locationChanges?.invoke(locationUpdates)

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}