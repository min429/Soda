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

package com.soda.soda.fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.soda.soda.R
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.soda.soda.MainActivity

private val PERMISSIONS_REQUIRED = arrayOf(
    Manifest.permission.RECORD_AUDIO, Manifest.permission.POST_NOTIFICATIONS)


class PermissionsFragment : Fragment() {
    private lateinit var mainActivity: MainActivity


    /** 권한 요청 런처 **/
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // 허용되지 않은 권한에 RECORD_AUDIO가 없거나 RECORD_AUDIO가 허용된 경우
            if (!permissions.contains(Manifest.permission.RECORD_AUDIO) || permissions[Manifest.permission.RECORD_AUDIO] == true) {
                navigateToAudioFragment()
            }
            else{
                mainActivity.showAuthorizationDialog(requireContext(), "소리 인식")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity


        // 다른앱 위애 표기 부분 권한 허용 함수 코드 작성
        if (!hasOverlayPermission(requireContext())) {
            top_of_otherDialog(requireContext(), "위험 알림")
        }

        checkPermissionAndRequest()
    }

    /** 다른 앱 위에 표시 권한 체크 **/
    private fun hasOverlayPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // 마시멜로우 이전 버전에서는 항상 허용되어 있다고 간주
            return true
        }
        return Settings.canDrawOverlays(context)
    }




    /** 권한 대화상자 **/
    fun top_of_otherDialog(context: Context, feature: String) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.authorization_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val layoutParams = dialog.window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.gravity = Gravity.BOTTOM // 화면의 밑에 위치하도록 설정

        // 100px 만큼 margin 추가
        val marginInPx = 200
        layoutParams?.y = marginInPx

        // TextView 설정
        val textView = dialog.findViewById<TextView>(R.id.confirm_textView)
        textView?.text = feature+"을 위해 다른앱 위에 표시권한을 허용해 주세요."
        if(feature == "소리 인식")
            textView?.text = feature+"을 위해 다른앱 위에 표시권한을 허용해 주세요."

        dialog.findViewById<Button>(R.id.yes_button)?.setOnClickListener {
            dialog.dismiss()
            // 다른 앱 위에 표시되는 권한 설정 화면으로 이동
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.packageName))
            context.startActivity(intent)
        }

        dialog.findViewById<Button>(R.id.no_button)?.setOnClickListener {
            dialog.dismiss()
            // 다이얼로그를 닫을 때 추가적인 작업을 수행하고 싶다면 여기에 추가합니다.
        }

        dialog.show()
    }

    /** 권한 체크 및 요청 **/
    private fun checkPermissionAndRequest() {
        // 허용된 권한 확인
        val permissionsNotGranted = PERMISSIONS_REQUIRED.filter {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
        when {
            // 모두 허용 -> AudioFragment로 이동
            permissionsNotGranted.isEmpty() -> {
                navigateToAudioFragment()
            }
            // 그렇지 않다면 허용되지 않은 권한만 요청
            else -> {
                requestPermissionLauncher.launch(permissionsNotGranted.toTypedArray())
            }
        }
    }


    private fun navigateToAudioFragment() {
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                PermissionsFragmentDirections.actionPermissionsToAudio()
            )
        }
    }

    companion object {
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        fun hasAudioPermission(context: Context) : Boolean{
            return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        }

        fun hasNotificationsPermission(context: Context) : Boolean{
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }
    }

}


