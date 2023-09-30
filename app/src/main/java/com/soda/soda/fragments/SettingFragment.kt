package com.soda.soda.fragments

import MessageSettingFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.soda.soda.MainActivity
import com.soda.soda.R
import com.soda.soda.databinding.FragmentSettingBinding
import org.tensorflow.lite.examples.audio.fragments.UserguideFragment

private const val TAG = "SettingFragment"

class SettingFragment : Fragment(){
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

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

        /** 세부 설정 **/
        binding.subSettingCard.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, SubSettingFragment())
            transaction.addToBackStack(null)
            transaction.commit()
            mainActivity.toolbarLayoutBinding.toolbarTitle.text ="세부 설정"
        }

        /** 위험 소리 설정 **/
        binding.warningCustomCard.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, WarningCustomFragment())
            transaction.addToBackStack(null)
            transaction.commit()
            mainActivity.toolbarLayoutBinding.toolbarTitle.text = "위험 소리 설정"
        }

        /** 데시벨 설정 **/
        binding.decibelCard.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, DecibelCustomFragment())
            transaction.addToBackStack(null)
            transaction.commit()
            mainActivity.toolbarLayoutBinding.toolbarTitle.text = "데시벨 설정"
        }

        /** 메시지 전송 설정 **/
        binding.messagegoCard.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, MessageSettingFragment())
            transaction.addToBackStack(null)
            transaction.commit()
            mainActivity.toolbarLayoutBinding.toolbarTitle.text = "메시지 전송 설정"
        }

        /** 주변환경 설정 **/
        binding.surroundCard.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, SurroundCustomFragment())
            transaction.addToBackStack(null)
            transaction.commit()
            mainActivity.toolbarLayoutBinding.toolbarTitle.text = "주변 환경 설정"
        }

        /** 사용설명서 **/
        binding.instructionCard.setOnClickListener {
            val imageSliderFragment = ImageSliderFragment()
            imageSliderFragment.show(parentFragmentManager, "userguide_dialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}