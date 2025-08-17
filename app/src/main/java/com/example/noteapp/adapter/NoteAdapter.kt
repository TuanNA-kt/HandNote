package com.example.noteapp.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.databinding.NoteLayoutBinding
import com.example.noteapp.model.Note
import com.example.noteapp.ui.component.home.fragment.HomeFragmentDirections

/**
 * Best Practice RecyclerView Adapter with Headers
 * - Sử dụng ListAdapter thay vì RecyclerView.Adapter
 * - DiffUtil được configure đúng cho DisplayItem
 * - Immutable data structures
 * - Proper ViewHolder pattern
 */
class NoteAdapter : ListAdapter<NoteAdapter.DisplayItem, RecyclerView.ViewHolder>(DisplayItemDiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_NOTE = 1
    }

    /**
     * Sealed class cho type-safe items
     * Sử dụng data class để có equals/hashCode tự động
     */
    sealed class DisplayItem {
        data class Header(val title: String) : DisplayItem()
        data class NoteItem(val note: Note) : DisplayItem()
    }

    /**
     * DiffCallback optimized cho DisplayItem
     */
    private class DisplayItemDiffCallback : DiffUtil.ItemCallback<DisplayItem>() {
        override fun areItemsTheSame(oldItem: DisplayItem, newItem: DisplayItem): Boolean {
            return when {
                oldItem is DisplayItem.Header && newItem is DisplayItem.Header ->
                    oldItem.title == newItem.title
                oldItem is DisplayItem.NoteItem && newItem is DisplayItem.NoteItem ->
                    oldItem.note.id == newItem.note.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: DisplayItem, newItem: DisplayItem): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: DisplayItem, newItem: DisplayItem): Any? {
            // Có thể implement partial updates nếu cần
            return when {
                oldItem is DisplayItem.NoteItem && newItem is DisplayItem.NoteItem -> {
                    val changes = mutableListOf<String>()
                    if (oldItem.note.title != newItem.note.title) changes.add("title")
                    if (oldItem.note.plainTextContent != newItem.note.plainTextContent) changes.add("content")
                    if (oldItem.note.isPinned != newItem.note.isPinned) changes.add("pin")
                    if (changes.isNotEmpty()) changes else null
                }
                else -> null
            }
        }
    }

    /**
     * Public method để submit danh sách notes
     * Sẽ tự động build mixed list và submit
     */
    fun submitNotes(notes: List<Note>) {
        val displayItems = buildDisplayItems(notes)
        submitList(displayItems)
    }

    /**
     * Build mixed list từ notes
     * Pure function - không có side effects
     */
    private fun buildDisplayItems(notes: List<Note>): List<DisplayItem> {
        val pinnedNotes = notes.filter { it.isPinned }
        val otherNotes = notes.filter { !it.isPinned }

        return buildList {
            // Add pinned section
            if (pinnedNotes.isNotEmpty()) {
                add(DisplayItem.Header("Pinned"))
                addAll(pinnedNotes.map { DisplayItem.NoteItem(it) })
            }

            // Add others section
            if (otherNotes.isNotEmpty()) {
                add(DisplayItem.Header("Others"))
                addAll(otherNotes.map { DisplayItem.NoteItem(it) })
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DisplayItem.Header -> TYPE_HEADER
            is DisplayItem.NoteItem -> TYPE_NOTE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder.create(parent)
            TYPE_NOTE -> NoteViewHolder.create(parent)
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is DisplayItem.Header -> (holder as HeaderViewHolder).bind(item.title)
            is DisplayItem.NoteItem -> (holder as NoteViewHolder).bind(item.note)
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val layoutParams = holder.itemView.layoutParams
        if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            layoutParams.isFullSpan = holder is HeaderViewHolder
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            // Handle partial updates
            val item = getItem(position)
            if (item is DisplayItem.NoteItem && holder is NoteViewHolder) {
                holder.bindWithPayload(item.note, payloads.first() as List<String>)
            }
        }
    }

    /**
     * Header ViewHolder với static factory method
     */
    class HeaderViewHolder private constructor(
        private val textView: TextView
    ) : RecyclerView.ViewHolder(textView) {

        fun bind(title: String) {
            textView.text = title
        }

        companion object {
            fun create(parent: ViewGroup): HeaderViewHolder {
                val textView = TextView(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(32, 16, 32, 16)
                    textSize = 18f
                    setTypeface(null, Typeface.BOLD)
                    // Set accessibility
                    contentDescription = "Section header"
                }
                return HeaderViewHolder(textView)
            }
        }
    }

    /**
     * Note ViewHolder với static factory method
     */
    class NoteViewHolder private constructor(
        private val binding: NoteLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.tvTitle.text = note.title
            binding.editor.html = note.plainTextContent
            binding.editor.isEnabled = false

            // Set click listener
            itemView.setOnClickListener {
                val direction = HomeFragmentDirections.actionHomeFragmentToNewNoteFragment(note.id)
                it.findNavController().navigate(direction)
            }

            // Accessibility
            itemView.contentDescription = "Note: ${note.title}"
        }

        /**
         * Bind với payload để partial update
         */
        fun bindWithPayload(note: Note, changes: List<String>) {
            changes.forEach { change ->
                when (change) {
                    "title" -> binding.tvTitle.text = note.title
                    "content" -> binding.editor.html = note.plainTextContent
                    "pin" -> {
                        // Update pin indicator nếu có
                        // binding.pinIndicator.isVisible = note.isPinned
                    }
                }
            }

            // Update accessibility
            itemView.contentDescription = "Note: ${note.title}"
        }

        companion object {
            fun create(parent: ViewGroup): NoteViewHolder {
                val binding = NoteLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return NoteViewHolder(binding)
            }
        }
    }
}