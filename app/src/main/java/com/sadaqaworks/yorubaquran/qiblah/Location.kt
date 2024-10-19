package com.sadaqaworks.yorubaquran.qiblah
typealias  QuranLocation = Location
abstract class Location {
    protected var locationChanges: ((Pair<Boolean, Float>) -> Unit)? = null
    abstract fun startListening()
    abstract fun stopListening()

    fun setOnLocationChanges(listener: (Pair<Boolean,Float>) -> Unit){
        locationChanges = listener
    }

    class PermissionNotGranted : Exception()
    class DeviceNotSupported :Exception()
    class LocationNotEnabled :Exception()
}