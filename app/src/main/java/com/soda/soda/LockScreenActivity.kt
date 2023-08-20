package com.soda.soda

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.soda.soda.databinding.ActivityLockScreenBinding


class LockScreenActivity : AppCompatActivity() {

    private val handler = Handler()
    private var isRedBackground = true
    private lateinit var binding: ActivityLockScreenBinding // 바인딩 객체 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockScreenBinding.inflate(layoutInflater) // 바인딩 초기화
        setContentView(binding.root)

        setLockScreenFlags()
        startBackgroundColorChange()

        val shouldClose = intent.getBooleanExtra("close_lock_screen", false)
        if (shouldClose) {
            closeLockScreenAfterDelay()
        }

        val notificationText = intent.getStringExtra("notification_text")
        if (!notificationText.isNullOrEmpty()) {
            updateLockScreenText(notificationText)
        }
    }

    private fun setLockScreenFlags() {
        val window = window
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }

    private fun startBackgroundColorChange() {
        val backgroundChangeRunnable = object : Runnable {
            override fun run() {
                Log.d("LockScreenActivity", "Changing background color")
                if (isRedBackground) {
                    updateBackgroundDrawable(binding.root.context, R.drawable.background_shape_red)
                } else {
                    updateBackgroundDrawable(binding.root.context, R.drawable.background_shape_white)
                }
                isRedBackground = !isRedBackground
                handler.postDelayed(this, 200)
            }
        }
        handler.post(backgroundChangeRunnable)
    }

    private fun updateBackgroundDrawable(context: Context, drawableResId: Int) {
        val backgroundDrawable = ContextCompat.getDrawable(context, drawableResId)
        binding.lockScreenLayout.background = backgroundDrawable
    }

    private fun updateLockScreenText(text: String) {
        binding.lockScreenText.text = text
    }

    private fun closeLockScreenAfterDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
