import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soda.soda.R

class ChatHelper(private val chatMessages: MutableList<ChatHelper.ChatMessage>) : RecyclerView.Adapter<ChatHelper.ViewHolder>() {

    data class ChatMessage(
        val message: String,
        val isUserMessage: Boolean
    )

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        holder.messageText.text = chatMessage.message

        // isUserMessage 속성에 따라 메시지의 위치와 배경을 설정
        if (chatMessage.isUserMessage) {
            // 사용자가 보낸 메시지인 경우
            holder.messageText.setBackgroundResource(R.drawable.bubble_outgoing)
            // 메시지를 오른쪽으로 정렬
            holder.itemView.findViewById<LinearLayout>(R.id.message_layout).gravity = Gravity.END // 사용자 메시지인 경우 오른쪽 정렬
        } else {
            // 상대방이 보낸 메시지인 경우
            holder.messageText.setBackgroundResource(R.drawable.bubble_incoming)
            // 메시지를 왼쪽으로 정렬
            holder.itemView.findViewById<LinearLayout>(R.id.message_layout).gravity = Gravity.START // 상대방 메시지인 경우 왼쪽 정렬
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    fun addMessage(chatMessage: ChatMessage) {
        chatMessages.add(chatMessage)
        notifyDataSetChanged()
    }
}
