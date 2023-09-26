import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.soda.soda.helper.SoundCheckHelper
import com.soda.soda.R
import com.soda.soda.fragments.SettingFragment
import kotlinx.android.synthetic.main.fragment_message_setting.*
import com.soda.soda.databinding.FragmentMessageSettingBinding


class MessageSettingFragment : Fragment() {
    private var _binding: FragmentMessageSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /** 진동알림 스위치 **/
        binding.messageSwitch.isChecked = MessageSettingFragment.messageSwitchState
        binding.messageSwitch.setOnCheckedChangeListener { _, isChecked ->
            MessageSettingFragment.messageSwitchState = isChecked
        }

        // 저장 버튼 클릭 이벤트 처리
        save_button.setOnClickListener {
            // 입력된 번호와 메시지 가져오기
            val phoneNumber = phone_number_edittext.text.toString()
            val message = message_content_edittext.text.toString()

            // 설정한 번호와 메시지를 SoundCheckHelper에 전달
            SoundCheckHelper.setPhoneNumberAndMessage(phoneNumber, message)

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // SettingFragment.messageSwitchState 대신 여기에서 직접 설정
        var messageSwitchState: Boolean = true
    }
}
