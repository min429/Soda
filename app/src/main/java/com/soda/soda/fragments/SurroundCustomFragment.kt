package com.soda.soda.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.soda.soda.R
import com.soda.soda.databinding.FragmentSurrondCustomBinding
import com.soda.soda.helper.AudioClassificationHelper
import com.soda.soda.helper.DECIBEL_THRESHOLD

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val surroundState = isSurroundSaved(requireContext())
        if(surroundState != null){
            if(surroundState == "default"){
                binding.defaultButton.isChecked = true
            }
            else if(surroundState == "crowded"){
                binding.crowdedButton.isChecked = true
            }
        }
    }

    private fun setupRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.crowded_button -> {
                    AudioClassificationHelper.excludedLabel = listOf("Silence", "Speech")
                    setSurroundSaved(requireContext(), "crowded")
                }
                R.id.default_button -> {
                    AudioClassificationHelper.excludedLabel = listOf("Silence")
                    setSurroundSaved(requireContext(), "default")
                }
            }
            Log.d(TAG, "setupRadioGroup: ${AudioClassificationHelper.excludedLabel}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지를 위해 null로 설정
    }

    private fun isSurroundSaved(context: Context): String? {
        val sharedPref = context.getSharedPreferences("surround_saved_pref", Context.MODE_PRIVATE)
        return sharedPref.getString("surround", null)
    }

    private fun setSurroundSaved(context: Context, value: String){
        val sharedPref = context.getSharedPreferences("surround_saved_pref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("surround", value)
        editor.apply()
    }

}
