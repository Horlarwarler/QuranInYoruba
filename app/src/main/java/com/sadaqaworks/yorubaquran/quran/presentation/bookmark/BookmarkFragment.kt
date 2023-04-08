package com.sadaqaworks.yorubaquran.quran.presentation.bookmark

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sadaqaworks.yorubaquran.R
import com.sadaqaworks.yorubaquran.databinding.FragmentBookmarkBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkFragment : Fragment() {

    private  lateinit var  fragmentBookmarkBinding: FragmentBookmarkBinding
    private val viewModel: BookmarkViewmodel  by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment

        fragmentBookmarkBinding = FragmentBookmarkBinding.inflate(inflater)
        val bookmarkRecycler = fragmentBookmarkBinding.surahListRecycler

        val bookmarkAdapter = BookmarkAdapter {
            ayahId ->
            viewModel.deleteFromBookmark(ayahId)
        }
        viewModel.bookmarkState.observe(viewLifecycleOwner){
            bookmarkState ->
            bookmarkAdapter.apply {
                setBookmark(bookmarkState.bookmarks)
                if (bookmarkState.bookmarks.isNotEmpty()){
                    val layoutAnimation = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation)
                    bookmarkRecycler.layoutAnimation = layoutAnimation
                }

            }
            val messages = bookmarkState.messages
            if (messages.isNotEmpty()){
                val currentMessages = messages[0]
                showToast(currentMessages)
                viewModel.deleteShowedMessage()
            }

        }
        fragmentBookmarkBinding.bookmarkIcon.setOnClickListener {
            viewModel.deleteAllBookmark()
        }

        fragmentBookmarkBinding.backButton.setOnClickListener {
            navigateBack()
        }




        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        bookmarkRecycler.apply {
            adapter = bookmarkAdapter
            layoutManager = linearLayoutManager
        }

        return fragmentBookmarkBinding.root
    }



    private fun showToast(
        message:String
    ){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

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
    private fun navigateBack(){
        findNavController().navigateUp()
    }



}