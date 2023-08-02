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
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.soda.soda.R
import android.util.Log
import com.soda.soda.MainActivity

private val PERMISSIONS_REQUIRED = arrayOf(
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.POST_NOTIFICATIONS,
    Manifest.permission.VIBRATE,
    Manifest.permission.FOREGROUND_SERVICE,
    Manifest.permission.INTERNET)

/**
 * The sole purpose of this fragment is to request permissions and, once granted, display the
 * audio fragment to the user.
 */

class PermissionsFragment : Fragment() {
    private lateinit var mainActivity: MainActivity

    /** 권한 요청 런처 **/
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.RECORD_AUDIO] == true && permissions[Manifest.permission.POST_NOTIFICATIONS] == true) {
                navigateToAudioFragment()
            }
            else{
                mainActivity.showAuthorizationDialog(requireContext(), "소리 인식, 위험 알림")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity

        checkPermissionAndRequest()
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
        /** Convenience method used to check if all permissions required by this app are granted */
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


