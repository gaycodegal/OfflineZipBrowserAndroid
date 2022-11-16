package foss.zip.offline.browser.offlinezipbrowser

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.core.net.toUri
import java.io.File

class HtmlViewActivity : WebActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        val webView = findViewById<WebView>(R.id.webview)
        val file = File(intent.getStringExtra(ZipConstants.FILE_NAME) ?: return)
        title = loadWebsiteFromFile(this, webView, file)
        webviewSetup(webView)
    }

    companion object {
        fun startHtmlActivity (context: Activity, path: String) {
            val intent = Intent(context, HtmlViewActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            intent.putExtra(ZipConstants.FILE_NAME, path)
            context.startActivity(intent)
            context.finish()
        }

        @SuppressLint("SetJavaScriptEnabled")
        fun loadWebsiteFromFile(context: Context?, webView: WebView, file: File, url: String): String {
            val name = file.toUri().lastPathSegment
            webView.webViewClient = HtmlAssetLoader(file, context?.assets?.open("downloadNameHelper.js")?.bufferedReader(Charsets.UTF_8)?.readText())
            Util.webviewSetup(webView)
            webView.loadUrl(url)
            return name ?: ""
        }

        fun loadWebsiteFromFile(context: Context?, webView: WebView, file: File): String {
            return loadWebsiteFromFile(context, webView, file, Util.siteNameFromFile(file))
        }
    }
}