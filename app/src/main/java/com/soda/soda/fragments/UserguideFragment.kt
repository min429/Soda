package org.tensorflow.lite.examples.audio.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.soda.soda.R

class UserguideFragment :  DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //레이아웃 배경 투명화
        return inflater.inflate(R.layout.fragment_userguide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //텍스트라인
        val guidelineTextView: TextView = view.findViewById(R.id.guideline)
        guidelineTextView.text = "메인 기능 소개------\n\n" +
                "1. 위험감지 : \n\n앱이 켜져 있을 때 위험한 소리(폭발, 경적 등 다수)를 감지 했을 시 앱에서 위험 알림을 보내줍니다.\n\n"+
                "2. 음성분류 : \n\n앱 메인화면에서 들리는 소리를 계속 분석하여 현재 들리는 가장 큰 소리에 대해 어떤 소리인지 알려줍니다. \n\n" +
                "3. stt 기능 : \n\n앱 메인화면에서 녹음버튼을 누르면 녹음을 시작합니다.\n말을 해서음성이 인식되면 녹음버튼이 플레이 버튼으로 바뀌고 플레이버튼을 누르면 텍스트로 바뀐 음성의 내용을 확인할 수 있습니다.\n\n\n"+
                "설정창 소개------\n\n" +
                "1. 백그라운드 : \n\n설정에 백그라운드 스위치를 ON 하면 홈 화면으로 나가도 위험감지 및 음성분류가 작동합니다.\nOFF시 백그라운드에서는 동작하지않고 앱화면을 켜두웠을 때만 작동합니다.\n\n" +
                "2. 자동분류 : \n\n스위치를 ON 하면 2번기능인 음성분류를 계속 실행하고 OFF 하면 2번기능 음성분류를 중단합니다. \n\n"+
                "3. 진동알림 : \n\n스위치를 ON 하면 1번기능인 위험상황 알림시 진동을 발생시키고, OFF 하면 진동을 발생시키지 않습니다."
    }

}