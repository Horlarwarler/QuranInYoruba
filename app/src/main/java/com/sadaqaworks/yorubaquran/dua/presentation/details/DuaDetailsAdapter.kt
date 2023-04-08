package com.sadaqaworks.yorubaquran.dua.presentation.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sadaqaworks.yorubaquran.dua.domain.model.DuaItemModel
import com.sadaqaworks.yorubaquran.R

class DuaDetailsAdapter(val onShareClick:(DuaItemModel) -> Unit =  {}) : RecyclerView.Adapter<DuaDetailsAdapter.DuaDetailsViewHolder>() {

     private  var duas : List<DuaItemModel> = emptyList()
    class DuaDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val duaReference:TextView  = view.findViewById(R.id.dua_reference)
        val duaArabic:TextView  = view.findViewById(R.id.dua_arabic)
        val duaTranslation:TextView  = view.findViewById(R.id.dua_translation)
        val duaId:TextView = view.findViewById(R.id.dua_number)
        val shareIcon:ImageButton = view.findViewById(R.id.share_icon)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuaDetailsViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.dua_item_layout,parent, false)
        return  DuaDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: DuaDetailsViewHolder, position: Int) {
        val dua = duas[position]
        bindDua(dua, holder)
        holder.shareIcon.setOnClickListener {
            onShareClick(dua)
        }

    }

    override fun getItemCount(): Int {
        return  duas.size
    }

    fun setDua(duas: List<DuaItemModel>){
        this.duas = duas
        notifyDataSetChanged()
    }

    private fun bindDua(duaModel: DuaItemModel, holder:DuaDetailsViewHolder){
        holder.duaReference.text = duaModel.duaReference
        holder.duaTranslation.text = duaModel.duaTranslation
        holder.duaArabic.text = duaModel.duaArabic
        holder.duaId.text = (holder.absoluteAdapterPosition + 1).toString()


    }




}