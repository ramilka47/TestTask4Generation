package com.tesk.task.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tesk.task.app.Repository
import javax.inject.Inject

class FactoryViewModel @Inject constructor(private val repository: Repository) : ViewModelProvider.Factory {

    private val viewModelMyFace = ViewModelMyFace()

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            AViewModel.SearchViewModel::class.java->{
                AViewModel.SearchViewModel(repository)
            }
            AViewModel.GetHubViewModel::class.java->{
                AViewModel.GetHubViewModel(repository)
            }
            ViewModelAuthorize::class.java->{
                ViewModelAuthorize(repository)
            }
            ViewModelLogOut::class.java->{
                ViewModelLogOut(repository)
            }
            ViewModelGetFollowers::class.java->{
                ViewModelGetFollowers(repository)
            }
            ViewModelGetMyFace::class.java->{
                ViewModelGetMyFace(repository)
            }
            ViewModelMyFace::class.java->{
                viewModelMyFace
            }
            else ->{
                null
            }
        } as T
    }

}