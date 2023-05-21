package com.example.linksbox.utils.stringprovider

import android.content.Context
import com.example.linksbox.R

class StringProviderImpl(val context: Context) : StringProvider {

    override fun getStringByStringRes(stringRes: StringRes, vararg args: String?): String
    = String.format(
            format = context.resources.getString(stringRes.id),
            args = args
    )

}

enum class StringRes(
    @androidx.annotation.StringRes val id: Int
) {
    APP_NAME(R.string.app_name),
    DELETE(R.string.delete),
    CANCEL(R.string.cancel),
    DIALOG_DELETE_FOLDER_MESSAGE(R.string.dialog_delete_folder_message),
    DIALOG_DELETE_LINK_MESSAGE(R.string.dialog_delete_link_message),
    OPEN_IN_BROWSER_TITLE(R.string.dialog_open_in_browser_title),
    OPEN_IN_BROWSER_MESSAGE(R.string.dialog_open_in_browser_message),
    OPEN(R.string.open),
    ERROR_OPEN_LINK(R.string.error_open_link),
    ERROR_DELETE_LINK(R.string.error_delete_link),
    EMPTY_FOLDERS(R.string.empty_folders),
    ERROR_GETTING_FOLDERS(R.string.error_getting_folders),
    DELETE_FOLDER_ERROR(R.string.error_delete_folder),
    ERROR_SAVE_PREVIEW(R.string.error_save_preview),
    ERROR_UPDATE_PREVIEW(R.string.error_update_preview),
    EMPTY_LINKS(R.string.empty_links),
    ERROR_GETTING_LINKS(R.string.error_getting_links),
    CREATE_FOLDER(R.string.create_folder),
    CREATE(R.string.create)
}