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

package org.tensorflow.lite.examples.audio.fragments

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.tensorflow.lite.examples.audio.helper.AudioClassificationHelper
import org.tensorflow.lite.examples.audio.R
import org.tensorflow.lite.examples.audio.databinding.FragmentAudioBinding
import org.tensorflow.lite.examples.audio.helper.TextMatchingHelper
import org.tensorflow.lite.examples.audio.service.ForegroundService
import org.tensorflow.lite.examples.audio.ui.ProbabilitiesAdapter
import org.tensorflow.lite.support.label.Category


//stt
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.os.Handler
import android.os.Looper
import androidx.navigation.Navigation
import org.tensorflow.lite.examples.audio.fragments.SettingFragment.Companion.isMyServiceRunning


private const val TAG = "AudioFragment"

interface AudioClassificationListener {
    fun onError(error: String)  // 오류 발생 시 호출
    fun onResult(results: List<Category>, inferenceTime: Long)  // 결과가 도착하면 호출
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

    // 스위치 & 버튼용

    // stt 용
    private lateinit var speechRecognizer: SpeechRecognizer
    private var duplication_check = false // 음성인식 시작 중복 처리 플래그 변수
    private var result_check = false // 음성인식 결과 처리 플래그 변수
    val resultText = mutableListOf<String>() // 결과 stt 표기용

    private val audioClassificationListener = object : AudioClassificationListener {
        // 결과가 도착하면 호출되며, 전달된 결과 및 추론 시간 정보를 기반으로 어댑터를 업데이트합니다.
        override fun onResult(results: List<Category>, inferenceTime: Long) {
            requireActivity().runOnUiThread {
                adapter.categoryList = results
                if(!results.isEmpty())
                    ForegroundService.label = TextMatchingHelper.textMatch(results[0])
                adapter.notifyDataSetChanged()
            }
        }
        // 오류 발생 시 호출되며 Toast 메시지 출력 후 어댑터를 초기화하여 화면에 표시되는 확률값을 모두 0으로 만듭니다.
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
        // 뷰 바인딩 초기화
        _fragmentBinding = FragmentAudioBinding.inflate(inflater, container, false)
        return fragmentAudioBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 어댑터 설정
        fragmentAudioBinding.recyclerView.adapter = adapter

        // AudioClassificationHelper 객체 생성 및 초기화
        audioHelper = AudioClassificationHelper(
            requireContext(),
            audioClassificationListener
        )

        // 스위치 + STT 녹음 버튼------------------------------------------------------------------------
        val recordButton = view.findViewById<Button>(R.id.record_button)
        val RECORDING_TIMEOUT = 5000 // 녹음 타임아웃 시간 (5초)
        var isEndOfSpeech  = false //인식된 소리가 없을 때 처리 플래그 변수




        // 녹음 버튼 클릭-----------------------------------------------------------------------
        recordButton.setOnClickListener {
            if(!result_check){
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
                        if(!isEndOfSpeech ){
                            Log.e(TAG, "stt 소리X 5초 지남")
                            isListening = false
                            duplication_check= false
                            speechRecognizer.stopListening()
                            fragmentAudioBinding.recordingText.visibility = View.GONE
                            stopRecordingAnimation()
                            recordButton.setBackgroundResource(R.drawable.record_start) // 녹음 중지 시 이미지 변경
                            val text = resultText.joinToString(separator = " ")
                            resultText.clear()// 다음 stt인식을 위해 비우기
                            startRecording()
                            result_check=false
                            Toast.makeText(requireContext(), "5초 이상 말소리가 감지되지 않음", Toast.LENGTH_SHORT).show() // 수정: applicationContext 대신 requireContext() 사용
                        }
                    }, RECORDING_TIMEOUT.toLong())
                }
                else { // 음성 인식 중이면 음성인식 종료
                    Log.d(TAG, "stt 음성인식 중단")
                    isListening = false
                    duplication_check= false
                    speechRecognizer.stopListening()
                    fragmentAudioBinding.recordingText.visibility = View.GONE
                    stopRecordingAnimation()
                    recordButton.setBackgroundResource(R.drawable.record_start) // 녹음 중지 시 이미지 변경
                    val text = resultText.joinToString(separator = " ")
                    resultText.clear()// 다음 stt인식을 위해 비우기
                    //startRecording() //-> 녹음 재개 - 주석 처리해야 stt 중지되는 부분 처리할 수 있음
                    navigateToFragment(SttFragment(),text)//stt 처리 끝났을 시 SttFragment로 교체
                }

            }

        }

        // SpeechRecognizer 초기화 ------------------------------------------------------------------------
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext()) // 수정: applicationContext 대신 requireContext() 사용
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // 음성 인식 준비 완료
                if (!duplication_check) {
                    Toast.makeText(requireContext(), "음성 인식 시작", Toast.LENGTH_SHORT).show() // 수정: applicationContext 대신 requireContext() 사용
                    duplication_check = true
                }
                else{
                    Toast.makeText(requireContext(), "음성 인식 중간 이어서~", Toast.LENGTH_SHORT).show() // 수정: applicationContext 대신 requireContext() 사용
                }
            }

            override fun onBeginningOfSpeech() {} //동작 시작시 무조건 호출

            override fun onRmsChanged(rmsdB: Float) {} // 계속 호출되는 함수

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() { //동작 종료시 무조건 호출
                Log.d(TAG, "stt onEndOfSpeech ")


            }

            override fun onError(error: Int) {
                //Toast.makeText(requireContext(), "음성 인식 오류 발생", Toast.LENGTH_SHORT).show() // 수정: applicationContext 대신 requireContext() 사용
            }

            override fun onResults(results: Bundle?) {
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

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
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
                audioHelper?.startAudioClassification()
                Log.d("AudioFragment", "녹음 재개")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(!isMyServiceRunning(requireContext(), ForegroundService::class.java)){
            audioHelper?.stopAudioClassification()
            Log.d("AudioFragment", "녹음 중단")
        }
    }

    companion object {
        var isListening = false // 음성 인식 중 여부를 추적하는 플래그 변수
        var audioHelper: AudioClassificationHelper? = null
        private val adapter by lazy { ProbabilitiesAdapter() }
        fun startRecording() {
            audioHelper?.startAudioClassification()
            Log.d(TAG, "startRecording")
        }
        fun stopRecording() {
            adapter.categoryList = emptyList()
            adapter.notifyDataSetChanged()
            audioHelper?.stopAudioClassification()
            Log.d(TAG, "stopRecording")
        }
    }

}