package com.pukachkosnt.newstask.ui.listnews

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.ui.listnews.all.ListNewsFragment

class NewsActivity : AppCompatActivity(), ListNewsFragment.Callbacks {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        navController = (supportFragmentManager
            .findFragmentById(R.id.nav_host_list_news_fragment_container) as NavHostFragment)
            .navController
    }

    override fun onFavoriteItemActionBarClicked() {
        navController.navigate(R.id.action_to_favorites_fragment)
    }

    override fun onSettingsItemActionBarClicked() {
        navController.navigate(R.id.action_to_settings_fragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController.popBackStack()
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.setDisplayShowHomeEnabled(false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}