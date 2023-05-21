package com.example.linksbox.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.linksbox.data.entities.FolderEntity
import com.example.linksbox.data.entities.LinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LinksDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertFolder(folder: FolderEntity) : Long

    @Query("SELECT * FROM folders ORDER BY id ASC")
    abstract fun getFolders() : Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE id =:folderId")
    abstract fun getFolderById(folderId: Long) : FolderEntity

    @Query("UPDATE folders SET name=:name, description=:description WHERE id=:folderId ")
    abstract fun updateFolderById(folderId: Long, name: String, description: String)

    @Query("DELETE FROM folders WHERE id =:folderId")
    abstract fun deleteFolderById(folderId: Long)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertLink(linkEntity: LinkEntity) : Long

    @Query("SELECT * FROM links WHERE id=:linkId ")
    abstract fun getLinkById(linkId: Long) : LinkEntity

    @Query("SELECT * FROM links WHERE folderId =:folderId ORDER BY id")
    abstract fun getLinksByFolderId(folderId: Long) : Flow<List<LinkEntity>>

    @Query("UPDATE links SET title=:title, description=:description WHERE id=:linkId ")
    abstract fun updateLinkById(linkId: Long, title: String, description: String)

    @Query("DELETE FROM links WHERE id =:id")
    abstract suspend fun deleteLinkById(id: Long)
}