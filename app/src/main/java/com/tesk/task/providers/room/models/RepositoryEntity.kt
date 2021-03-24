package com.tesk.task.providers.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class RepositoryEntity(@PrimaryKey val id : String,
                            val name : String,
                            val value : String?,
                            val lastCommit : String?,
                            val lastRoot : String?,
                            val countOfFork : Int,
                            val rating : Int,
                            val language : String,
                            val userName : String)