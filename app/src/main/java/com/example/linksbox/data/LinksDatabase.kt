package com.example.linksbox.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.linksbox.data.entities.FolderEntity
import com.example.linksbox.data.entities.LinkEntity

@Database(entities = [FolderEntity::class, LinkEntity::class], version = 1)
abstract class LinksDatabase : RoomDatabase() {

    abstract fun getLinksDao() : LinksDao
}