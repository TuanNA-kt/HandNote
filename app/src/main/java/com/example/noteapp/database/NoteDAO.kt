package com.example.noteapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.noteapp.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM NOTES ORDER BY timestamp DESC ")
    fun getAllNotes(): Flow<List<Note>>

    @Query("""
        SELECT * FROM NOTES
        WHERE LOWER(title) LIKE LOWER(:query) OR LOWER(body) LIKE LOWER(:query) 
        ORDER BY timestamp DESC 
    """)
    fun searchNote(query: String?): Flow<List<Note>>
}