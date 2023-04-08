package com.sadaqaworks.yorubaquran.quran.presentation.surah

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sadaqaworks.yorubaquran.R
import com.sadaqaworks.yorubaquran.databinding.FragmentSurahBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SurahFragment : Fragment() {
    private val viewModel: SurahViewModel by viewModels()
    lateinit var surahBinding: FragmentSurahBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        surahBinding = FragmentSurahBinding.inflate(layoutInflater)
        return  surahBinding.root

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
        val showLastRead = viewModel.lastReadSurah != null


        surahBinding.bookmarkIcon.setOnClickListener {
            findNavController().navigate(R.id.action_surahFragment_to_bookmarkFragment)
        }
        Log.d("State", "Called Again")

        if (showLastRead){
            showLastRead()
        }
        else{
            hideLastRead()
        }
        viewModel.surahs.observe(viewLifecycleOwner){
            it?.let {
                surahAdapter.surah = it
                surahAdapter.notifyDataSetChanged()
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

    private  fun hideLastRead(){
        surahBinding.lastReadCard.root.visibility = View.GONE
    }
    private fun showLastRead(){
        surahBinding.lastReadCard.root.visibility = View.VISIBLE

        setLastReadCard()
    }

    private fun setLastReadCard(){
        viewModel.setLastRead()
        val lastReadSurah = viewModel.lastReadSurah
        val lastReadVerse = viewModel.lastReadVerse
        surahBinding.lastReadCard.lastReadSurah.text = lastReadSurah?. capitalize(Locale.ROOT)
        surahBinding.lastReadCard.lastReadVerse.text = "Verse $lastReadVerse"
    }


}