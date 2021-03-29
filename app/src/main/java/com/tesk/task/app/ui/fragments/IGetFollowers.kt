package com.tesk.task.app.ui.fragments

import com.tesk.task.providers.api.impl.models.User

interface IGetFollowers {

    fun getForUser(user : User)

}