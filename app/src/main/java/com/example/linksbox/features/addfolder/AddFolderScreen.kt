package com.example.linksbox.features.addfolder

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.linksbox.R
import com.example.linksbox.features.addfolder.AddFolderContract.ViewEffect
import com.example.linksbox.features.main.*

@Composable
fun AddFolderScreen(
    addFolderViewModel: AddFolderViewModel,
    folderId: Long?,
    goToFolders: () -> Unit
) {
    LaunchedEffect(Unit) {
        addFolderViewModel.fetchFolder(folderId)
    }
    val state by addFolderViewModel.state.collectAsState()
    if (state.isLoading) {
        CenteredProgressBar()
    } else if (state.hasError) {
        CenteredText(text = stringResource(id = R.string.error_preview_loading))
    } else {
        AddFolder(
            folderName = addFolderViewModel.folderNameValue.value,
            onFolderNameChanged = {
                addFolderViewModel.folderNameValue.value = it
            },
            description = addFolderViewModel.folderDescriptionValue.value,
            onDescriptionChanged = {
                addFolderViewModel.folderDescriptionValue.value = it
            },
            onSaveButtonClicked = {
                addFolderViewModel.onSaveButtonClicked()
            }
        )
    }
    val context = LocalContext.current
    addFolderViewModel.getCollectedEffect()?.let {
        LaunchedEffect(Unit) {
            when (it) {
                is ViewEffect.GoToFolders -> {
                    goToFolders()
                }
                is ViewEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_save_link_in_folder),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            addFolderViewModel.clearEffect()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            addFolderViewModel.onDispose()
        }
    }
}

@Composable
fun AddFolder(
    folderName: String,
    onFolderNameChanged: (String) -> Unit,
    description: String,
    onDescriptionChanged: (String) -> Unit,
    onSaveButtonClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(15.dp),
        elevation = 10.dp,
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.Center
        ) {
            MaxWidthTextField(
                textForLabel = stringResource(id = R.string.title),
                initialValue = folderName,
                onValueChange = {
                    onFolderNameChanged(it)
                }
            )
            Spacer(modifier = Modifier.padding(top = 16.dp))
            MaxWidthTextField(
                textForLabel = stringResource(id = R.string.description),
                initialValue = description,
                onValueChange = {
                    onDescriptionChanged(it)
                }
            )
            Spacer(modifier = Modifier.padding(top = 16.dp))
            MaxWidthButton(text = stringResource(id = R.string.create_folder), onButtonClicked =  {
                onSaveButtonClicked()
            })
        }
    }
}
