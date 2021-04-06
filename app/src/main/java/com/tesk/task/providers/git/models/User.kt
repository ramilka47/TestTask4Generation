package com.tesk.task.providers.git.models

import com.tesk.task.providers.git.response.UserResponse
import com.tesk.task.providers.room.models.UserEntity

class User {
    val id : String
    val name : String
    val avatar : String
    var followers : Int = 0

    constructor(userResult: UserResponse){
        this.id = userResult.id
        this.name = userResult.login
        this.avatar = userResult.avatar_url
    }

    constructor(userEntity: UserEntity){
        this.id = userEntity.id
        this.name = userEntity.name
        this.avatar = userEntity.avatar
        this.followers = userEntity.followers
    }
}