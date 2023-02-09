package com.rajchenbergstudios.hoygenda.di

import android.app.Application
import androidx.room.Room
import com.rajchenbergstudios.hoygenda.data.HGDADatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: HGDADatabase.Callback
    ) = Room.databaseBuilder(app, HGDADatabase::class.java, "hoygenda_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideTodayDao(db: HGDADatabase) = db.taskDao()

    @Provides
    fun provideDayDao(db: HGDADatabase) = db.dayDao()

    @Provides
    fun provideTaskSetDao(db: HGDADatabase) = db.taskSetDao()

    @Provides
    fun provideTaskInSetDao(db: HGDADatabase) = db.taskInSetDao()

    @Provides
    fun provideJournalEntryDao(db: HGDADatabase) = db.journalEntryDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @MainThreadScope
    @Provides
    @Singleton
    fun provideMainThreadScope() = CoroutineScope(Dispatchers.Main)
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class MainThreadScope