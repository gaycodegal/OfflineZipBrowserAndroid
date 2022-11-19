package foss.zip.offline.browser.offlinezipbrowser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class DeepLinkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent.action) {
            "android.intent.action.VIEW"->
                deepLink(intent.data)
            else ->
                finishAndRemoveTask()
        }
    }

    private fun deepLink(uri: Uri?) {
        if (uri == null) {
            finishAndRemoveTask()
            return
        }
        when (uri.pathSegments.firstOrNull()) {
            "action" ->
                actionLink(uri)
            "play" ->
                playLink(uri)
            else ->
            {
                finishAndRemoveTask()
                Log.e("OfflineZipBrowser", "Could not open uri $uri")
            }
        }

    }

    private fun actionLink (uri: Uri) {
        val intent: Intent?
        when (uri.path) {
            "/action/pick" ->
                intent = Intent(this, MainActivity::class.java)
            "/action/delete-file" -> {
                intent = Intent(this, RenameImporterActivity::class.java)
                intent.action = "DELETE_FILE"
            }
            "/action/rename-file" -> {
                intent = Intent(this, RenameImporterActivity::class.java)
                intent.action = "RENAME_FILE"
            }
            "/action/create-shortcut" -> {
                intent = Intent(this, RenameImporterActivity::class.java)
                intent.action = "CREATE_SHORTCUT"
            }
            else -> {
                Log.e("OfflineZipBrowser", "Could not open action $uri")
                return finishAndRemoveTask()
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent);
        setIntent(Intent())
        finish();
    }

    private fun playLink (uri: Uri) {
        val fileName = File(getExternalFilesDir(null),uri.path?.removePrefix("/play") ?: return finishAndRemoveTask()).path
        if (fileName.endsWith(".zip")){
            ZipViewActivity.startZipActivity(this, fileName)
        }else if (fileName.endsWith(".html")){
            HtmlViewActivity.startHtmlActivity(this, fileName)
        } else {
            Log.e("OfflineZipBrowser", "Could not open play link $uri")
            finishAndRemoveTask()
        }
        finish()
    }

}