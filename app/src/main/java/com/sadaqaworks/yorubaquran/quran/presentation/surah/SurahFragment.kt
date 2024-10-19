package com.sadaqaworks.yorubaquran.quran.presentation.surah

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowMetrics
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sadaqaworks.yorubaquran.R
import com.sadaqaworks.yorubaquran.databinding.FragmentSurahBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sadaqaworks.yorubaquran.util.checkPermission
import com.sadaqaworks.yorubaquran.util.surahList
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SurahFragment : Fragment() {
    private val viewModel: SurahViewModel by viewModels()
    private lateinit var surahBinding: FragmentSurahBinding

    private var phoneIsSmallDevice: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment

        surahBinding = FragmentSurahBinding.inflate(layoutInflater)

        val height = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.windowManager?.currentWindowMetrics?.bounds?.height()
        } else {
            activity?.windowManager?.defaultDisplay?.height
        }
        if (height != null) {
            phoneIsSmallDevice = height < 900
        }

        if (height != null && phoneIsSmallDevice) {
            Log.d("TAG", "HEIGHT IS $height")

            surahBinding.lastReadCard.root.visibility = View.GONE
            surahBinding.surahListRecycler.top = surahBinding.quranName.bottom

        }

        requireActivity().checkForNotificationPermission()




        return surahBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val surahLayoutManager = LinearLayoutManager(context)
        val savedStateHandle = viewModel.savedStateHandle
        val surahAdapter = SurahAdapter {
            val navigationAction = SurahFragmentDirections.actionQuranFragmentToSurahFragment(it)
            savedStateHandle["surahId"] = it
            findNavController().navigate(navigationAction)
        }
        surahBinding.surahListRecycler.apply {
            //setHasFixedSize(true)
            adapter = surahAdapter
            layoutManager = surahLayoutManager
        }
        val showLastRead = viewModel.lastReadSurah != -1


        surahBinding.bookmarkIcon.setOnClickListener {
            findNavController().navigate(R.id.action_surahFragment_to_bookmarkFragment)
        }
        Log.d("State", "Called Again")

        if (showLastRead && !phoneIsSmallDevice) {
            showLastRead()
        } else {
            hideLastRead()
        }
        viewModel.surahs.observe(viewLifecycleOwner) {
            it?.let {
                surahAdapter.surah = it
                surahAdapter.notifyDataSetChanged()
            }

        }

    }


    @SuppressLint("InlinedApi")
    private fun Activity.checkForNotificationPermission() {
        val permissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermission(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }

        if (permissionGranted) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val showPermissionRationale =
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            if (!showPermissionRationale) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                       Manifest.permission.POST_NOTIFICATIONS
                    ),
                    1
                )
            } else {
                Toast.makeText(context, "Enable Notification Permission In Phone Setting", Toast.LENGTH_LONG)
                    .show()
            }

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                1
            )
        }


    }


    private fun hideLastRead() {
        surahBinding.lastReadCard.root.visibility = View.GONE
    }

    private fun showLastRead() {
        surahBinding.lastReadCard.root.visibility = View.VISIBLE

        setLastReadCard()
    }

    private fun setLastReadCard() {
        viewModel.setLastRead()
        val lastReadSurah = viewModel.lastReadSurah
        val lastReadVerse = viewModel.lastReadVerse
        surahBinding.lastReadCard.lastReadSurah.text = surahList[lastReadSurah - 1]
        surahBinding.lastReadCard.lastReadVerse.text = "Verse ${lastReadVerse + 1}"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permission Needed For Notification", Toast.LENGTH_LONG)
                    .show()

            }
        }
    }

    override fun onPause() {
        super.onPause()
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.VISIBLE


    }


}