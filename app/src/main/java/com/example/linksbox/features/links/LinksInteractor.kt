package com.example.linksbox.features.links

import com.example.linksbox.data.entities.LinkEntity
import kotlinx.coroutines.flow.Flow

interface LinksInteractor {

    suspend fun getLinks(folderId: Long) : Flow<List<LinkEntity>>

    suspend fun deleteLinkById(id: Long)

    suspend fun getLinkById(linkId: Long) : LinkEntity
}