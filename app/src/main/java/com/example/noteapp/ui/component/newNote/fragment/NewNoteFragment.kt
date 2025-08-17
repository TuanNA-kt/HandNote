package com.example.noteapp.ui.component.newNote.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentNewNoteBinding
import com.example.noteapp.model.Note
import com.example.noteapp.ui.base.BaseFragmentBinding
import com.example.noteapp.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.richeditor.RichEditor
import jp.wasabeef.richeditor.RichEditor.OnTextChangeListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewNoteFragment : BaseFragmentBinding<FragmentNewNoteBinding>() {

    private val noteViewModel: NoteViewModel by activityViewModels()
    private val args: NewNoteFragmentArgs by navArgs()
    private var noteId: String? = null
    private var note: Note? = null
    private var originalTitle: String = ""
    private var originalContent: String = ""
    private var isNotePinned = false

    override fun getContentViewId(): Int = R.layout.fragment_new_note

    override fun initializeViews() {
        setUpNoteEditor()
    }

    override fun registerListeners() {
        binding.ivBack.setOnClickListener {
            handleBackPressed()
        }

        binding.ivPin.setOnClickListener {
            note?.let { currentNote ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val updatedNote = noteViewModel.togglePin(currentNote)
                        note = updatedNote // Update local reference
                        updatePinUI(updatedNote.isPinned)
                        showToast(if (updatedNote.isPinned) "Note pinned" else "Note unpinned")
                    } catch (e: Exception) {
                        showToast("Error updating pin status")
                    }
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }
    }

    override fun initializeData() {
        noteId = args.noteId
        noteId?.let { notNullId ->
            viewLifecycleOwner.lifecycleScope.launch {
                note = noteViewModel.getNoteById(notNullId)
                note?.let {
                    // Store original values for comparison
                    originalTitle = it.title
                    originalContent = it.htmlContent
                    updatePinUI(it.isPinned)
                    updateEditor()
                }
            }
        }
    }

    private fun updatePinUI(isPinned: Boolean) {
        val pinIcon = if (isPinned) {
            R.drawable.ic_unpin // Show unpin icon when note is pinned
        } else {
            R.drawable.ic_pin   // Show pin icon when note is not pinned
        }
        binding.ivPin.setImageResource(pinIcon)
    }

    private fun handleBackPressed() {
//        val htmlContent = binding.editor.html ?: ""
//        val (title, content) = extractTitleAndContentFromHtml(htmlContent)
//        val plainTextContent = getPlainTextFromHtml(htmlContent)

        val htmlContent = binding.editor.html ?: ""
        val (title, content) = extractTitleAndContentFromHtml(htmlContent)

// tách theo <br>, bỏ phần tử đầu, nối lại
        val plainTextContent = htmlContent
            .split("<br>")
            .drop(1)
            .joinToString("<br>")

        Log.d("SAVENOTE", htmlContent)

        if (hasContentChanged(title, htmlContent)) {
            if (title.isNotEmpty() || content.trim().isNotEmpty()) {
                saveNote(title, htmlContent, plainTextContent)
            }
        }

        val action = NewNoteFragmentDirections.actionNewNoteFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun hasContentChanged(currentTitle: String, currentHtmlContent: String): Boolean {
        if (noteId == null) {
            return currentTitle.isNotEmpty() || getPlainTextFromHtml(currentHtmlContent).trim().isNotEmpty()
        }

        return currentTitle != originalTitle || currentHtmlContent != originalContent
    }

    private fun extractTitleAndContentFromHtml(htmlContent: String): Pair<String, String> {
        if (htmlContent.isEmpty()) return Pair("", "")

        // Remove outer HTML tags and split by div/p tags
        val cleanContent = htmlContent
            .replace("<div><br></div>", "\n")
            .replace("<div>", "\n")
            .replace("</div>", "")
            .replace("<p>", "\n")
            .replace("</p>", "")
            .replace("<br>", "\n")
            .trim()

        val lines = cleanContent.split("\n").filter { it.trim().isNotEmpty() }

        if (lines.isEmpty()) return Pair("", "")

        // First line is title, rest is content
        val title = getPlainTextFromHtml(lines[0]).trim()
        val contentLines = if (lines.size > 1) lines.drop(1) else emptyList()
        val content = contentLines.joinToString("\n")

        return Pair(title, content)
    }

    private fun saveNote(title: String, htmlContent: String, plainTextContent: String) {
        val currentTime = System.currentTimeMillis()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (noteId != null && note != null) {
                    // Update existing note
                    val updatedNote = note!!.copy(
                        title = title,
                        htmlContent = htmlContent,
                        plainTextContent = plainTextContent,
                        updatedAt = currentTime
                    )
                    noteViewModel.updateNote(updatedNote)
                    showToast("Note updated")
                } else {
                    // Create new note
                    val newNote = Note(
                        title = title,
                        htmlContent = htmlContent,
                        plainTextContent = plainTextContent,
                        createdAt = currentTime,
                        updatedAt = currentTime
                    )
                    noteViewModel.insertNote(newNote)
                    showToast("Note saved")
                }
            } catch (e: Exception) {
                showToast("Error saving note: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun getPlainTextFromHtml(htmlContent: String): String {
        return htmlContent
            .replace(Regex("<[^>]*>"), "")
            .replace("&nbsp;", " ")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .trim()
    }

    private fun setUpNoteEditor() {
        val editor = binding.editor
        editor.setEditorHeight(400)
        editor.setEditorFontSize(18)
        editor.setEditorFontColor(Color.BLACK)
        editor.setPadding(16, 16, 16, 16)
        editor.setPlaceholder("Title")


        editor.setOnTextChangeListener { text ->

        }

        // Toolbar actions
        binding.actionUndo.setOnClickListener { editor.undo() }
        binding.actionRedo.setOnClickListener { editor.redo() }
        binding.actionBold.setOnClickListener { editor.setBold() }
        binding.actionItalic.setOnClickListener { editor.setItalic() }
        binding.actionSubscript.setOnClickListener { editor.setSubscript() }
        binding.actionSuperscript.setOnClickListener { editor.setSuperscript() }
        binding.actionStrikethrough.setOnClickListener { editor.setStrikeThrough() }
        binding.actionUnderline.setOnClickListener { editor.setUnderline() }

        binding.actionHeading1.setOnClickListener {
            editor.setHeading(1)
            showToast("Use H1 for title")
        }
        binding.actionHeading2.setOnClickListener { editor.setHeading(2) }
        binding.actionHeading3.setOnClickListener { editor.setHeading(3) }
        binding.actionHeading4.setOnClickListener { editor.setHeading(4) }
        binding.actionHeading5.setOnClickListener { editor.setHeading(5) }
        binding.actionHeading6.setOnClickListener { editor.setHeading(6) }

        binding.actionIndent.setOnClickListener { editor.setIndent() }
        binding.actionOutdent.setOnClickListener { editor.setOutdent() }
        binding.actionAlignLeft.setOnClickListener { editor.setAlignLeft() }
        binding.actionAlignCenter.setOnClickListener { editor.setAlignCenter() }
        binding.actionAlignRight.setOnClickListener { editor.setAlignRight() }
        binding.actionBlockquote.setOnClickListener { editor.setBlockquote() }
        binding.actionInsertBullets.setOnClickListener { editor.setBullets() }

        binding.actionInsertImage.setOnClickListener {
            editor.insertImage(
                "https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg",
                "dachshund", 320
            )
        }

        binding.actionInsertLink.setOnClickListener {
            editor.insertLink("https://github.com/wasabeef", "wasabeef")
        }

        binding.actionInsertCheckbox.setOnClickListener { editor.insertTodo() }
    }

    private fun updateEditor() {
        if (!isAdded || view == null) return

        note?.let { currentNote ->
            val formattedContent = formatNoteForEditor(currentNote)
            binding.editor.html = formattedContent
            binding.editor.focusEditor()
        }
    }

    private fun formatNoteForEditor(note: Note): String {
        if (note.htmlContent.isNotEmpty()) {
            return note.htmlContent
        }

        val titleHtml = if (note.title.isNotEmpty()) {
            "<h1>${note.title}</h1>"
        } else ""

        val contentHtml = if (note.plainTextContent.isNotEmpty()) {
            val htmlContent = note.plainTextContent
                .replace("\n", "<br>")
                .replace(note.title, "")
                .trim()

            if (htmlContent.isNotEmpty()) {
                "<div>$htmlContent</div>"
            } else ""
        } else ""

        return titleHtml + contentHtml
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}