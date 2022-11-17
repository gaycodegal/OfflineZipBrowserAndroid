package foss.zip.offline.browser.offlinezipbrowser

import abhishekti7.unicorn.filepicker.utils.Constants
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    var ignoreBack = false
    override fun onResume() {
        super.onResume()
        if (!ignoreBack) {
            Util.launchFilePicker(this)
        } else {
            ignoreBack = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ignoreBack = true
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