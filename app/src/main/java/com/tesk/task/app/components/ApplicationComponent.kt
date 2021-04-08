package com.tesk.task.app.components

import com.tesk.task.app.modules.*
import com.tesk.task.app.mvp.presenters.PresenterExit
import com.tesk.task.app.mvp.presenters.PresenterHub
import com.tesk.task.app.mvp.presenters.PresenterSearch
import com.tesk.task.app.ui.MainActivity
import com.tesk.task.app.ui.dialogs.DialogExit
import com.tesk.task.app.ui.fragments.HubFragment
import com.tesk.task.app.ui.fragments.SearchFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ModuleBd::class, ModuleCoroutineScope::class, ModuleGitService::class, ModulePreference::class))
interface ApplicationComponent {

    fun inject(presenterSearch: PresenterSearch)

    fun inject(presenterHub: PresenterHub)

    fun inject(presenterExit: PresenterExit)

}