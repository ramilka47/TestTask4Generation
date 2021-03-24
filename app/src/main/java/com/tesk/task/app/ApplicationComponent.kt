package com.tesk.task.app

import com.tesk.task.app.modules.ModuleApi
import com.tesk.task.app.modules.ModuleApp
import com.tesk.task.app.modules.ModuleBd
import com.tesk.task.app.ui.dialogs.DialogExit
import com.tesk.task.app.ui.dialogs.DialogLogin
import com.tesk.task.app.ui.fragments.RepositoryFragment
import com.tesk.task.app.ui.fragments.SearchFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ModuleApp::class, ModuleBd::class, ModuleApi::class))
interface ApplicationComponent {

    fun inject(repositoryFragment: RepositoryFragment)

    fun inject(searchFragment: SearchFragment)

    fun inject(dialogLogin : DialogLogin)

    fun inject(dialogExit : DialogExit)

}