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


import android.media.MediaRecorder

import android.util.Log
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import org.tensorflow.lite.examples.audio.AudioClassificationHelper
import org.tensorflow.lite.examples.audio.R
import org.tensorflow.lite.examples.audio.databinding.FragmentAudioBinding
import org.tensorflow.lite.examples.audio.ui.ProbabilitiesAdapter
import org.tensorflow.lite.support.label.Category




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
    private var isRecording = false


    private lateinit var audioHelper: AudioClassificationHelper// AudioClassificationHelper 객체 선언
    // AudioClassificationListener 객체 생성 및 오버라이딩 메서드 구현
    private val audioClassificationListener = object : AudioClassificationListener {
        // 결과가 도착하면 호출되며, 전달된 결과 및 추론 시간 정보를 기반으로 어댑터를 업데이트합니다.
        override fun onResult(results: List<Category>, inferenceTime: Long) {
            requireActivity().runOnUiThread {
                adapter.categoryList = results
                adapter.notifyDataSetChanged()
                fragmentAudioBinding.bottomSheetLayout.inferenceTimeVal.text =
                    String.format("%d ms", inferenceTime)

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


    //private var isSwitchOn = false

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


        // 스위치 + 녹음 버튼------------------------------------------------------------------------
        val switch = fragmentAudioBinding.switchButton
        val recordButton = view.findViewById<Button>(R.id.record_button)
        val sttButton=view.findViewById<Button>(R.id.stt_button)

        //스위치 클릭
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Switch가 On인 경우
                Log.d("AudioFragment", "Switch is ON")
                isSwitchOn = true
                onResume() //-> 녹음 재개
                recordButton.text = "녹음 시작" // 녹음 중지 시 버튼 텍스트 변경

            } else {
                // Switch가 Off인 경우
                Log.d("AudioFragment", "Switch is OFF")
                // ProbabilitiesAdapter에서 categoryList 객체를 빈 리스트로 초기화 함
                adapter.categoryList = emptyList()
                adapter.notifyDataSetChanged()
                isSwitchOn = false
                onPause()// -> 녹음중단



                //audioHelper.stopAudioClassification()
            }
        }

        // 녹음 버튼 클릭
        recordButton.setOnClickListener {
            Log.e("스위치 여부", "${isSwitchOn}")
            if (!isSwitchOn) { // 녹음 스위치 off 상태에서만 작동
                if (isRecording) {
                    stopRecording()
                    isRecording = false
                    recordButton.text = "녹음 시작" // 녹음 중지 시 버튼 텍스트 변경
                } else {
                    startRecording()
                    isRecording = true
                    recordButton.text = "녹음 중지" // 녹음 시작 시 버튼 텍스트 변경
                }
            }
            else{
                Toast.makeText(recordButton.context, "수동녹음을 하려면 자동녹음 스위치를 off로 해주세요", Toast.LENGTH_SHORT).show()
            }

        }
        // stt button
        sttButton.setOnClickListener {



        }


        // stt 텍스트표기될 위치
        val stt_text = fragmentAudioBinding.sttText
        stt_text.setText("stt표기될 부분")
        //----------------------------------------------------------------------------------


        // Allow the user to select between multiple supported audio models.
        // The original location and documentation for these models is listed in
        // the `download_model.gradle` file within this sample. You can also create your own
        // audio model by following the documentation here:
        // https://www.tensorflow.org/lite/models/modify/model_maker/speech_recognition

        // 라디오 그룹 클릭 리스너 설정
        fragmentAudioBinding.bottomSheetLayout.modelSelector.setOnCheckedChangeListener(
            object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                // 모델 선택에 따라 AudioClassificationHelper의 멤버 변수 설정 변경
                when (checkedId) {
                    R.id.yamnet -> {
                        audioHelper.stopAudioClassification()
                        audioHelper.currentModel = AudioClassificationHelper.YAMNET_MODEL
                        audioHelper.initClassifier()
                    }
                    R.id.speech_command -> {
                        audioHelper.stopAudioClassification()
                        audioHelper.currentModel = AudioClassificationHelper.SPEECH_COMMAND_MODEL
                        audioHelper.initClassifier()
                    }
                }
            }
        })

        // Allow the user to change the amount of overlap used in classification. More overlap
        // can lead to more accurate resolves in classification.
        // 스피너 선택 리스너 설정
        fragmentAudioBinding.bottomSheetLayout.spinnerOverlap.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                  view: View?,
                  position: Int,
                  id: Long
                ) {
                    // Overlap 설정 변경
                    audioHelper.stopAudioClassification()
                    audioHelper.overlap = 0.25f * position
                    audioHelper.startAudioClassification(requireContext())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // no op
                }
            }

        // Allow the user to change the max number of results returned by the audio classifier.
        // Currently allows between 1 and 5 results, but can be edited here.
        // 결과 및 확률값 수정 버튼 클릭 리스너 설정
        fragmentAudioBinding.bottomSheetLayout.resultsMinus.setOnClickListener {
            if (audioHelper.numOfResults > 1) {
                audioHelper.numOfResults--
                audioHelper.stopAudioClassification()
                audioHelper.initClassifier()
                fragmentAudioBinding.bottomSheetLayout.resultsValue.text =
                    audioHelper.numOfResults.toString()
            }
        }

        fragmentAudioBinding.bottomSheetLayout.resultsPlus.setOnClickListener {
            if (audioHelper.numOfResults < 5) {
                audioHelper.numOfResults++
                audioHelper.stopAudioClassification()
                audioHelper.initClassifier()
                fragmentAudioBinding.bottomSheetLayout.resultsValue.text =
                    audioHelper.numOfResults.toString()
            }
        }

        // Allow the user to change the confidence threshold required for the classifier to return
        // a result. Increments in steps of 10%.

        // Confidence threshold 수정 버튼 클릭 리스너 설정
        fragmentAudioBinding.bottomSheetLayout.thresholdMinus.setOnClickListener {
            if (audioHelper.classificationThreshold >= 0.2) {
                audioHelper.stopAudioClassification()
                audioHelper.classificationThreshold -= 0.1f
                audioHelper.initClassifier()
                fragmentAudioBinding.bottomSheetLayout.thresholdValue.text =
                    String.format("%.2f", audioHelper.classificationThreshold)
            }
        }


        // Thread 개수 수정 버튼 클릭 리스너 설정
        fragmentAudioBinding.bottomSheetLayout.thresholdPlus.setOnClickListener {
            if (audioHelper.classificationThreshold <= 0.8) {
                audioHelper.stopAudioClassification()
                audioHelper.classificationThreshold += 0.1f
                audioHelper.initClassifier()
                fragmentAudioBinding.bottomSheetLayout.thresholdValue.text =
                    String.format("%.2f", audioHelper.classificationThreshold)
            }
        }

        // Allow the user to change the number of threads used for classification
        fragmentAudioBinding.bottomSheetLayout.threadsMinus.setOnClickListener {
            if (audioHelper.numThreads > 1) {
                audioHelper.stopAudioClassification()
                audioHelper.numThreads--
                fragmentAudioBinding.bottomSheetLayout.threadsValue.text = audioHelper
                    .numThreads
                    .toString()
                audioHelper.initClassifier()
            }
        }

        fragmentAudioBinding.bottomSheetLayout.threadsPlus.setOnClickListener {
            if (audioHelper.numThreads < 4) {
                audioHelper.stopAudioClassification()
                audioHelper.numThreads++
                fragmentAudioBinding.bottomSheetLayout.threadsValue.text = audioHelper
                    .numThreads
                    .toString()
                audioHelper.initClassifier()
            }
        }

        // When clicked, change the underlying hardware used for inference. Current options are CPU
        // and NNAPI. GPU is another available option, but when using this option you will need
        // to initialize the classifier on the thread that does the classifying. This requires a
        // different app structure than is used in this sample.

        // Delegate 선택 리스너 설정
        fragmentAudioBinding.bottomSheetLayout.spinnerDelegate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                  parent: AdapterView<*>?,
                  view: View?,
                  position: Int,
                  id: Long
                ) {
                    audioHelper.stopAudioClassification()
                    audioHelper.currentDelegate = position
                    audioHelper.initClassifier()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    /* no op */
                }
            }

        fragmentAudioBinding.bottomSheetLayout.spinnerOverlap.setSelection(
            2,
            false
        )
        fragmentAudioBinding.bottomSheetLayout.spinnerDelegate.setSelection(
            0,
            false
        )
    }

    private fun startRecording() {
        audioHelper.startAudioClassification()
    }

    private fun stopRecording() {
        audioHelper.stopAudioClassification()
    }





    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if ( !PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                .navigate(AudioFragmentDirections.actionAudioToPermissions())
        }

        if (::audioHelper.isInitialized ) {
            audioHelper.startAudioClassification(requireContext())
        }

        Log.e("온리슘실행", "녹음 실행중")
    }

    override fun onPause() {
        super.onPause()
        if (::audioHelper.isInitialized ) {
            audioHelper.stopAudioClassification()
        }
        Log.e("온퍼즈실행", "녹음 중단중")
    }

    override fun onDestroyView() {
        _fragmentBinding = null
        super.onDestroyView()
    }

}
