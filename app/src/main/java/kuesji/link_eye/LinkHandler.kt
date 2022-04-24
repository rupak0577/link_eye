package kuesji.link_eye

import android.app.Activity
import android.os.Bundle
import android.app.ActivityManager.TaskDescription
import android.content.*
import kuesji.link_eye.R
import kuesji.link_eye.HistoryHelper
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import kuesji.link_eye.LinkHandler.HandlerEntry
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.view.Gravity
import android.graphics.Typeface
import android.net.Uri
import android.util.TypedValue
import android.view.View
import android.widget.*
import java.lang.Exception
import java.text.Collator
import java.util.*

class LinkHandler : Activity() {
    private lateinit var urlArea: EditText
    private lateinit var actionCopy: Button
    private lateinit var actionOpen: Button
    private lateinit var actionShare: Button
    private lateinit var contentArea: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Theme.background_primary
        window.navigationBarColor = Theme.background_primary
        setTaskDescription(
            TaskDescription(
                getString(R.string.app_name),
                R.drawable.ic_link_eye,
                getColor(R.color.background_primary)
            )
        )
        setContentView(R.layout.link_handler)
        urlArea = findViewById(R.id.link_handler_url)
        actionCopy = findViewById(R.id.link_handler_action_copy)
        actionCopy.setOnClickListener(View.OnClickListener { v: View? ->
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(
                ClipData.newPlainText(
                    "link eye",
                    urlArea.getText().toString()
                )
            )
            Toast.makeText(
                this,
                getString(R.string.link_handler_copied_to_clipboard),
                Toast.LENGTH_SHORT
            ).show()
        })
        actionOpen = findViewById(R.id.link_handler_action_open)
        actionOpen.setOnClickListener(View.OnClickListener { v: View? ->
            actionOpen.setBackgroundColor(getColor(R.color.background_secondary))
            actionShare!!.setBackgroundColor(getColor(R.color.background_seconday_not_selected))
            listHandlers("open")
        })
        actionShare = findViewById(R.id.link_handler_action_share)
        actionShare.setOnClickListener(View.OnClickListener { v: View? ->
            actionShare.setBackgroundColor(getColor(R.color.background_secondary))
            actionOpen.setBackgroundColor(getColor(R.color.background_seconday_not_selected))
            listHandlers("share")
        })
        contentArea = findViewById(R.id.link_handler_content)
        val intent = intent
        if (intent.action == Intent.ACTION_VIEW) {
            urlArea.setText(intent.data.toString())
        } else if (intent.action == Intent.ACTION_SEND) {
            var text: String? = ""
            if (intent.hasExtra(Intent.EXTRA_SUBJECT)) {
                text += """
                    ${intent.getStringExtra(Intent.EXTRA_SUBJECT)}
                    
                    """.trimIndent()
            }
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                text += intent.getStringExtra(Intent.EXTRA_TEXT)
            }
            urlArea.setText(text)
        }
        actionOpen.performClick()
        val historyHelper = HistoryHelper(this)
        historyHelper.insert(urlArea.getText().toString())
        historyHelper.close()
    }

    private fun listHandlers(target: String) {
        val pm = packageManager
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (target == "open") {
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(urlArea!!.text.toString())
        } else if (target == "share") {
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, urlArea!!.text.toString())
        }
        val resolves = pm.queryIntentActivities(
            intent,
            PackageManager.MATCH_ALL or PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS
        )
        val collator = Collator.getInstance(Locale.getDefault())
        Collections.sort(resolves) { o1: ResolveInfo, o2: ResolveInfo ->
            collator.compare(
                o1.activityInfo.loadLabel(pm).toString().toLowerCase(Locale.getDefault()),
                o2.activityInfo.loadLabel(pm).toString().toLowerCase(Locale.getDefault())
            )
        }
        contentArea!!.removeAllViews()
        for (resolve_info in resolves) {
            if (resolve_info.activityInfo.packageName == packageName) {
                continue
            }
            val entry = HandlerEntry(this)
                .setIcon(resolve_info.loadIcon(pm))
                .setLabel(resolve_info.loadLabel(pm).toString())
                .setComponent(
                    ComponentName.createRelative(
                        resolve_info.activityInfo.packageName,
                        resolve_info.activityInfo.name
                    ).flattenToShortString()
                )
                .setIntent(intent)
            contentArea!!.addView(entry)
        }
    }

    internal inner class HandlerEntry(context: Context?) : LinearLayout(context) {
        private var component: String? = null
        private var intent: Intent? = null
        private val icon: ImageView
        private val label: TextView
        fun setIcon(icon: Drawable?): HandlerEntry {
            this.icon.setImageDrawable(icon)
            return this@HandlerEntry
        }

        fun setLabel(label: String?): HandlerEntry {
            this.label.text = label
            return this@HandlerEntry
        }

        fun setComponent(component: String?): HandlerEntry {
            this.component = component
            return this@HandlerEntry
        }

        fun setIntent(intent: Intent?): HandlerEntry {
            this.intent = intent
            return this@HandlerEntry
        }

        init {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 112)
            orientation = HORIZONTAL
            setOnClickListener { v: View? ->
                if (intent != null && component != null) {
                    intent!!.component = ComponentName.unflattenFromString(component!!)
                    try {
                        startActivity(intent)
                        finishAndRemoveTask()
                    } catch (e: Exception) {
                        Toast.makeText(
                            getContext(),
                            getString(R.string.link_handler_error_launch_failed),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            icon = ImageView(context)
            icon.layoutParams = LayoutParams(144, 112)
            icon.setPadding(16, 16, 16, 16)
            icon.scaleType = ImageView.ScaleType.CENTER_INSIDE
            addView(icon)
            label = TextView(context)
            label.layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            label.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            label.text = ""
            label.typeface = Typeface.MONOSPACE
            label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            label.setTextColor(getColor(R.color.foreground_primary))
            addView(label)
        }
    }
}