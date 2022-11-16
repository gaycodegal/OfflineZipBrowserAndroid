package foss.zip.offline.browser.offlinezipbrowser

import android.os.Handler
import android.os.Looper
import android.webkit.DownloadListener
import android.webkit.JavascriptInterface
import java.io.File
import java.util.Collections

class Downloader (val downloader: DownloadListener) {
    private var fileId = 0
    private var fileLock = Any()
    private val files:MutableMap<Int, File> = Collections.synchronizedMap(mutableMapOf());
    @JavascriptInterface
    fun writeToDisk(bytes: String, fileId: Int){
        synchronized(fileLock){
            val file = files[fileId] ?: return
            file.writeBytes(bytes.toByteArray())
        }
    }
    @JavascriptInterface
    fun close(fileId: Int) {
        synchronized(fileLock){
            files.remove(fileId)
        }
    }

    @Synchronized
    fun newFile(file: File): Int {
        files[fileId] = file
        return fileId++
    }

    @Synchronized
    @JavascriptInterface
    fun download(href: String?, downloadName: String?, mimeType: String?) {
        if (href == null) {
            return
        }
        val contentDisposition = if (downloadName.isNullOrEmpty()) {
            ""
        } else {
            "attachment;filename=$downloadName"
        }
        Handler(Looper.getMainLooper()).post {
            downloader.onDownloadStart(href, "", contentDisposition, mimeType ?: "", 0)
        }
    }
}