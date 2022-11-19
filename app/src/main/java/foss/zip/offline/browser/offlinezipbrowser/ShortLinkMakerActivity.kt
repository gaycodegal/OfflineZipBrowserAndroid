package foss.zip.offline.browser.offlinezipbrowser

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import java.io.File
import java.util.zip.ZipFile

class ShortLinkMakerActivity : AppCompatActivity() {
    var icons: List<String>? = null
    var iconIndex =0
    var zipFile: ZipFile? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shortcut_maker)
        val file = File(intent.getStringExtra(ZipConstants.FILE_NAME) ?: return finish())
        if (file.extension == "zip") {
            val icon = findViewById<ImageView>(R.id.imageView)
            val tempZipFile = ZipFile(file)
            val tempIcons = findAllIcons(tempZipFile)
            zipFile = tempZipFile
            icons = tempIcons
            if (tempIcons.isNotEmpty()) {
                val bitmap = bitmapFromZip(zipFile, tempIcons.firstOrNull())
                icon.setImageBitmap(bitmap)
                findViewById<EditText>(R.id.shortcut_zip_path).setText(tempIcons.first())
            } else {
                icons = null
            }
        }
        if (icons == null) {
            findViewById<View>(R.id.shortcut_next_icon).visibility = View.INVISIBLE
            findViewById<View>(R.id.shortcut_previous_icon).visibility = View.INVISIBLE
            findViewById<View>(R.id.shortcut_zip_path).visibility = View.INVISIBLE
        }
        findViewById<EditText>(R.id.shortcut_zip_path).setOnClickListener(this::onTextChange)
        findViewById<Button>(R.id.accept).setOnClickListener(this::accept)
        findViewById<Button>(R.id.reject).setOnClickListener(this::reject)
        findViewById<ImageButton>(R.id.shortcut_next_icon).setOnClickListener(this::nextIcon)
        findViewById<ImageButton>(R.id.shortcut_previous_icon).setOnClickListener(this::prevIcon)

    }

    private fun nextIcon(view: View){
        iconIndex+=1
        val max = icons?.size?:1
        if(iconIndex>=max){
            iconIndex=max - 1
        }
        setIcon()
    }
    private fun prevIcon(view: View){
        iconIndex-=1
        if(iconIndex<0){
            iconIndex=0
        }
        setIcon()
    }

    private fun setIcon(){
        val tempIcons = icons
        if (tempIcons != null) {
            val bitmap = bitmapFromZip(zipFile, tempIcons[iconIndex])

            findViewById<ImageView>(R.id.imageView).setImageBitmap(bitmap)
            findViewById<EditText>(R.id.shortcut_zip_path).setText(tempIcons[iconIndex])
        }
    }

    private fun onTextChange(view: View) {
        val editText = findViewById<EditText>(R.id.shortcut_zip_path)
        val bitmap = bitmapFromZip(zipFile, "${editText.text}")
        findViewById<ImageView>(R.id.imageView).setImageBitmap(bitmap)
    }

    private fun accept(view: View){
        val filePath = intent.getStringExtra(ZipConstants.FILE_NAME) ?: return finish()
        val name = Uri.parse(filePath).lastPathSegment?.split('.')?.first()?:"Untitled"
        val shortcutIntent = Intent(
            applicationContext,
            DeepLinkActivity::class.java
        )
        shortcutIntent.action = Intent.ACTION_VIEW
        shortcutIntent.addCategory(Intent.CATEGORY_BROWSABLE)
        val uriString = "app://${getString(R.string.package_name)}/play/${filePath.replace(getExternalFilesDir(null)?.absolutePath?:"","")}"
        shortcutIntent.data = Uri.parse(uriString)

        val editText = findViewById<EditText>(R.id.shortcut_zip_path)
        val bitmap = bitmapFromZip(zipFile, "${editText.text}")
        val icon = if (bitmap != null) {
            IconCompat.createWithBitmap(bitmap)
        } else {
            IconCompat.createWithResource(this, R.drawable.ic_launcher_foreground)
        }
        val shortcutInfo: ShortcutInfoCompat = ShortcutInfoCompat.Builder(applicationContext, uriString)
            .setIntent(
                shortcutIntent
            )
            .setShortLabel(name)
            .setIcon(icon)
            .build()
        ShortcutManagerCompat.requestPinShortcut(this, shortcutInfo, null)
        finish()


    }

    private fun reject(view: View){
        finish()
    }

    private fun bitmapFromZip(zipFile: ZipFile?, path: String?): Bitmap? {
        if (zipFile == null || path == null) {
            return null
        }
        val am: ActivityManager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val opts: BitmapFactory.Options = BitmapFactory.Options()
        opts.inSampleSize = if (am.isLowRamDevice()) 4 else 2
        opts.inPreferredConfig = Bitmap.Config.RGB_565
        val file = zipFile.getEntry(path) ?: return null

        return BitmapFactory.decodeStream(zipFile.getInputStream(file), null, opts);
    }

    private fun findAllIcons (zipFile: ZipFile): List<String> {
        val extensions = setOf<String>("png", "jpg")
        val result = mutableListOf<String>()
        if (zipFile.getEntry("favicon.ico") != null) {
            result.add("favicon.ico")
        }
        for (file in zipFile.entries()) {
            if (file.isDirectory) {
                continue
            }
            val segments = Uri.parse(file.name).pathSegments
            if (extensions.contains(segments.last().split('.').last())) {
                //segments.subList(1, segments.size).joinToString("/")
                result.add(file.name)
            }
        }
        return result.sortedBy { it.count { it -> it == '/' } }
    }
}