package com.soda.soda.fragments

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
import com.soda.soda.MainActivity
import com.soda.soda.R
import com.soda.soda.databinding.FragmentSettingBinding
import com.soda.soda.service.ForegroundService
import org.tensorflow.lite.examples.audio.fragments.UserguideFragment
import kotlin.properties.Delegates

private const val TAG = "SettingFragment"

class SettingFragment : Fragment(){
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
        binding.backgroundSwitch.isChecked = backgroundSwitchState
        binding.backgroundSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save switch state to SharedPreferences when state changes
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return@setOnCheckedChangeListener
            with (sharedPref.edit()) {
                putBoolean("saved_switch_state_key", isChecked) //"saved_switch_state_key"라는 키 값으로 isChecked라는 값을 SharedPreferences에 저장
                apply()
                backgroundSwitchState = isChecked
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
            if(isMyServiceRunning(requireContext(), ForegroundService::class.java)){
                if(isChecked) AudioFragment.startRecording()
                else AudioFragment.stopRecording()
            }
            autoSwitchState = isChecked
            Log.d(TAG, "isListening: "+ AudioFragment.isListening)
            Log.d(TAG, "isChecked: $isChecked")
        }

        /** 진동알림 스위치 **/
        binding.vibrateSwitch.isChecked = vibrateSwitchState
        binding.vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            vibrateSwitchState = isChecked
        }

        /** 사용설명서 **/
        binding.instructionCard.setOnClickListener {
            navigateToFragment(UserguideFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToFragment(fragment: UserguideFragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    /** 백그라운드 스위치 상태 설정 **/
    fun setBackgroundSwitchState(activity: MainActivity){
        // Restore switch state from SharedPreferences
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        //getString(R.string.saved_switch_state_key)를 통해 strings.xml 파일에 정의된 키 값을 가져오고 sharedPref.getBoolean은 이 키 값에 해당하는 값이
        //SharedPreferences에 저장되어 있으면 그 값을 반환하고 없으면 false를 반환함
        backgroundSwitchState = sharedPref.getBoolean(getString(R.string.saved_background_switch_state_key), false)
    }

    companion object {
        var autoSwitchState: Boolean = true
        var vibrateSwitchState: Boolean = true
        var backgroundSwitchState by Delegates.notNull<Boolean>()

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