package foss.zip.offline.browser.offlinezipbrowser

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RenameImporterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data = intent.data ?: return
        getName(data.lastPathSegment ?: "Untitled")
    }

    private fun getName(name: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Zip file name")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(name)
        builder.setView(input)

        builder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val data = intent.data ?: return
                if (contentResolver.getType(data) == "application/zip") {
                    val file = writeToDisk(data, "${input.getText()}.zip") ?: return
                    ZipViewActivity.startZipActivity(this@RenameImporterActivity, file.path)
                } else {
                    val file = writeToDisk(data, "${input.getText()}.html") ?: return
                    HtmlViewActivity.startHtmlActivity(this@RenameImporterActivity, file.path)
                }
            }
        })
        builder.setNegativeButton("Cancel"
        ) { dialog, which ->
            dialog.cancel()
            finish()
        }

        builder.show()

    }

    private fun writeToDisk(data: Uri, name: String): File? {
        //Use content Resolver to get the input stream that it holds the data and copy that in a temp file of your app file directory for your references
        val selectedFile = File(getExternalFilesDir(null), name); //your app file dir or cache dir you can use
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