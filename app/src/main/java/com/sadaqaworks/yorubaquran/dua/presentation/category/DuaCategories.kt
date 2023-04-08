package com.sadaqaworks.yorubaquran.dua.presentation.category

import com.sadaqaworks.yorubaquran.dua.domain.model.DuaCategoryModel
import com.sadaqaworks.yorubaquran.R

class DuaCategories {
    companion object DuaChapter{
        private val categoryOne = DuaCategoryModel(categoryId = 1, categoryName = "Morning & Evening" , category_icon = R.drawable.morning)
        private val categoryTwo = DuaCategoryModel(categoryId = 2, categoryName = "Home & Family" , category_icon = R.drawable.family)
        private val categoryThree = DuaCategoryModel(categoryId =3 , categoryName = "Food & Drink" , category_icon = R.drawable.food)
        private val categoryFour = DuaCategoryModel(categoryId = 4, categoryName = "Joy & Distress" , category_icon = R.drawable.joy )
        private val categoryFive = DuaCategoryModel(categoryId = 5, categoryName = " Travel" , category_icon = R.drawable.travel )
        private val categorySix = DuaCategoryModel(categoryId = 6, categoryName = "Prayer" , category_icon = R.drawable.prayer )
        private val categorySeven = DuaCategoryModel(categoryId =7 , categoryName = "Praising Allah" , category_icon = R.drawable.praise)
        private val categoryEight = DuaCategoryModel(categoryId = 8, categoryName = "Hajj & Umrah" , category_icon = R.drawable.mecca)
        private val categoryNine = DuaCategoryModel(categoryId = 9, categoryName = "Good Etiquette" , category_icon = R.drawable.etiquete)
        private val categoryTen = DuaCategoryModel(categoryId = 10, categoryName = " Nature" , category_icon = R.drawable.nature )
        private val categoryEleven = DuaCategoryModel(categoryId =11 , categoryName = "Sickness & Death" , category_icon = R.drawable.sickness)

        val duaCategory: List<DuaCategoryModel> = listOf(
            categoryOne, categoryTwo, categoryThree, categoryFour, categoryFive, categorySix,
            categorySeven, categoryEight, categoryNine, categoryTen, categoryEleven
        )
    }

}