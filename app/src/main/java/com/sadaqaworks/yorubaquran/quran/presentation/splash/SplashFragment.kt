package com.sadaqaworks.yorubaquran.quran.presentation.splash

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sadaqaworks.yorubaquran.internet.InternetConnectionState
import com.sadaqaworks.yorubaquran.shared.SharedViewModel
import com.sadaqaworks.yorubaquran.util.CustomToast
import com.sadaqaworks.yorubaquran.R
import com.sadaqaworks.yorubaquran.databinding.FragmentSplashBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var viewModel: SplashViewModel

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var splashBinding: FragmentSplashBinding
    private var internetConnectionState: InternetConnectionState = InternetConnectionState.UNAVAILABLE
    private var isShown = false
    private var dialogIsCanceled = false



    override fun onPause() {
        super.onPause()
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.GONE
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        splashBinding = FragmentSplashBinding.inflate(inflater)
        return  splashBinding.root
    //    return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =  viewModels<SplashViewModel>().value


        val uiState = viewModel.splashUiState

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        sharedViewModel.shareViewModelState.observe(viewLifecycleOwner){
                state ->
                internetConnectionState = state.internetConnectionState
            if (internetIsAvailable() && !isShown){
                isShown = true
                viewModel.getQuranVersion()
            }


        }


        coroutineScope.launch(Dispatchers.Main) {
            delay(5000)

            var isOutDated = true
            var  successToastShown = false
            while (isOutDated && !dialogIsCanceled && isActive){
                uiState.observe(viewLifecycleOwner){
                    state->
                    val versionIsOutdated= state.isOutDated
                    val isLoading =  state.isLoading
                    val dialogIsShown =  state.showDialog
                    val downloadFailed =  state.downloadFailed
                    val messages = state.messages

                    if (versionIsOutdated && !dialogIsShown){
                        viewModel.dialogIsShown()
                        if (downloadFailed){
                            val customToast = CustomToast(requireContext())
                            customToast.showToast(
                                "Data not downloaded, ${messages[0]}", R.drawable.custom_toast_red_background,R.drawable.baseline_file_download_off_24
                            )
                            showRetryDialog()
                        }
                        else{
                            showDialog()
                        }

                    }

                    hideOrShowProgress(isLoading)
                    if (!versionIsOutdated && dialogIsShown && !successToastShown){
                        val customToast = CustomToast(requireContext())
                        customToast.showToast(
                            "Quran Updated", R.drawable.custom_toast_green_background,R.drawable.baseline_file_download_24
                        )
                        successToastShown = true
                    }
                    if(!versionIsOutdated ){
                        isOutDated = false
                    }

                }
                delay(1000)
            }
            navigateFromSplashScreen()
        }


    }
    private fun internetIsAvailable():Boolean{
        return  internetConnectionState == InternetConnectionState.AVAILABLE
    }
    private fun updateButtonClick(){
        if (!internetIsAvailable()){
            val customToast = CustomToast(requireContext())
            customToast.showToast(
                "No Internet Connection", R.drawable.custom_toast_yellow_background,R.drawable.no_internet
            )
            return

        }
        viewModel.downloadUpdatedQuran()
    }

    private  fun navigateFromSplashScreen(){
     //   SplashFragmentDirections.actionSplashFragmentToSurahFragment()

       // findNavController().popBackStack(R.id.splashFragment, true)
        findNavController().navigate(R.id.action_splashFragment_to_surahFragment)


    }

    private fun showRetryDialog(){
        showDialog(
            title = "Failed To Update",
            description = "An error occurs while trying to update the app data, you can update later",
            positiveTextButton = "Retry",
            negativeTextButton = "Ignore For Now"
        )
    }
    private fun showDialog(
        title:String = "Update Quran",
        description:String = "There is a recent version of the quran than the one on your local storage",
        positiveTextButton:String ="Download",
        negativeTextButton:String ="Ignore Update"
    ){

        splashBinding.customDialog.root.visibility = View.VISIBLE
        splashBinding.customDialog.apply {
            root.visibility = View.VISIBLE
            dialogTitle.text = title
            dialogDescription.text = description
            positiveButton.text = positiveTextButton
            negativeButton.text = negativeTextButton
            positiveButton.setOnClickListener {
                updateButtonClick()
            }
            negativeButton.setOnClickListener {
                hideDialog()
            }
        }

    }

    private fun hideDialog(){
        splashBinding.customDialog.root.visibility = View.INVISIBLE
        dialogIsCanceled = true


    }

    private fun showProgress(){
        splashBinding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        splashBinding.progressBar.visibility = View.GONE
    }



    private fun hideOrShowProgress(isLoading:Boolean){
        if (isLoading){
            showProgress()
        }
        else{
            hideProgress()
        }
    }




}