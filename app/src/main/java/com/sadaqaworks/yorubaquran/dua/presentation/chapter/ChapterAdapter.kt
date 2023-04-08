package com.sadaqaworks.yorubaquran.dua.presentation.chapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sadaqaworks.yorubaquran.dua.domain.model.DuaChapterModel
import com.sadaqaworks.yorubaquran.R

class ChapterAdapter(val onChapterSelected: (Int,String) -> Unit) : RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {

    private var chapters:List<DuaChapterModel> = emptyList()
    class ChapterViewHolder(view:View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
       val chapterView = LayoutInflater.from(parent.context).inflate(R.layout.dua_chapter_item,parent,false)
        return  ChapterViewHolder(chapterView)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapterModel = chapters[position]
        holder.itemView.setOnClickListener {
            onChapterSelected(chapterModel.chapterId, chapterModel.chapterName)
        }
        bindChapter(holder,chapterModel,)
    }

    override fun getItemCount(): Int {
        return  chapters.size
    }

    fun setChapter(chapters:List<DuaChapterModel>){
        this.chapters = chapters
    }

    private  fun bindChapter(holder: ChapterViewHolder, duaChapterModel: DuaChapterModel){
        val duaNumber = holder.itemView.findViewById<TextView>(R.id.dua_number)
        val duaInfo = holder.itemView.findViewById<TextView>(R.id.dua_info)
        duaNumber.text = (holder.position+1).toString()
        duaInfo.text = duaChapterModel.chapterName
    }
}