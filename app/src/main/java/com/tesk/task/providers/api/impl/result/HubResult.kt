package com.tesk.task.providers.api.impl.result

import java.util.*

data class HubResult(val id : String,
                     val name : String,
                     val full_name : String,
                     val description : String,
                     val updated_at : Date,
                     val stargazers_count : Int,
                     val language : String,
                     val forks_count : Int,
                     val default_branch : String)