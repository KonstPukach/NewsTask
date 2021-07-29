package com.pukachkosnt.newstask.ui.webdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.pukachkosnt.newstask.R

class WebActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findNavController(R.id.nav_host_web_fragment_container)
            .setGraph(R.navigation.web_nav_graph, intent.extras)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}