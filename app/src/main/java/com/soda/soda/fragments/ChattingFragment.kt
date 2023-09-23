import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.soda.soda.fragments.SettingFragment
import com.soda.soda.fragments.SttFragment
import com.soda.soda.service.ForegroundService
import com.soda.soda.databinding.FragmentAudioBinding



/** stt 용 **/
private const val TAG = "ChattingFragment"
private lateinit var speechRecognizer: SpeechRecognizer
private var duplication_check = false
private var result_check = false
private var record_cancel = false
private val resultText = mutableListOf<String>()

class ChattingFragment : Fragment() {
    private var recordingDotCount = 0
    private val recordingHandler = Handler(Looper.getMainLooper())
    private val recordingRunnable = object : Runnable {
        override fun run() {
            recordingDotCount = (recordingDotCount + 1) % 4
            binding.recordingText.text = "녹음중" + ".".repeat(recordingDotCount)
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
        val autoSwitchStateValue = SettingFragment.autoSwitchState


        /** STT 녹음 버튼 클릭 이벤트 **/
        recordButton.setOnClickListener {
            if(!result_check){
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
                    result_check=true //터치 제한
                    Log.d(TAG, "stt 음성인식 시작")

                    // 5초 타임아웃 처리
                    recordingHandler.postDelayed({
                        if(!isEndOfSpeech && !record_cancel){
                            Log.d(TAG, "stt 소리X 5초 지남")
                            speechRecognizer.stopListening()
                            binding.recordingText.visibility = View.GONE
                            stopRecordingAnimation()
                            recordButton.setBackgroundResource(R.drawable.record_start)
                            val text = resultText.joinToString(separator = " ")
                            resultText.clear()
                            if(autoSwitchStateValue){
                                AudioFragment.startRecording()
                            }
                            result_check=false
                            AudioFragment.setListening(false)
                            duplication_check= false
                            Toast.makeText(requireContext(), "5초 이상 말소리가 감지되지 않음", Toast.LENGTH_SHORT).show()
                        }
                    }, RECORDING_TIMEOUT.toLong())

                    record_cancel = false
                }
                // 음성 인식 중일때 음성인식 종료시퀀스 -> 채팅에 추가
                else {
                    Log.d(TAG, "stt 음성인식 중단")
                    speechRecognizer.stopListening()
                    binding.recordingText.visibility = View.GONE
                    stopRecordingAnimation()
                    recordButton.setBackgroundResource(R.drawable.record_start)
                    val text = resultText.joinToString(separator = " ")
                    if (text.isNotEmpty()) {
                        addChatMessage(ChatHelper.ChatMessage(text, false)) // STT 결과를 채팅에 추가
                        Log.d(TAG, "중복체크")
                    }
                    resultText.clear()
                    AudioFragment.setListening(false)
                    duplication_check = false

                    //전송하기 취소
                    //navigateToFragment(SttFragment(),text)
                }
            }

            // 녹음 취소 버튼 누를 때
            else{
                Log.d(TAG, "stt 인식 취소")
                speechRecognizer.stopListening()
                binding.recordingText.visibility = View.GONE
                stopRecordingAnimation()
                recordButton.setBackgroundResource(R.drawable.record_start)
                val text = resultText.joinToString(separator = " ")
                resultText.clear()
                if(autoSwitchStateValue){
                    AudioFragment.startRecording()
                    Log.d(TAG, "스위치 on 일때 자동녹음 레코딩 시작")
                }
                result_check=false
                AudioFragment.setListening(false)
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
                    binding.recordingText.text = "녹음완료"

                    // 인식된 결과 처리
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches != null && matches.isNotEmpty()) {
                        resultText.add(matches[0])
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
            SettingFragment.isMyServiceRunning(requireContext(), ForegroundService::class.java)
        var serviceIntent = Intent(requireActivity(), ForegroundService::class.java)
        if(SettingFragment.backgroundSwitchState && !isRunning){
            ContextCompat.startForegroundService(requireActivity(), serviceIntent)
        }


    //on view created
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
