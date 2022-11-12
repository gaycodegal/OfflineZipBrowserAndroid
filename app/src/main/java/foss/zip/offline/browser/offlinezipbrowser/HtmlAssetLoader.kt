package foss.zip.offline.browser.offlinezipbrowser

import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.webkit.WebViewClientCompat
import java.io.File


class HtmlAssetLoader(val htmlFile: File) : WebViewClientCompat() {
    private val utf8: String = Charsets.UTF_8.displayName()


    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return shouldInterceptRequest(request.url)
    }

    // for API < 21
    @Deprecated("Deprecated in Java")
    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        return shouldInterceptRequest(Uri.parse(url))
    }

    private fun return404(uri: Uri): WebResourceResponse {
        val reasonPhrase = "404 Not Found";
        val emptyBody = "404 Not Found $uri";
        val emptyBytes = emptyBody.byteInputStream(Charsets.UTF_8)
        return WebResourceResponse(
            "text/plain",
            utf8,
            404,
            reasonPhrase,
            mapOf("Content-Length" to emptyBytes.available().toString()),
            emptyBytes
        )
    }

    private fun shouldInterceptRequest(uri: Uri): WebResourceResponse? {
        if (uri.scheme == "data") {
            return null
        }
        var path = uri.path ?: return return404(uri)
        if (path.startsWith("/")) {
            path = path.substring(1)
        } else if (path.startsWith("./")) {
            path = path.substring(2)
        }
        if (path == "index.html") {
            val stream = htmlFile.inputStream()

            return WebResourceResponse(
                "text/html",
                utf8,
                200,
                "Ok",
                mapOf("Content-Length" to stream.available().toString()),
                stream)
        }
        return return404(uri)
    }
}
