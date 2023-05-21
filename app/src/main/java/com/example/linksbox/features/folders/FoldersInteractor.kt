package com.example.linksbox.features.folders

import com.example.linksbox.data.entities.FolderEntity
import com.example.linksbox.data.entities.LinkEntity
import kotlinx.coroutines.flow.Flow

interface FoldersInteractor {

    suspend fun getFolders() : Flow<List<FolderEntity>>

    suspend fun deleteFolderById(folderId: Long)

    suspend fun getLinks(folderId: Long) : Flow<List<LinkEntity>>
}