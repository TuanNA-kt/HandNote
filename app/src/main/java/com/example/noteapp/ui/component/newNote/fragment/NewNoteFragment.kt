package com.example.noteapp.ui.component.newNote.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentNewNoteBinding
import com.example.noteapp.model.Note
import com.example.noteapp.ui.base.BaseFragmentBinding
import com.example.noteapp.ui.component.MainActivity
import com.example.noteapp.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewNoteFragment : BaseFragmentBinding<FragmentNewNoteBinding>() {

    private val noteViewModel: NoteViewModel by activityViewModels()

    override fun getContentViewId(): Int = R.layout.fragment_new_note

    override fun initializeViews() {

    }

    override fun registerListeners() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.save_item -> {
                    saveNote()
                    true
                }
                else -> false
            }
        }
    }

    override fun initializeData() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun saveNote() {
        val noteTitle = binding.titleEt.text.toString().trim()
        val noteBody = binding.noteEt.text.toString().trim()
        if (noteTitle.isNotEmpty()) {
            val note = Note(0, noteTitle, noteBody)

            findNavController().navigate(R.id.action_newNoteFragment_to_homeFragment)
            noteViewModel.insertNote(note)
            Toast.makeText(context, "Note saved successfully!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Please enter the note title!", Toast.LENGTH_LONG).show()
        }
    }

}