package com.example.linksbox.features.folders

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.linksbox.R
import com.example.linksbox.data.entities.FolderEntity
import com.example.linksbox.features.folders.FoldersContract.ViewEffect
import com.example.linksbox.features.main.*

@Composable
fun FoldersScreen(
    foldersViewModel: FoldersViewModel,
    onItemClick: (Long) -> Unit,
    onItemEditClick: (Long) -> Unit,
    onAddFolderButtonClicked: () -> Unit,
) {
    val state by foldersViewModel.state.collectAsState()
    if (state.isLoading) {
        CenteredProgressBar()
    } else if (state.folders.isNotEmpty()) {
        Folders(
            folders = state.folders,
            onItemClick = {
                onItemClick(it)
            },
            onItemEditClick = {
                onItemEditClick(it)
            },
            onItemDeleteClick = {
                foldersViewModel.onDeleteButtonClicked(it)
            },
            onAddFolderButtonClicked = {
                onAddFolderButtonClicked()
            }
        )
    } else if (state.failureOrExceptionMessage.isNotEmpty()) {
        EmptyOrFailureFolders(
            text = state.failureOrExceptionMessage,
            buttonText = stringResource(
                id = R.string.add_folder_button
            ),
            onButtonClicked = {
                onAddFolderButtonClicked()
            }
        )
    }
    ItemDialogById(
        showDialogData = state.editFolderDialog,
        onConfirmClicked = {
            foldersViewModel.onDeleteDialogConfirmClicked(it)
        },
        onDismissClicked = {
            foldersViewModel.onDeleteDialogDismissClicked()
        }
    )
    val context = LocalContext.current
    foldersViewModel.getCollectedEffect().let {
        LaunchedEffect(Unit) {
            when (it) {
                is ViewEffect.GoToFolders -> {
                    onItemClick(it.folderId)
                }
                is ViewEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            foldersViewModel.clearEffect()
        }
    }
}

@Composable
fun Folders(
    folders: List<FolderEntity>,
    onItemClick: (Long) -> Unit,
    onItemEditClick: (Long) -> Unit,
    onItemDeleteClick: (Long) -> Unit,
    onAddFolderButtonClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(folders) { i1, i2 ->
                Folder(
                    folderItem = i2,
                    index = i1,
                    onItemClick = { onItemClick(it) },
                    onItemEditClick = {
                        onItemEditClick(it)
                    },
                    onItemDeleteClick = {
                        onItemDeleteClick(it)
                    }
                )
            }
        }
        MaxWidthButton(
            text = stringResource(id = R.string.add_folder_button),
            onButtonClicked = {
                onAddFolderButtonClicked()
            }
        )
    }
}

@Composable
fun Folder(
    folderItem: FolderEntity,
    index: Int,
    onItemClick: (Long) -> Unit,
    onItemEditClick: (Long) -> Unit,
    onItemDeleteClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = if (index % 2 == 0) Color.White else Color.LightGray,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onItemClick(folderItem.id) }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = folderItem.name,
                    style = MaterialTheme.typography.h6
                )
                if (!folderItem.description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text(
                        text = folderItem.description,
                        style = MaterialTheme.typography.caption
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_edit_24),
                        contentDescription = ContentDescriptions.EDIT_FOLDER,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onItemEditClick(folderItem.id) }
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_delete_24),
                        contentDescription = ContentDescriptions.DELETE_FOLDER,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onItemDeleteClick(folderItem.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyOrFailureFolders(
    text: String,
    buttonText: String,
    onButtonClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        MaxWidthButton(text = buttonText,
            onButtonClicked =  {
                onButtonClicked()
            }
        )
    }
}