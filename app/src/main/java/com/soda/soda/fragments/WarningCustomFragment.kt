package com.soda.soda.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.soda.soda.R
import com.soda.soda.databinding.FragmentWarningCustomBinding
import com.soda.soda.ui.Item
import com.soda.soda.ui.WarningCustomAdapter

interface OnItemClickedListener {
    fun onItemClicked(item: Item)
}

class WarningCustomFragment : Fragment(R.layout.fragment_warning_custom), OnItemClickedListener {

    private var _binding: FragmentWarningCustomBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WarningCustomAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWarningCustomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WarningCustomAdapter(warningSoundsList, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(item: Item){
        saveData(requireContext(), item)
        adapter.updateList(warningSoundsList)
    }

    companion object {
        private var warningSoundsList = mutableListOf(
            Item("자동차 경적 소리", true),
            Item("트럭 경적 소리", true),
            Item("기차 경적 소리", true),
            Item("경보 소리", true),
            Item("화재 경보 소리", true),
            Item("자동차 도난 경보 소리", true),
            Item("사이렌 소리", true),
            Item("구급차 사이렌 소리", true),
            Item("소방차 사이렌 소리", true),
            Item("경찰차 사이렌 소리", true),
            Item("민방위 사이렌 소리", true),
            Item("비명 소리", true),
            Item("어린 아이 비명 소리", true),
            Item("울부짖는 소리", true),
            Item("고함 소리", true),
            Item("쾅 소리", true),
            Item("폭발 소리", true),
            Item("포격 소리", true),
            Item("부서지는 소리", true),
            Item("깨지는 소리", true),
            Item("폭발 소리", true),
            Item("부딪치는 소리", true),
        )
        var warningSounds: HashMap<String, String> = HashMap()

        private fun isListSaved(context: Context): Boolean{
            val sharedPref = context.getSharedPreferences("list_saved_pref", Context.MODE_PRIVATE)
            return sharedPref.getBoolean("list_saved", false)
        }

        private fun setListSaved(context: Context){
            val sharedPref = context.getSharedPreferences("list_saved_pref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("list_saved", true)
            editor.apply()
        }

        private fun saveData(context: Context, item: Item) {
            val sharedPref = context.getSharedPreferences("saved_warning_sounds", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            val gson = Gson()

            // Check if the list already contains an item with the same label
            val itemIdx = warningSoundsList.indexOfFirst { it.label == item.label }
            if (itemIdx != -1) {
                // Replace the item in the list
                warningSoundsList[itemIdx] = item
            }

            // warningSounds 갱신
            if(!item.isChecked)
                warningSounds.remove(item.label)
            else
                warningSounds[item.label] = item.label + " 같습니다."

            // Convert list to JSON and save
            editor.putString("item_list", gson.toJson(warningSoundsList))
            editor.apply()
        }

        private fun saveData(context: Context, list: MutableList<Item>) {
            val sharedPref = context.getSharedPreferences("saved_warning_sounds", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            val gson = Gson()

            // Convert list to JSON and save
            editor.putString("item_list", gson.toJson(list))
            editor.apply()

            // 위험 소리 설정 완료
            setListSaved(context)
        }

        fun loadData(context: Context) {
            // 아직 위험 소리 설정을 안한 경우 메모리에 데이터가 없으므로 default값 넣기
            if(!isListSaved(context)) {
                saveData(context, warningSoundsList)
            }
            val sharedPref = context.getSharedPreferences("saved_warning_sounds", Context.MODE_PRIVATE)
            val gson = Gson()

            // warningSoundsList 초기화
            val storedData = sharedPref.getString("item_list", "[]")
            warningSoundsList = gson.fromJson(storedData, object : TypeToken<MutableList<Item>>() {}.type)

            // warningSounds 초기화
            for(item in warningSoundsList){
                if(item.isChecked)
                    warningSounds[item.label] = item.label + " 같습니다."
            }
        }
    }

}
