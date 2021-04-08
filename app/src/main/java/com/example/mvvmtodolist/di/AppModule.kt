package com.example.mvvmtodolist.di

import android.app.Application
import android.app.SharedElementCallback
import androidx.room.Room
import com.example.mvvmtodolist.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent :: class)
object AppModule {

    @Provides
    @Singleton // to only create one instance
    fun provideDatabase(
        //to pass context
      app : Application,
    callback: TaskDatabase.Callback
    ) = Room.databaseBuilder(app,TaskDatabase::class.java,"task_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
        // it tell what it should do if we update our db schema but did not if migration strategy then it will create new one
            .build()

        @Provides
        fun provideTaskDao(db  : TaskDatabase) = db.taskDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())  //coroutien scope that live as long as our application
    }

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
