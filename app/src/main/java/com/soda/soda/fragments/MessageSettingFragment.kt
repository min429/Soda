import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.soda.soda.helper.SoundCheckHelper
import com.soda.soda.databinding.FragmentMessageSettingBinding
import com.soda.soda.fragments.SubSettingFragment


class MessageSettingFragment : Fragment() {
    private var _binding: FragmentMessageSettingBinding? = null
    private val binding get() = _binding!!

    // SharedPreferences 키 상수
    private val PREFS_NAME = "MessageSettingsPrefs"
    private val PREF_PHONE_NUMBER = "saved_phone_number"
    private val PREF_MESSAGE = "saved_message"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 사용자가 입력한 텍스트를 불러와 설정
        val (phoneNumber, message) = loadTextFromSharedPreferences()
        binding.phoneNumberEdittext.setText(phoneNumber)
        binding.messageContentEdittext.setText(message)

        /** 메세지 전송 스위치 **/
        binding.messageSwitch.isChecked = messageSwitchState
        binding.messageSwitch.setOnCheckedChangeListener { _, isChecked ->
            val sharedPref = context?.getSharedPreferences("message_shared_pref", Context.MODE_PRIVATE) ?: return@setOnCheckedChangeListener
            with (sharedPref.edit()) {
                putBoolean("message_switch_state", isChecked)
                apply()
                messageSwitchState = isChecked
            }
        }

        // 저장 버튼 클릭 이벤트 처리
        binding.saveButton.setOnClickListener {
            // 입력된 번호와 메시지 가져오기
            val phoneNumber = binding.phoneNumberEdittext.text.toString()
            val message = binding.messageContentEdittext.text.toString()

            // 설정한 번호와 메시지를 SoundCheckHelper에 전달
            SoundCheckHelper.setPhoneNumberAndMessage(phoneNumber, message)
            // 메시지가 저장되었다는 토스트 메시지 표시
            Toast.makeText(requireContext(), "메시지 주소가 저장되었습니다.", Toast.LENGTH_SHORT).show()

            // 사용자가 입력한 텍스트를 SharedPreferences에 저장
            saveTextToSharedPreferences(phoneNumber, message)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var messageSwitchState: Boolean = false
    }

    private fun saveTextToSharedPreferences(phoneNumber: String, message: String) {
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(PREF_PHONE_NUMBER, phoneNumber)
        editor.putString(PREF_MESSAGE, message)
        editor.apply()
    }

    private fun loadTextFromSharedPreferences(): Pair<String, String> {
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val phoneNumber = sharedPreferences.getString(PREF_PHONE_NUMBER, "") ?: ""
        val message = sharedPreferences.getString(PREF_MESSAGE, "") ?: ""
        return Pair(phoneNumber, message)
    }
}
