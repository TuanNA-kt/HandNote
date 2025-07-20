package com.example.noteapp.ui.component.home.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.ui.component.MainActivity
import com.example.noteapp.R
import com.example.noteapp.adapter.NoteAdapter
import com.example.noteapp.databinding.FragmentHomeBinding
import com.example.noteapp.model.Note
import com.example.noteapp.ui.base.BaseFragmentBinding
import com.example.noteapp.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragmentBinding<FragmentHomeBinding>() {
    private val noteViewModel: NoteViewModel by activityViewModels()
    private lateinit var noteAdapter: NoteAdapter

    override fun getContentViewId(): Int = R.layout.fragment_home

    override fun initializeViews() {
        setUpRecyclerView()
        // Observe notes
        viewLifecycleOwner.lifecycleScope.launch {
            noteViewModel.queriedNotes.collect { notes ->
                noteAdapter.differ.submitList(notes)
                updateUI(notes)
            }
        }

        // Observe loading
//        viewLifecycleOwner.lifecycleScope.launch {
//            noteViewModel.isLoading.collect { isLoading ->
//                progressBar.isVisible = isLoading
//            }
//        }

        // Set search query
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                noteViewModel.setQuery(newText.orEmpty())
                return true
            }
        })

//        val menuSearch = binding.homeToolbar.menu.findItem(R.id.search_item).actionView as SearchView
//        menuSearch.setOnQueryTextListener(this)
    }

    override fun registerListeners() {
        binding.fabAdd.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
        }
    }

    override fun initializeData() {
        noteViewModel.loadAllNotes()
    }


    private fun setUpRecyclerView() {
        noteAdapter = NoteAdapter()
        binding.noteRv.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = noteAdapter
        }

//        activity?.let {
//            noteViewModel.getAllNotes().collect { note ->
//                noteAdapter.differ.submitList(note)
//                updateUI(note)
//            }
//        }
    }

    private fun updateUI(note : List<Note>?) {
        if (note != null) {
            if(note.isEmpty()) {
                binding.clNoNote.visibility = View.VISIBLE
                binding.noteRv.visibility = View.GONE
            }else {
                binding.clNoNote.visibility = View.GONE
                binding.noteRv.visibility = View.VISIBLE
            }
        }
    }

    private fun searchNote(query: String?) {
        val searchQuery = "%$query%"
//        noteViewModel.searchNote(searchQuery).observe(this) { list ->
//            noteAdapter.differ.submitList(list)
//        }
    }
}

