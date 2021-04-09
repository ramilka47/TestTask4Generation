package com.test.task.app.components

import com.test.task.app.modules.*
import com.test.task.app.ui.dialogs.DialogExit
import com.test.task.app.ui.fragments.HubFragment
import com.test.task.app.ui.fragments.SearchFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ModuleBd::class, ModuleGitService::class, ModulePreference::class])
interface ApplicationComponent {

    fun inject(searchFragment: SearchFragment)

    fun inject(dialogExit: DialogExit)

    fun inject(hubFragment: HubFragment)

}