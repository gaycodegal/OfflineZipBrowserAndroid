package foss.zip.offline.browser.offlinezipbrowser

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class LicenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        val webView = findViewById<WebView>(R.id.webview)
        val file =
            externalCacheDir?.let {
                Util.writeInputStreamToFile(assets.open("license.html"),
                    it, "license.html")
            } ?: return
        val name = file.toUri().lastPathSegment
        title = name
        webView.webViewClient = HtmlAssetLoader(file, null)
        webView.loadUrl(Util.siteNameFromFile(file))
    }
}