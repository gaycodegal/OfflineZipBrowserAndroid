package foss.zip.offline.browser.offlinezipbrowser

import abhishekti7.unicorn.filepicker.UnicornFilePicker
import abhishekti7.unicorn.filepicker.utils.Constants
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.launchFilePicker(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQ_UNICORN_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                val files = data?.getStringArrayListExtra("filePaths")
                for (file in files!!) {
                    if (file.endsWith("zip")) {
                        ZipViewActivity.startZipActivity(this, file)
                    } else {
                        HtmlViewActivity.startHtmlActivity(this, file)
                    }
                    break
                }
            } else {
                finish()
            }
        }
    }


}