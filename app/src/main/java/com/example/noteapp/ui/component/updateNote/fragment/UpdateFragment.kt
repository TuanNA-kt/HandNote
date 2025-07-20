package com.example.noteapp.ui.component.updateNote.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentUpdateBinding
import com.example.noteapp.model.Note
import com.example.noteapp.ui.base.BaseFragmentBinding
import com.example.noteapp.ui.component.MainActivity
import com.example.noteapp.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UpdateFragment : BaseFragmentBinding<FragmentUpdateBinding>() {

    private val noteViewModel: NoteViewModel by activityViewModels()

    private lateinit var mView: View
    private lateinit var currentNote: Note
    private val args: UpdateFragmentArgs by navArgs()

    override fun getContentViewId(): Int = R.layout.fragment_update

    override fun initializeViews() {

    }

    override fun registerListeners() {
        binding.fabDone.setOnClickListener {
            updateNote()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.delete_item -> {
                    deleteNote()
                    true
                }
                else -> false
            }
        }
    }

    override fun initializeData() {
        currentNote = args.note!!


        binding.titleEt.setText(currentNote.title)
        binding.noteEt.setText(currentNote.body)
    }

    private fun updateNote() {
        val noteTitle = binding.titleEt.text.toString().trim()
        val noteBody = binding.noteEt.text.toString().trim()
        if (noteTitle.isNotEmpty()) {
            val note = Note(currentNote.id, noteTitle, noteBody)
            mView.findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
            noteViewModel.updateNote(note)
            Toast.makeText(mView.context, "Note updated successfully!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(mView.context, "Please enter the note title!", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteNote() {
        AlertDialog.Builder(mView.context).apply {
            setTitle("DELETE NOTE")
            setMessage("You want to delete this note?")
            setPositiveButton("Yes") { _, _ ->
                noteViewModel.deleteNote(currentNote)
                mView.findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }
}
