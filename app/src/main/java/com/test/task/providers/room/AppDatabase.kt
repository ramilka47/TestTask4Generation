package com.test.task.providers.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.test.task.providers.room.dao.MyFaceDao
import com.test.task.providers.room.dao.RepositoryDao
import com.test.task.providers.room.dao.UserDao
import com.test.task.providers.room.models.MyFaceEntity
import com.test.task.providers.room.models.RepositoryEntity
import com.test.task.providers.room.models.UserEntity

@Database(entities = [RepositoryEntity::class, UserEntity::class, MyFaceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun hubDao() : RepositoryDao

    abstract fun usersDao() : UserDao

    abstract fun myFaceDao() : MyFaceDao
}