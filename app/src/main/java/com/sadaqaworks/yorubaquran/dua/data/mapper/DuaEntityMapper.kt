package com.sadaqaworks.yorubaquran.dua.data.mapper

import com.sadaqaworks.yorubaquran.dua.data.database.DuaChapterEntity
import com.sadaqaworks.yorubaquran.dua.data.database.DuaItemEntity
import com.sadaqaworks.yorubaquran.dua.domain.model.DuaChapterModel
import com.sadaqaworks.yorubaquran.dua.domain.model.DuaItemModel

fun DuaItemEntity.convertToModel(): DuaItemModel{

    return  DuaItemModel(
       duaId = duaId,
        chapterId = chapterId,
        duaTranslation = duaTranslation,
        duaArabic = duaArabic,
        duaReference = duaReference
    )
}

fun DuaChapterEntity.convertToModel():DuaChapterModel{

    return  DuaChapterModel(
        categoryId = categoryId,
        chapterId = chapterId,
        chapterName = chapterName
    )
}

//fun DuaCategoryEntity.convertToModel():DuaCategoryModel{
//
//    return  DuaCategoryModel(
//        categoryId = categoryId,
//        category_icon = category_count,
//        categoryName = categoryName
//    )
//}


