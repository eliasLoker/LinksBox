package com.example.linksbox.features.addlink

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.linksbox.R
import com.example.linksbox.data.entities.FolderEntity
import com.example.linksbox.features.addfolder.AddFolder
import com.example.linksbox.features.addlink.AddLinkContract.ViewEffect
import com.example.linksbox.features.main.*

@Composable
fun AddLinkScreen(
    addLinkViewModel: AddLinkViewModel,
    url: String?,
    linkId: Long?,
    goToLinks: (Long) -> Unit,
    goToLinksWithClearBackStack: (Long) -> Unit
) {
    LaunchedEffect(Unit) {
        addLinkViewModel.fetchLinkInfo(url, linkId)
    }
    val state by addLinkViewModel.state.collectAsState()
    if (state.isLoading) {
        CenteredProgressBar()
    } else if (state.hasError) {
        CenteredText(text = stringResource(id = R.string.error_preview_loading))
    } else {
        AddLink(
            url = addLinkViewModel.link.value,
            title = addLinkViewModel.linkTitle.value,
            description = addLinkViewModel.linkDescription.value,
            previewData = state.previewData,
            onBitmapLoaded = { addLinkViewModel.bitmapLoaded(it) },
            onTitleChanged = { addLinkViewModel.linkTitle.value = it },
            onDescriptionChanged = { addLinkViewModel.linkDescription.value = it },
            onSaveButtonClicked = { addLinkViewModel.onSaveClicked() }
        )
    }
    DialogSelectFolder(
        dialogSelectFolderData = state.selectFolderDialog,
        onFolderSelected = {
            addLinkViewModel.onDialogFolderSelectedClicked(it)
        },
        onAddFolderInDialogClicked = {
            addLinkViewModel.onAddFolderDialogClicked()
        },
        onDismiss = {
            addLinkViewModel.onDialogDismissClicked()
        }
    )

    DialogCreateFolder(
        showDialogData = state.createFolderDialog,
        onConfirmClicked = { i1, i2 ->
            addLinkViewModel.onCreateFolderDialogClicked(i1, i2)
        },
        onDismissClicked = {
            addLinkViewModel.onDialogCreateFolderDismissClicked()
        }
    )

    val context = LocalContext.current
    addLinkViewModel.getCollectedEffect()?.let {
        LaunchedEffect(Unit) {
            when (it) {
                is ViewEffect.GoToLinks -> {
                    goToLinks(it.linkId)
                }
                is ViewEffect.GoToLinksWithClearBackStack -> {
                    goToLinksWithClearBackStack(it.folderId)
                }
                is ViewEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            addLinkViewModel.clearEffect()
        }
    }

    DisposableEffect(Unit) {

        onDispose {
            addLinkViewModel.onDispose()
        }
    }
}

@Composable
fun AddLink(
    url: String,
    title: String,
    description: String,
    previewData: AddLinkContract.PreviewData,
    onBitmapLoaded: (Bitmap) -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSaveButtonClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(15.dp),
        elevation = 10.dp,
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .verticalScroll(rememberScrollState())
                .padding(15.dp),
            verticalArrangement = Arrangement.Top
        ) {
            ImagePreview(
                previewData = previewData,
                onBitmapLoaded = { onBitmapLoaded(it) }
            )
            Spacer(modifier = Modifier.padding(top = 16.dp))
            MaxWidthTextField(
                textForLabel = stringResource(id = R.string.link),
                initialValue = url,
                onValueChange = {
                    //do nothing
                },
                enabled = false
            )
            Spacer(modifier = Modifier.padding(top = 16.dp))
            MaxWidthTextField(
                textForLabel = stringResource(id = R.string.title),
                initialValue = title,
                onValueChange = {
                    onTitleChanged(it)
                },
            )
            Spacer(modifier = Modifier.padding(top = 16.dp))
            MaxWidthTextField(
                textForLabel = stringResource(id = R.string.description),
                initialValue = description,
                onValueChange = {
                    onDescriptionChanged(it)
                },
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            MaxWidthButton(
                text = stringResource(id = R.string.save_link),
                onButtonClicked = { onSaveButtonClicked() }
            )
        }
    }
}

@Composable
fun ImagePreview(
    previewData: AddLinkContract.PreviewData,
    onBitmapLoaded: (Bitmap) -> Unit,
) {
    if (previewData.imageUrl != null) {
        val painter = rememberAsyncImagePainter(model = ImageRequest.Builder(LocalContext.current)
            .data(previewData.imageUrl)
            .placeholder(R.drawable.ic_preview_progress_24)
            .listener { _, result ->
                val currentBitmap = result.drawable.toBitmap()
                onBitmapLoaded(currentBitmap)
            }
            .error(R.drawable.ic_preview_error_24)
            .build()
        )
        Image(
            painter = painter,
            contentDescription = ContentDescriptions.PREVIEW,
            alignment = Alignment.Center,
            modifier = Modifier
                .aspectRatio(16f / 9f)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        )
    } else if (previewData.bitmap != null) {
        Image(
            bitmap = previewData.bitmap.asImageBitmap(),
            contentDescription = ContentDescriptions.PREVIEW,
            modifier = Modifier
                .aspectRatio(16f / 9f)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.ic_preview_error_24),
            contentDescription = ContentDescriptions.PREVIEW,
            modifier = Modifier
                .aspectRatio(16f / 9f)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        )
    }
}

@Composable
fun DialogCreateFolder(
    showDialogData: DialogData,
    onConfirmClicked: (String, String) -> Unit,
    onDismissClicked: () -> Unit
) {
    val folderName = mutableStateOf("")
    val description = mutableStateOf("")
    if (showDialogData.isShowDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismissClicked()
            },
            confirmButton = {
                val buttonText = showDialogData.positiveButton
                if (buttonText.isNotEmpty()) {
                    TextButton(onClick = {
                        onConfirmClicked(folderName.value, description.value)
                    }) {
                        Text(text = buttonText)
                    }
                }
            },
            dismissButton = {
                val buttonText = showDialogData.negativeButton
                if (buttonText.isNotEmpty()) {
                    TextButton(onClick = {
                        onDismissClicked()
                    }) {
                        Text(text = buttonText)
                    }
                }
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = showDialogData.title,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                    MaxWidthTextField(
                        textForLabel = stringResource(id = R.string.title),
                        initialValue = folderName.value,
                        onValueChange = {
                            folderName.value = it
                        }
                    )
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                    MaxWidthTextField(
                        textForLabel = stringResource(id = R.string.description),
                        initialValue = description.value,
                        onValueChange = {
                            description.value = it
                        }
                    )
                }
            },
            shape = RoundedCornerShape(5.dp),
            backgroundColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(32.dp)
        )
    }
}

data class DialogSelectFolderData(
    val isShowDialog: Boolean = false,
    val folders: List<FolderEntity>? = null
)

@Composable
fun DialogSelectFolder(
    dialogSelectFolderData: DialogSelectFolderData,
    onFolderSelected: (Long) -> Unit,
    onAddFolderInDialogClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    if (dialogSelectFolderData.isShowDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {

            },
            dismissButton = {
                TextButton(onClick = onDismiss)
                { Text(text = stringResource(id = R.string.cancel)) }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.select_folder),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.add_folder_button_with_plus),
                        color = Color.Blue,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                onAddFolderInDialogClicked()
                            }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (dialogSelectFolderData.folders?.isEmpty() == true) {
                        Text(
                            text = stringResource(id = R.string.empty_folders),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        DialogFolders(
                            folders = dialogSelectFolderData.folders!!,
                            onItemClick = {
                                onFolderSelected.invoke(it)
                            }
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun DialogFolders(folders: List<FolderEntity>, onItemClick: (Long) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        itemsIndexed(folders) { i1, i2 ->
            DialogFolderItem(folderItem = i2, index = i1, onItemClick = { onItemClick(it) })
        }
    }
}

@Composable
fun DialogFolderItem(folderItem: FolderEntity, index: Int, onItemClick: (Long) -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = if (index % 2 == 0) Color.White else Color.LightGray,
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        Row(modifier = Modifier.clickable {
            onItemClick(folderItem.id)
        }) {
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
            }
        }
    }
}