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

import ChattingFragment
import android.content.Intent
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
import com.soda.soda.fragments.SettingFragment.Companion.isMyServiceRunning
import kotlin.properties.Delegates

private const val TAG = "AudioFragment"

interface AudioClassificationListener {
    fun onError(error: String)
    fun onResult(results: List<Category>)
}

class AudioFragment : Fragment() {
    private var _fragmentBinding: FragmentAudioBinding? = null
    private val fragmentAudioBinding get() = _fragmentBinding!!
    private var recordingDotCount = 0
    private val recordingHandler = Handler(Looper.getMainLooper())
    private val recordingRunnable = object : Runnable {
        override fun run() {
            recordingDotCount = (recordingDotCount + 1) % 4
            fragmentAudioBinding.recordingText.text = "녹음중" + ".".repeat(recordingDotCount)
            recordingHandler.postDelayed(this, 500)
        }
    }

    /** STT 기능 관련 변수  **/
    private lateinit var speechRecognizer: SpeechRecognizer
    private var duplication_check = false // 음성인식 시작 중복 처리 플래그 변수
    private var result_check = false // 음성인식 결과 처리 플래그 변수
    private var record_cancel = false // 음성인식 취소 처리 플래그 변수
    val resultText = mutableListOf<String>() // 결과 stt 표기용

    private val audioClassificationListener = object : AudioClassificationListener {
        override fun onResult(results: List<Category>) {
            requireActivity().runOnUiThread {
                adapter.categoryList = results
                if(!results.isEmpty())
                    AudioClassificationHelper.label = TextMatchingHelper.textMatch(results[0])
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
            // 이동할 프래그먼트인 ChattingFragment를 생성합니다.
            val destinationFragment = ChattingFragment()

            // ChattingFragment로 이동합니다.
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, destinationFragment)
                .addToBackStack(null) // 백스택에 추가하여 이전 프래그먼트로 돌아갈 수 있도록 합니다.
                .commit()
        }


        /** STT 기능 관련 변수 (onViewCreated 내부) **/
        val recordButton = view.findViewById<Button>(R.id.record_button)
        val RECORDING_TIMEOUT = 5000 //인식된 소리가 없을 때 녹음 지속 시간 (5초)
        var isEndOfSpeech  = false   //인식된 소리가 없을 때 처리 플래그 변수
        val autoSwitchStateValue = SettingFragment.autoSwitchState


        /** STT 녹음 버튼 클릭 이벤트 **/
        recordButton.setOnClickListener {
            if(!result_check){
                // STT 음성 인식 중이 아닌 경우에만 음성인식 시작
                if (!isListening) {
                    //분류 중단
                    adapter.categoryList = emptyList()
                    adapter.notifyDataSetChanged()
                    stopRecording()

                    // STT녹음시작
                    isListening = true
                    startSTT()
                    fragmentAudioBinding.recordingText.visibility = View.VISIBLE
                    startRecordingAnimation()
                    recordButton.setBackgroundResource(R.drawable.record_stop)
                    result_check=true //터치 제한
                    Log.d(TAG, "stt 음성인식 시작")

                    // 5초 타임아웃 처리
                    recordingHandler.postDelayed({
                        if(!isEndOfSpeech && !record_cancel){
                            Log.e(TAG, "stt 소리X 5초 지남")
                            speechRecognizer.stopListening()
                            fragmentAudioBinding.recordingText.visibility = View.GONE
                            stopRecordingAnimation()
                            recordButton.setBackgroundResource(R.drawable.record_start)
                            val text = resultText.joinToString(separator = " ")
                            resultText.clear()
                            if(autoSwitchStateValue){
                                startRecording()
                            }
                            result_check=false
                            isListening = false
                            duplication_check= false
                            Toast.makeText(requireContext(), "5초 이상 말소리가 감지되지 않음", Toast.LENGTH_SHORT).show()
                        }
                    }, RECORDING_TIMEOUT.toLong())

                    record_cancel = false
                }
                // 음성 인식 중일때 음성인식 종료시퀀스 -> 프래그먼트 이동
                else {
                    Log.d(TAG, "stt 음성인식 중단")
                    speechRecognizer.stopListening()
                    fragmentAudioBinding.recordingText.visibility = View.GONE
                    stopRecordingAnimation()
                    recordButton.setBackgroundResource(R.drawable.record_start)
                    val text = resultText.joinToString(separator = " ")
                    resultText.clear()
                    isListening = false
                    duplication_check= false

                    //stt 처리후 SttFragment로 교체
                    navigateToFragment(SttFragment(),text)
                }
            }

            // 녹음 취소 버튼 누를 때
            else{
                Log.e(TAG, "stt 인식 취소")
                speechRecognizer.stopListening()
                fragmentAudioBinding.recordingText.visibility = View.GONE
                stopRecordingAnimation()
                recordButton.setBackgroundResource(R.drawable.record_start)
                val text = resultText.joinToString(separator = " ")
                resultText.clear()
                if(autoSwitchStateValue){
                    startRecording()
                    Log.d(TAG, "스위치 on 일때 자동녹음 레코딩 시작")
                }
                result_check=false
                isListening = false
                duplication_check= false
                record_cancel =true
                recordingHandler.removeCallbacksAndMessages(null)
            }
            Log.d(TAG, "취소 여부 확인 확인 = "+ record_cancel)
        }


        /** SpeechRecognizer 초기화  **/
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) { // stt 음성 인식 준비 완료시
                if (!duplication_check) {
                    //Toast.makeText(requireContext(), "stt음성 인식 시작", Toast.LENGTH_SHORT).show()
                    duplication_check = true
                }
                else{
                    //Toast.makeText(requireContext(), "stt음성 인식 중간 이어서~", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {}

            override fun onResults(results: Bundle?) {
                //녹음을 취소하지 않았을 때
                if (!record_cancel){
                    Log.d(TAG, "stt 처리 완료")
                    isEndOfSpeech = true
                    result_check=false
                    recordButton.setBackgroundResource(R.drawable.play_button)
                    stopRecordingAnimation()
                    fragmentAudioBinding.recordingText.text = "녹음완료"

                    // 인식된 결과 처리
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches != null && matches.isNotEmpty()) {
                        resultText.add(matches[0])
                    }

                    // 추가 녹음시 stt 처리 재시작
                    if(isListening) {
                        Log.d(TAG, " stt 처리 재시작")
                        startSTT()
                        fragmentAudioBinding.recordingText.visibility = View.VISIBLE
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val isRunning = isMyServiceRunning(requireContext(), ForegroundService::class.java)
        var serviceIntent = Intent(requireActivity(), ForegroundService::class.java)
        if(SettingFragment.backgroundSwitchState && !isRunning){
            ContextCompat.startForegroundService(requireActivity(), serviceIntent)
        }
    }

    override fun onDestroyView() {
        _fragmentBinding = null
        super.onDestroyView()
        speechRecognizer.destroy()
    }

    private fun startRecordingAnimation() {
        recordingHandler.post(recordingRunnable)
    }

    private fun stopRecordingAnimation() {
        recordingHandler.removeCallbacks(recordingRunnable)
    }


    /** STT 음성 인식 시작 함수 **/
    private fun startSTT() {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        //intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10000); // 녹음 완전 종료 후 연장 기한 10초
        //intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000); // 녹음 중간 공백 연장 기한 5초
        speechRecognizer.startListening(intent)
    }

    /** STT 프래그먼트 교체 함수 **/
    private fun navigateToFragment(fragment: SttFragment, text: String) {
        val bundle = Bundle()
        bundle.putString("text", text)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
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