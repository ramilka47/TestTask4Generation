package com.test.task.providers.git.response

data class SearchResponse(val total_count : Int, val incomplete_results : Boolean, val items : Array<UserResponse>)