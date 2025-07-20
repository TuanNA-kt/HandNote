package com.example.noteapp.model

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "body")
    val body: String,

    // 🔗 Ảnh đính kèm (URI dạng String, bạn có thể dùng Glide để load)
    @ColumnInfo(name = "image_uri")
    val imageUri: String? = null,

    // ✅ Ghi chú kiểu checklist
    @ColumnInfo(name = "is_checklist")
    val isChecklist: Boolean = false,

    // 🧾 Checklist items → convert sang JSON string (sử dụng TypeConverter)
    @ColumnInfo(name = "checklist_items")
    val checklistItems: List<String>? = null,

    // 🕒 Thời gian nhắc lại (đơn vị millis)
    @ColumnInfo(name = "reminder_time")
    val reminderTime: Long? = null,

    // 🏷 Nhãn (tag) như "Work", "Study", lưu dưới dạng List<String>
    @ColumnInfo(name = "labels")
    val labels: List<String>? = null,

    // 📌 Ghim ghi chú lên đầu
    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean = false,

    // ❤️ Yêu thích
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    // 🔐 Khóa ghi chú
    @ColumnInfo(name = "is_locked")
    val isLocked: Boolean = false,

    // 🔑 Mã PIN (hoặc null nếu không có)
    @ColumnInfo(name = "pin_code")
    val pinCode: String? = null,

    // 🕓 Thời gian tạo hoặc chỉnh sửa
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable
