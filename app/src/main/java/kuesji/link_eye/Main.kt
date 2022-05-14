package kuesji.link_eye

import android.app.ActivityManager.TaskDescription
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import kuesji.link_eye.databinding.MainBinding

class Main : FragmentActivity() {
    private lateinit var binding: MainBinding

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

        loadStatusTab()

        binding.mainTabStatus.setOnClickListener {
            loadStatusTab()
        }

        binding.mainTabHistory.setOnClickListener {
            resetTabColors()
            binding.mainTabHistory.setBackgroundColor(getColor(R.color.background_secondary))

            if (supportFragmentManager.findFragmentByTag("HistoryFragment") == null)
                supportFragmentManager.beginTransaction()
                    .replace(binding.mainContent.id, FragmentHistory(), "HistoryFragment")
                    .commit()
        }

        binding.mainTabAbout.setOnClickListener {
            resetTabColors()
            binding.mainTabAbout.setBackgroundColor(getColor(R.color.background_secondary))

            if (supportFragmentManager.findFragmentByTag("AboutFragment") == null)
                supportFragmentManager.beginTransaction()
                    .replace(binding.mainContent.id, FragmentAbout(), "AboutFragment")
                    .commit()
        }
    }

    private fun loadStatusTab() {
        resetTabColors()
        binding.mainTabStatus.setBackgroundColor(getColor(R.color.background_secondary))

        if (supportFragmentManager.findFragmentByTag("StatusFragment") == null)
            supportFragmentManager.beginTransaction()
                .replace(binding.mainContent.id, FragmentStatus(), "StatusFragment")
                .commit()
    }

    private fun resetTabColors() {
        binding.mainTabStatus.setBackgroundColor(getColor(R.color.background_seconday_not_selected))
        binding.mainTabHistory.setBackgroundColor(getColor(R.color.background_seconday_not_selected))
        binding.mainTabAbout.setBackgroundColor(getColor(R.color.background_seconday_not_selected))
    }
}