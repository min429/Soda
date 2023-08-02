package com.soda.soda

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment
import com.soda.soda.databinding.ActivityMainBinding
import com.soda.soda.databinding.ToolbarLayoutBinding
import com.soda.soda.fragments.AudioFragment
import com.soda.soda.fragments.PermissionsFragment
import com.soda.soda.fragments.SettingFragment
import com.soda.soda.fragments.WarningFragment
import com.soda.soda.helper.SoundCheckHelper
import com.soda.soda.service.ForegroundService

private const val TAG = "MainActivity"

interface DialogInterface{
    fun dialogEvents()
    fun showAuthorizationDialog(context: Context, feature: String)
}

class MainActivity : AppCompatActivity(), DialogInterface{
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var toolbarLayoutBinding: ToolbarLayoutBinding
    private var currentDialog: DialogFragment? = null
    private var isPaused = false
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // 앱 권한 설정 화면에서 돌아왔을 때 수행
        this.recreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            if(!AudioFragment.isListening()){ // stt 녹음중 아닐 때만
                navigateToFragment(SettingFragment())
            }
            else{
                Toast.makeText(this, "먼저 녹음을 완료해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        toolbarLayoutBinding.buttonBack.setOnClickListener {
            onBackPressed()
        }

        // ActivityResult API를 사용하기 위한 초기화
        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // 결과 처리. 이 경우 단순히 설정 화면에서 돌아왔을 때 수행됩니다.

        }
    }

    override fun onResume() {
        super.onResume()
        isPaused = false

        // 'show_dialog' 키의 값을 가져옴 (기본값은 false)
        val showDialog = intent.getBooleanExtra("show_dialog", false)

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
        // 포그라운드 서비스를 종료합니다.
        stopService(Intent(this, ForegroundService::class.java))
        // 녹음 및 스레드 작업 종료
        AudioFragment.getAudioHelper()?.stopAudioClassification()

        // audioHelper = null로 초기화 -> 앱을 다시 실행할 때마다 audioHelper를 다시 생성해야 함
        // 앱을 다시 실행할 때마다 MainActivity가 다시 생성되므로
        // AudioFragment에서 다시 requireContext()로 context를 갱신한 후 다시 audioHelper객체를 생성하면서 context를 넘겨줘야 함
        AudioFragment.setAudioHelper()
    }

    override fun onPause() {
        super.onPause()
        isPaused = true
    }

    override fun onBackPressed() {
        // 현재 프래그먼트가 SettingFragment인지 확인
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if(currentFragment is NavHostFragment){ // AudioFragment, PermissionsFragment = NavHostFragment
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
            val currentNavHostFragment = navHostFragment.childFragmentManager.fragments[0]
            // currentNavHostFragment = AudioFragment or PermissionsFragment
            if (currentNavHostFragment is PermissionsFragment) {
                // PermissionsFragment에서 뒤로 가기를 눌렀을 때 앱을 종료함
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition()
                } else {
                    finish()
                }
                return
            }
        }
        else if (currentFragment is SettingFragment) { // SettingFragment != NavHostFragment
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

    /** 권한 대화상자 **/
    override fun showAuthorizationDialog(context: Context, feature: String) {
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
        textView?.text = feature+"을 위해 권한을 허용해 주세요."

        dialog.findViewById<Button>(R.id.yes_button)?.setOnClickListener {
            dialog.dismiss()
            // 핸드폰 설정 화면으로 이동
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.packageName))
            startForResult.launch(intent)
        }

        dialog.findViewById<Button>(R.id.no_button)?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
