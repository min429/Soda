package com.soda.soda.fragments;

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.soda.soda.databinding.FragmentWarningBinding

/** 위험알림 다이얼로그 **/
class WarningFragment(
    text: String
) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: FragmentWarningBinding? = null
    private val binding get() = _binding!!

    private var text: String? = null

    init {
        this.text = text
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWarningBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.confirmTextView.text = text

        // 확인 버튼 클릭
        binding.checkButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
