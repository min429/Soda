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
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import org.tensorflow.lite.examples.audio.helper.AudioClassificationHelper
import org.tensorflow.lite.examples.audio.R
import org.tensorflow.lite.examples.audio.databinding.FragmentAudioBinding
import org.tensorflow.lite.examples.audio.helper.TextMatchingHelper
import org.tensorflow.lite.examples.audio.service.ForegroundService
import org.tensorflow.lite.examples.audio.ui.ProbabilitiesAdapter
import org.tensorflow.lite.support.label.Category
import java.io.File
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale
import android.net.Uri
import android.os.Environment

//stt
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission

// AudioClassificationListener 인터페이스
interface AudioClassificationListener {
    fun onError(error: String)  // 오류 발생 시 호출
    fun onResult(results: List<Category>, inferenceTime: Long)  // 결과가 도착하면 호출
}

class AudioFragment : Fragment() {
    // 뷰 바인딩을 위한 변수와 어댑터 초기화
    private var _fragmentBinding: FragmentAudioBinding? = null
    private val fragmentAudioBinding get() = _fragmentBinding!!
    private val adapter by lazy { ProbabilitiesAdapter() }

    // 스위치 & 버튼용
    private var isSwitchOn = true
    //private var isRecording = false //- 녹음 버튼 통합으로 코드 통일

    //stt 용
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isListening = false // 음성 인식 중 여부를 추적하는 플래그 변수
    private var duplication_check = false // 음성인식 시작 중복 처리 플래그 변수
    val recognizedText = mutableListOf<String>() // 수정: recognizedText 변수를 리스트로 선언하고 초기화
    //private var recognizedText: String = "" // 음성 인식 결과를 저장하는 변수
    //

    private lateinit var audioHelper: AudioClassificationHelper// AudioClassificationHelper 객체 선언

    // AudioClassificationListener 객체 생성 및 오버라이딩 메서드 구현
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
        val switch = fragmentAudioBinding.switchButton
        val recordButton = view.findViewById<Button>(R.id.record_button)

        val stttext = fragmentAudioBinding.sttText
        //
        val sttButton=view.findViewById<Button>(R.id.stt_button)

        //스위치 클릭
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Switch가 On인 경우
                Log.d("AudioFragment", "Switch is ON")
                isSwitchOn = true
                startRecording() //-> 녹음 재개
                recordButton.text = "녹음 시작" // 녹음 중지 시 버튼 텍스트 변경
            } else {
                // Switch가 Off인 경우
                Log.d("AudioFragment", "Switch is OFF")
                // ProbabilitiesAdapter에서 categoryList 객체를 빈 리스트로 초기화 함
                adapter.categoryList = emptyList()
                adapter.notifyDataSetChanged()
                isSwitchOn = false
                audioHelper.stopAudioClassification() //-> 녹음 중단
            }
        }

        // 녹음 버튼 클릭------------------------------------------------------------------------
        recordButton.setOnClickListener {
            Log.d("AudioFragment", "스위치 여부: ${isSwitchOn}")
            if (!isSwitchOn) { // 녹음 스위치 off 상태에서만 작동

                if (!isListening) { // 음성 인식 중이 아닌 경우에만 음성인식 시작
                    isListening = true
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
                    //intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000); // 녹음 기한 5초
                    //intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000); // 녹음 기한 5분

                    speechRecognizer.startListening(intent)
                    recordButton.text = "녹음 중지" // stt녹음 시작 시 버튼 텍스트 변경

                    Log.d("isListening", isListening.toString())
                    Log.d("stt", "stt 음성인식 시작")


                } else { // 음성 인식 중이면 음성인식 종료
                    isListening = false
                    speechRecognizer.cancel()
                    recordButton.text = "녹음 시작" // stt녹음 중지 시 버튼 텍스트 변경
                    val text = recognizedText.joinToString(separator = " ")
                    stttext.setText(text)
                    recognizedText.clear() // 다음 인식을 위해 비우기

                    Log.d("isListening", isListening.toString())
                    Log.d("stt", "stt 음성인식 중단")

                    duplication_check=false
                }

                  // 녹음 버튼 통합으로 임시 주석 처리
//                if (isRecording) {
//                    stopRecording()
//                    isRecording = false
//                    recordButton.text = "녹음 시작" // 녹음 중지 시 버튼 텍스트 변경
//                } else {
//                    startRecording()
//                    isRecording = true
//                    recordButton.text = "녹음 중지" // 녹음 시작 시 버튼 텍스트 변경
//                }


            }
            else{
                Toast.makeText(recordButton.context, "수동녹음을 하려면 자동녹음 스위치를 off로 해주세요", Toast.LENGTH_SHORT).show()
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

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                // 음성 인식 오류 처리
                // Toast.makeText(requireContext(), "음성 인식 오류 발생", Toast.LENGTH_SHORT).show() // 수정: applicationContext 대신 requireContext() 사용
            }

            override fun onResults(results: Bundle?) {

                Log.d("STTonResults", " stt 처리 쏴주기")
                // 인식된 결과 처리
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    recognizedText.add(matches[0])
                    //recognizedText = matches[0] // 그냥 텍스트 넘기기
                    //val recognizedText = matches[0]
                    // resultTextView.text = recognizedText // 수정: resultTextView가 정의되어 있지 않으므로 해당 코드 주석 처리
                    //stttext.setText(recognizedText)// 수정: stttext를 사용하여 인식된 텍스트를 표시
                }
                if(isListening) {
                    Log.d("STTonResults", " stt 처리 쏴주기 다시재시작")
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
//                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000); // 녹음 기한 5초
//                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000); // 녹음 기한 5분

                    speechRecognizer.startListening(intent)

                }

            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

//        // stt 버튼 클릭  - 녹음 버튼 통합으로 주석처리
//        sttButton.setOnClickListener {
//            if (!isListening) { // 음성 인식 중이 아닌 경우에만 음성인식 시작
//                isListening = true
//                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
//                //intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000); // 녹음 기한 5초
//                //intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000); // 녹음 기한 5분
//
//                speechRecognizer.startListening(intent)
//                sttButton.text = "stt 녹음중지" // stt녹음 시작 시 버튼 텍스트 변경
//
//                Log.d("isListening", isListening.toString())
//                Log.d("stt", "stt 음성인식 시작")
//
//            } else { // 음성 인식 중이면 음성인식 종료
//                isListening = false
//                speechRecognizer.cancel()
//                sttButton.text = "stt 녹음시작" // stt녹음 중지 시 버튼 텍스트 변경
//                val text = recognizedText.joinToString(separator = " ")
//                stttext.setText(text)
//                recognizedText.clear() // 다음 인식을 위해 비우기
//
//                Log.d("isListening", isListening.toString())
//                Log.d("stt", "stt 음성인식 중단")
//            }
//        }

        // stt 텍스트표기될 위치

        stttext.setText("stt표기될 부분")
        //----------------------------------------------------------------------------------

    }

// 녹음 버튼 통합으로 임시 주석 처리-------------------------------------------------------------------------------------
//
//
//    private var recorder: MediaRecorder? = null
//    private var outputFile: File? = null
//
//
//    private fun startRecording() {
//        //기본 분류 작동
//        audioHelper.startAudioClassification()
//
//        //별개 녹음 작동
//        try {
//            // 녹음 파일 생성
//            val fileName = "${System.currentTimeMillis()}.3gp"
//            // 녹음 파일의 이름을 생성합니다. 현재 시간을 기준으로 파일명을 지정하고 확장자는 ".3gp"를 사용합니다.
//
//            val filePath = "${requireActivity().externalCacheDir?.absolutePath}/$fileName"
//            // 녹음 파일이 저장될 경로를 생성합니다. 외부 캐시 디렉토리에 파일을 저장하기 위함입니다.
//
//            outputFile = File(filePath)
//            // 생성된 파일 경로를 이용해 File 객체를 생성합니다.
//            MediaRecorder().apply { //MediaRecorder 객체를 생성하고, 녹음을 시작합니다
//                setAudioSource(MediaRecorder.AudioSource.MIC) // 마이크 사용
//                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//                setOutputFile(outputFile?.absolutePath)
//                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//                prepare()
//                start() // 녹음 시작
//            }
//            isRecording = true
//            Log.d("AudioFragment", "녹음 시작")
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("AudioFragment", "녹음 시작 실패")
//        }
//    }
//
//    private fun stopRecording() {
//        //기본분류 작동
//        audioHelper.stopAudioClassification()
//
//        //별개 녹음 작동
//        try {
//            if (isRecording && outputFile != null) {
//                recorder?.apply {
//                    stop() // 녹음 중지
//                    release() // 녹음 리소스 해제
//                }
//                addRecordingToMediaLibrary() // 녹음된 파일을 미디어 라이브러리에 추가
//                isRecording = false
//                Log.d("AudioFragment", "녹음 중지")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("AudioFragment", "녹음 중지실패")
//        }
//
//
//
//    }
//
//    // 미디어 라이브러리에 녹음된 파일을 추가
//    private fun addRecordingToMediaLibrary() {
//        // 녹음된 파일을 미디어 라이브러리에 추가
//        MediaScannerConnection.scanFile(
//            requireContext(),
//            arrayOf(outputFile?.absolutePath),
//            arrayOf("audio/*"),
//            null
//        )
//    }
//-------------------------------------------------------------------------------------

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                .navigate(AudioFragmentDirections.actionAudioToPermissions())
        }
        audioHelper?.startAudioClassification()
        Log.d("AudioFragment", "녹음 실행중")
    }

    override fun onPause() {
        super.onPause()
        if (::audioHelper.isInitialized ) {
            audioHelper.stopAudioClassification()
        }
        Log.e("AudioFragment", "녹음 중단중")
    }

    override fun onDestroyView() {
        _fragmentBinding = null
        super.onDestroyView()
        speechRecognizer.destroy()
    }

    companion object {
        var audioHelper: AudioClassificationHelper? = null

        fun startRecording() {
            audioHelper?.startAudioClassification()
        }

        fun stopRecording() {
            audioHelper?.stopAudioClassification()
        }
    }


}
