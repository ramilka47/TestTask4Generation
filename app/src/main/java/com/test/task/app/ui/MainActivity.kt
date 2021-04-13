package com.test.task.app.ui

import android.os.Bundle
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.test.task.R
import com.test.task.app.ui.controllers.HubController
import com.test.task.app.ui.controllers.UserController
import com.test.task.providers.git.models.User
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity

class MainActivity : MvpAppCompatActivity() {

    private lateinit var router : Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        router = Conductor.attachRouter(this, container, savedInstanceState)
        showUserController()
    }

    private fun showUserController(){
        if (!router.hasRootController())
            router.setRoot(RouterTransaction.with(UserController()))
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed();
        }
    }
}