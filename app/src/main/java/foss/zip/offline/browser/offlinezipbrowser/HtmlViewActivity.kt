package foss.zip.offline.browser.offlinezipbrowser

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
        title = loadWebsiteFromFile(webView, file)
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
        fun loadWebsiteFromFile(webView: WebView, file: File, url: String): String {
            val name = file.toUri().lastPathSegment
            webView.webViewClient = HtmlAssetLoader(file)
            Util.webviewSetup(webView)
            webView.loadUrl(url)
            return name ?: ""
        }

        fun loadWebsiteFromFile(webView: WebView, file: File): String {
            return loadWebsiteFromFile(webView, file, Util.siteNameFromFile(file))
        }
    }
}