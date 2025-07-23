package com.example.noteapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.noteapp.databinding.NoteLayoutBinding
import com.example.noteapp.model.Note
import com.example.noteapp.ui.component.home.fragment.HomeFragmentDirections
import kotlin.random.Random

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    class NoteViewHolder(val itemBinding: NoteLayoutBinding) : ViewHolder(itemBinding.root)
        private val differCallBack = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.id == newItem.id &&
                        oldItem.title == newItem.title &&
                        oldItem.htmlContent == newItem.htmlContent
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }

        }
    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(NoteLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = differ.currentList[position]
        val color = Color.argb(255, Random.nextInt(256),
            Random.nextInt(256), Random.nextInt(256))

        holder.itemBinding.tvTitle.text = currentNote.title
        holder.itemBinding.noteTv.text = currentNote.htmlContent
        //holder.itemBinding.noteColor.setBackgroundColor(color)

        holder.itemView.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToNewNoteFragment(currentNote.id)
            it.findNavController().navigate(direction)
        }
    }
}