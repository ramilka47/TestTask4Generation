package com.tesk.task.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            AViewModel.SearchViewModel::class.java->{
                AViewModel.SearchViewModel(repository)
            }
            AViewModel.GetHubViewModel::class.java->{
                AViewModel.GetHubViewModel(repository)
            }
            else ->{
                null
            }
        } as T
    }

}