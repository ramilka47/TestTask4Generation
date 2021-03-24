package com.tesk.task.app.viewmodels.models

import com.tesk.task.providers.api.impl.models.User

data class SearchSuccess(val query : String, val users : List<User>)