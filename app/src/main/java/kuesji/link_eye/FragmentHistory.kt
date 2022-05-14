package kuesji.link_eye

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import kuesji.link_eye.databinding.MainHistoryBinding

class FragmentHistory : Fragment() {
    private var _binding: MainHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var historyHelper: HistoryHelper

    private inner class HistoryEntry(context: Context?) : TextView(context) {
        var historyId = 0
        var historyEpoch: Long = 0

        init {
            typeface = Typeface.MONOSPACE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.root.layoutParams = LinearLayout.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        )

        historyHelper = HistoryHelper(requireContext())

        binding.mainHistorySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                listHistoryEntries(binding.mainHistorySearch.text.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.mainHistoryDeleteAll.setOnClickListener {
            AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.AlertDialogCustom))
                .setCancelable(true)
                .setTitle(getString(R.string.main_history_header_delete_all_title))
                .setMessage(getString(R.string.main_history_header_delete_all_description))
                .setNegativeButton(getString(R.string.main_history_header_delete_all_no)) { dlg: DialogInterface, which: Int -> dlg.dismiss() }
                .setPositiveButton(getString(R.string.main_history_header_delete_all_yes)) { dlg: DialogInterface, which: Int ->
                    historyHelper.clear()
                    listHistoryEntries(null)
                    dlg.dismiss()
                }.show()
        }

        listHistoryEntries(null)
    }

    private fun listHistoryEntries(query: String?) {
        val historyEntries: List<HistoryHelper.HistoryModel> = if (query == null) {
            historyHelper.list()
        } else {
            historyHelper.search(query)
        }
        binding.mainHistoryContent.removeAllViews()
        if (historyEntries.size < 0) {
        } else {
            for (model in historyEntries) {
                val view = generateHistoryEntry()
                view.text = model.content
                view.historyId = model.id
                view.historyEpoch = model.epoch.toLong()
                binding.mainHistoryContent.addView(view)
                val divider = View(requireContext())
                divider.layoutParams =
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 4)
                binding.mainHistoryContent.addView(divider)
            }
        }
    }

    private fun generateHistoryEntry(): HistoryEntry {
        val entry = HistoryEntry(requireContext())
        entry.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        entry.setPadding(16, 16, 16, 16)
        entry.setBackgroundColor(requireContext().getColor(R.color.background_seconday_not_selected))
        entry.setTextColor(requireContext().getColor(R.color.foreground_primary))
        entry.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        entry.text = "https://kuesji.koesnu.com"
        entry.setOnClickListener { v: View? ->
            AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.AlertDialogCustom))
                .setTitle(getString(R.string.main_history_url_clicked_title))
                .setMessage(entry.text.toString())
                .setPositiveButton(getString(R.string.main_history_url_clicked_open)) { dlg: DialogInterface?, which: Int ->
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_TEXT, entry.text.toString())
                    intent.component =
                        ComponentName.createRelative(
                            requireActivity().packageName,
                            LinkHandler::class.java.name
                        )
                    startActivity(intent)
                }
                .setNegativeButton(getString(R.string.main_history_url_clicked_delete)) { dlg: DialogInterface?, which: Int ->
                    historyHelper.delete(entry.historyId)
                    binding.mainHistoryContent.removeView(entry)
                }
                .setNeutralButton(getString(R.string.main_history_url_clicked_cancel)) { dlg: DialogInterface?, which: Int -> }
                .show()
        }
        return entry
    }

    override fun onDestroyView() {
        super.onDestroyView()
        historyHelper.close()
        _binding = null
    }
}