package com.rajchenbergstudios.hoytask.di

import android.app.Application
import androidx.room.Room
import com.rajchenbergstudios.hoytask.data.HoytaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
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
        callback: HoytaskDatabase.Callback
    ) = Room.databaseBuilder(app, HoytaskDatabase::class.java, "hoytask_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideTaskDao(db: HoytaskDatabase) = db.taskDao()

    @Provides
    fun provideDayDao(db: HoytaskDatabase) = db.dayDao()

    @Provides
    fun provideTaskSetDao(db: HoytaskDatabase) = db.taskSetDao()

    @Provides
    fun provideTaskInSetDao(db: HoytaskDatabase) = db.taskInSetDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope