package com.example.linksbox.utils.previewmanager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.example.linksbox.utils.stringprovider.StringProvider
import com.example.linksbox.utils.stringprovider.StringRes
import java.io.File
import java.io.FileOutputStream

class PreviewManagerImpl(
    private val stringProvider: StringProvider
) : PreviewManager {

    override fun downloadPreviewToInternalStorage(bitmap: Bitmap): String {
        val myDirectory = File(getPathName())
        myDirectory.mkdirs()
        val imageName = "${System.currentTimeMillis()/1000}$JPG_POSTFIX"
        val image = File(myDirectory, imageName)
        if (image.exists()) image.delete()
        val fileOutputStream = FileOutputStream(image)
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPG_QUALITY, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
        return imageName
    }

    override fun getPreviewFromInternalStorage(filename: String): Bitmap? {
        val file = File(getPathName(), filename)
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    override fun deletePreview(fileName: String) {
        val myDirectory = File(getPathName())
        myDirectory.mkdirs()
        val image = File(myDirectory, fileName)
        if (image.exists()) image.delete()
    }

    override fun deletePreviewList(fileNames: List<String>) {
        val myDirectory = File(getPathName())
        myDirectory.mkdirs()
        fileNames.forEach {
            val currentImage = File(myDirectory, it)
            if (currentImage.exists()) currentImage.delete()
        }
    }

    private fun getPathName() : String {
        val path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return "$path/${stringProvider.getStringByStringRes(StringRes.APP_NAME)}"
    }

    companion object {
        private const val JPG_QUALITY = 100
        private const val JPG_POSTFIX = ".jpg"
    }
}