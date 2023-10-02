/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.soda.soda.fragments


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioRecord
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.soda.soda.helper.AudioClassificationHelper
import com.soda.soda.R
import com.soda.soda.databinding.FragmentAudioBinding
import com.soda.soda.helper.TextMatchingHelper
import com.soda.soda.service.ForegroundService
import com.soda.soda.ui.ProbabilitiesAdapter
import org.tensorflow.lite.support.label.Category


//stt
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.soda.soda.MainActivity
import com.soda.soda.fragments.SubSettingFragment.Companion.isMyServiceRunning
import kotlin.properties.Delegates

private const val requestCode = 123

private const val TAG = "AudioFragment"

interface AudioClassificationListener {
    fun onError(error: String)
    fun onResult(results: List<Category>)
}


class AudioFragment : Fragment() {
    private var _fragmentBinding: FragmentAudioBinding? = null
    private val fragmentAudioBinding get() = _fragmentBinding!!
    private var recordingDotCount = 0
    private lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        }
    }
    private val recordingHandler = Handler(Looper.getMainLooper())
    private val recordingRunnable = object : Runnable {
        override fun run() {
            recordingDotCount = (recordingDotCount + 1) % 4
            fragmentAudioBinding.recordingText.text = "녹음중" + ".".repeat(recordingDotCount)
            recordingHandler.postDelayed(this, 500)
        }
    }

    private val audioClassificationListener = object : AudioClassificationListener {
        override fun onResult(results: List<Category>) {
            requireActivity().runOnUiThread {
                adapter.categoryList = results
                if(!results.isEmpty())
                    selectLabel(results)
                adapter.notifyDataSetChanged()
            }
        }
        override fun onError(error: String) {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                adapter.categoryList = emptyList()
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun selectLabel(results: List<Category>) {
        val selectedCategory = results.firstOrNull { it.label !in AudioClassificationHelper.excludedLabel }

        if (selectedCategory != null) {
            AudioClassificationHelper.label = TextMatchingHelper.textMatch(selectedCategory)
        } else {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentBinding = FragmentAudioBinding.inflate(inflater, container, false)
        return fragmentAudioBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!ImageSliderFragment.loadGuideState(requireContext()) && ImageSliderFragment.isFirstOpen){
            val imageSliderFragment = ImageSliderFragment()
            imageSliderFragment.show(parentFragmentManager, "userguide_dialog")
        }

        // 메시지 전송 권한을 요청하는 함수 호출
        if (!hasSMSPermission(requireContext())) {
            requestSMSPermission(requireContext())
        }

        fragmentAudioBinding.recyclerView.adapter = adapter

        if(audioHelper == null){
            audioHelper = AudioClassificationHelper(
                requireContext(),
                audioClassificationListener
            )
        }

        /** 임시 채팅 프래그먼트 이동 관련 코드 **/

        val leftButton = view.findViewById<Button>(R.id.left_button)
        leftButton.setOnClickListener {
            val destinationFragment = ChattingFragment()

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(

                    R.anim.slide_in_left, // 역방향 애니메이션 (뒤로 가기 시)
                    R.anim.slide_out_right, // 역방향 애니메이션 (뒤로 가기 시)
                    R.anim.slide_in_right, // 들어올 때 애니메이션
                    R.anim.slide_out_left // 나갈 때 애니메이션

                )
                .replace(R.id.fragment_container, destinationFragment)
                .addToBackStack(null)
                .commit()

            mainActivity?.let { activity ->
                activity.toolbarLayoutBinding.toolbarTitle.text = "STT 채팅"
                activity.toolbarLayoutBinding.toolbarTitle.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        _fragmentBinding = null
        super.onDestroyView()
    }

    /** 메시지 전송 권한을 가지고 있는지 확인하는 함수 **/
    private fun hasSMSPermission(context: Context): Boolean {
        val permission = Manifest.permission.SEND_SMS
        val granted = PackageManager.PERMISSION_GRANTED

        return ContextCompat.checkSelfPermission(context, permission) == granted
    }

    /** 메시지 전송 권한을 요청하는 함수 **/
    private fun requestSMSPermission(context: Context) {
        val permission = Manifest.permission.SEND_SMS
        val granted = PackageManager.PERMISSION_GRANTED

        if (ContextCompat.checkSelfPermission(context, permission) != granted) {
            requestPermissions(arrayOf(permission), requestCode)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionsFragment.hasAudioPermission(requireContext())) {

            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                .navigate(AudioFragmentDirections.actionAudioToPermissions())
        }
        else{
            startRecording()
        }
    }

    override fun onPause() {
        super.onPause()
        if(!isMyServiceRunning(requireContext(), ForegroundService::class.java)){
            stopRecording()
        }
    }

    companion object {
        private var isListening = false
        private var audioHelper: AudioClassificationHelper? = null
        private val adapter by lazy { ProbabilitiesAdapter() }

        // Getter 메서드 추가
        fun getAdapterInstance(): ProbabilitiesAdapter {
            return adapter
        }

        //setter 추가
        fun setListening(value: Boolean) {
            isListening = value
        }

        fun startRecording() {
            if(audioHelper?.getRecorderState() != AudioRecord.RECORDSTATE_RECORDING){
                audioHelper?.startAudioClassification()
                Log.d(TAG, "녹음 재개")
            }
        }
        fun stopRecording() {
            if(audioHelper?.getRecorderState() != AudioRecord.RECORDSTATE_STOPPED){
                audioHelper?.stopAudioClassification()
                adapter.categoryList = emptyList()
                adapter.notifyDataSetChanged()
                Log.d(TAG, "녹음 중단")
            }
        }

        fun getAudioHelper(): AudioClassificationHelper? {
            return audioHelper
        }

        fun setAudioHelper(){
            audioHelper = null
        }

        fun isListening(): Boolean {
            return isListening
        }
    }
}