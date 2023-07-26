package org.tensorflow.lite.examples.audio.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.soda.soda.R




class UserguideFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_userguide, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //텍스트라인
        val guidelineTextView: TextView = view.findViewById(R.id.guideline)
        guidelineTextView.text = "메인 기능 소개------\n" +
                "\n" +
                "1. 백그라운드\n" +
                "2. 음성분류\n" +
                "3. stt 기능\n" +
                "\n" +
                "설정창 소개------\n" +
                "\n" +
                "-백그라운드\n" +
                "-자동분류\n" +
                "-진동알림"
    }

    companion object {}
}