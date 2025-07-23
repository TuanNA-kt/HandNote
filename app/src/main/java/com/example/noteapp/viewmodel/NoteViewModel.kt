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
import kotlinx.coroutines.Dispatchers
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

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _queriedNotes = MutableStateFlow<List<Note>>(emptyList())
    val queriedNotes: StateFlow<List<Note>> = _queriedNotes.asStateFlow()

    private var searchJob: Job? = null

    fun setQuery(query: String) {
        _query.value = query

        searchJob?.cancel()

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
            }
        }
    }

    fun insertNote(note: Note) = viewModelScope.launch {
        noteRepository.insertNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteRepository.deleteNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        noteRepository.updateNote(note)
    }

    suspend fun getNoteById(id: String): Note? {
        return noteRepository.getNoteById(id)
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}