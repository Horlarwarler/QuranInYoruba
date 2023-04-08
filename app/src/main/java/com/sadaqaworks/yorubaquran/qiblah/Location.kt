package com.sadaqaworks.yorubaquran.qiblah

abstract class Location {
    protected var locationChanges: ((Pair<Boolean, Float>) -> Unit)? = null
    abstract val isPermissionEnabled: Boolean
    abstract val isDeviceSupported:Boolean
    abstract val locationIsEnabled:Boolean
    abstract fun startListening()
    abstract fun stopListening()

    fun setOnLocationChanges(listener: (Pair<Boolean,Float>) -> Unit){
        locationChanges = listener
    }

    class PermissionNotGranted : Exception()
    class DeviceNotSupported :Exception()
    class LocationNotEnabled :Exception()
}