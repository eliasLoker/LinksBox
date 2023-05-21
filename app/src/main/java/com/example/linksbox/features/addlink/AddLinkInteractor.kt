package com.example.linksbox.features.addlink

import com.example.linksbox.data.entities.FolderEntity
import com.example.linksbox.data.entities.LinkEntity
import com.example.linksbox.features.folders.LinkItem
import kotlinx.coroutines.flow.Flow

interface AddLinkInteractor {

    suspend fun getLinkItemFromNetwork(url: String) : LinkItem

    suspend fun insertLink(folderId: Long, url: String, title: String, description: String, imageUrl: String)

    suspend fun getFolders() : Flow<List<FolderEntity>>

    suspend fun getLinkById(linkId: Long) : LinkEntity

    suspend fun updateLinkById(linkId: Long, title: String, description: String)

    suspend fun insertFolder(folder: FolderEntity) : Long
}