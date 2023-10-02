package com.soda.soda.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.soda.soda.R
import com.soda.soda.databinding.FragmentImageSliderBinding
import com.soda.soda.ui.ImageSliderAdapter

class ImageSliderFragment : DialogFragment(R.layout.fragment_image_slider) {
    private var _binding: FragmentImageSliderBinding? = null
    private val binding get() = _binding!!
    private val images = listOf(
        R.drawable.group1,
        R.drawable.group2,
        R.drawable.group3,
        R.drawable.group4,
        R.drawable.group5,
        R.drawable.group6,
        R.drawable.group7,
        R.drawable.group8,
        R.drawable.group9,
        R.drawable.group10,
        R.drawable.group11,
        R.drawable.group12,
        R.drawable.group13,
    )

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentImageSliderBinding.bind(view)

        val adapter = ImageSliderAdapter(images)
        binding.viewPager.adapter = adapter

        val dontShowAgainVisible = loadGuideState(requireContext())
        binding.btnDoNotShowAgain.visibility = if (!dontShowAgainVisible) View.VISIBLE else View.GONE

        // 페이지 번호를 업데이트하는 콜백 등록
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 현재 페이지와 전체 페이지 수를 표시
                binding.textViewPageNumber.text = "${position + 1}/${images.size}"
            }
        })

        // 닫기 버튼 클릭 리스너 설정
        binding.btnClose.setOnClickListener {
            isFirstOpen = false
            dismiss() // 다이얼로그 종료
        }

        // "다시 보지 않기" 버튼 클릭 리스너 설정
        binding.btnDoNotShowAgain.setOnClickListener {
            dismiss() // 다이얼로그 종료

            saveGuideState(requireContext(), true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var isFirstOpen = true

        private fun saveGuideState(context: Context, value: Boolean){
            val sharedPref = context.getSharedPreferences("guide_saved_pref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("guide", value)
            editor.apply()
        }

        fun loadGuideState(context: Context): Boolean {
            val sharedPref = context.getSharedPreferences("guide_saved_pref", Context.MODE_PRIVATE)
            return sharedPref.getBoolean("guide", false)
        }
    }

}
