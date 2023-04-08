package com.sadaqaworks.yorubaquran.dua.presentation.chapter

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.sadaqaworks.yorubaquran.R
import com.sadaqaworks.yorubaquran.databinding.FragmentDuaChapterBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChapterFragment : Fragment() {

    private lateinit var viewModel: ChapterViewModel
    private  lateinit var chapterBinding: FragmentDuaChapterBinding
    private var isSet:Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        chapterBinding = FragmentDuaChapterBinding.inflate(inflater)
        return   chapterBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ChapterViewModel::class.java]

        val chapterAdapter = ChapterAdapter {chapterId, chapterName ->
            chapterSelected(chapterId,chapterName)
        }
        val linearLayoutManager = LinearLayoutManager(requireContext())
        setRecyclerView(chapterAdapter,linearLayoutManager)

        viewModel.duaChapterUiState.observe(viewLifecycleOwner){
                state ->

            state?.let {
                if (!isSet){
                    chapterAdapter.setChapter(state.chapters)
                    chapterBinding.categoryName.text = state.title
                    chapterAdapter.notifyDataSetChanged()
                    //isSet = true
                }

            }
        }
        backClick()
    }



    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.GONE
    }



    private fun setRecyclerView(chapterAdapter: ChapterAdapter, linearLayoutManager: LayoutManager){
        val recyclerView = chapterBinding.duaCategoryRecycler
        recyclerView.apply {
            adapter  = chapterAdapter
            layoutManager = linearLayoutManager
        }
    }

    private fun backClick(){
       chapterBinding.backButton.setOnClickListener {
           findNavController().navigateUp()
       }
    }

    private fun chapterSelected(chapterSelected:Int, chapterName:String){
       val navigationAction = ChapterFragmentDirections.actionChapterFragmentToDetailsDuaFragment(chapterSelected,chapterName)
        findNavController().popBackStack(R.id.chapterFragment, false)
        findNavController().navigate(navigationAction)
    }




}



