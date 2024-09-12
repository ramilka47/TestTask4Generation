package com.test.task.providers.room.dao

import androidx.room.*
import com.test.task.providers.room.models.RepositoryEntity

@Dao
interface RepositoryDao {

    @Delete
    suspend fun delete(repositoryEntity: RepositoryEntity)

    @Update
    suspend fun update(repositoryEntity: RepositoryEntity)

    @Insert
    suspend fun insert(repositoryEntity: RepositoryEntity)

    @Query("Select * From RepositoryEntity Where id = :id")
    suspend fun getById(id : String) : RepositoryEntity

    @Query("Select * From RepositoryEntity")
    suspend fun getAll() : List<RepositoryEntity>

    @Query("Select * From RepositoryEntity Where userName = :userName")
    suspend fun getAllByUserName(userName : String) : List<RepositoryEntity>

    @Query("Delete From RepositoryEntity Where userName = :userName")
    suspend fun deleteAllByUserName(userName : String)
}