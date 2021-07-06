package com.pukachkosnt.newstask.ui.webdetails

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.pukachkosnt.newstask.R


class WebFragment : Fragment() {
    private lateinit var uri: Uri
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uri = arguments?.getParcelable(ARG_URI) ?: Uri.EMPTY
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
        webView.loadUrl(uri.toString())

        return view
    }

    companion object {
        private const val ARG_URI = "param_uri"

        fun newInstance(uri: Uri) =
            WebFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_URI, uri)
                }
            }
    }
}