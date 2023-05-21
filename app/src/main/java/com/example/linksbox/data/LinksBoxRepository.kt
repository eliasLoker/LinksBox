package com.example.linksbox.features.folders

import com.example.linksbox.data.LinksDao
import com.example.linksbox.data.entities.FolderEntity
import com.example.linksbox.data.entities.LinkEntity
import com.example.linksbox.features.addfolder.AddFolderInteractor
import com.example.linksbox.features.addlink.AddLinkInteractor
import com.example.linksbox.features.links.LinksInteractor
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class FoldersRepository(
    private val linksDao: LinksDao
) : FoldersInteractor, LinksInteractor, AddFolderInteractor, AddLinkInteractor {

    private val repositoryDispatcher = Dispatchers.IO

    override suspend fun getLinkItemFromNetwork(url: String): LinkItem =
        withContext(repositoryDispatcher) {
            createRequest(url)
        }


    override suspend fun getFolders(): Flow<List<FolderEntity>> =
        withContext(repositoryDispatcher) {
            linksDao.getFolders()
        }

    override suspend fun insertFolder(folder: FolderEntity): Long =
        withContext(repositoryDispatcher) {
            linksDao.insertFolder(folder)
        }

    override suspend fun getLinks(folderId: Long): Flow<List<LinkEntity>> =
        withContext(repositoryDispatcher) {
            linksDao.getLinksByFolderId(folderId)
        }

    override suspend fun insertLink(
        folderId: Long,
        url: String,
        title: String,
        description: String,
        imageUrl: String
    ) {
        withContext(repositoryDispatcher) {
            linksDao.insertLink(
                LinkEntity(
                    folderId, url, title, description, imageUrl
                )
            )
        }
    }

    private suspend fun createRequest(url: String): LinkItem {
        val client = HttpClient()
        val response: HttpResponse = client.get(url)
        val strResponse = String(response.readBytes())
        return strToXml(strResponse.replace(DOCTYPE, ""), url)
    }

    private fun strToXml(response: String, url: String): LinkItem {
        val bodyHtml = Jsoup.parse(response)
        val meta = bodyHtml.select(DOC_SELECT_OGTAGS)
        val imageUrl = getImageUrl(meta, url)
        val title = getTitle(meta)

        val description = getDescription(meta)

        return LinkItem(
            imageUrl = imageUrl,
            title = title,
            description = description,
            url = url
        )
    }

    private fun getImageUrl(elements: Elements, url: String): String? {
        val imageUrlElement = elements.select(DOC_SELECT_OGTAGS)
            .firstOrNull {
                it.attr(PROPERTY) == TAG_OG_IMAGE
            } ?: return null
        val imageUrl = imageUrlElement.attr(CONTENT)
        if (imageUrl.startsWith(HTTPS_STARTS) || imageUrl.startsWith(HTTP_STARTS)) return imageUrl
        val lastSlashIndex = url.indexOf(SPLITTER, 8)
        return "${url.substring(0, lastSlashIndex + 1)}$imageUrl"
    }

    private fun getTitle(elements: Elements): String? {
        val title = elements.select(DOC_SELECT_OGTAGS)
            .firstOrNull {
                it.attr(PROPERTY) == TAG_OG_TITLE
            } ?: return null
        return title.attr(CONTENT)
    }

    private fun getDescription(elements: Elements): String? {
        val description = elements.select(DOC_SELECT_OGTAGS)
            .firstOrNull {
                it.attr(PROPERTY) == TAG_OG_DESCRIPTION
            } ?: return null
        return description.attr(CONTENT)
    }

    override suspend fun deleteLinkById(id: Long) {
        withContext(repositoryDispatcher) {
            linksDao.deleteLinkById(id)
        }
    }

    override suspend fun deleteFolderById(folderId: Long) {
        withContext(repositoryDispatcher) {
            linksDao.deleteFolderById(folderId)
        }
    }

    override suspend fun getLinkById(linkId: Long): LinkEntity {
        return withContext(repositoryDispatcher) {
            linksDao.getLinkById(linkId)
        }
    }

    override suspend fun getFolderById(id: Long): FolderEntity {
        return withContext(repositoryDispatcher) {
            linksDao.getFolderById(id)
        }
    }

    override suspend fun updateProjectById(folderId: Long, name: String, description: String) {
        return withContext(repositoryDispatcher) {
            linksDao.updateFolderById(folderId, name, description)
        }
    }

    override suspend fun updateLinkById(linkId: Long, title: String, description: String) {
        return withContext(repositoryDispatcher) {
            linksDao.updateLinkById(linkId, title, description)
        }
    }

    companion object {

        private const val DOC_SELECT_OGTAGS = "meta[property^=og:]"
        private const val DOCTYPE = "<!DOCTYPE html>"
        private const val PROPERTY = "property"
        private const val CONTENT = "CONTENT"
        private const val TAG_OG_IMAGE = "og:image"
        private const val TAG_OG_TITLE = "og:title"
        private const val TAG_OG_DESCRIPTION = "og:description"
        private const val HTTPS_STARTS = "https://"
        private const val HTTP_STARTS = "http://"
        private const val SPLITTER = "/"
    }
}

data class LinkItem(
    val url: String,
    val title: String?,
    val description: String?,
    val imageUrl: String?
)