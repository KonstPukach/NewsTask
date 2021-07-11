package com.pukachkosnt.newstask.ui.webdetails

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.pukachkosnt.newstask.R

class WebActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragment = supportFragmentManager.findFragmentById(R.id.web_comtainer)

        if (fragment == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.web_comtainer, WebFragment.newInstance(intent.data ?: Uri.EMPTY))
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        fun newIntent(context: Context, uri: Uri): Intent {
            return Intent(context, WebActivity::class.java).apply {
                data = uri
            }
        }
    }

}