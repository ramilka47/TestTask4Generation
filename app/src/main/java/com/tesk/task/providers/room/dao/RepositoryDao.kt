package com.tesk.task.providers.room.dao

import androidx.room.*
import com.tesk.task.providers.room.models.RepositoryEntity

@Dao
interface RepositoryDao {

    @Delete
    fun delete(repositoryEntity: RepositoryEntity)

    @Update
    fun update(repositoryEntity: RepositoryEntity)

    @Insert
    fun insert(repositoryEntity: RepositoryEntity)

    @Query("Select * From RepositoryEntity Where id = :id")
    fun getById(id : String) : RepositoryEntity

    @Query("Select * From RepositoryEntity")
    fun getAll() : List<RepositoryEntity>

    @Query("Select * From RepositoryEntity Where userName = :userName")
    fun getAllByUserName(userName : String) : List<RepositoryEntity>

    @Query("Delete From RepositoryEntity Where userName = :userName")
    fun deleteAllByUserName(userName : String)
}