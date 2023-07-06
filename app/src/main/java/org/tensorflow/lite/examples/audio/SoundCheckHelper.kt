package org.tensorflow.lite.examples.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.content.ContextCompat
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.log10

private const val TAG = "SoundCheckHelper"
private lateinit var buffer: TensorBuffer

object SoundCheckHelper {

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
        val soundDecibel = 30 * log10(rms * 5000 + 1e-7)
        Log.d(TAG, "soundDecibel: $soundDecibel")

        // 소리 크기가 100데시벨 이상인 경우 핸드폰에 진동을 울리는 코드를 작성합니다
        if (soundDecibel >= 100) {
            // 진동을 울리는 코드를 작성합니다
            try {
                viberate(context)
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred while vibrating", e)
                val exceptionMessage = e.message
                Log.e(TAG, "Exception message: $exceptionMessage")
            }
        }
    }

    private fun viberate(context: Context){
        val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(1000)
            }
        }
    }
}