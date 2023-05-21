package com.example.linksbox.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)], tableName = "folders")
data class FolderEntity(
    val name: String,
    val description: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
