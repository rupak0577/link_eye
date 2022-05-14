package kuesji.link_eye

import android.app.ActivityManager.TaskDescription
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import kuesji.link_eye.databinding.MainBinding

class Main : FragmentActivity() {
    private lateinit var binding: MainBinding

    private val tabButtonClick = View.OnClickListener { v: View ->
        val button = v as Button
        binding.mainTabStatus.setBackgroundColor(getColor(R.color.background_seconday_not_selected))
        binding.mainTabHistory.setBackgroundColor(getColor(R.color.background_seconday_not_selected))
        binding.mainTabAbout.setBackgroundColor(getColor(R.color.background_seconday_not_selected))
        button.setBackgroundColor(getColor(R.color.background_secondary))
        binding.mainContent.removeAllViews()
        if (button === binding.mainTabStatus) {
            supportFragmentManager.beginTransaction()
                .replace(binding.mainContent.id, FragmentStatus())
                .commit()
        } else if (button === binding.mainTabHistory) {
            supportFragmentManager.beginTransaction()
                .replace(binding.mainContent.id, FragmentHistory())
                .commit()
        } else if (button === binding.mainTabAbout) {
            supportFragmentManager.beginTransaction()
                .replace(binding.mainContent.id, FragmentAbout())
                .commit()
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
    }

    override fun onStart() {
        super.onStart()
        if (binding.mainContent.childCount < 1) {
            binding.mainTabStatus.performClick()
        }
    }
}