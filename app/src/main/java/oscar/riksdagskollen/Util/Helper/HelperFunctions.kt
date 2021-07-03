package oscar.riksdagskollen.Util.Helper

import android.content.Intent
import android.net.Uri
import android.os.Build
import oscar.riksdagskollen.BuildConfig

fun createDeveloperEmailIntent(email: String): Intent {

    val selectorIntent = Intent(Intent.ACTION_SENDTO)
    selectorIntent.data = Uri.parse("mailto:")

    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Tankar kring Riksdagskollen")
    emailIntent.putExtra(
        Intent.EXTRA_TEXT, """
                        
                        
                        Systeminformation:
                        App-version: ${BuildConfig.VERSION_NAME}
                        Sdk-version: ${Build.VERSION.SDK_INT}
                        """.trimIndent()
    )
    emailIntent.selector = selectorIntent
    emailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    return emailIntent
}