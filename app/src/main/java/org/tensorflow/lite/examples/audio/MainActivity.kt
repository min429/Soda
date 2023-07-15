package org.tensorflow.lite.examples.audio

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.tensorflow.lite.examples.audio.databinding.ActivityMainBinding
import org.tensorflow.lite.examples.audio.databinding.ToolbarLayoutBinding
import org.tensorflow.lite.examples.audio.fragments.SettingFragment
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
            navigateToFragment(SettingFragment())
        }

        toolbarLayoutBinding.buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }

    private fun navigateToFragment(fragment: SettingFragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        //현재 프래그먼트가 SettingFragment가 아닌 경우에만 백스택에 추가
        //설정창이 여러겹 쌓이는 것을 방지
        if (currentFragment !is SettingFragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

}
