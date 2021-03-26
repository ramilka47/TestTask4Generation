package com.tesk.task.app.components

import com.tesk.task.app.modules.ModuleApi
import com.tesk.task.app.modules.ModuleApp
import com.tesk.task.app.modules.ModuleBd
import com.tesk.task.app.modules.ModuleViewModelFactory
import com.tesk.task.app.ui.dialogs.DialogExit
import com.tesk.task.app.ui.dialogs.DialogLogin
import com.tesk.task.app.ui.fragments.HubFragment
import com.tesk.task.app.ui.fragments.SearchFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ModuleViewModelFactory::class))
interface ApplicationComponent {

    fun inject(repositoryFragment: HubFragment)

    fun inject(searchFragment: SearchFragment)

    fun inject(dialogLogin : DialogLogin)

    fun inject(dialogExit : DialogExit)

}