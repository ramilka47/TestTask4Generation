package com.tesk.task.app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tesk.task.R
import com.tesk.task.app.adapters.IShowUserHub
import com.tesk.task.app.ui.fragments.HubFragment
import com.tesk.task.app.ui.fragments.SearchFragment
import com.tesk.task.providers.git.models.User

class MainActivity : AppCompatActivity(), IShowUserHub {

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
        if (supportFragmentManager.backStackEntryCount > 1){
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private fun showSearch(){
        val fragment = SearchFragment().apply {
            iShowUserHub = this@MainActivity
        }
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    override fun showRepo(user: User) {
        val fragment = HubFragment().apply {
            this.user = user
        }
        execute(fragment)
    }

    private fun execute(fragment : Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(fragment.javaClass.name).commit()
    }
}