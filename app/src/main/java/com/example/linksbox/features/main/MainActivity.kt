package com.example.linksbox.features.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.linksbox.features.addfolder.AddFolderScreen
import com.example.linksbox.features.addfolder.AddFolderViewModel
import com.example.linksbox.features.addlink.AddLinkScreen
import com.example.linksbox.features.addlink.AddLinkViewModel
import com.example.linksbox.features.folders.FoldersScreen
import com.example.linksbox.features.folders.FoldersViewModel
import com.example.linksbox.features.links.LinksScreen
import com.example.linksbox.features.links.LinksViewModel
import com.example.linksbox.features.main.MainContract.ViewEffect
import com.example.linksbox.features.splash.SplashScreen
import com.example.linksbox.features.webview.WebViewScreen
import com.example.linksbox.ui.theme.LinksBoxTheme
import org.koin.androidx.compose.getViewModel

class MainActivity : ComponentActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            mainViewModel = getViewModel()
            LinksBoxTheme {
                val navController = rememberNavController()
                MainContainer(
                    intent = intent,
                    navController = navController
                )
                mainViewModel.getCollectedEffect()?.let {
                    LaunchedEffect(Unit) {
                        when (it) {
                            is ViewEffect.OpenAddLinkScreen -> {
                                navController.navigate(
                                    NavigationRoutes.AddLink.createRoute(null, it.link)
                                )
                            }
                        }
                        mainViewModel.clearEffect()
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        mainViewModel.onNewIntent(
            link = intent?.getStringExtra(Intent.EXTRA_TEXT)
        )
    }
}

@Composable
fun MainContainer(
    intent: Intent,
    navController: NavHostController,
    foldersViewModel: FoldersViewModel = getViewModel(),
    linksViewModel: LinksViewModel = getViewModel(),
    addFolderViewModel: AddFolderViewModel = getViewModel(),
    addLinkViewModel: AddLinkViewModel = getViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Splash.route,
    ) {
        composable(
            route = NavigationRoutes.Folders.route,
        ) {
            FoldersScreen(
                foldersViewModel = foldersViewModel,
                onItemClick = {
                    navController.navigate(
                        route = NavigationRoutes.Links.createRoute(it)
                    )
                },
                onAddFolderButtonClicked = {
                    navController.navigate(
                        route = NavigationRoutes.AddFolder.createRoute(null)
                    )
                },
                onItemEditClick = {
                    navController.navigate(
                        route = NavigationRoutes.AddFolder.createRoute(it)
                    )
                },
            )
        }
        composable(
            route = NavigationRoutes.Links.route,
            arguments = listOf(
                navArgument(
                    NavigationRoutes.FOLDER_ID
                ) {
                    type = NavType.LongType
                }
            )
        ) {
            LinksScreen(
                linksViewModel = linksViewModel,
                folderId = it.arguments?.getLong(NavigationRoutes.FOLDER_ID) ?: 0L,
                onItemClick = {
                    navController.navigate(
                        route = NavigationRoutes.WebView.createRoute(it)
                    )
                },
                onEditLinkClicked = {
                    navController.navigate(
                        route = NavigationRoutes.AddLink.createRoute(it, null)
                    )
                }
            )
        }
        composable(
            route = NavigationRoutes.AddLink.route,
            arguments = listOf(
                navArgument(NavigationRoutes.LINK) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(NavigationRoutes.LINK_ID) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            AddLinkScreen(
                addLinkViewModel = addLinkViewModel,
                url = it.arguments?.getString(NavigationRoutes.LINK),
                linkId = it.arguments?.getString(NavigationRoutes.LINK_ID)?.toLongOrNull(),
                goToLinks = {
                    navController.popBackStack()
                },
                goToLinksWithClearBackStack = {
                    navController.navigate(route = NavigationRoutes.Folders.route) {
                        popUpTo(NavigationRoutes.AddLink.route) {
                            inclusive = true
                        }
                    }
                    navController.navigate(NavigationRoutes.Links.createRoute(it))
                }
            )
        }
        composable(
            route = NavigationRoutes.AddFolder.route,
            arguments = listOf(
                navArgument(NavigationRoutes.FOLDER_ID) {
                    type = NavType.StringType
                    nullable = true
                }
            ),
        ) {
            AddFolderScreen(
                addFolderViewModel = addFolderViewModel,
                folderId = it.arguments?.getString(NavigationRoutes.FOLDER_ID)?.toLongOrNull(),
                goToFolders = {
                    navController.navigate(NavigationRoutes.Folders.route) {
                        popUpTo(route = NavigationRoutes.AddLink.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = NavigationRoutes.WebView.route,
            arguments = listOf(
                navArgument(NavigationRoutes.LINK) {
                    type = NavType.StringType
                }
            )
        ) {
            WebViewScreen(
                url = it.arguments?.getString(NavigationRoutes.LINK) ?: ""
            )
        }
        composable(
            route = NavigationRoutes.Splash.route
        ) {
            val link = intent.getStringExtra(Intent.EXTRA_TEXT)
            SplashScreen()
            navController.navigate(
                route = when (link) {
                    null -> NavigationRoutes.Folders.route
                    else -> NavigationRoutes.AddLink.createRoute(null, link)
                }
            ) {
                popUpTo(route = NavigationRoutes.Splash.route) {
                    inclusive = true
                }
            }
        }
    }
}