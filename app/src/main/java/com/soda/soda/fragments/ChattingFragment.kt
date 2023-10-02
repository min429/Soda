package com.soda.soda.fragments

import ChatHelper
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.soda.soda.R
import com.soda.soda.databinding.FragmentChattingBinding
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.soda.soda.fragments.AudioFragment
import com.soda.soda.fragments.AudioFragment.Companion.setListening
import com.soda.soda.fragments.SttFragment
import com.soda.soda.service.ForegroundService
import com.soda.soda.fragments.SubSettingFragment

/** stt 용 **/
private const val TAG = "ChattingFragment"
private lateinit var speechRecognizer: SpeechRecognizer
private var record_cancel = false
private val resultText = mutableListOf<String>()

class ChattingFragment : Fragment() {
    private var recordingDotCount = 0
    private val recordingHandler = Handler(Looper.getMainLooper())
    private val recordingRunnable = object : Runnable {
        override fun run() {
            recordingDotCount = (recordingDotCount + 1) % 4
            binding.recordingText.text = "STT 인식중" + ".".repeat(recordingDotCount)
            recordingHandler.postDelayed(this, 500)
        }
    }

    private val chatMessageList = mutableListOf<ChatHelper.ChatMessage>()
    private lateinit var chatAdapter: ChatHelper
    private lateinit var binding: FragmentChattingBinding // 바인딩 객체 추가


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChattingBinding.inflate(inflater, container, false) // 바인딩 초기화
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatAdapter = ChatHelper(chatMessageList)



        val recyclerView = binding.chatMessageList // 바인딩으로 RecyclerView 참조
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = chatAdapter


        val sendButton = binding.sendButton // 바인딩으로 Button 참조
        val chatInput = binding.chatInput // 바인딩으로 TextView 참조



        sendButton.setOnClickListener {
            val message = chatInput.text.toString()
            if (message.isNotEmpty()) {
                val chatMessage = ChatHelper.ChatMessage(message, true)
                addChatMessage(chatMessage) // ChatHelper.ChatMessage 객체를 전달
                chatInput.text.clear()
            }
        }



        /** stt 기능 카피 **/
        val recordButton = view.findViewById<Button>(R.id.record_button)
        val RECORDING_TIMEOUT = 5000 //인식된 소리가 없을 때 녹음 지속 시간 (5초)
        var isEndOfSpeech  = false   //인식된 소리가 없을 때 처리 플래그 변수
        val autoSwitchStateValue = SubSettingFragment.autoSwitchState


        if(autoSwitchStateValue){
            AudioFragment.startRecording()
            Log.d(TAG, "스위치 on 일때 자동녹음 레코딩 시작")
        }


        /** STT 녹음 버튼 클릭 이벤트 **/
        recordButton.setOnClickListener {
                // STT 음성 인식 중이 아닌 경우에만 음성인식 시작
                if (!AudioFragment.Companion.isListening()) {
                    //분류 중단
                    AudioFragment.getAdapterInstance().categoryList = emptyList()
                    AudioFragment.getAdapterInstance().notifyDataSetChanged()
                    AudioFragment.stopRecording()

                    // STT녹음시작
                    AudioFragment.setListening(true)
                    startSTT()
                    binding.recordingText.visibility = View.VISIBLE
                    startRecordingAnimation()
                    recordButton.setBackgroundResource(R.drawable.record_stop)
                    Log.d(TAG, "stt 음성인식 시작")
                    record_cancel = false
                }

                // 음성 인식 중일때 음성인식 종료시퀀스 -> 채팅에 추가
                else {
                    Log.d(TAG, "stt 음성인식 중단")
                    speechRecognizer.stopListening()
                    binding.recordingText.visibility = View.GONE
                    stopRecordingAnimation()
                    recordButton.setBackgroundResource(R.drawable.record_start)

                    AudioFragment.setListening(false)

                    if(autoSwitchStateValue){
                        AudioFragment.startRecording()
                        Log.d(TAG, "스위치 on 일때 자동녹음 레코딩 시작")
                    }

                    record_cancel =true
                    recordingHandler.removeCallbacksAndMessages(null)

                }
        }




        /** SpeechRecognizer 초기화  **/
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.e(TAG, "STT 음성 인식 오류 발생. 오류 코드:endofspeech")
            }

            override fun onError(error: Int) {
                // STT 음성 인식 중에 오류 발생 시 자동으로 다시 시작
                Log.d(TAG, "STT 음성 인식 오류 발생. 오류 코드: $error")

                if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                    // 오류 코드가 ERROR_NO_MATCH일 때 (소리를 듣지 못한 경우)
                    if (!record_cancel) { // 녹음을 취소하지 않았을 때만 다시 시작
                        Log.d(TAG, "STT 녹음 다시 시작")
                        startSTT()
                        binding.recordingText.visibility = View.VISIBLE
                    }
                } else {
                    // 다른 오류 코드에 대한 처리 추가 (선택 사항)
                }
            }


            override fun onResults(results: Bundle?) {
                //녹음을 취소하지 않았을 때
                if (!record_cancel){
                    Log.d(TAG, "stt 처리 완료")
                    isEndOfSpeech = true

                    // 인식된 결과 처리
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches != null && matches.isNotEmpty()) {
                        val text = matches[0] // 인식된 텍스트 가져오기
                        resultText.add(text)

                        // 음성 인식 결과를 바로 메시지로 추가
                        addChatMessage(ChatHelper.ChatMessage(text, false))
                    }

                    // 추가 녹음시 stt 처리 재시작
                    if(AudioFragment.Companion.isListening()) {
                        Log.d(TAG, "stt 처리 재시작")
                        startSTT()
                        binding.recordingText.visibility = View.VISIBLE
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val isRunning =
            SubSettingFragment.isMyServiceRunning(requireContext(), ForegroundService::class.java)
        var serviceIntent = Intent(requireActivity(), ForegroundService::class.java)
        if(SubSettingFragment.backgroundSwitchState && !isRunning){
            ContextCompat.startForegroundService(requireActivity(), serviceIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setListening(false)
        speechRecognizer.destroy()
    }

    private fun startRecordingAnimation() {
        recordingHandler.post(recordingRunnable)
    }

    private fun stopRecordingAnimation() {
        recordingHandler.removeCallbacks(recordingRunnable)
    }

    private fun startSTT() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
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

    private fun addChatMessage(chatMessage: ChatHelper.ChatMessage) {
        chatAdapter.addMessage(chatMessage)
        // RecyclerView에서 마지막 아이템 위치로 스크롤
        val lastItemPosition = chatAdapter.itemCount - 1
        if (lastItemPosition >= 0) {
            binding.chatMessageList.scrollToPosition(lastItemPosition)
        }
    }
}
