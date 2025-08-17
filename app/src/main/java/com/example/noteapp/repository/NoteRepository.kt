package com.example.noteapp.repository

import androidx.lifecycle.LiveData
import com.example.noteapp.database.NoteDAO
import com.example.noteapp.database.NoteDatabase
import com.example.noteapp.model.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(private val noteDao: NoteDAO) {
    suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    suspend fun getNoteById(id: String) = noteDao.getNoteById(id)

    fun getAllNotes() = noteDao.getAllNotes()
    fun getPinnedNotes() = noteDao.getPinnedNotes()
    fun getUnpinnedNotes() = noteDao.getUnpinnedNotes()

    //fun searchNote(query: String) = noteDao.searchNote(query)
    fun searchNote(query: String) : Flow<List<Note>> {
        return if(query.isBlank()) {
            getAllNotes()
        } else {
            val searchQuery = "%${query.trim()}%"
            noteDao.searchNote(searchQuery)
        }
    }
}
