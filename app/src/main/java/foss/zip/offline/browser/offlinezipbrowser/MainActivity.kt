package foss.zip.offline.browser.offlinezipbrowser

import abhishekti7.unicorn.filepicker.UnicornFilePicker
import abhishekti7.unicorn.filepicker.utils.Constants
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val path = this.applicationContext.getExternalFilesDir(null)!!
            .absolutePath
        val picker = UnicornFilePicker.from(this@MainActivity)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQ_UNICORN_FILE && resultCode == RESULT_OK) {
            val files = data?.getStringArrayListExtra("filePaths")
            for (file in files!!) {
                if (file.endsWith("zip")) {
                    ZipViewActivity.startZipActivity(this, file)
                } else {
                    HtmlViewActivity.startHtmlActivity(this, file)
                }
                break
            }
        }
    }


}