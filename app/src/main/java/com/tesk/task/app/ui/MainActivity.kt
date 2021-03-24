package com.tesk.task.app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tesk.task.R
import com.tesk.task.app.adapters.IShowUserRepositories
import com.tesk.task.app.ui.fragments.RepositoryFragment
import com.tesk.task.app.ui.fragments.SearchFragment
import com.tesk.task.providers.api.impl.models.User

class MainActivity : AppCompatActivity(), IShowUserRepositories {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        switch()
    }

    private fun switch(){
        val fragments = supportFragmentManager.fragments
        if (fragments.size > 0){
            execute(fragments.last())
        } else {
            showSearch()
        }
    }

    override fun onBackPressed() {
        val fragments = supportFragmentManager.fragments
        if (supportFragmentManager.backStackEntryCount > 1){
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private fun showSearch(){
        val fragment = SearchFragment().apply {
            iShowUserRepositories = this@MainActivity
        }
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    override fun showRepo(user: User) {
        val fragment = RepositoryFragment().apply {
            this.user = user
        }
        execute(fragment)
    }

    private fun execute(fragment : Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(fragment.javaClass.name).commit()
    }
}