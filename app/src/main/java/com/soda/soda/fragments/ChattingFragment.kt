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

class ChattingFragment : Fragment() {

    private val chatMessageList = mutableListOf<String>()
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
                chatAdapter.addMessage(message)
                chatInput.text.clear() // Editable 대신 clear()를 사용하여 텍스트를 지우세요.
            }
        }

    }
}
