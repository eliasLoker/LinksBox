package com.example.linksbox.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "links",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
)
data class LinkEntity(
    var folderId: Long,
    var url: String,
    var title: String,
    var description: String,
    var imageUrl: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
