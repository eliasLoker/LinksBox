package com.example.linksbox.features.addfolder

import com.example.linksbox.data.entities.FolderEntity

interface AddFolderInteractor {

    suspend fun insertFolder(folder: FolderEntity) : Long

    suspend fun getFolderById(id: Long) : FolderEntity

    suspend fun updateProjectById(folderId: Long, name: String, description: String)
}