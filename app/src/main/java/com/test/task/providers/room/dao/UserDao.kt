package com.test.task.providers.room.dao

import androidx.room.*
import com.test.task.providers.room.models.UserEntity

@Dao
interface UserDao {

    @Delete
    fun delete(userEntity: UserEntity)

    @Update
    fun update(userEntity: UserEntity)

    @Insert
    fun insert(userEntity: UserEntity)

    @Query("Select * From UserEntity Where id = :id")
    fun getById(id : String) : UserEntity

    @Query("Select * From UserEntity")
    fun getAll() : List<UserEntity>

    // типа по запросу
    @Query("Select * From UserEntity Where `query` = :query")
    fun getByQuery(query: String) : List<UserEntity>

    // типа по запросу
    @Query("Delete From UserEntity Where `query` = :query")
    fun deleteByQuery(query: String)
}