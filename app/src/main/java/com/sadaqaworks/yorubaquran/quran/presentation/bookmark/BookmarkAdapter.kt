package com.sadaqaworks.yorubaquran.quran.presentation.bookmark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.sadaqaworks.yorubaquran.quran.domain.model.Bookmark
import com.sadaqaworks.yorubaquran.R
import com.sadaqaworks.yorubaquran.quran.domain.model.Verse

class BookmarkAdapter(val onBookmarkClick: (verse:Verse ) -> Unit) :
    RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    private var bookmarks: List<Verse> = emptyList()

    class BookmarkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val parent: ConstraintLayout = view.findViewById(R.id.bookmark_layout)
        val ayahFootnote: TextView = view.findViewById(R.id.ayah_footnote)
        val ayahArabic: TextView = view.findViewById(R.id.ayah_arabic)
        val ayahTranslation: TextView = view.findViewById(R.id.ayah_translation)
        val ayahId: TextView = view.findViewById(R.id.ayah_number)
        val footnoteDivider: View = view.findViewById(R.id.footnote_divider)
        val toggleFootnote: ImageButton = view.findViewById(R.id.toggle_footnote)
        val bookmarkIcon: ImageButton = view.findViewById(R.id.bookmark_icon)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.bookmark_layout, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val originalHeight = holder.parent.height
        var expandedHeight: Int = originalHeight
        var isExpanded: Boolean = false
        val bookmark = bookmarks[position]
        bindBookmark(bookmark, holder)
        val footnoteIsNullOrEmpty = bookmark.footnote.isNullOrBlank() || bookmark.footnote.isEmpty()
        val translationView = holder.ayahTranslation
        val translationParam = translationView.layoutParams as ConstraintLayout.LayoutParams
        if (footnoteIsNullOrEmpty) {

            translationParam.bottomToBottom = holder.parent.id
            translationParam.bottomMargin = 20

        } else {
            holder.toggleFootnote.visibility = View.VISIBLE
            translationParam.bottomToTop = holder.toggleFootnote.id
        }

        holder.bookmarkIcon.setOnClickListener {
            onBookmarkClick(
                bookmark

            )
            showDeleteAnimation(holder.itemView)
        }
        holder.toggleFootnote.setOnClickListener {
            isExpanded = !isExpanded
            expandedHeight = holder.parent.height
            if (isExpanded) {
                holder.ayahFootnote.visibility = View.VISIBLE
                holder.footnoteDivider.visibility = View.VISIBLE
                holder.toggleFootnote.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24)
                // holder.parent.animation.
                translationParam.bottomToTop = holder.footnoteDivider.id

            } else {
                holder.ayahFootnote.visibility = View.GONE
                holder.footnoteDivider.visibility = View.GONE
                holder.toggleFootnote.setImageResource(R.drawable.ic_baseline_arrow_drop_down_circle_24)

            }

        }

        setAnimation(holder.parent, 0)

    }

    override fun getItemCount(): Int {
        return bookmarks.size
    }

    fun setBookmark(bookmarks: List<Verse>) {
        this.bookmarks = bookmarks
        notifyDataSetChanged()
    }

    private fun bindBookmark(bookmark: Verse, holder: BookmarkViewHolder) {
        val surahId = bookmark.surahId
        val verseId = bookmark.verseId
        val surahAyah = "Surah $surahId, Verse $verseId"
        holder.ayahId.text = surahAyah
        holder.ayahFootnote.text = bookmark.footnote
        holder.ayahTranslation.text = bookmark.translation
        holder.ayahArabic.text = bookmark.arabic

    }

    private fun setAnimation(view: View, position: Int) {
        val slideLayout = AnimationUtils.loadLayoutAnimation(view.context, R.anim.layout_animation)
        val slideIn = AnimationUtils.loadAnimation(view.context, R.anim.slide_out)
        //  view.startAnimation(slideLayout.animation)
        //  view.startAnimation(slideIn)
    }

    private fun showDeleteAnimation(view: View) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.slide_out)
        animation.duration = 500
        view.startAnimation(animation)

    }

}