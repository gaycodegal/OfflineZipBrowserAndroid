package foss.zip.offline.browser.offlinezipbrowser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.*
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

open class WebActivity: AppCompatActivity() {

    var web: WebView? = null
    var downloader: Downloader? = null

    private var mUploadMessage: ValueCallback<Array<Uri>>? = null
    private val PICKED_SUCCESS_RESULTCODE = 3

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == PICKED_SUCCESS_RESULTCODE) {
            if (null == mUploadMessage) return
            val result: Uri? =
                if (intent == null || resultCode != Activity.RESULT_OK) null else intent.getData()
            if (result != null) {
                mUploadMessage?.onReceiveValue(arrayOf(result))
            } else {
                mUploadMessage?.onReceiveValue(arrayOf())
            }
            mUploadMessage = null
        }
    }


    fun webviewSetup(webView: WebView) {
        val downloadListener = object : DownloadListener {
            var urlToIgnore:String? = null
            override fun onDownloadStart(
                url: String,
                userAgent: String,
                contentDisposition: String,
                mimetype: String,
                contentLength: Long
            ) {
                if (urlToIgnore == url) {
                    urlToIgnore = null
                    return
                }
                urlToIgnore = url
                try {
                    val isBlob = url.startsWith("blob:")
                    val tempFileName = if (isBlob) {
                        url.split('.').first() ?: url
                    } else {
                        url
                    }
                    val fileName = URLUtil.guessFileName(tempFileName, contentDisposition, mimetype)
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)?.let {
                        var file = File(it, fileName)
                        var fileI = 0
                        var fileNameParts = fileName.split('.')
                        val fileExt = if(fileNameParts.size > 1) {
                            ".${fileNameParts.last()}"
                        } else {
                            ""
                        }
                        if (fileNameParts.size > 1) {
                            fileNameParts = fileNameParts.subList(0, fileNameParts.size - 1)
                        }
                        val fileNameNew = fileNameParts.joinToString(".")
                        while (file.exists()) {
                            file = File(it, "$fileNameNew ($fileI)$fileExt")
                            fileI += 1
                        }
                        val fileId = downloader?.newFile(file) ?: return@let
                        val script = assets.open("openBlobDownloader.js").bufferedReader(Charsets.UTF_8).readText()
                        web?.evaluateJavascript("$script(\"$url\", 1024 * 1024, $fileId)", null)
                        Toast.makeText(
                            applicationContext,
                            "Downloading $fileName",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }

        }
        web = webView
        Util.webviewSetup(webView)
        downloader = Downloader(downloadListener)
        webView.addJavascriptInterface(downloader!!, "Android")
        webView.webChromeClient = object : WebChromeClient() {
            override fun onCloseWindow(window: WebView?) {
                super.onCloseWindow(window)
                onBackPressed()
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                mUploadMessage = filePathCallback
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.setType("*/*")
                
                this@WebActivity.startActivityForResult(
                    Intent.createChooser(i, "File Chooser"),
                    PICKED_SUCCESS_RESULTCODE
                )
                return true
            }
        }

        webView.setDownloadListener(downloadListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val webBundle = Bundle()
        web?.saveState(webBundle)
        outState.putBundle("webBundle", webBundle)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val webBundle = savedInstanceState.getBundle("webBundle") ?: return
        web?.restoreState(webBundle)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (web?.canGoBack() == true) {
                web!!.goBack()
                return true
            } else {
                web?.destroy()
                onBackPressed()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}