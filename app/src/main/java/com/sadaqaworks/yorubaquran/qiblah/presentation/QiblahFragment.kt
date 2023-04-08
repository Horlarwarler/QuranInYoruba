package com.sadaqaworks.yorubaquran.qiblah.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.sadaqaworks.yorubaquran.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sadaqaworks.yorubaquran.databinding.FragmentQiblahBinding
import com.sadaqaworks.yorubaquran.qiblah.Location
import com.sadaqaworks.yorubaquran.qiblah.QiblahLocation
import kotlinx.coroutines.*
import java.util.*

class QiblahFragment : Fragment() {
    private lateinit  var fusedLocationClient: FusedLocationProviderClient
    private lateinit var fragmentQiblahBinding: FragmentQiblahBinding
    private val permissionId = 2
    private val locationId= 1
    lateinit var location: QiblahLocation
    private var error: Exception? = null

    private var currentDegree = 38F

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this
        fragmentQiblahBinding = FragmentQiblahBinding.inflate(inflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        location = QiblahLocation(requireContext(), fusedLocationClient)

        return fragmentQiblahBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentQiblahBinding.location.setOnClickListener {
            locationClick()
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private fun getLocation(){
        CoroutineScope(Dispatchers.IO).launch {

            try {

                location.startListening()

                fusedLocationClient.lastLocation.addOnSuccessListener {
                        location ->
                    if (location != null){
                        setLocation(location = location)
                    }
                }
                location.setOnLocationChanges {
                    val isAligned = it.first
                    val degree = it.second
                    setQiblahText(isAligned)
                    setQiblah(degree)
                }

            }
            catch (error:Location.DeviceNotSupported){
                withContext(Dispatchers.Main){
                    fragmentQiblahBinding.alignment.text = "Device Not Supported "
                }
                this@QiblahFragment.error = Location.DeviceNotSupported()

                // Toast.makeText(requireContext(),"Device Not",Toast.LENGTH_SHORT).show()
            }
            catch (error:Location.LocationNotEnabled){
                this@QiblahFragment.error = Location.LocationNotEnabled()
                withContext(Dispatchers.Main){
                    fragmentQiblahBinding.alignment.text = "Location is not enabled "
                }

            }
            catch (error: Location.PermissionNotGranted){
                withContext(Dispatchers.Main){
                    fragmentQiblahBinding.alignment.text = "Location Permision Not Granted "
                }
                this@QiblahFragment.error = Location.PermissionNotGranted()
            }


            catch (error:Exception){
               // Toast.makeText(requireContext(),"oOther",Toast.LENGTH_SHORT).show()

            }
        }


    }

    private fun locationClick(){
        if (error == null || error == Location.DeviceNotSupported() ){
            return
        }
        Log.d("CLick","Location is Click ${error}")

        if(error is Location.LocationNotEnabled){
            accessLocation()
        }

        if (error is Location.PermissionNotGranted){
            Log.d("CLick","Location is Click too")

            requestPermissions()
        }
    }

    private  fun accessLocation(){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent,locationId)
    }



    @RequiresApi(Build.VERSION_CODES.M)
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == locationId){
            if (resultCode == 1){
                error = null
                getLocation()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )

    }
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                error = null
                getLocation()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setQiblahText(
        isAligned:Boolean,
    ){
        if (isAligned){
            fragmentQiblahBinding.alignment.setTextColor(resources.getColor(R.color.white))
            val color = context?.getColor(R.color.deep_green)
            fragmentQiblahBinding.alignment.setBackgroundColor(color!!)
            fragmentQiblahBinding.alignment.text = "You are in the right direction"

        }
        else{
            fragmentQiblahBinding.alignment.text = "You are not in the right direction"
            val color = context?.getColor(R.color.light_green)
            fragmentQiblahBinding.alignment.setBackgroundColor(color!!)

        }
    }

    private fun setQiblah(degree:Float){
        val kabbahAnimation = RotateAnimation(currentDegree,-degree,
            Animation.RELATIVE_TO_SELF,0.5F,
            Animation.RELATIVE_TO_SELF,0.5F)
        kabbahAnimation.duration = 210
        kabbahAnimation.fillAfter = true

        fragmentQiblahBinding.background.startAnimation(kabbahAnimation)
        currentDegree = -degree
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setLocation(location: android.location.Location){

        val latitude = location.latitude
        val longitude = location.longitude
        val altitude = location.altitude
        val latLong = "$latitude $longitude"
        fragmentQiblahBinding.latitude.text = latLong
        fragmentQiblahBinding.locationText.text = "Test"
        fragmentQiblahBinding.locationText.isSelected = true
//
//        val arrow = fragmentQiblahBinding.arrow
//        val background = fragmentQiblahBinding.background
//        qiblahFinder = QiblahFinder(requireContext(),longitude
//            ,latitude,altitude,background,arrow)
//

    }

    override fun onPause() {
        super.onPause()
        location.stopListening()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        getLocation()
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.VISIBLE
    }

}