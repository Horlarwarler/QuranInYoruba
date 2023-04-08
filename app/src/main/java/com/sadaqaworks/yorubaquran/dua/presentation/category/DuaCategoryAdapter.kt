package com.sadaqaworks.yorubaquran.dua.presentation.category

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.sadaqaworks.yorubaquran.dua.domain.model.DuaCategoryModel
import com.sadaqaworks.yorubaquran.dua.presentation.category.DuaCategories.DuaChapter.duaCategory
import com.sadaqaworks.yorubaquran.R

class DuaCategoryAdapter(
    val onCategorySelected:(Int) -> Unit,
    context: Context
): ArrayAdapter<DuaCategoryModel>(context, R.layout.dua_category_item) {

    private  var layoutInflater: LayoutInflater? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertedView = convertView
        if (layoutInflater == null){
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        if (convertedView == null){
            convertedView = layoutInflater!!.inflate(R.layout.dua_category_item, parent, false)
        }
        val duaCategoryModel = getItem(position)

        convertedView?.setOnClickListener {
            onCategorySelected(duaCategoryModel.categoryId)
            Log.d("Category","Category 1 is ${duaCategoryModel.categoryId}")
        }

        bind(duaCategoryModel, convertedView!!)


        return convertedView
    }

    private fun bind(duaCategoryModel: DuaCategoryModel, view: View) {
        val image = view.findViewById<ImageView>(R.id.dua_category_image)
        val text = view.findViewById<TextView>(R.id.dua_category_text)
        image.setImageResource(duaCategoryModel.category_icon)
        text.text  = duaCategoryModel.categoryName


    }

    override fun getItem(position: Int): DuaCategoryModel {

        return duaCategory[position]
    }

    override fun getCount(): Int {
        return duaCategory.size
    }


}

