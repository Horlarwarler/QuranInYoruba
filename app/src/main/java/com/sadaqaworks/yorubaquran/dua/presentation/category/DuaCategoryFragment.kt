package com.sadaqaworks.yorubaquran.dua.presentation.category

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.navigation.fragment.findNavController
import com.sadaqaworks.yorubaquran.R
import com.sadaqaworks.yorubaquran.databinding.FragmentDuaCategoryBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sadaqaworks.yorubaquran.dua.presentation.chapter.ChapterFragmentDirections


class DuaCategoryFragment : Fragment() {
    private var phoneIsSmallDevice: Boolean = false


    private lateinit var duaCategoryBinding: FragmentDuaCategoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        duaCategoryBinding = FragmentDuaCategoryBinding.inflate(inflater)

//        val height = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            activity?.windowManager?.currentWindowMetrics?.bounds?.height()
//        } else {
//            activity?.windowManager?.defaultDisplay?.height
//        }
//        if (height != null && height < 900) {
//            val constraintLayout = duaCategoryBinding.root
//            val constraintSet = ConstraintSet()
//            duaCategoryBinding.title.visibility = View.VISIBLE
//            duaCategoryBinding.chapterBackground.root.visibility = View.GONE
//
//            constraintSet.clone(constraintLayout)
//            constraintSet.connect(
//                duaCategoryBinding.title.id,
//                ConstraintSet.TOP,
//                duaCategoryBinding.root.id,
//                ConstraintSet.TOP,
//                0
//            )
//            constraintSet.connect(
//                duaCategoryBinding.duaChapters.id,
//                ConstraintSet.TOP,
//                duaCategoryBinding.title.id,
//                ConstraintSet.BOTTOM,0
//            )
//            constraintSet.applyTo(constraintLayout)
//
//
//        }
        duaCategoryBinding.ruqyah.root.setOnClickListener {
            chapterSelected()
        }


        val duaCategoryAdapter = DuaCategoryAdapter({
            navigateToChapterModel(it)
        }, requireContext())
        duaCategoryBinding.duaChapters.apply {
            adapter = duaCategoryAdapter
        }


        return duaCategoryBinding.root
    }

    private fun navigateToChapterModel(categoryId: Int) {
        val navigationAction =
            DuaCategoryFragmentDirections.actionDuaCategoryFragmentToChapterFragment(categoryId)
        findNavController().popBackStack(R.id.duaCategoryFragment, false)
        findNavController().navigate(navigationAction)
    }

    private fun chapterSelected() {
        val navigationAction =
            DuaCategoryFragmentDirections.actionDuaCategoryFragmentToDetailsDuaFragment(
                0, "Ruqyah"
            )
        findNavController().popBackStack(R.id.duaCategoryFragment, false)
        findNavController().navigate(navigationAction)
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.VISIBLE

    }


}