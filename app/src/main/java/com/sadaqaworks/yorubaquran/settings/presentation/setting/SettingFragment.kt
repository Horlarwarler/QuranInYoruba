package com.sadaqaworks.yorubaquran.settings.presentation.setting

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        changeAutoScroll()
        downloadSurahBeforePlaying()
        changeContinueReading()
        changeFontSize()
        changeReciter()
        navigateToAbout()
        changeAutoPlay()


    }

    private fun navigateToAbout(){
        settingBinding.aboutPage.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_aboutFragment)
        }
    }
    private fun changeAutoScroll(){
        val isChecked = viewModel.settingUiState.value?.scrollAuto!!
        settingBinding.autoScroll.apply {
            this.isChecked = isChecked
            setOnClickListener {
                viewModel.handleUiEvent(SettingUIEvent.ScrollAuto)
            }
        }
    }

    private fun changeAutoPlay(){
        val isChecked = viewModel.settingUiState.value?.playAuto!!
        settingBinding.playAuto.apply {
            this.isChecked = isChecked
            setOnClickListener {
                viewModel.handleUiEvent(SettingUIEvent.PlayAuto)
            }
        }
    }

    private fun changeContinueReading(){
        val isChecked = viewModel.settingUiState.value?.continueReading!!

        settingBinding.continueReading.apply {
            this.isChecked = isChecked
            setOnClickListener {
                viewModel.handleUiEvent(SettingUIEvent.ContinueReading)
            }
        }
    }


    private fun downloadSurahBeforePlaying(){
        val isChecked = viewModel.settingUiState.value?.downloadBeforePlaying!!

        settingBinding.automaticallyDownload.apply {
            this.isChecked = isChecked
            setOnClickListener {
                viewModel.handleUiEvent(SettingUIEvent.DownloadSurahBeforePlaying)
            }
        }
    }

    private fun changeFontSize(){
      setInitialFont()
        val fontSizeClickListener : AdapterView.OnItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, position, _ ->
                val selectedFont = adapterView.getItemAtPosition(position).toString().toInt()
                viewModel.handleUiEvent(SettingUIEvent.SelectFont(selectedFont))
            }
        val fontAdapter = ArrayAdapter(requireContext(),R.layout.list_item,requireContext().resources.getStringArray(R.array.font))
        settingBinding.font.setAdapter(fontAdapter)
        settingBinding.font.onItemClickListener = fontSizeClickListener
    }

    private fun setInitialFont(){
        val font = viewModel.settingUiState.value?.fontSize?.toString()!!
        settingBinding.font.setText(font)
    }

    private fun changeReciter(){
        setInitialReciter()
        val reciterSizeClickListener : AdapterView.OnItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, position, _ ->
                val selectedReciter = adapterView.getItemAtPosition(position).toString()
                viewModel.handleUiEvent(SettingUIEvent.SelectReciter(selectedReciter))
            }

        val reciterAdapter = ArrayAdapter(requireContext(),R.layout.list_item,requireContext().resources.getStringArray(R.array.reciter))
        settingBinding.reciterText.setAdapter(reciterAdapter)
        settingBinding.reciterText.onItemClickListener = reciterSizeClickListener
    }

    private fun setInitialReciter(){
        val font = viewModel.settingUiState.value?.reciter
        settingBinding.reciterText.setText(font)
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.VISIBLE
    }

}