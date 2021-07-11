package com.pukachkosnt.newstask.ui.listnews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.ui.listnews.all.ListNewsFragment
import com.pukachkosnt.newstask.ui.listnews.favorites.FavoritesFragment

class NewsActivity : AppCompatActivity(), ListNewsFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        // or supportFragmentManager.findFragmentById(R.id.fragment_id)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.news_fragment_container, ListNewsFragment.newInstance())
                .commit()
        }
    }

    override fun onFavoriteItemActionBarClicked() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.news_fragment_container, FavoritesFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                supportFragmentManager.popBackStack()
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.setDisplayShowHomeEnabled(false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}