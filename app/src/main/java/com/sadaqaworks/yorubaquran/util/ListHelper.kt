package com.sadaqaworks.yorubaquran.util


class ListHelper<T>  {
    fun convertToMutable(list: List<T>): MutableList<T>{
        return  list.toMutableList()
    }

    fun  convertToList(mutableList: MutableList<T>): List<T>{
        return mutableList.toList()
    }

    fun addElement(element :T,elements:List<T>, position:Int = elements.size) : List<T>{
        val convertToMutable = convertToMutable(elements)
        convertToMutable.add(index = position, element = element)
        return  convertToList(convertToMutable)
    }


    fun removeElement(elements:List<T>, position:Int =0) : List<T>{
        val convertToMutable = convertToMutable(elements)
        convertToMutable.removeAt(position)
        return  convertToList(convertToMutable)
    }


}
