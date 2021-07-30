package com.pukachkosnt.newstask.ui.webdetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.pukachkosnt.newstask.R

class WebActivity : AppCompatActivity() {
    private val args: WebFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        (supportFragmentManager
            .findFragmentById(R.id.nav_host_web_fragment_container) as NavHostFragment)
            .navController
            .setGraph(
                R.navigation.web_nav_graph,
                bundleOf(WebFragment.PARAM_URL to args.paramUrl)
            )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}