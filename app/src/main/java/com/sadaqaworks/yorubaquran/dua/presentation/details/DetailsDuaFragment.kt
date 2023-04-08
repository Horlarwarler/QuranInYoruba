package com.sadaqaworks.yorubaquran.dua.presentation.details

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sadaqaworks.yorubaquran.dua.domain.model.DuaItemModel
import com.sadaqaworks.yorubaquran.R
import com.sadaqaworks.yorubaquran.databinding.FragmentDetailsDuaBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailsDuaFragment : Fragment() {

    private lateinit var viewModel: DetailsDuaViewModel
    private lateinit var detailsFragmentBinding:FragmentDetailsDuaBinding
    private var isSet:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        detailsFragmentBinding =  FragmentDetailsDuaBinding.inflate(inflater)
        return detailsFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DetailsDuaViewModel::class.java]
        val duaDetailsAdapter = DuaDetailsAdapter{duaItem->
            shareClick(duaItem)
        }
        val duaRecyclerView = detailsFragmentBinding.duaListRecycler
        backClick()

        val linearLayoutManager = LinearLayoutManager(requireContext())
        duaRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = duaDetailsAdapter
        }
        viewModel.duaDetailsUiState.observe(viewLifecycleOwner){
                state ->
            if (state == null){
                return@observe
            }
            if (!isSet && state.duas.isNotEmpty()){
                setDua(state.duas, adapter = duaDetailsAdapter)
                setTitle(state.title)
                isSet = true
            }

//            val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)
//            duaRecyclerView.layoutAnimation = animation

            duaDetailsAdapter.notifyDataSetChanged()


        }

    }

    private fun setDua(duas:List<DuaItemModel>, adapter: DuaDetailsAdapter){
        adapter.setDua(duas)
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

    private fun backClick(){
        detailsFragmentBinding.backButton.setOnClickListener {
            navigateBack()
        }
    }
    private fun navigateBack(){
        findNavController().navigateUp()
    }

    private fun setTitle(title:String){
        detailsFragmentBinding.duaChapterName.text = title
        detailsFragmentBinding.duaChapterName.isSelected = true
    }
    private fun shareClick(duaItemModel: DuaItemModel){
        val chapterName = detailsFragmentBinding.duaChapterName.text
        val body = "*Dua $chapterName*\n" +
                "${duaItemModel.duaArabic} \n\n" +
                "${duaItemModel.duaTranslation} \n\n" +
                "_${duaItemModel.duaReference}_"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, body)
        requireActivity().startActivity(Intent.createChooser(shareIntent,"Share Dua"))
    }

}