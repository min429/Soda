package com.soda.soda.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.soda.soda.R

class SttFragment : Fragment() {
    private var Rtext: String? = null

    companion object {
        private const val ARG_TEXT = "text"

        fun newInstance(text: String): SttFragment {
            val fragment = SttFragment()
            val args = Bundle()
            args.putString(ARG_TEXT, text)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stt, container, false)
        Rtext = arguments?.getString(ARG_TEXT)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** stt 텍스트 가져오기 **/
        val sttResultTextView = view.findViewById<TextView>(R.id.stt_result)
        Rtext?.let {
            sttResultTextView.text = it
        }

        /** 중앙 X= 뒤로가기 **/
        val xButton = view.findViewById<Button>(R.id.x_button)
        xButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

}
