package com.example.linksbox.features.main

sealed class NavigationRoutes(
    val route: String
) {

    object Folders : NavigationRoutes(route = "folders")

    object Links : NavigationRoutes(route = "links?$FOLDER_ID={$FOLDER_ID}") {
        fun createRoute(folderId: Long) : String = "links?$FOLDER_ID=$folderId"
    }

    object AddLink : NavigationRoutes(route = "addLink?$LINK={$LINK}&$LINK_ID={$LINK_ID}") {
        fun createRoute(linkId: Long?, link:String?) : String = "addLink?&$LINK_ID=${linkId ?: ""}&$LINK=${link ?: ""}"
    }

    object AddFolder : NavigationRoutes(route = "addFolder?$FOLDER_ID={$FOLDER_ID}") {
        fun createRoute(folderId: Long?) : String = "addFolder?$FOLDER_ID=${folderId ?: ""}"
    }

    object WebView : NavigationRoutes(route = "webView?$LINK={$LINK}") {
        fun createRoute(link: String) : String = "webView?$LINK=$link"
    }

    object Splash : NavigationRoutes(route = "splash")

    companion object {
        const val FOLDER_ID = "folderId"

        const val LINK_ID = "linkId"

        const val LINK = "link"
    }
}
