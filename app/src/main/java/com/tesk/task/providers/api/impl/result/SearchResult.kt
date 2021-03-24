package com.tesk.task.providers.api.impl.result

data class SearchResult(val total_count : Int, val incomplete_results : Boolean, val items : Array<UserResult>)