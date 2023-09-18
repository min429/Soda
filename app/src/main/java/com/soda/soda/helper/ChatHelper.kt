import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soda.soda.R

class ChatHelper(private val chatMessages: MutableList<String>) : RecyclerView.Adapter<ChatHelper.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.messageText.text = chatMessages[position]
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    fun addMessage(message: String) {
        chatMessages.add(message)
        notifyDataSetChanged()
    }
}
