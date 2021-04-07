package com.tesk.task.app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tesk.task.providers.git.GitService
import com.tesk.task.providers.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import retrofit2.Retrofit
import javax.inject.Inject

class FactoryViewModel @Inject constructor(private val bd : AppDatabase, private val gitService: GitService, private val coroutineIO : CoroutineScope) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            ViewModelSearch::class.java->{
                ViewModelSearch(gitService, bd, coroutineIO)
            }
            ViewModelRepository::class.java->{
                ViewModelRepository(gitService, bd, coroutineIO)
            }
            ViewModelLogout::class.java->{
                ViewModelLogout()
            }
            else ->{
                null
            }
        } as T
    }

}