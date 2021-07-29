package com.pukachkosnt.newstask.ui.webdetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.pukachkosnt.newstask.R

class WebFragment : Fragment() {
    private lateinit var url: String
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    private val args by navArgs<WebFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = args.paramUrl
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_web, container, false)

        webView = view.findViewById(R.id.web_view_article_details)
        progressBar = view.findViewById(R.id.progress_bar_web)

        webView.settings.javaScriptEnabled = true

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar.isVisible = false
                } else {
                    progressBar.progress = newProgress
                }
            }
        }

        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)

        return view
    }
}