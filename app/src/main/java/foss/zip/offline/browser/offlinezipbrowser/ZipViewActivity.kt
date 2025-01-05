package foss.zip.offline.browser.offlinezipbrowser

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.core.net.toUri
import java.io.File
import java.util.zip.ZipFile

class ZipViewActivity : WebActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        val webView = findViewById<WebView>(R.id.webview)
        val file = File(intent.getStringExtra(ZipConstants.FILE_NAME) ?: return)
        val name = file.toUri().lastPathSegment ?: "no-name"
        title = name
        val zip = ZipFile(file)
        val downloadHelperScript = assets.open("downloadNameHelper.js").bufferedReader(Charsets.UTF_8).readText()
        val dateOverrideScript = assets.open("dateReplacer.js").bufferedReader(Charsets.UTF_8).readText()

        var zipAssetLoader = ZipAssetLoader(zip, name, dateOverrideScript, dateOverrideScript)
        webView.webViewClient = zipAssetLoader
        webviewSetup(webView)

        webView.loadUrl("https://${zipAssetLoader.baseURL}/index.html")
    }

    companion object {
        fun startZipActivity (context: Activity, path: String) {
            val intent = Intent(context, ZipViewActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME or Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra(ZipConstants.FILE_NAME, path)
            context.startActivity(intent)
        }
    }
}