package com.sadaqaworks.yorubaquran.dua.presentation.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sadaqaworks.yorubaquran.R
import com.sadaqaworks.yorubaquran.databinding.FragmentDuaCategoryBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class DuaCategoryFragment : Fragment() {

    private lateinit var  duaCategoryBinding: FragmentDuaCategoryBinding
     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       duaCategoryBinding = FragmentDuaCategoryBinding.inflate(inflater)

        val duaCategoryAdapter = DuaCategoryAdapter({
            navigateToChapterModel(it)
        },requireContext())
        val categoryGrid = duaCategoryBinding.duaChapters
        categoryGrid.apply {
            adapter = duaCategoryAdapter
        }

        return  duaCategoryBinding.root
    }

    private fun  navigateToChapterModel(categoryId:Int){
       val navigationAction = DuaCategoryFragmentDirections.actionDuaCategoryFragmentToChapterFragment(categoryId)
        findNavController().popBackStack(R.id.duaCategoryFragment, false)
        findNavController().navigate(navigationAction)
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.VISIBLE

    }



}