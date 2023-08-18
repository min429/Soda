package com.soda.soda.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.soda.soda.DialogInterface
import com.soda.soda.MainActivity
import com.soda.soda.R
import com.soda.soda.fragments.SettingFragment
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.log10

private const val TAG = "SoundCheckHelper"

private var DECIBEL_THRESHOLD = 100

object SoundCheckHelper{
    private lateinit var buffer: TensorBuffer
    private val channelId = "VibrateChannel"
    private val notificationId = 2
    lateinit var warningLabel: String
    var soundDecibel: Int = 0
    private var dialogInterface: DialogInterface? = null
    private var isNotifying = false

    fun soundCheck(tensorAudio: TensorAudio, bytesRead: Int, context: Context) {
        buffer = tensorAudio.tensorBuffer
        // 버퍼에서 읽은 데이터 -> 소리 크기로 변환후 확인
        var soundAmplitude = 0.0
        for (i in 0 until bytesRead) {
            val sample = buffer.getFloatValue(i)
            soundAmplitude += sample * sample
        }

        // Root Mean Square (RMS) 계산
        val rms = kotlin.math.sqrt(soundAmplitude / bytesRead)

        // RMS를 데시벨로 변환 (0에 로그를 취하는 것을 방지하기 위해 1e-7 추가)
        soundDecibel = (30 * log10(rms * 5000 + 1e-7) - 5).toInt()
        if(soundDecibel < 0) Log.d(TAG, "soundDecibel: 0")
        else Log.d(TAG, "soundDecibel: $soundDecibel")

        DECIBEL_THRESHOLD =
            if(!SettingFragment.autoSwitchState) 120
            else 45

        // 소리 크기가 임계값 이상 -> 핸드폰 진동
        if (soundDecibel >= DECIBEL_THRESHOLD) {
            try {
                if(AudioClassificationHelper.label == null) return // 아직 분류가 안됨
                if(SettingFragment.autoSwitchState){
                    if(AudioClassificationHelper.label!! != "자동차 경적 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "트럭 경적 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "기차 경적 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "경보 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "화재 경보 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "자동차 도난 경보 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "구급차(사이렌) 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "소방차(사이렌) 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "경찰차(사이렌) 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "사이렌 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "민방위 사이렌 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "비명 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "쾅 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "폭발 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "포격 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "소리 지르는 것 같습니다." &&
                        AudioClassificationHelper.label!! != "어린 아이가 소리 지르는 것 같습니다." &&
                        AudioClassificationHelper.label!! != "울부짖는 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "부서지는 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "깨지는 소리 같습니다." &&
                        AudioClassificationHelper.label!! != "물체가 부딪치거나 떨어지는 소리 같습니다."){
                        return
                    }
                }
                createNotification(context)
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred while notifying", e)
                val exceptionMessage = e.message
                Log.e(TAG, "Exception message: $exceptionMessage")
            }
        }
    }

    /** 진동 함수 **/
    private fun vibrate(context: Context){
        if(!SettingFragment.vibrateSwitchState) return // 진동알림 off

        val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        2000,
                        255
                    )
                )
            } else {
                vibrator.vibrate(2000)
            }
        }
    }

    /** 위험알림 생성 **/
    private fun createNotification(context: Context) {
        if (isNotifying) return // 이미 위험 알림중
        isNotifying = true

        if(SettingFragment.autoSwitchState)
            warningLabel = AudioClassificationHelper.label!! + " 주의하세요!"
        else
            warningLabel = "큰 소리가 난 것 같습니다. 주의하세요!"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Warning Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 알림을 클릭했을 때 실행될 Intent 정의
        val openAppIntent = Intent(context, MainActivity::class.java)
        openAppIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        openAppIntent.putExtra("show_dialog", true) // 'show_dialog' 키에 'true' 값을 추가 -> 해당 알림을 클릭했을 때만 대화상자 띄우기 위해

        // 알림을 클릭했을 때 openAppIntent 실행
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentText(warningLabel)
            .setSmallIcon(R.drawable.ic_warning)
            .setAutoCancel(true) // 알림 탭할 시 ->  제거
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(notificationId, notification)

        dialogInterface?.dialogEvents()

        vibrate(context)


        // 플래시 효과발생 = 5회 100ms 간격
        flashRepeatedly(context, times = 5, interval = 100)


        // 3초 후 다시 진동이 발생 가능
        Handler(Looper.getMainLooper()).postDelayed({
            isNotifying = false
        }, 3000)
    }


    /** 플래시 효과 반복 함수  **/
    private fun flashRepeatedly(context: Context, times: Int, interval: Long) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]  // 첫 번째 카메라 선택 (일반적으로 후면 카메라)

        val handler = Handler(Looper.getMainLooper())
        var flashCount = 0
        val flashRunnable: Runnable = object : Runnable {
            override fun run() {
                if (flashCount < times * 2) { // 각 깜박임은 켜짐과 꺼짐 두 번씩이므로 times * 2 번 반복
                    val enable = flashCount % 2 == 0
                    try {
                        cameraManager.setTorchMode(cameraId, enable)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                    flashCount++
                    handler.postDelayed(this, interval)
                } else {
                    try {
                        cameraManager.setTorchMode(cameraId, false) // 마지막에 플래시 끄기
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        flashRunnable.run()
    }

    fun setInterface(dialogInterface: DialogInterface?) {
        this.dialogInterface = dialogInterface
    }

}