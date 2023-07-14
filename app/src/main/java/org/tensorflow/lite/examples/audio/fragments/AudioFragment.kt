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


import android.util.Log
import android.os.Bundle
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
                startRecording() //-> 녹음 재개
                recordButton.text = "녹음 시작" // 녹음 중지 시 버튼 텍스트 변경
            } else {
                // Switch가 Off인 경우
                Log.d("AudioFragment", "Switch is OFF")
                // ProbabilitiesAdapter에서 categoryList 객체를 빈 리스트로 초기화 함
                adapter.categoryList = emptyList()
                adapter.notifyDataSetChanged()
                isSwitchOn = false
                stopRecording()// -> 녹음중단
            }
        }

        // 녹음 버튼 클릭
        recordButton.setOnClickListener {
            Log.d("AudioFragment", "스위치 여부: ${isSwitchOn}")
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

    }

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

    override fun onDestroyView() {
        _fragmentBinding = null
        super.onDestroyView()
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
