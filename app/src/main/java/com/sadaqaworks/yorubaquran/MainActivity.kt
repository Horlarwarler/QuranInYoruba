package com.sadaqaworks.yorubaquran

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment

import com.sadaqaworks.yorubaquran.shared.SharedViewModel
import com.google.android.material.navigation.NavigationBarView
import com.sadaqaworks.yorubaquran.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController:NavController
    var selectedFragment : Int? = null

    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController =  navHostFragment.navController
        binding.bottomNavigation.setOnItemSelectedListener(navigationBarListener)
        setContentView(binding.root)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return true

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        Log.d("Menu", "Selected")

        return true
    }

    private val navigationBarListener : NavigationBarView.OnItemSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            when(item.itemId){
                R.id.qiblah -> {
                    selectedFragment = R.id.qiblahFragment

                }
                R.id.quran -> {
                    selectedFragment = R.id.surahFragment
                }

                R.id.settings ->{
                    selectedFragment = R.id.settingFragment
                }

                R.id.dua ->{
                    selectedFragment  = R.id.duaCategoryFragment

                }
                else -> {
                }

            }
            val previousSelectedFragment = navController.currentDestination?.id
            if (selectedFragment == previousSelectedFragment){
               return@OnItemSelectedListener false
            }
            if(selectedFragment != R.id.surahFragment){
              navController.popBackStack(R.id.surahFragment,false)
            }
            else {
                navController.popBackStack(R.id.surahFragment, true)
            }
            navController.navigate(selectedFragment!!)
            true
        }

    override fun onBackPressed() {
        val otherMenuFragment = listOf(
            R.id.duaCategoryFragment,
            R.id.qiblahFragment,
            R.id.settingFragment
        )
        val backStackId = navController.currentBackStackEntry?.destination?.id
        val backPressedInMainMenu = otherMenuFragment.contains(backStackId )
        Log.d("Backpressed","$backStackId")
        if ( backPressedInMainMenu ){
            Log.d("Backpressed","true")
            binding.bottomNavigation.selectedItemId = R.id.quran
        }
        else{
            super.onBackPressed()
        }


    }

    override fun onStart() {
        super.onStart()
       // mediaBrowserCompat?.connect()
    }


    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun onStop() {
        super.onStop()
//        MediaControllerCompat.getMediaController(this).unregisterCallback(controllerCallback)


    }

    override fun onDestroy() {
        super.onDestroy()

    }
    private fun getFragmentId(destinationId:Int):Int?{
        val fragment = navController.findDestination(destinationId)

        return  fragment?.id
    }





}