package org.tensorflow.lite.examples.audio

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.tensorflow.lite.examples.audio.databinding.ActivityMainBinding
import org.tensorflow.lite.examples.audio.databinding.ToolbarLayoutBinding
import org.tensorflow.lite.examples.audio.fragments.AudioFragment
import org.tensorflow.lite.examples.audio.fragments.SettingFragment
import org.tensorflow.lite.examples.audio.fragments.WarningFragment
import org.tensorflow.lite.examples.audio.helper.SoundCheckHelper
import org.tensorflow.lite.examples.audio.service.ForegroundService

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var toolbarLayoutBinding: ToolbarLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Get the toolbar layout view
        val toolbarLayoutView = activityMainBinding.root.findViewById<View>(R.id.toolbar_layout)
        // Bind the toolbar layout view
        toolbarLayoutBinding = ToolbarLayoutBinding.bind(toolbarLayoutView)
        setSupportActionBar(toolbarLayoutBinding.toolbar)

        toolbarLayoutBinding.buttonSetting.setOnClickListener {
            if(!AudioFragment.isListening){ // stt 녹음중 아닐 때만
                navigateToFragment(SettingFragment())
            }
            else{
                Toast.makeText(this, "먼저 녹음을 완료해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        toolbarLayoutBinding.buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()

        // 'show_dialog' 키의 값을 가져옴 (기본값은 false)
        val showDialog = intent.getBooleanExtra("show_dialog", false)

        Log.d(TAG, "showDialog: $showDialog")
        // 알림을 클릭하여 MainActivity를 실행했을 때 대화상자를 띄우도록 합니다.
        // 값이 'true'이면 대화상자를 표시
        if (showDialog) {
            clickViewEvents(this)
        }
    }

    // intent를 갱신하기 위해 필요
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // 새로운 Intent를 액티비티의 Intent로 설정
        this.intent = intent
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        // 현재 프래그먼트가 SettingFragment인지 확인
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is SettingFragment) {
            // SettingFragment에서 뒤로 가기를 눌렀을 때 setting button을 다시 보이게 함
            toolbarLayoutBinding.buttonSetting.visibility = View.VISIBLE
            // TextView를 다시 안보이게 함
            toolbarLayoutBinding.toolbarTitle.visibility = View.GONE
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }


    private fun navigateToFragment(fragment: SettingFragment) {
        // Setting button을 gone으로 설정
        toolbarLayoutBinding.buttonSetting.visibility = View.GONE
        // TextView를 보이게 설정
        toolbarLayoutBinding.toolbarTitle.visibility = View.VISIBLE

        //현재 프래그먼트가 SettingFragment가 아닌 경우에만 백스택에 추가
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)  // 백스택에 추가
            .commit()
    }

    /** 대화상자 생성 **/
    private fun clickViewEvents(activity: MainActivity) {
        val dialog = WarningFragment(SoundCheckHelper.warningLabel)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(activity.supportFragmentManager, "WarningDialog")
        // 대화상자를 보여준 후에는 "show_dialog" 값을 다시 false로 설정
        intent.putExtra("show_dialog", false)
    }

}
