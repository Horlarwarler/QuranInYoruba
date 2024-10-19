package com.sadaqaworks.yorubaquran.dua.domain.model

data class RuqyahModel(
    val id:Int,
    val arabic:String,
    val translation :String,
    val note:String? = null
)
