package com.tesk.task.providers.api.impl.models

import com.tesk.task.providers.api.impl.result.UserResult
import com.tesk.task.providers.room.models.UserEntity

class User {
    val id : String
    val name : String
    val avatar : String
    val followers : Int

    constructor(userResult: UserResult){
        this.id = userResult.id
        this.name = userResult.login
        this.avatar = userResult.avatar_url
        this.followers = 0 // todo сделать запрос по адресу...
    }

    constructor(userEntity: UserEntity){
        this.id = userEntity.id
        this.name = userEntity.name
        this.avatar = userEntity.avatar
        this.followers = userEntity.followers
    }
}