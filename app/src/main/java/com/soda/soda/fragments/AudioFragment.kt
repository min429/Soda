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
    // 뷰 바인딩을 위한 변수와 어댑터 초기화
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

    // stt 용
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

        // AudioClassificationHelper 객체 생성 및 초기화
        if(audioHelper == null){
            audioHelper = AudioClassificationHelper(
                requireContext(),
                audioClassificationListener
            )
        }

        // 스위치 + STT 녹음 버튼------------------------------------------------------------------------
        val recordButton = view.findViewById<Button>(R.id.record_button)
        val RECORDING_TIMEOUT = 5000 // 녹음 타임아웃 시간 (5초)
        var isEndOfSpeech  = false //인식된 소리가 없을 때 처리 플래그 변수
        val autoSwitchStateValue = SettingFragment.autoSwitchState


        // 녹음 버튼 클릭-----------------------------------------------------------------------
        recordButton.setOnClickListener {
            if(!result_check){ // 결과 처리중일 때 -> 아직 결과 안나온 동안
                if (!isListening) { // 음성 인식 중이 아닌 경우에만 음성인식 시작
                    //분류 중단
                    adapter.categoryList = emptyList()
                    adapter.notifyDataSetChanged()
                    stopRecording() // -> 녹음중단

                    // 녹음시작
                    isListening = true
                    startSTT()
                    fragmentAudioBinding.recordingText.visibility = View.VISIBLE
                    startRecordingAnimation()
                    recordButton.setBackgroundResource(R.drawable.record_stop) // 녹음 시작 시 이미지 변경
                    result_check=true//터치 막기
                    Log.d(TAG, "stt 음성인식 시작")

                    // 5초 타임아웃 처리
                    recordingHandler.postDelayed({
                        if(!isEndOfSpeech && !record_cancel){
                            Log.e(TAG, "stt 소리X 5초 지남")
                            speechRecognizer.stopListening()
                            fragmentAudioBinding.recordingText.visibility = View.GONE
                            stopRecordingAnimation()
                            recordButton.setBackgroundResource(R.drawable.record_start) // 녹음 중지 시 이미지 변경
                            val text = resultText.joinToString(separator = " ")
                            resultText.clear()// 다음 stt인식을 위해 비우기
                            if(autoSwitchStateValue){
                                startRecording()
                                Log.d(TAG, "스위치 on 일때 자동녹음 레코딩 시작")
                            }
                            result_check=false
                            isListening = false
                            duplication_check= false
                            Toast.makeText(requireContext(), "5초 이상 말소리가 감지되지 않음", Toast.LENGTH_SHORT).show() // 수정: applicationContext 대신 requireContext() 사용

                        }
                    }, RECORDING_TIMEOUT.toLong())

                    record_cancel = false
                }
                else { // 음성 인식 중이면 음성인식 종료시퀀스 -> 프래그먼트 이동

                    Log.d(TAG, "stt 음성인식 중단")
                    speechRecognizer.stopListening()
                    fragmentAudioBinding.recordingText.visibility = View.GONE
                    stopRecordingAnimation()
                    recordButton.setBackgroundResource(R.drawable.record_start) // 녹음 중지 시 이미지 변경
                    val text = resultText.joinToString(separator = " ")
                    resultText.clear() // 다음 stt인식을 위해 비우기
                    //startRecording() //-> 녹음 재개 - 주석 처리해야 stt 중지되는 부분 처리할 수 있음
                    isListening = false
                    duplication_check= false
                    navigateToFragment(SttFragment(),text)//stt 처리 끝났을 시 SttFragment로 교체
                }
            }
            else{ // 강제로 취소 버튼 누를 때
                Log.e(TAG, "stt 인식 취소")
                speechRecognizer.stopListening()
                fragmentAudioBinding.recordingText.visibility = View.GONE
                stopRecordingAnimation()
                recordButton.setBackgroundResource(R.drawable.record_start) // 녹음 중지 시 이미지 변경
                val text = resultText.joinToString(separator = " ")
                resultText.clear()// 다음 stt인식을 위해 비우기
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

        // SpeechRecognizer 초기화 ------------------------------------------------------------------------
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext()) // 수정: applicationContext 대신 requireContext() 사용
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // 음성 인식 준비 완료
                if (!duplication_check) {
//                    Toast.makeText(requireContext(), "음성 인식 시작", Toast.LENGTH_SHORT).show() // 수정: applicationContext 대신 requireContext() 사용
                    duplication_check = true
                }
                else{
//                    Toast.makeText(requireContext(), "음성 인식 중간 이어서~", Toast.LENGTH_SHORT).show() // 수정: applicationContext 대신 requireContext() 사용
                }
            }

            override fun onBeginningOfSpeech() {} //동작 시작시 무조건 호출

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() { //동작 종료시 무조건 호출
                Log.d(TAG, "stt onEndOfSpeech ")
            }

            override fun onError(error: Int) {
                //Toast.makeText(requireContext(), "음성 인식 오류 발생", Toast.LENGTH_SHORT).show() // 수정: applicationContext 대신 requireContext() 사용
            }

            override fun onResults(results: Bundle?) {
                if (!record_cancel){ //인식 취소 안됬을 때만 아래 코드 실행되도록
                    Log.d(TAG, "stt 처리 완료")

                    // 녹음완료 표시
                    isEndOfSpeech = true
                    result_check=false
                    //결과 출력 버튼으로 변경
                    recordButton.setBackgroundResource(R.drawable.play_button) // 녹음 중지 시 이미지 변경
                    stopRecordingAnimation()
                    fragmentAudioBinding.recordingText.text = "녹음완료"

                    // 인식된 결과 처리
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches != null && matches.isNotEmpty()) {
                        resultText.add(matches[0])
                    }

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

    // STT 음성 인식을 시작하는 startSTT 함수
    private fun startSTT() {
        // 음성 인식 시작
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        //intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10000); // 녹음 완전 종료 후 연장 기한 10초
        //intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000); // 녹음 중간 공백 연장 기한 5초
        speechRecognizer.startListening(intent)
    }

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
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                .navigate(AudioFragmentDirections.actionAudioToPermissions())
        }
        else{
            if(SettingFragment.autoSwitchState){
                startRecording()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(!isMyServiceRunning(requireContext(), ForegroundService::class.java)){
            stopRecording()
        }
    }

    companion object {
        private var isListening = false // 음성 인식 중 여부를 추적하는 플래그 변수
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

        fun getAudioHelper(): AudioClassificationHelper {
            return audioHelper!!
        }

        fun setAudioHelper(){
            audioHelper = null
        }

        fun isListening(): Boolean {
            return isListening
        }
    }
}