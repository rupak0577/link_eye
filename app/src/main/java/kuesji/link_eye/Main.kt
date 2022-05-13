package kuesji.link_eye

import android.app.Activity
import android.app.ActivityManager.TaskDescription
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewGroup
import android.widget.*
import kuesji.link_eye.HistoryHelper.HistoryModel
import kuesji.link_eye.databinding.MainAboutBinding
import kuesji.link_eye.databinding.MainBinding
import kuesji.link_eye.databinding.MainHistoryBinding
import kuesji.link_eye.databinding.MainStatusBinding

class Main : Activity() {
    private lateinit var binding: MainBinding

    private lateinit var tabStatus: MainStatusBinding
    private lateinit var tabStatusChange: Button
    private lateinit var tabStatusTest: Button
    private lateinit var tabStatusStatus: TextView
    private lateinit var tabHistory: MainHistoryBinding
    private lateinit var tabHistorySearch: EditText
    private lateinit var tabHistoryDeleteAll: Button
    private lateinit var tabHistoryContent: LinearLayout
    private lateinit var historyHelper: HistoryHelper
    private lateinit var tabAbout: MainAboutBinding

    private inner class HistoryEntry(context: Context?) : TextView(context) {
        var historyId = 0
        var historyEpoch: Long = 0

        init {
            typeface = Typeface.MONOSPACE
        }
    }

    private val tabButtonClick = View.OnClickListener { v: View ->
        val button = v as Button
        binding.mainTabStatus.setBackgroundColor(getColor(R.color.background_seconday_not_selected))
        binding.mainTabHistory.setBackgroundColor(getColor(R.color.background_seconday_not_selected))
        binding.mainTabAbout.setBackgroundColor(getColor(R.color.background_seconday_not_selected))
        button.setBackgroundColor(getColor(R.color.background_secondary))
        binding.mainContent.removeAllViews()
        if (button === binding.mainTabStatus) {
            binding.mainContent.addView(tabStatus.root)
        } else if (button === binding.mainTabHistory) {
            binding.mainContent.addView(tabHistory.root)
        } else if (button === binding.mainTabAbout) {
            binding.mainContent.addView(tabAbout.root)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.background_primary)
        window.navigationBarColor = getColor(R.color.background_primary)
        setTaskDescription(
            TaskDescription(
                getString(R.string.app_name),
                R.drawable.ic_link_eye,
                getColor(R.color.background_primary)
            )
        )
        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainTabStatus.setOnClickListener(tabButtonClick)
        binding.mainTabHistory.setOnClickListener(tabButtonClick)
        binding.mainTabAbout.setOnClickListener(tabButtonClick)
        setup_tab_status()
        setup_tab_history()
        setup_tab_about()
    }

    private fun setup_tab_status() {
        tabStatus = MainStatusBinding.inflate(layoutInflater)
        tabStatus.root.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                val browserIntent =
                    Intent("android.intent.action.VIEW", Uri.parse("https://kuesji.koesnu.com"))
                val resolveInfo =
                    packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
                if (resolveInfo!!.activityInfo.packageName != null && resolveInfo.activityInfo.packageName == packageName) {
                    tabStatusStatus!!.text = getString(R.string.main_status_enabled)
                } else {
                    tabStatusStatus!!.text = getString(R.string.main_status_disabled)
                }
            }

            override fun onViewDetachedFromWindow(v: View) {}
        })
        tabStatusChange = tabStatus.mainStatusChange
        tabStatusChange.setOnClickListener(View.OnClickListener { vx: View? ->
            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    getString(R.string.main_status_error_launch_settings),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        tabStatusTest = tabStatus.mainStatusTest
        tabStatusTest.setOnClickListener(View.OnClickListener { vx: View? ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.main_status_test_url))
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    getString(R.string.main_status_error_launch_test),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        tabStatusStatus = tabStatus.mainStatusStatus
    }

    private fun setup_tab_history() {
        tabHistory = MainHistoryBinding.inflate(layoutInflater)
        tabHistory.root.setLayoutParams(
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        tabHistory.root.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                historyHelper = HistoryHelper(this@Main)
                listHistoryEntries(null)
            }

            override fun onViewDetachedFromWindow(v: View) {
                historyHelper!!.close()
            }
        })
        tabHistorySearch = tabHistory.mainHistorySearch
        tabHistorySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                listHistoryEntries(tabHistorySearch.getText().toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
        tabHistoryDeleteAll = tabHistory.mainHistoryDeleteAll
        tabHistoryDeleteAll.setOnClickListener(View.OnClickListener { v: View? ->
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setCancelable(true)
                .setTitle(getString(R.string.main_history_header_delete_all_title))
                .setMessage(getString(R.string.main_history_header_delete_all_description))
                .setNegativeButton(getString(R.string.main_history_header_delete_all_no)) { dlg: DialogInterface, which: Int -> dlg.dismiss() }
                .setPositiveButton(getString(R.string.main_history_header_delete_all_yes)) { dlg: DialogInterface, which: Int ->
                    historyHelper!!.clear()
                    listHistoryEntries(null)
                    dlg.dismiss()
                }.show()
        })
        tabHistoryContent = MainHistoryBinding.inflate(layoutInflater).mainHistoryContent
    }

    private fun setup_tab_about() {
        tabAbout = MainAboutBinding.inflate(layoutInflater)
    }

    private fun generateHistoryEntry(): HistoryEntry {
        val entry = HistoryEntry(this)
        entry.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        entry.setPadding(16, 16, 16, 16)
        entry.setBackgroundColor(getColor(R.color.background_seconday_not_selected))
        entry.setTextColor(getColor(R.color.foreground_primary))
        entry.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        entry.text = "https://kuesji.koesnu.com"
        entry.setOnClickListener { v: View? ->
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle(getString(R.string.main_history_url_clicked_title))
                .setMessage(entry.text.toString())
                .setPositiveButton(getString(R.string.main_history_url_clicked_open)) { dlg: DialogInterface?, which: Int ->
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_TEXT, entry.text.toString())
                    intent.component =
                        ComponentName.createRelative(packageName, LinkHandler::class.java.name)
                    startActivity(intent)
                }
                .setNegativeButton(getString(R.string.main_history_url_clicked_delete)) { dlg: DialogInterface?, which: Int ->
                    historyHelper!!.delete(entry.historyId)
                    tabHistoryContent!!.removeView(entry)
                }
                .setNeutralButton(getString(R.string.main_history_url_clicked_cancel)) { dlg: DialogInterface?, which: Int -> }
                .show()
        }
        return entry
    }

    private fun listHistoryEntries(query: String?) {
        val historyEntries: List<HistoryModel>
        historyEntries = if (query == null) {
            historyHelper!!.list()
        } else {
            historyHelper!!.search(query)
        }
        tabHistoryContent!!.removeAllViews()
        if (historyEntries.size < 0) {
        } else {
            for (model in historyEntries) {
                val view = generateHistoryEntry()
                view.text = model.content
                view.historyId = model.id
                view.historyEpoch = model.epoch.toLong()
                tabHistoryContent!!.addView(view)
                val divider = View(this)
                divider.layoutParams =
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 4)
                tabHistoryContent!!.addView(divider)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (binding.mainContent.childCount < 1) {
            binding.mainTabStatus.performClick()
        }
        if (tabStatus.root.parent != null) {
            val browserIntent =
                Intent("android.intent.action.VIEW", Uri.parse("https://kuesji.koesnu.com"))
            val resolveInfo =
                packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveInfo!!.activityInfo.packageName != null && resolveInfo.activityInfo.packageName == packageName) {
                tabStatusStatus!!.text = getString(R.string.main_status_enabled)
            } else {
                tabStatusStatus!!.text = getString(R.string.main_status_disabled)
            }
        }
        if (tabHistory.root.parent != null) {
            if (tabHistorySearch!!.text.toString().length < 1) {
                listHistoryEntries(null)
            }
        }
    }
}