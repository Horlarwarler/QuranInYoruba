package com.sadaqaworks.yorubaquran.qiblah.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.sadaqaworks.yorubaquran.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sadaqaworks.yorubaquran.databinding.FragmentQiblahBinding
import com.sadaqaworks.yorubaquran.qiblah.Location
import com.sadaqaworks.yorubaquran.qiblah.QiblahLocation
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

class QiblahFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var fragmentQiblahBinding: FragmentQiblahBinding
    private val permissionId = 2
    private val locationId = 1
    lateinit var location: QiblahLocation
    private var error: Exception? = null

    var job: Job? = null

    private var currentDegree = 38F

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            getLocation()
        }
    private val locationLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { resultCode ->
            if (resultCode.resultCode == Activity.RESULT_OK) {
                getLocation()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this
        fragmentQiblahBinding = FragmentQiblahBinding.inflate(inflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        location = QiblahLocation(requireContext(), fusedLocationClient)
        val height = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.windowManager?.currentWindowMetrics?.bounds?.height()
        } else {
            activity?.windowManager?.defaultDisplay?.height
        }

        return fragmentQiblahBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentQiblahBinding.location.setOnClickListener {
            locationClick()
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        try {

            location.startListening()

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    setLocation(location = location)
                }
            }
            Log.d("TAG", "START LISTNEING")
            error = null
            fragmentQiblahBinding.alignment.text = ""

            location.setOnLocationChanges {
                val isAligned = it.first
                val degree = it.second
                setQiblahText(isAligned)
                setQiblah(degree)
            }

        } catch (error: Location.DeviceNotSupported) {
            fragmentQiblahBinding.alignment.text = "Device Not Supported "
            this@QiblahFragment.error = Location.DeviceNotSupported()

            // Toast.makeText(requireContext(),"Device Not",Toast.LENGTH_SHORT).show()
        } catch (error: Location.LocationNotEnabled) {
            fragmentQiblahBinding.alignment.text = "Location is not enabled "
            this@QiblahFragment.error = Location.LocationNotEnabled()

        } catch (error: Location.PermissionNotGranted) {
            val showRationale =
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

            val permissionText =
                if (showRationale) "Permission Denied, Click Icon Below Settings To Enable In The App Settings" else "Location Permission Not Granted, Click Icon Below"
            fragmentQiblahBinding.alignment.text = permissionText
            this@QiblahFragment.error = Location.PermissionNotGranted()
        } catch (error: Exception) {
            // Toast.makeText(requireContext(),"oOther",Toast.LENGTH_SHORT).show()

        }


    }

    private fun locationClick() {
        if (error == null || error == Location.DeviceNotSupported()) {
            return
        }
        Log.d("CLick", "Location is Click ${error}")

        if (error is Location.LocationNotEnabled) {
            accessLocation()
        }

        if (error is Location.PermissionNotGranted) {
            val showRationale =
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            if (!showRationale) {
                requestPermission()
            } else {
                openAppSettings()
            }
        }
    }

    private fun accessLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        locationLauncher.launch(intent)
    }

    private fun requestPermission() {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun openAppSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireContext().packageName, null)
        ).also {
            startActivity(it)
        }

    }

    private fun setQiblahText(
        isAligned: Boolean,
    ) {
        if (isAligned) {
            fragmentQiblahBinding.alignment.setTextColor(resources.getColor(R.color.white))
            val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context?.getColor(R.color.deep_green)
            } else {
                resources.getColor(R.color.deep_green)
            }
            fragmentQiblahBinding.alignment.setBackgroundColor(color!!)
            fragmentQiblahBinding.alignment.text = "You are in the right direction"

        } else {
            fragmentQiblahBinding.alignment.text = "You are not in the right direction"
            val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context?.getColor(R.color.light_green)
            } else {
                resources.getColor(R.color.light_green)

            }
            fragmentQiblahBinding.alignment.setBackgroundColor(color!!)

        }
    }

    private fun setQiblah(degree: Float) {
        val kabbahAnimation = RotateAnimation(
            currentDegree, -degree,
            Animation.RELATIVE_TO_SELF, 0.5F,
            Animation.RELATIVE_TO_SELF, 0.5F
        )
        kabbahAnimation.duration = 210
        kabbahAnimation.fillAfter = true

        fragmentQiblahBinding.background.startAnimation(kabbahAnimation)
        currentDegree = -degree
    }

    private fun setLocation(location: android.location.Location) {

        val latitude = location.latitude
        val longitude = location.longitude
        val altitude = location.altitude
        val latLong = "$latitude $longitude"
        fragmentQiblahBinding.latitude.text = latLong
        fragmentQiblahBinding.locationText.text = "Test"
        fragmentQiblahBinding.locationText.isSelected = true


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