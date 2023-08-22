package com.soda.soda.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.soda.soda.R
import com.soda.soda.databinding.FragmentDecibelCustomBinding
import com.soda.soda.helper.DECIBEL_THRESHOLD

class DecibelCustomFragment : Fragment(R.layout.fragment_decibel_custom) {

    private var _binding: FragmentDecibelCustomBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDecibelCustomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기값 설정
        val decibel = isDecibelSaved(requireContext())
        DECIBEL_THRESHOLD = decibel
        binding.decibelSeekbar.progress = decibel
        binding.decibelValueText.text = decibel.toString() + "dB"

        binding.decibelSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // SeekBar의 값이 변경될 때마다 TextView 업데이트
                binding.decibelValueText.text = progress.toString()
                // 값을 SoundCheckHelper.DECIBEL_THRESHOLD로 설정
                DECIBEL_THRESHOLD = progress
                setDecibelSaved(requireContext(), progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isDecibelSaved(context: Context): Int {
        val sharedPref = context.getSharedPreferences("decibel_saved_pref", Context.MODE_PRIVATE)
        return sharedPref.getInt("decibel", 100)
    }

    private fun setDecibelSaved(context: Context, value: Int){
        val sharedPref = context.getSharedPreferences("decibel_saved_pref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("decibel", value)
        editor.apply()
    }

}
