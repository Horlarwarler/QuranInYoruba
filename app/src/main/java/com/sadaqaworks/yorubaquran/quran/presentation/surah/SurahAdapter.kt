package com.sadaqaworks.yorubaquran.quran.presentation.surah


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sadaqaworks.yorubaquran.quran.domain.model.SurahDetails
import com.sadaqaworks.yorubaquran.R

class SurahAdapter(private val onSurahClick: (Int) -> Unit = {} ) : RecyclerView.Adapter<SurahAdapter.ViewHolder>() {
    class ViewHolder (view: View, onSurahClick:(Int) -> Unit) : RecyclerView.ViewHolder(view){
        val surahNumber: TextView = view.findViewById(R.id.ayah_number)
        val surahInfo:TextView = view.findViewById(R.id.surah_info)
        val surahNameTranslation:TextView = view.findViewById(R.id.bookmark)
        val surahNameArabic:TextView = view.findViewById(R.id.surah_name_arabic)

        init {
            view.setOnClickListener {
                val surahInt = absoluteAdapterPosition+1
                onSurahClick(surahInt)
            }
        }
    }

    var surah : List<SurahDetails> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val surahView = LayoutInflater.from(parent.context).inflate(R.layout.surah_layout,parent,false)

        return  ViewHolder(surahView, onSurahClick)
    }

    override fun getItemCount(): Int {
       return  surah.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val arabic = surah[position].arabic
        val translation = surah[position].translation
        val surahId = surah[position].id
        val surahType = surah[position].type
        val ayahNumber = surah[position].ayahNumber
        val bottomText = "$surahType, $ayahNumber Ayah"
        holder.surahNumber.text = surahId.toString()
        holder.surahInfo.text = bottomText
        holder.surahNameTranslation.text = translation.replaceFirstChar {
            it.uppercaseChar()
        }
        holder.surahNameArabic.text = arabic
    }

}