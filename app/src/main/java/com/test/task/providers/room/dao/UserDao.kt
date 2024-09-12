package com.test.task.providers.room.dao

import androidx.room.*
import com.test.task.providers.room.models.UserEntity

@Dao
interface UserDao {

    @Delete
    suspend fun delete(userEntity: UserEntity)

    @Update
    suspend fun update(userEntity: UserEntity)

    @Insert
    suspend fun insert(userEntity: UserEntity)

    @Query("Select * From UserEntity Where id = :id")
    suspend fun getById(id : String) : UserEntity

    @Query("Select * From UserEntity")
    suspend fun getAll() : List<UserEntity>

    // типа по запросу
    @Query("Select * From UserEntity Where `query` = :query")
    suspend fun getByQuery(query: String) : List<UserEntity>

    // типа по запросу
    @Query("Delete From UserEntity Where `query` = :query")
    suspend fun deleteByQuery(query: String)
}