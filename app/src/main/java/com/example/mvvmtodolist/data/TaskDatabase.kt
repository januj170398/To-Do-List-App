package com.example.mvvmtodolist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mvvmtodolist.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

//abstract because we don't want to define a body and do auto implementation

@Database(entities =  [Task :: class],version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao() : TaskDao

    class Callback @Inject constructor(
        private val database : Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("Wash it"))
                dao.insert(Task("Wt",important = true))
                dao.insert(Task("h it",completed = true))
                dao.insert(Task("hi"))
                dao.insert(Task("Wt",completed = true))
                dao.insert(Task("Wash it"))
                dao.insert(Task("Wt",important = true))
                dao.insert(Task("h it",completed = true))
                dao.insert(Task("hi"))
                dao.insert(Task("Wt",completed = true))
            }
        }
    }

}