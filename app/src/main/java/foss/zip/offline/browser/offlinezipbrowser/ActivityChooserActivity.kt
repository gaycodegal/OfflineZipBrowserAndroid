package foss.zip.offline.browser.offlinezipbrowser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ActivityChooserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chooser_layout)
        findViewById<Button>(R.id.button).setOnClickListener(this::openDeeplink)
        findViewById<Button>(R.id.button2).setOnClickListener(this::openDeeplink)
        findViewById<Button>(R.id.button3).setOnClickListener(this::openDeeplink)
        findViewById<Button>(R.id.button4).setOnClickListener(this::openDeeplink)
    }
    fun openDeeplink(view: View) {
        val deepIntent = Intent(
            applicationContext,
            DeepLinkActivity::class.java
        )
        deepIntent.action = Intent.ACTION_VIEW
        deepIntent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val tag = view.tag as String
        deepIntent.data = Uri.parse("app://${getString(R.string.package_name)}${tag}")
        startActivity(deepIntent)
    }
}