package com.test.task.app.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bumptech.glide.manager.SupportRequestManagerFragment
import com.test.task.R
import com.test.task.app.ui.fragments.SearchFragment
import moxy.MvpAppCompatActivity

class MainActivity : MvpAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        switch()
    }

    private fun switch(){
        val fragments = supportFragmentManager.fragments
        if (fragments.size > 0){
            if (fragments.last() is SupportRequestManagerFragment)
                fragments.remove(fragments.last())

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
        val fragment = SearchFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    private fun execute(fragment : Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(fragment.javaClass.name).commit()
    }
}