package com.soda.soda.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
private lateinit var buffer: TensorBuffer

object SoundCheckHelper{
    private val channelId = "VibrateChannel"
    private val notificationId = 2
    lateinit var warningLabel: String
    var soundDecibel: Int = 0
    private var dialogInterface: DialogInterface? = null
    private var isVibrating = false

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
        soundDecibel = (30 * log10(rms * 5000 + 1e-7) - 20).toInt()
        if(soundDecibel < 0) Log.d(TAG, "soundDecibel: 0")
        else Log.d(TAG, "soundDecibel: $soundDecibel")

        // 소리 크기가 80데시벨 이상 -> 핸드폰 진동
        if (soundDecibel >= 80) {
            try {
                vibrate(context)
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred while vibrating", e)
                val exceptionMessage = e.message
                Log.e(TAG, "Exception message: $exceptionMessage")
            }
        }
    }

    /** 진동 함수 **/
    private fun vibrate(context: Context){
        if(!SettingFragment.vibrateSwitchState) return // 진동알림 off
        if (isVibrating) return // 이미 진동 중
        if(AudioClassificationHelper.label == null) return // 아직 분류가 안됨
        if(SettingFragment.autoSwitchState){
            if(AudioClassificationHelper.label!! != "경적 소리 같습니다." &&
                AudioClassificationHelper.label!! != "화재 경보기 소리 같습니다." &&
                AudioClassificationHelper.label!! != "구급차(사이렌) 소리 같습니다." &&
                AudioClassificationHelper.label!! != "소방차(사이렌) 소리 같습니다." &&
                AudioClassificationHelper.label!! != "경찰차(사이렌) 소리 같습니다." &&
                AudioClassificationHelper.label!! != "사이렌 소리 같습니다." &&
                AudioClassificationHelper.label!! != "민방위 사이렌 소리 같습니다." &&
                AudioClassificationHelper.label!! != "비명 소리 같습니다." &&
                AudioClassificationHelper.label!! != "쾅 소리 같습니다." &&
                AudioClassificationHelper.label!! != "폭발 소리 같습니다." &&
                AudioClassificationHelper.label!! != "총 소리 같습니다."){
                return
            }
        }

        isVibrating = true
        val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        1000,
                        255
                    )
                )
            } else {
                vibrator.vibrate(1000)
            }
        }
        createNotification(context)

        // 3초 후 다시 진동이 발생 가능화
        Handler(Looper.getMainLooper()).postDelayed({
            isVibrating = false
        }, 3000)
    }

    /** 위험알림 생성 **/
    private fun createNotification(context: Context) {
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
    }

    fun setInterface(dialogInterface: DialogInterface?) {
        this.dialogInterface = dialogInterface
    }

}