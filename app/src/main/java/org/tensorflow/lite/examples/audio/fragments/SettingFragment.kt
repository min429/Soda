package org.tensorflow.lite.examples.audio.fragments

import android.app.Activity
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
import org.tensorflow.lite.examples.audio.R
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

        /** 백그라운드 스위치 **/
        // Restore switch state from SharedPreferences
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultBackgroundSwitchState = false
        //getString(R.string.saved_switch_state_key)를 통해 strings.xml 파일에 정의된 키 값을 가져오고 sharedPref.getBoolean은 이 키 값에 해당하는 값이
        //SharedPreferences에 저장되어 있으면 그 값을 반환하고 없으면 defaultSwitchState를 반환함
        val backgroundSwitchState = sharedPref.getBoolean(getString(R.string.saved_background_switch_state_key), defaultBackgroundSwitchState)

        binding.backgroundSwitch.isChecked = backgroundSwitchState
        binding.backgroundSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save switch state to SharedPreferences when state changes
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return@setOnCheckedChangeListener
            with (sharedPref.edit()) {
                putBoolean("saved_switch_state_key", isChecked) //"saved_switch_state_key"라는 키 값으로 isChecked라는 값을 SharedPreferences에 저장
                apply()
            }

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

        /** 자동분류 스위치 **/
        binding.autoClassificationSwitch.isChecked = autoSwitchState
        binding.autoClassificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!AudioFragment.isListening) {
                autoSwitchState = isChecked
            } else {
                binding.autoClassificationSwitch.isChecked = autoSwitchState  // 스위치를 다시 이전 상태로 변경
            }
            Log.d(TAG, "isListening: "+AudioFragment.isListening)
            Log.d(TAG, "isListening: $isChecked")
        }

        /** 진동알림 스위치 **/
        binding.vibrateSwitch.isChecked = vibrateSwitchState
        binding.vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            vibrateSwitchState = isChecked
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var autoSwitchState: Boolean = true
        var vibrateSwitchState: Boolean = true

        fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }
    }
}