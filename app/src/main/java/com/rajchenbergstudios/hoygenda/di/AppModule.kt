package com.rajchenbergstudios.hoygenda.di

import android.app.Application
import androidx.room.Room
import com.rajchenbergstudios.hoygenda.data.HTSKDatabase
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
        callback: HTSKDatabase.Callback
    ) = Room.databaseBuilder(app, HTSKDatabase::class.java, "hoytask_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideTaskDao(db: HTSKDatabase) = db.taskDao()

    @Provides
    fun provideDayDao(db: HTSKDatabase) = db.dayDao()

    @Provides
    fun provideTaskSetDao(db: HTSKDatabase) = db.taskSetDao()

    @Provides
    fun provideTaskInSetDao(db: HTSKDatabase) = db.taskInSetDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope