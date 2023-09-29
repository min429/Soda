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
import com.soda.soda.databinding.FragmentSubSettingBinding
import com.soda.soda.helper.DECIBEL_THRESHOLD
import com.soda.soda.service.ForegroundService
import kotlin.properties.Delegates

private const val TAG = "SubSettingFragment"

class SubSettingFragment : Fragment(){
    private var _binding: FragmentSubSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private lateinit var serviceIntent: Intent
    private lateinit var stopServiceIntent: Intent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubSettingBinding.inflate(inflater, container, false)
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
            val sharedPref = context?.getSharedPreferences("background_shared_pref", Context.MODE_PRIVATE) ?: return@setOnCheckedChangeListener
            with (sharedPref.edit()) {
                putBoolean("background_switch_state", isChecked) //"saved_switch_state_key"라는 키 값으로 isChecked라는 값을 SharedPreferences에 저장
                apply()
                backgroundSwitchState = isChecked
            }
            if (isChecked) {
                ContextCompat.startForegroundService(mainActivity, serviceIntent)
            } else {
                mainActivity.stopService(stopServiceIntent)
            }
        }

        /** 자동분류 스위치 **/
        binding.autoClassificationSwitch.isChecked = autoSwitchState
        binding.autoClassificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            val sharedPref = context?.getSharedPreferences("auto_shared_pref", Context.MODE_PRIVATE) ?: return@setOnCheckedChangeListener
            with (sharedPref.edit()) {
                putBoolean("auto_switch_state", isChecked)
                apply()
                autoSwitchState = isChecked
            }
            if(isMyServiceRunning(requireContext(), ForegroundService::class.java)){
                if(isChecked) AudioFragment.startRecording()
                else AudioFragment.stopRecording()
            }
        }

        /** 진동알림 스위치 **/
        binding.vibrateSwitch.isChecked = vibrateSwitchState
        binding.vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            val sharedPref = context?.getSharedPreferences("vibrate_shared_pref", Context.MODE_PRIVATE) ?: return@setOnCheckedChangeListener
            with (sharedPref.edit()) {
                putBoolean("vibrate_switch_state", isChecked)
                apply()
                vibrateSwitchState = isChecked
            }
        }

        /** 플래시 알림 스위치 **/
        binding.flashSwitch.isChecked = flashSwitchState
        binding.flashSwitch.setOnCheckedChangeListener { _, isChecked ->
            val sharedPref = context?.getSharedPreferences("flash_shared_pref", Context.MODE_PRIVATE) ?: return@setOnCheckedChangeListener
            with (sharedPref.edit()) {
                putBoolean("flash_switch_state", isChecked)
                apply()
                flashSwitchState = isChecked
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var autoSwitchState: Boolean = true
        var vibrateSwitchState: Boolean = true
        var backgroundSwitchState by Delegates.notNull<Boolean>()
        var flashSwitchState: Boolean = true

        fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }

        /** 스위치 상태 설정 **/
        fun setSwitchState(context: Context, sharedPref: String, key: String, defValue: Boolean): Boolean{
            //SharedPreferences에서 스위치 상태 가져옴
            val sharedPref = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE)
            //sharedPref.getBoolean은 이 키 값에 해당하는 값이 SharedPreferences에 저장되어 있으면 그 값을 반환하고 없으면 defValue를 반환
            return sharedPref.getBoolean(key, defValue)
        }

    }

}