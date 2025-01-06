package foss.zip.offline.browser.offlinezipbrowser

import abhishekti7.unicorn.filepicker.utils.Constants
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.webkit.MimeTypeMap
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLDecoder
import java.net.URLEncoder

class RenameImporterActivity : AppCompatActivity() {
    private var onFilePicked: ((ArrayList<String>)->Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent.action) {
            "android.intent.action.OPEN_DOCUMENT"->
                importFile()
            "android.intent.action.VIEW"->
                importFile()
            "RENAME_FILE"->
                renameFile()
            "DELETE_FILE"->
                deleteFile()
            "CREATE_SHORTCUT"->
                createShortcut()
        }

    }

    private fun deleteFile(){
        setContentView(R.layout.activity_view)

        onFilePicked = {
            if (!it.isEmpty()){
                val source = it.first()
                val sourceName = File(source).toUri().lastPathSegment ?: "Untitled"
                AlertDialog.Builder(this)
                    .setTitle("Delete $sourceName?")
                    .setMessage("Do you really want to delete $sourceName? This cannot be undone")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(
                        "Yes"
                    ) { _, _ ->
                        val sourceFile = File(source)
                        clearCache(sourceFile)
                        sourceFile.delete()
                        finishAndRemoveTask()
                    }
                    .setNegativeButton("No", null).show()
            }
        }
        Util.launchFilePicker(this)
    }

    private fun clearCache(sourceFile: File){
        val file =
            externalCacheDir?.let {
                Util.writeInputStreamToFile(assets.open("clearCache.html"),
                    it, "clearCache.html")
            }
        if (file != null) {
            val webView = findViewById<WebView>(R.id.webview)
            HtmlViewActivity.loadWebsiteFromFile(null, webView, file, Util.siteNameFromFile(sourceFile))
        }
    }

    private fun renameFile(){
        setContentView(R.layout.activity_view)
        onFilePicked = {
            if (!it.isEmpty()){
                val source = it.first()
                val sourceName = File(source).toUri().lastPathSegment ?: "Untitled"
                getName(sourceName){
                        dest ->
                    val sourceFile = File(source)
                    clearCache(sourceFile)
                    sourceFile.renameTo(File(sourceFile.parent, dest))
                    finishAndRemoveTask()
                }
            }
        }
        Util.launchFilePicker(this)
    }

    private fun createShortcut(){
        setContentView(R.layout.activity_main)
        onFilePicked = {
            if (!it.isEmpty()){
                val source = it.first()
                val newIntent = Intent(this, ShortLinkMakerActivity::class.java)
                newIntent.putExtra(ZipConstants.FILE_NAME, source)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(newIntent)
                finish()
                intent = Intent()
            }
        }
        Util.launchFilePicker(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQ_UNICORN_FILE ) {
            if (resultCode == RESULT_OK) {
                val files = data?.getStringArrayListExtra("filePaths") ?: return
                onFilePicked?.let { it(files) }
                onFilePicked = null
            } else {
                finish()
            }
        }
    }

    private fun importFile(){
        setContentView(R.layout.activity_main)
        val data = intent.data ?: return
        getName(URLDecoder.decode(data.lastPathSegment, Charsets.UTF_8.name())?.split('/')?.last() ?: "Untitled") {
            text ->
            if (text.endsWith(".zip")){
                val file = writeToDisk(data, text) ?: return@getName
                ZipViewActivity.startZipActivity(this@RenameImporterActivity, file.path)
            }else if (text.endsWith(".html")){
                val file = writeToDisk(data, text) ?: return@getName
                HtmlViewActivity.startHtmlActivity(this@RenameImporterActivity, file.path)
            }else if (contentResolver.getType(data) == "application/zip") {
                val file = writeToDisk(data, "$text.zip") ?: return@getName
                ZipViewActivity.startZipActivity(this@RenameImporterActivity, file.path)
            } else {
                val file = writeToDisk(data, "$text.html") ?: return@getName
                HtmlViewActivity.startHtmlActivity(this@RenameImporterActivity, file.path)
            }
            finish()
            intent = Intent()
        }
    }

    private fun getName(name: String, onSuccess: (text:String)->Unit) {
        val mime = MimeTypeMap.getSingleton()
        val ext = mime.getExtensionFromMimeType(intent.data?.let { contentResolver.getType(it) })
        val builder = AlertDialog.Builder(this)
        builder.setTitle("$ext file name")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        input.setText(name)
        builder.setView(input)

        builder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                var text = Util.safeNameify("${input.text}")
                onSuccess(text)
            }
        })
        builder.setNegativeButton("Cancel"
        ) { dialog, which ->
            dialog.cancel()
            finishAndRemoveTask()
        }

        builder.show()

    }

    private fun writeToDisk(data: Uri, name: String): File? {
        //Use content Resolver to get the input stream that it holds the data and copy that in a temp file of your app file directory for your references
        val selectedFile = File(getExternalFilesDir(null), name);
        val inputStream = contentResolver.openInputStream(data);
        val ouputStream = FileOutputStream(selectedFile);
        try {
            val buf = ByteArray(1024);
            var len:Int


            if (inputStream != null) {
                len = inputStream.read(buf)
                while (len > 0) {
                    ouputStream.write(buf, 0, len);
                    len = inputStream.read(buf)
                }
                ouputStream.close();
                inputStream.close();
            }
        } catch (ie: IOException) {
            ie.printStackTrace();
            return null
        }

        return selectedFile

    }
}