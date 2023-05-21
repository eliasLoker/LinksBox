package com.example.linksbox.app

import android.app.Application
import androidx.room.Room
import com.example.linksbox.utils.stringprovider.StringProvider
import com.example.linksbox.utils.stringprovider.StringProviderImpl
import com.example.linksbox.data.LinksDatabase
import com.example.linksbox.features.addfolder.AddFolderInteractor
import com.example.linksbox.features.addfolder.AddFolderViewModel
import com.example.linksbox.features.addlink.*
import com.example.linksbox.features.folders.FoldersInteractor
import com.example.linksbox.features.folders.FoldersRepository
import com.example.linksbox.features.folders.FoldersViewModel
import com.example.linksbox.features.links.LinksInteractor
import com.example.linksbox.features.links.LinksViewModel
import com.example.linksbox.features.main.MainViewModel
import com.example.linksbox.utils.previewmanager.PreviewManager
import com.example.linksbox.utils.previewmanager.PreviewManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.binds
import org.koin.dsl.module

class LinksBoxApplication : Application() {

    private val appModule = module {

        single {
            Room.databaseBuilder(
                applicationContext,
                LinksDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        single {
            val database = get<LinksDatabase>()
            database.getLinksDao()
        }

        single { FoldersRepository(get()) } binds
                arrayOf(
                    FoldersInteractor::class,
                    AddLinkInteractor::class,
                    LinksInteractor::class,
                    AddFolderInteractor::class
                )

        single <StringProvider> { StringProviderImpl(get()) }

        single <PreviewManager> { PreviewManagerImpl(get()) }

        viewModel { MainViewModel() }

        viewModel { FoldersViewModel(get(), get(), get()) }

        viewModel { LinksViewModel(get(), get(), get()) }

        viewModel { AddFolderViewModel(get()) }

        viewModel { AddLinkViewModel(get(), get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@LinksBoxApplication)
            modules(appModule)
        }
    }

    companion object {
        private const val DATABASE_NAME = "links_database"
    }
}