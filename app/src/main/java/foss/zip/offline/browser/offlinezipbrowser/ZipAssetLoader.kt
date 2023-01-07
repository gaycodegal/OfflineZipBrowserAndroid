package foss.zip.offline.browser.offlinezipbrowser

import android.graphics.Bitmap
import android.net.Uri
import android.webkit.MimeTypeMap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.webkit.WebViewClientCompat
import java.io.ByteArrayInputStream
import java.io.File
import java.util.zip.ZipFile


class ZipAssetLoader(private val zipFile: ZipFile, private val onPageStartedScript: String?) : WebViewClientCompat() {
    private val utf8: String = Charsets.UTF_8.displayName()
    private val basePath: String = findBasePath()

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        val script = onPageStartedScript ?: return
        view?.evaluateJavascript(script, null)
    }

    private fun findBasePath (): String {
        if (zipFile.getEntry("index.html") != null) {
            return ""
        }
        for (file in zipFile.entries()) {
            if (file.isDirectory) {
                if (zipFile.getEntry(file.name + "index.html") != null) {
                    return file.name
                }
            }
        }
        return ""
    }


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
        if (path.endsWith("/")) {
            path += "index.html"
        }
        path = File(basePath, path).path

        if (path.startsWith("/")) {
            path = path.substring(1)
        } else if (path.startsWith("./")) {
            path = path.substring(2)
        }

        val zipEntry = zipFile.getEntry(path) ?: return return404(uri)
        val bytes = zipFile.getInputStream(zipEntry).use {
            return@use it.readBytes()
        }
        val mime = MimeTypeMap.getSingleton()
        val ext = path.split(".").last()
        return WebResourceResponse(
            mime.getMimeTypeFromExtension(ext) ?: "text/plain",
            utf8,
            200,
            "Ok",
            mapOf("Content-Length" to bytes.size.toString()),
            ByteArrayInputStream(bytes))
    }
}
