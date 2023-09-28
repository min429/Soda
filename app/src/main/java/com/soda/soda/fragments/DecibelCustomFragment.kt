package com.soda.soda.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.soda.soda.R
import com.soda.soda.ui.DecibelCustomAdapter
import com.soda.soda.databinding.FragmentDecibelCustomBinding
import com.soda.soda.helper.DECIBEL_THRESHOLD
import com.soda.soda.ui.Item

data class DecibelItem(
    val dBText: String,
    val infoText: String,
    val threshold: Int
)

interface OnDecibelItemClickListener {
    fun onDecibelItemClick(threshold: Int)
}

class DecibelCustomFragment : Fragment(R.layout.fragment_decibel_custom), OnDecibelItemClickListener {
    private var _binding: FragmentDecibelCustomBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DecibelCustomAdapter
    private val decibelItems = listOf(
        DecibelItem("20dB", "시계 초침, 나뭇잎 부딪치는 소리", 20),
        DecibelItem("30dB", "심야의 교외, 속삭이는 소리", 30),
        DecibelItem("40dB", "도서관, 조용한 실내", 40),
        DecibelItem("50dB", "조용한 사무실", 50),
        DecibelItem("60dB", "조용한 승용차, 일상적인 대화 소리", 60),
        DecibelItem("70dB", "전화벨, 시끄러운 사무실", 70),
        DecibelItem("80dB", "지하철 내부 소음, 진공 청소기", 80),
        DecibelItem("90dB", "소음이 심한 공장 내부", 90),
        DecibelItem("100dB", "열차 통과시 철도변 소음", 100),
        DecibelItem("110dB", "자동차의 경적 소음", 110),
        DecibelItem("120dB", "전투기 이착륙 소음", 120)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDecibelCustomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 초기화
        adapter = DecibelCustomAdapter(decibelItems, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // 초기값 설정
        binding.decibelSeekbar.progress = DECIBEL_THRESHOLD
        binding.decibelValueText.text = DECIBEL_THRESHOLD.toString() + "dB"

        binding.decibelSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 값을 SoundCheckHelper.DECIBEL_THRESHOLD로 설정
                DECIBEL_THRESHOLD = progress
                // SeekBar의 값이 변경될 때마다 TextView 업데이트
                binding.decibelValueText.text = progress.toString()
                // 앱 메모리에 저장
                saveDecibel(requireContext(), progress)
                // UI 반영
                adapter.notifyDataSetChanged()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveDecibel(context: Context, value: Int){
        val sharedPref = context.getSharedPreferences("decibel_saved_pref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("decibel", value)
        editor.apply()
    }

    override fun onDecibelItemClick(threshold: Int) {
        DECIBEL_THRESHOLD = threshold
        binding.decibelValueText.text = "${threshold}dB"
        saveDecibel(requireContext(), threshold)
        binding.decibelSeekbar.progress = threshold
        adapter.notifyDataSetChanged()
    }

    companion object{
        fun loadDecibel(context: Context) {
            val sharedPref = context.getSharedPreferences("decibel_saved_pref", Context.MODE_PRIVATE)
            DECIBEL_THRESHOLD = sharedPref.getInt("decibel", DECIBEL_THRESHOLD)
        }
    }

}
