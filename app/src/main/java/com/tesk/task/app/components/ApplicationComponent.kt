package com.tesk.task.app.components

import com.tesk.task.app.modules.ModuleViewModelFactory
import com.tesk.task.app.ui.MainActivity
import com.tesk.task.app.ui.dialogs.DialogExit
import com.tesk.task.app.ui.fragments.HubFragment
import com.tesk.task.app.ui.fragments.SearchFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ModuleViewModelFactory::class))
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(repositoryFragment: HubFragment)

    fun inject(searchFragment: SearchFragment)

    fun inject(dialogExit: DialogExit)
}