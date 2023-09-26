package com.soda.soda.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.soda.soda.R
import com.soda.soda.databinding.FragmentSurrondCustomBinding
import com.soda.soda.helper.AudioClassificationHelper

private const val TAG = "SurroundCustomFragment"

class SurroundCustomFragment : Fragment() {

    private var _binding: FragmentSurrondCustomBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSurrondCustomBinding.inflate(inflater, container, false)
        setupRadioGroup()
        return binding.root
    }

    private fun setupRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.crowded_button -> AudioClassificationHelper.excludedLabel = listOf("Silence", "Speech")
                R.id.not_crowded_button -> AudioClassificationHelper.excludedLabel = listOf("Silence")
            }
            Log.d(TAG, "setupRadioGroup: ${AudioClassificationHelper.excludedLabel}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지를 위해 null로 설정
    }

}
