package com.example.noteapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.model.Note
import com.example.noteapp.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NoteViewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel() {

    // Search query state
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Notes result state
    private val _queriedNotes = MutableStateFlow<List<Note>>(emptyList())
    val queriedNotes: StateFlow<List<Note>> = _queriedNotes.asStateFlow()

    // Debounce job for search
    private var searchJob: Job? = null

    fun setQuery(query: String) {
        _query.value = query

        // Cancel previous search
        searchJob?.cancel()

        // Start new search with debounce
        searchJob = viewModelScope.launch {
            delay(300)
            performSearch(query)
        }
    }

    fun loadAllNotes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                noteRepository.getAllNotes().collect { notes ->
                    _queriedNotes.value = notes
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                // Handle error if needed
            }
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val flow = if (query.isBlank()) {
                    noteRepository.getAllNotes()
                } else {
                    noteRepository.searchNote(query)
                }

                flow.collect { notes ->
                    _queriedNotes.value = notes
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                // Handle error if needed
            }
        }
    }

    // CRUD operations
    fun insertNote(note: Note) = viewModelScope.launch {
        noteRepository.insertNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteRepository.deleteNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        noteRepository.updateNote(note)
    }

    // Direct repository access (if still needed)
    fun getAllNotes() = noteRepository.getAllNotes()

    fun searchNote(query: String) = noteRepository.searchNote(query)

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}