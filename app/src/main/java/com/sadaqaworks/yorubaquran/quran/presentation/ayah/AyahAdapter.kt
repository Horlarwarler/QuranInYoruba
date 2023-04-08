package com.sadaqaworks.yorubaquran.quran.presentation.ayah

import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sadaqaworks.yorubaquran.quran.domain.model.Verse
import com.sadaqaworks.yorubaquran.R

class AyahAdapter (
    var onBookmarkClick: (Verse) -> Unit ={},
    var onShareClick: (Verse) -> Unit = {},
    private val fontSize: Float = 20F
) : RecyclerView.Adapter<AyahAdapter.AyahViewHolder>() {

    class AyahViewHolder(view:View,fontSize:Float ) : RecyclerView.ViewHolder(view) {
        val arabic: TextView = view.findViewById<TextView>(R.id.ayah_arabic)
        val translation: TextView = view.findViewById<TextView>(R.id.ayah_translation)
        val footnote: TextView? = view.findViewById(R.id.ayah_footnote)
        val divider: View = view.findViewById(R.id.footnote_divider)
        val ayahNo: TextView = view.findViewById(R.id.ayah_number)
        val toggleIcon: ImageButton = view.findViewById(R.id.toggle_footnote)
        val shareIcon: ImageView = view.findViewById(R.id.share_icon)
        val bookmarkIcon: ImageView = view.findViewById(R.id.bookmark_icon)

        init {
            arabic.textSize = fontSize
            translation.textSize = fontSize
            footnote?.textSize = fontSize
        }

    }

    var ayahs :List<Verse> = emptyList()
    var playingSurah: Int? = null

    private var searchText: String = ""

    private fun binds(verse: Verse, holder:AyahViewHolder){
        var isExpanded = false
        val ayahTranslation = verse.translation
        val ayahArabic = verse.arabic
        val annotatedArabic = QuranAnnotated(verse.arabic, searchText)
        val annotatedTranslation = QuranAnnotated(verse.translation, searchText)
        val arabicText = if (searchTextIsEmpty())ayahArabic else annotatedArabic
        val translationText = if(searchTextIsEmpty())ayahTranslation else annotatedTranslation
        Log.d("Append", "This is anno $annotatedTranslation")
        holder.arabic.text = arabicText
        holder.translation.text = translationText
        holder.ayahNo.text = verse.verseId.toString()
        holder.footnote?.text = verse.footnote


    }

    private fun searchTextIsEmpty():Boolean = searchText.isEmpty() || searchText.isBlank()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AyahViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ayah_layout,parent,false)
        return AyahViewHolder(view,fontSize)
    }

    override fun onBindViewHolder(holder: AyahViewHolder, position: Int) {
        val currentAyah = ayahs[position]
        var isExpanded = false
        currentAyah.footnote?.run {
            holder.toggleIcon.visibility = View.VISIBLE
            holder.toggleIcon.setOnClickListener{
                isExpanded = !isExpanded
                if (isExpanded){
                    holder.toggleIcon.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24)
                    holder.footnote!!.visibility = View.VISIBLE
                    holder.divider.visibility = View.VISIBLE
                }
                else {
                    holder.toggleIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_down_circle_24)
                    holder.footnote!!.visibility = View.GONE
                    holder.divider.visibility = View.GONE
                    holder.toggleIcon.top = holder.translation.bottom

                }
            }

        }
        holder.shareIcon.setOnClickListener{
            onShareClick(currentAyah)
        }
        val isBookmarked = currentAyah.isBookmarked
        if (isBookmarked){
            holder.bookmarkIcon.setImageResource(R.drawable.ic_baseline_bookmark_24)
        }
        else{
            holder.bookmarkIcon.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
        }
        holder.bookmarkIcon.setOnClickListener{
            onBookmarkClick(currentAyah)
        }
        binds(currentAyah, holder)
        playingSurah?: kotlin.run {
            changeTextColor(holder,)
        }
        val shouldHighlight = playingSurah != null && playingSurah == currentAyah.id
        if (shouldHighlight){
            changeTextColor(holder, R.color.red)
        }
        else{
            changeTextColor(holder)
        }

    }

    override fun getItemCount(): Int {
        return  ayahs.size
    }

    private fun changeTextColor(holder:AyahViewHolder, color:Int = R.color.deep_green){
        val userColor = holder.itemView.context.getColor(color)
        holder.arabic.setTextColor(userColor)

    }

    private fun QuranAnnotated(
        quran: String,
        searchText: String = "",
    ): Spanned? {

        //  val builder = AnnotatedString.Builder(quran)
        val searchPattern = Regex(searchText, RegexOption.IGNORE_CASE)
        val modifiedText =  quran.replace(searchPattern, "<font color='#FF0000'>$0</font>")
        return  Html.fromHtml(
            modifiedText,
            Html.FROM_HTML_MODE_LEGACY
        )
    }

    fun changeSearchText(searchText: String){
        this.searchText = searchText
        Log.d("Search", "EMPTY ${searchTextIsEmpty()} search text $searchText")
        notifyDataSetChanged()
    }

    fun changeAyahs(ayahs:List<Verse>){
        this.ayahs = ayahs
        notifyDataSetChanged()
    }
    fun changePlayingId(playingId:Int?){
        playingSurah = playingId
        notifyDataSetChanged()
    }


}

