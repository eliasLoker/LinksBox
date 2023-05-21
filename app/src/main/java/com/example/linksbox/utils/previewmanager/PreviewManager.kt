package com.example.linksbox.utils.previewmanager

import android.graphics.Bitmap

interface PreviewManager {

    fun downloadPreviewToInternalStorage(bitmap: Bitmap) : String

    fun getPreviewFromInternalStorage(filename: String) : Bitmap?

    fun deletePreview(fileName: String)

    fun deletePreviewList(fileNames: List<String>)
}