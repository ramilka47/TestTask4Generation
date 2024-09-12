package com.test.task.providers.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

// query - типо запрос.
// по идее неправильно,
// должна быть таблица связей запрос - элемент.
// ( нужен только для восстановления последнего результата )
@Entity
data class UserEntity(@PrimaryKey val id :String,
                      val name : String,
                      val avatar : String,
                      val followers : Int,
                      val query : String)//todo исправить, сделать базу query и userId / query (делать join)