package com.example.linksbox.features.links

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.linksbox.R
import com.example.linksbox.features.links.LinksContract.ViewEffect
import com.example.linksbox.features.main.*

@Composable
fun LinksScreen(
    linksViewModel: LinksViewModel,
    folderId: Long,
    onItemClick: (result: String) -> Unit,
    onEditLinkClicked: (Long) -> Unit
) {
    LaunchedEffect(Unit) {
        linksViewModel.fetchLinks(folderId)
    }
    val state by linksViewModel.state.collectAsState()
    if (state.isLoading) {
        CenteredProgressBar()
    } else if (state.links.isNotEmpty()) {
        Links(
            links = state.links,
            onItemClick = {
                onItemClick(it)
            },
            onOpenBrowserClicked = {
                linksViewModel.onOpenBrowserButtonClicked(it)
            },
            onEditLinkClicked = {
                linksViewModel.onEditButtonClicked(it)
            },
            onDeleteLinkClicked = {
                linksViewModel.onDeleteButtonClicked(it)
            }
        )
    } else if (state.failureOrExceptionMessage.isNotEmpty()) {
        CenteredText(
            text = state.failureOrExceptionMessage
        )
    }
    ItemDialogById(
        showDialogData = state.deleteDialog,
        onConfirmClicked = {
            linksViewModel.onDeleteDialogConfirmClicked(it)
        },
        onDismissClicked = {
            linksViewModel.onDeleteDialogDismissClicked()
        }
    )

    ItemDialogById(
        showDialogData = state.openBrowserDialog,
        onConfirmClicked = {
            linksViewModel.onConfirmOpenBrowserClicked(it)
        },
        onDismissClicked = {
            linksViewModel.onOpenBrowserDialogDismissClicked()
        }
    )

    val context = LocalContext.current
    linksViewModel.getCollectedEffect()?.let {
        LaunchedEffect(Unit) {
            when (it) {
                is ViewEffect.OpenUrlInBrowse -> {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
                    )
                }

                is ViewEffect.GoToAddLink -> {
                    onEditLinkClicked(it.linkId)
                }

                is ViewEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            linksViewModel.clearEffect()
        }
    }
}

@Composable
fun Links(
    links: List<LinksContract.LinkEntityWithBitmap>,
    onItemClick: (result: String) -> Unit,
    onOpenBrowserClicked: (Long) -> Unit,
    onEditLinkClicked: (Long) -> Unit,
    onDeleteLinkClicked: (Long) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        itemsIndexed(links) { i1, i2 ->
            Link(
                linkItem = i2,
                position = i1,
                onItemClick = { onItemClick(it) },
                onOpenBrowserClicked = {
                    onOpenBrowserClicked(it)
                },
                onEditLinkClicked = {
                    onEditLinkClicked(it)
                },
                onDeleteLinkClicked = {
                    onDeleteLinkClicked(it)
                }
            )
        }
    }
}

@Composable
fun Link(
    linkItem: LinksContract.LinkEntityWithBitmap,
    position: Int,
    onItemClick: (result: String) -> Unit,
    onOpenBrowserClicked: (Long) -> Unit,
    onEditLinkClicked: (Long) -> Unit,
    onDeleteLinkClicked: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                onItemClick(linkItem.url)
            },
        elevation = 2.dp,
        backgroundColor = if (position % 2 == 0) Color.White else Color.LightGray,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                LinkImage(
                    bitmap = linkItem.bitmap
                )
                Text(
                    text = linkItem.title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = linkItem.description,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = linkItem.url,
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_web_view_24),
                        contentDescription = ContentDescriptions.OPEN_WEB_VIEW,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onItemClick(linkItem.url)
                            }
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_browser_24),
                        contentDescription = ContentDescriptions.OPEN_BROWSER,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onOpenBrowserClicked(linkItem.id)
                            }
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_edit_24),
                        contentDescription = ContentDescriptions.EDIT_LINK,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onEditLinkClicked(linkItem.id)
                            }
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_delete_24),
                        contentDescription = ContentDescriptions.DELETE_LINK,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onDeleteLinkClicked(linkItem.id)
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun LinkImage(
    bitmap: Bitmap?
) {
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = ContentDescriptions.PREVIEW,
            modifier = Modifier
                .aspectRatio(16f/9f)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.ic_preview_error_24),
            contentDescription = ContentDescriptions.PREVIEW,
            modifier = Modifier
                .aspectRatio(16f/9f)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        )
    }
}