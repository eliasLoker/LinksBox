package com.example.linksbox.features.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.linksbox.base.BaseAction
import com.example.linksbox.base.BaseViewEffect
import com.example.linksbox.base.BaseViewModel
import com.example.linksbox.base.BaseViewState

data class DialogData(
    val itemId: Long? = null,
    val isShowDialog: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    val positiveButton: String = "",
    val negativeButton: String = ""
)

@Composable
fun ItemDialogById(
    showDialogData: DialogData,
    onConfirmClicked: (Long?) -> Unit,
    onDismissClicked: () -> Unit
) {
    if (showDialogData.isShowDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismissClicked()
            },
            confirmButton = {
                val buttonText = showDialogData.positiveButton
                if (buttonText.isNotEmpty()) {
                    TextButton(onClick = {
                        onConfirmClicked(showDialogData.itemId)
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
                val titleText = showDialogData.title
                val subtitleText = showDialogData.subtitle
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                ) {
                    if (titleText.isNotEmpty()) {
                        Text(text = titleText)
                    }
                    if (subtitleText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = subtitleText)
                    }
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

@Composable
fun CenteredProgressBar() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun MaxWidthTextField(
    textForLabel: String,
    initialValue: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    TextField(
        value = initialValue,
        onValueChange = {
            onValueChange(it)
        },
        label = { Text(textForLabel) },
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    )
}

@Composable
fun MaxWidthButton(
    text: String,
    onButtonClicked: () -> Unit
) {
    Button(
        onClick = { onButtonClicked() },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(text = text)
    }
}

@Composable
fun CenteredText(
    text: String
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
    }
}

@Composable
fun BaseViewModel<out BaseViewState, out BaseAction, out BaseViewEffect>.getCollectedEffect()
        : BaseViewEffect? = this.effect.collectAsState().value
