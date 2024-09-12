package com.test.task.providers.git.models

import com.test.task.providers.git.response.HubResponse
import com.test.task.providers.room.models.RepositoryEntity
import java.util.*

class Hub {
    val id : String
    val name : String
    val desctiption : String
    val lastCommit : Date
    val currentFork : String
    val countOfFork : Int
    val rating : Int
    val language : String

    constructor(hubResult: HubResponse){
        this.id = hubResult.id
        this.name = hubResult.name
        this.desctiption = hubResult.description
        this.lastCommit = hubResult.updated_at
        this.currentFork = hubResult.default_branch
        this.countOfFork = hubResult.forks_count
        this.rating = hubResult.stargazers_count
        this.language = hubResult.language
    }

    constructor(repositoryEntity: RepositoryEntity){
        this.id = repositoryEntity.id
        this.name = repositoryEntity.name
        this.desctiption = repositoryEntity.value?:""
        this.lastCommit = Date(repositoryEntity.lastCommit)
        this.currentFork = repositoryEntity.lastRoot?:""
        this.countOfFork = repositoryEntity.countOfFork
        this.rating = repositoryEntity.rating
        this.language = repositoryEntity.language
    }
}