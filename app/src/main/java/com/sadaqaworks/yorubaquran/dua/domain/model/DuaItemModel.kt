package com.sadaqaworks.yorubaquran.dua.domain.model

data class DuaItemModel(
    val duaId:Int,
    val chapterId: Int,
    val duaArabic:String,
    val duaTranslation:String,
    val duaReference:String,
)
