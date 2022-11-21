package foss.zip.offline.browser.offlinezipbrowser

import abhishekti7.unicorn.filepicker.UnicornFilePicker
import abhishekti7.unicorn.filepicker.utils.Constants
import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class Util {
    companion object {
        fun launchFilePicker (activity: Activity){
            val path = activity.getExternalFilesDir(null)!!
                .absolutePath
            UnicornFilePicker.from(activity)
                .addConfigBuilder()
                .selectMultipleFiles(false)
                .showOnlyDirectory(false)
                .setRootDirectory(path)
                .showHiddenFiles(false)
                .setFilters(arrayOf("zip", "html"))
                .addItemDivider(true)
                .build()
                .forResult(Constants.REQ_UNICORN_FILE)
        }

        fun siteNameFromFile(file: File) : String {
            val name = file.toUri().lastPathSegment
            return "https://${name}.androidplatform.net/index.html"
        }

        fun safeNameify(name: String?): String {
            return name?.replace("/", "_")?.trimStart('.')?.trim() ?: "Untitled.txt"
        }

        @SuppressLint("SetJavaScriptEnabled")
        fun webviewSetup(webView: WebView){
            webView.settings.domStorageEnabled = true
            webView.settings.javaScriptEnabled = true
            webView.settings.builtInZoomControls = true
            webView.settings.displayZoomControls = false
            webView.settings.mediaPlaybackRequiresUserGesture = false
            webView.settings.allowFileAccess = false
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
        }

        fun writeInputStreamToFile(inputStream: InputStream, path: File, name: String): File? {
            val selectedFile = File(path, name)
            val outputStream = FileOutputStream(selectedFile);
            try {
                val buf = ByteArray(1024);
                var len:Int = inputStream.read(buf)
                while (len > 0) {
                    outputStream.write(buf, 0, len);
                    len = inputStream.read(buf)
                }
                outputStream.close();
                inputStream.close();
            } catch (ie: IOException) {
                ie.printStackTrace();
                return null
            }

            return selectedFile

        }

    }
}