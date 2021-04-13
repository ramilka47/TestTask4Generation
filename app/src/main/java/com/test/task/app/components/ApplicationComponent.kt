package com.test.task.app.components

import com.test.task.app.modules.*
import com.test.task.app.ui.controllers.HubController
import com.test.task.app.ui.controllers.UserController
import com.test.task.app.ui.dialogs.DialogExitController
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ModuleBd::class, ModuleGitService::class, ModulePreference::class])
interface ApplicationComponent {

    fun inject(userController: UserController)

    fun inject(dialogExitController: DialogExitController)

    fun inject(hubController: HubController)

}