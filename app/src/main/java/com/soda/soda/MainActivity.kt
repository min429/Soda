package com.soda.soda

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.soda.soda.databinding.ActivityMainBinding
import com.soda.soda.databinding.ToolbarLayoutBinding
import com.soda.soda.fragments.AudioFragment
import com.soda.soda.fragments.SettingFragment
import com.soda.soda.fragments.WarningFragment
import com.soda.soda.helper.SoundCheckHelper

private const val TAG = "MainActivity"

interface DialogInterface{
    fun dialogEvents()
}

class MainActivity : AppCompatActivity(), DialogInterface{
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var toolbarLayoutBinding: ToolbarLayoutBinding
    private var currentDialog: DialogFragment? = null
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // 백그라운드 스위치 상태 설정
        setBackgroundSwitchState(this)

        // 인터페이스 설정
        SoundCheckHelper.setInterface(this)

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
        isPaused = false

        // 'show_dialog' 키의 값을 가져옴 (기본값은 false)
        val showDialog = intent.getBooleanExtra("show_dialog", false)

        Log.d(TAG, "showDialog: $showDialog")
        // 알림을 클릭하여 MainActivity를 실행했을 때 대화상자를 띄우도록 합니다.
        // 값이 'true'이면 대화상자를 표시
        if (showDialog) {
            dialogEvents()
        }
    }

    // intent를 갱신하기 위해 필요
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // 새로운 Intent를 액티비티의 Intent로 설정
        this.intent = intent
    }

    override fun onDestroy() {
        super.onDestroy()

        // 액티비티가 종료될 때 Object에 설정된 인터페이스 제거
        SoundCheckHelper.setInterface(null)
    }

    override fun onPause() {
        super.onPause()
        isPaused = true
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
    override fun dialogEvents() {
        // 백그라운드에서 실행된 경우 팝업을 띄우지 않음
        if(isPaused) return

        // 이전에 띄워진 다이얼로그가 있으면 닫기
        currentDialog?.dismiss()

        val dialog = WarningFragment(SoundCheckHelper.warningLabel)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this.supportFragmentManager, "WarningDialog")
        // 대화상자를 보여준 후에는 "show_dialog" 값을 다시 false로 설정
        intent.putExtra("show_dialog", false)

        // 현재 띄워진 다이얼로그를 기억하여 나중에 닫기 위해 변수에 저장
        currentDialog = dialog
    }

    /** 백그라운드 스위치 상태 설정 **/
    private fun setBackgroundSwitchState(activity: MainActivity){
        // Restore switch state from SharedPreferences
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        //getString(R.string.saved_switch_state_key)를 통해 strings.xml 파일에 정의된 키 값을 가져오고 sharedPref.getBoolean은 이 키 값에 해당하는 값이
        //SharedPreferences에 저장되어 있으면 그 값을 반환하고 없으면 false를 반환함
        SettingFragment.backgroundSwitchState = sharedPref.getBoolean(getString(R.string.saved_background_switch_state_key), false)
    }

}
