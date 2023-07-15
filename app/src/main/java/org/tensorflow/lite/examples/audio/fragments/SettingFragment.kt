package org.tensorflow.lite.examples.audio.fragments

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.tensorflow.lite.examples.audio.MainActivity
import org.tensorflow.lite.examples.audio.databinding.FragmentSettingBinding
import org.tensorflow.lite.examples.audio.service.ForegroundService

private const val TAG = "SettingFragment"

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private lateinit var serviceIntent: Intent
    private lateinit var stopServiceIntent: Intent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as MainActivity

        serviceIntent = Intent(mainActivity, ForegroundService::class.java)
        stopServiceIntent = Intent(mainActivity, ForegroundService::class.java)

        binding.background.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ContextCompat.startForegroundService(mainActivity, serviceIntent)
                val isRunning = isMyServiceRunning(requireContext(), ForegroundService::class.java)
                Log.d(TAG, "isRunning: $isRunning")
            } else {
                mainActivity.stopService(stopServiceIntent)
                val isRunning = isMyServiceRunning(requireContext(), ForegroundService::class.java)
                Log.d(TAG, "isRunning: $isRunning")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
