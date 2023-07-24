package org.tensorflow.lite.examples.audio.helper

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.tensorflow.lite.examples.audio.MainActivity
import org.tensorflow.lite.examples.audio.R
import org.tensorflow.lite.examples.audio.fragments.SettingFragment
import org.tensorflow.lite.examples.audio.fragments.WarningFragment
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

    fun soundCheck(tensorAudio: TensorAudio, bytesRead: Int, context: Context) {
        buffer = tensorAudio.tensorBuffer

        // 버퍼에서 읽은 데이터를 소리 크기로 변환하여 확인합니다
        var soundAmplitude = 0.0
        for (i in 0 until bytesRead) {
            val sample = buffer.getFloatValue(i)
            soundAmplitude += sample * sample
        }

        // Root Mean Square (RMS) 계산하기
        val rms = kotlin.math.sqrt(soundAmplitude / bytesRead)

        // RMS를 데시벨로 변환하기
        // 0에 로그를 취하는 것을 방지하기 위해 1e-7 추가하기
        soundDecibel = (30 * log10(rms * 5000 + 1e-7) - 20).toInt()
        if(soundDecibel < 0) Log.d(TAG, "soundDecibel: 0")
        else Log.d(TAG, "soundDecibel: $soundDecibel")

        // 소리 크기가 100데시벨 이상인 경우 핸드폰에 진동을 울리는 코드를 작성합니다
        if (soundDecibel >= 80) {
            // 진동을 울리는 코드를 작성합니다
            try {
                vibrate(context)
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred while vibrating", e)
                val exceptionMessage = e.message
                Log.e(TAG, "Exception message: $exceptionMessage")
            }
        }
    }

    private fun vibrate(context: Context){
        if(!SettingFragment.vibrateSwitchState) return

        val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        1000,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vibrator.vibrate(1000)
            }
        }
        createNotification(context)
    }

    /** 알림 생성 **/
    private fun createNotification(context: Context) {
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
        // 'show_dialog' 키에 'true' 값을 추가 -> 해당 알림을 클릭했을 때만 대화상자 띄우기 위해
        openAppIntent.putExtra("show_dialog", true)

        // 알림을 클릭했을 때 openAppIntent를 실행할 수 있도록 함
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (AudioClassificationHelper.label != null) {
            warningLabel = AudioClassificationHelper.label!! + " 주의하세요!"
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentText(warningLabel)
            .setSmallIcon(R.drawable.ic_warning)
            .setAutoCancel(true) // 알림을 탭한 후 자동으로 제거
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(notificationId, notification)
    }

}