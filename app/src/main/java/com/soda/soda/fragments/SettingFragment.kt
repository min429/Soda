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
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return@setOnCheckedChangeListener
            with (sharedPref.edit()) {
                putBoolean("saved_switch_state_key", isChecked) //"saved_switch_state_key"라는 키 값으로 isChecked라는 값을 SharedPreferences에 저장
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
            if(isMyServiceRunning(requireContext(), ForegroundService::class.java)){
                if(isChecked) AudioFragment.startRecording()
                else AudioFragment.stopRecording()
            }
            autoSwitchState = isChecked
        }

        /** 진동알림 스위치 **/
        binding.vibrateSwitch.isChecked = vibrateSwitchState
        binding.vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            vibrateSwitchState = isChecked
        }

        /** 위험 소리 설정 **/
        binding.warningCustomCard.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, WarningCustomFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        /** 데시벨 설정 **/
        binding.decibelCard.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, DecibelCustomFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        /** 사용설명서 **/
        binding.instructionCard.setOnClickListener {
            val userguideFragment = UserguideFragment()
            userguideFragment.show(parentFragmentManager, "userguide_dialog")
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