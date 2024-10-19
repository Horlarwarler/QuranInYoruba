package com.sadaqaworks.yorubaquran.settings.presentation.setting

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sadaqaworks.yorubaquran.R
import com.sadaqaworks.yorubaquran.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var viewModel: SettingViewModel
    private lateinit var settingBinding: FragmentSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingBinding = FragmentSettingBinding.inflate(inflater)

        return settingBinding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingViewModel::class.java]

        settingBinding.otherApp.setOnClickListener {
            activity?.showAppsByDeveloper()
        }
        changeAutoScroll()
        downloadSurahBeforePlaying()
        changeContinueReading()
        changeFontSize()
        changeReciter()
        navigateToAbout()
        changeAutoPlay()
    }

    private fun navigateToAbout() {
        settingBinding.aboutPage.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_aboutFragment)
        }
    }

    private fun changeAutoScroll() {
        val isChecked = viewModel.settingUiState.value?.scrollAuto!!
        settingBinding.autoScroll.apply {
            this.isChecked = isChecked
            setOnClickListener {
                viewModel.handleUiEvent(SettingUIEvent.ScrollAuto)
            }
        }
    }

    private fun changeAutoPlay() {
        val isChecked = viewModel.settingUiState.value?.playAuto!!
        settingBinding.playAuto.apply {
            this.isChecked = isChecked
            setOnClickListener {
                viewModel.handleUiEvent(SettingUIEvent.PlayAuto)
            }
        }
    }

    private fun changeContinueReading() {
        val isChecked = viewModel.settingUiState.value?.continueReading!!

        settingBinding.continueReading.apply {
            this.isChecked = isChecked
            setOnClickListener {
                viewModel.handleUiEvent(SettingUIEvent.ContinueReading)
            }
        }
    }


    private fun downloadSurahBeforePlaying() {
        val isChecked = viewModel.settingUiState.value?.downloadBeforePlaying!!

        settingBinding.automaticallyDownload.apply {
            this.isChecked = isChecked
            setOnClickListener {
                viewModel.handleUiEvent(SettingUIEvent.DownloadSurahBeforePlaying)
            }
        }
    }

    private fun changeFontSize() {
        setInitialFont()
        val fontSizeClickListener: AdapterView.OnItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, position, _ ->
                val selectedFont = adapterView.getItemAtPosition(position).toString().toInt()
                viewModel.handleUiEvent(SettingUIEvent.SelectFont(selectedFont))
            }
        val fontAdapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item,
            requireContext().resources.getStringArray(R.array.font)
        )
        settingBinding.font.setAdapter(fontAdapter)
        settingBinding.font.onItemClickListener = fontSizeClickListener
    }

    private fun setInitialFont() {
        val font = viewModel.settingUiState.value?.fontSize?.toString()!!
        settingBinding.font.setText(font)
    }

    private fun changeReciter() {
        setInitialReciter()
        val reciterSizeClickListener: AdapterView.OnItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, position, _ ->
                val selectedReciter = adapterView.getItemAtPosition(position).toString()
                viewModel.handleUiEvent(SettingUIEvent.SelectReciter(selectedReciter))
            }

        val reciterAdapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item,
            requireContext().resources.getStringArray(R.array.reciter)
        )
        settingBinding.reciterText.setAdapter(reciterAdapter)
        settingBinding.reciterText.onItemClickListener = reciterSizeClickListener
    }

    private fun setInitialReciter() {
        val font = viewModel.settingUiState.value?.reciter
        settingBinding.reciterText.setText(font)
    }

    private fun Activity.showAppsByDeveloper() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.other_app_dialog)
        val hadithButton = dialog.findViewById(R.id.hadith_yoruba) as ConstraintLayout
        val secureButton = dialog.findViewById(R.id.secure_charge) as ConstraintLayout
        hadithButton.setOnClickListener{
            openPlayStore("com.crezent.hadithYoruba")
        }
        secureButton.setOnClickListener {
            openPlayStore("com.crezent.securecharge")
        }
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.VISIBLE
    }

    val openPlayStore: (String) -> Unit = {
        try {
            val appStoreIntent = Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    "market://details?id=$it"
                )
            )
            appStoreIntent.setPackage("com.android.vending")
            activity?.startActivity(appStoreIntent, null)
        } catch (exception: ActivityNotFoundException) {
            activity?.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$it")
                ),
                null
            )

        }
    }


}