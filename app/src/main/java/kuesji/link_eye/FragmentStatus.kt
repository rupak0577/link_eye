package kuesji.link_eye

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kuesji.link_eye.databinding.MainStatusBinding

class FragmentStatus : Fragment() {
    private var _binding: MainStatusBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainStatusChange.setOnClickListener {
            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.main_status_error_launch_settings),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.mainStatusTest.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.main_status_test_url))
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.main_status_error_launch_test),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        checkStatus()
    }

    private fun checkStatus() {
        val browserIntent =
            Intent("android.intent.action.VIEW", Uri.parse("https://kuesji.koesnu.com"))
        val resolveInfo =
            requireActivity().packageManager.resolveActivity(
                browserIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        if (resolveInfo?.activityInfo?.packageName == requireActivity().packageName) {
            binding.mainStatusStatus.text = getString(R.string.main_status_enabled)
        } else {
            binding.mainStatusStatus.text = getString(R.string.main_status_disabled)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}