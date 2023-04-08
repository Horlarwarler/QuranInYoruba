package com.sadaqaworks.yorubaquran.qiblah.presentation

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.IBinder
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData

class QiblahFinder(
    val  context: Context,
    val longitude:Double,
    val latitude:Double,
    val altitude:Double,
    val kabbahImage:ImageView,
    val arrowImage:ImageView
    ) : Service(), SensorEventListener {
    private var currentDegree:Float = 0F
    private var currentDegreeNeedle:Float = 0F
    private val userLocation = Location("service Provider")


    private val _isAlign :MutableLiveData<Boolean> = MutableLiveData()
    val isAlign :MutableLiveData<Boolean>
    get()  = _isAlign



    private var sensor: Sensor
    private var sensorManager:SensorManager
   init {

       userLocation.altitude = altitude
       userLocation.longitude = longitude
       userLocation.latitude = latitude
       sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
       sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
       startMonitoring()

   }
    override fun onCreate() {
        startMonitoring()
        super.onCreate()

    }

    override fun onDestroy() {
      destroy()
        super.onDestroy()
    }

    fun destroy(){
        sensorManager.unregisterListener(this,)
    }

    fun startMonitoring(){
        sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_GAME)

    }


    override fun onBind(p0: Intent?): IBinder? {
       return null
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        var head = sensorEvent?.values?.get(0)!!
        val degree = sensorEvent.values?.get(0)!!
        val destinationLocation = Location("service Provider")
        val qiblahCordinate = QiblahCordinate()
        destinationLocation.latitude =qiblahCordinate.latitude
        destinationLocation.longitude = qiblahCordinate.longitude
        var bearTo = userLocation.bearingTo(destinationLocation)

        val geomagneticField = GeomagneticField(
            userLocation.latitude.toFloat(),
            userLocation.longitude.toFloat(),
            userLocation.altitude.toFloat(),
            System.currentTimeMillis())
        head -= geomagneticField.declination


        if (bearTo < 0){
            bearTo += 360
        }

        isAlign.value = head - bearTo < 4 && head - bearTo > -4


        var direction = bearTo - head

        if (direction < 0){
            direction += 360
        }

        val kabbahAnimation = RotateAnimation(currentDegree,-degree,Animation.RELATIVE_TO_SELF,0.5F,Animation.RELATIVE_TO_SELF,0.5F)
        kabbahAnimation.duration = 210
        kabbahAnimation.fillAfter = true
        kabbahImage.startAnimation(kabbahAnimation)
        currentDegree = -degree
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

}


    //D/Bear: direction is 358.36914
//        D/Bear: bear to 64.193 , head is 65.822395
//
//
//        val arrowAnimation = RotateAnimation(
//            currentDegreeNeedle,direction,
//            Animation.RELATIVE_TO_SELF,0.5F,
//            Animation.RELATIVE_TO_SELF,0.5F
//        )
//        arrowAnimation.duration = 210
//        arrowAnimation.fillAfter = true
//        arrowImage.startAnimation(arrowAnimation)
//        currentDegreeNeedle = direction