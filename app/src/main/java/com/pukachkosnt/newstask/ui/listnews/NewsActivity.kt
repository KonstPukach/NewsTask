package com.pukachkosnt.newstask.ui.listnews

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.ui.webdetails.WebActivity

class NewsActivity : AppCompatActivity(), ArticleHolder.Callbacks {

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

    override fun onArticleItemSelected(url: String) {
        val intent = WebActivity.newIntent(this, Uri.parse(url))
        startActivity(intent)
    }
}