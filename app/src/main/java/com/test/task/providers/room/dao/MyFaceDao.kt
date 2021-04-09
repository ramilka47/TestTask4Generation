package com.test.task.providers.room.dao

import androidx.room.*
import com.test.task.providers.room.models.MyFaceEntity

@Dao
interface MyFaceDao {

    @Delete
    fun delete(myFaceEntity: MyFaceEntity)

    @Update
    fun update(myFaceEntity: MyFaceEntity)

    @Insert
    fun insert(myFaceEntity: MyFaceEntity)

    @Query ("Select * From MyFaceEntity Where id = :id")
    fun getById(id : Long) : MyFaceEntity?
}