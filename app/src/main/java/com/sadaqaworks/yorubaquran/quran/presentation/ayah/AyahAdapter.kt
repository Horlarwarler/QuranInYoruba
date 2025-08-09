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

class AyahAdapter(
    var onBookmarkClick: (Verse) -> Unit = {},
    var onShareClick: (Verse) -> Unit = {},
    private val fontSize: Float = 20F,
    var surahNumber: Int // pass in from Fragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_BISMILLAH = 0
        private const val TYPE_VERSE = 1
    }

    class AyahViewHolder(view: View, fontSize: Float) : RecyclerView.ViewHolder(view) {
        val arabic: TextView = view.findViewById(R.id.ayah_arabic)
        val translation: TextView = view.findViewById(R.id.ayah_translation)
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

    class BismillahViewHolder(view: View) : RecyclerView.ViewHolder(view)

    var ayahs: List<Verse> = emptyList()
    var playingSurah: Int? = null
    private var searchText: String = ""

    override fun getItemViewType(position: Int): Int {
        // Show Bismillah only if surah is NOT 1 or 9 and position == 0
        return if (showBismillahHeader() && position == 0) TYPE_BISMILLAH else TYPE_VERSE
    }

    private fun showBismillahHeader(): Boolean {
        return surahNumber != 1 && surahNumber != 9
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_BISMILLAH) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bismillah, parent, false)
            BismillahViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.ayah_layout, parent, false)
            AyahViewHolder(view, fontSize)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AyahViewHolder) {
            val ayahIndex = if (showBismillahHeader()) position - 1 else position
            val currentAyah = ayahs[ayahIndex]

            currentAyah.footnote?.run {
                holder.toggleIcon.visibility = View.VISIBLE
                holder.toggleIcon.setOnClickListener {
                    val isExpanded = holder.footnote?.visibility != View.VISIBLE
                    if (isExpanded) {
                        holder.toggleIcon.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24)
                        holder.footnote?.visibility = View.VISIBLE
                        holder.divider.visibility = View.VISIBLE
                    } else {
                        holder.toggleIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_down_circle_24)
                        holder.footnote?.visibility = View.GONE
                        holder.divider.visibility = View.GONE
                    }
                }
            }

            holder.shareIcon.setOnClickListener { onShareClick(currentAyah) }

            val isBookmarked = currentAyah.isBookmarked
            holder.bookmarkIcon.setImageResource(
                if (isBookmarked) R.drawable.ic_baseline_bookmark_24
                else R.drawable.ic_baseline_bookmark_border_24
            )
            holder.bookmarkIcon.setOnClickListener { onBookmarkClick(currentAyah) }

            binds(currentAyah, holder)

            val shouldHighlight = playingSurah != null && playingSurah == currentAyah.id
            changeTextColor(holder, if (shouldHighlight) R.color.red else R.color.deep_green)
        }
    }

    override fun getItemCount(): Int {
        return ayahs.size + if (showBismillahHeader()) 1 else 0
    }

    private fun binds(verse: Verse, holder: AyahViewHolder) {
        val annotatedArabic = QuranAnnotated(verse.arabic, searchText)
        val annotatedTranslation = QuranAnnotated(verse.translation, searchText)
        holder.arabic.text = if (searchTextIsEmpty()) verse.arabic else annotatedArabic
        holder.translation.text = if (searchTextIsEmpty()) verse.translation else annotatedTranslation
        holder.ayahNo.text = verse.verseId.toString()
        holder.footnote?.text = verse.footnote
    }

    private fun searchTextIsEmpty() = searchText.isEmpty() || searchText.isBlank()

    private fun changeTextColor(holder: AyahViewHolder, color: Int = R.color.deep_green) {
        val userColor = holder.itemView.context.getColor(color)
        holder.arabic.setTextColor(userColor)
    }

    private fun QuranAnnotated(quran: String, searchText: String): Spanned {
        val searchPattern = Regex(searchText, RegexOption.IGNORE_CASE)
        val modifiedText = quran.replace(searchPattern, "<font color='#FF0000'>$0</font>")
        return Html.fromHtml(modifiedText, Html.FROM_HTML_MODE_LEGACY)
    }

    fun changeSearchText(searchText: String) {
        this.searchText = searchText
        notifyDataSetChanged()
    }

    fun changeAyahs(ayahs: List<Verse>) {
        this.ayahs = ayahs
        notifyDataSetChanged()
    }

    fun changePlayingId(playingId: Int?) {
        playingSurah = playingId
        notifyDataSetChanged()
    }
}

