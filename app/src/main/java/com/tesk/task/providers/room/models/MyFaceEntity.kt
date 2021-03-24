package com.tesk.task.providers.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MyFaceEntity(@PrimaryKey val id : Long, val name : String)