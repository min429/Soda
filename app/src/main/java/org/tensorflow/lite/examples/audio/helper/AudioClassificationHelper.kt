/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.audio.helper

import android.content.Context
import android.media.AudioRecord
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.examples.audio.fragments.AudioClassificationListener
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.core.BaseOptions
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class AudioClassificationHelper(
    val context: Context,
    val listener: AudioClassificationListener,
    var currentModel: String = YAMNET_MODEL, //현재 모델
    var classificationThreshold: Float = DISPLAY_THRESHOLD,//분류 임계값
    var overlap: Float = DEFAULT_OVERLAP_VALUE, //오버랩 값
    var numOfResults: Int = DEFAULT_NUM_OF_RESULTS, //결과 개수
    var currentDelegate: Int = 0,// 현재 대리자 (CPU, NNAPI)
    var numThreads: Int = 2 //쓰레드 개수

) {
    private lateinit var classifier: AudioClassifier
    private lateinit var tensorAudio: TensorAudio
    private lateinit var recorder: AudioRecord
    private lateinit var executor: ScheduledThreadPoolExecutor
    private lateinit var soundCheckExecutor: ScheduledThreadPoolExecutor
    private var bytesRead by Delegates.notNull<Int>()


    private val classifyRunnable = Runnable {
        classifyAudio()
    }

    // 새로운 스레드에서 소리 크기 확인 및 진동 울리기
    private val soundCheckRunnable = Runnable {
        SoundCheckHelper.soundCheck(tensorAudio, bytesRead, context)
    }

    init {
        initClassifier()
    }

    fun initClassifier() {
        // Set general detection options, e.g. number of used threads
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(numThreads)


        // Use the specified hardware for running the model. Default to CPU.
        // Possible to also use a GPU delegate, but this requires that the classifier be created
        // on the same thread that is using the classifier, which is outside of the scope of this
        // sample's design.
        when (currentDelegate) {
            DELEGATE_CPU -> {
                // Default
                //모델은 CPU에서 실행
            }
            DELEGATE_NNAPI -> {
                baseOptionsBuilder.useNnapi()
            }
        }

        // Configures a set of parameters for the classifier and what results will be returned.
        val options = AudioClassifier.AudioClassifierOptions.builder()
            .setScoreThreshold(classificationThreshold) //분류할 임계값 설정
            .setMaxResults(numOfResults) //분류 결과의 최대 개수
            .setBaseOptions(baseOptionsBuilder.build())
            .build()

        try {
            // Create the classifier and required supporting objects
            classifier = AudioClassifier.createFromFileAndOptions(context, currentModel, options)
            tensorAudio = classifier.createInputTensorAudio()

            //필요한 객체를 생성하는 분류기 생성
            recorder = classifier.createAudioRecord()
            //분류 시작
            startAudioClassification()


        } catch (e: IllegalStateException) {
            listener.onError(
                "Audio Classifier failed to initialize. See error logs for details"
            )

            Log.e("AudioClassification", "TFLite failed to load with error: " + e.message)
        }
    }


    fun startAudioClassification() {
        Log.d("자동녹음",  "자동녹음 실행중")

        // 음성 녹음 중이면 중복 시작 방지
        if (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            return
        }
        // 녹음 시작
        recorder.startRecording()
        executor = ScheduledThreadPoolExecutor(1)
        soundCheckExecutor = ScheduledThreadPoolExecutor(1)

        // Each model will expect a specific audio recording length. This formula calculates that
        // length using the input buffer size and tensor format sample rate.
        // For example, YAMNET expects 0.975 second length recordings.
        // This needs to be in milliseconds to avoid the required Long value dropping decimals.
        val lengthInMilliSeconds = ((classifier.requiredInputBufferSize * 1.0f) /
                classifier.requiredTensorAudioFormat.sampleRate) * 1000  //오디오가 얼마나 길어야 하는지 계산

        interval = (lengthInMilliSeconds * (1 - overlap)).toLong() //오버랩값으로 반복 간격 계산

        executor.scheduleAtFixedRate(
            classifyRunnable,   //-> classifyAudio() 호출
            0,
            interval,
            TimeUnit.MILLISECONDS)

        // 소리 크기 확인 및 진동 실행 로직
        soundCheckExecutor.scheduleAtFixedRate(
            soundCheckRunnable,
            0,
            interval,
            TimeUnit.MILLISECONDS
        )
    }

    private fun classifyAudio() {
        bytesRead = tensorAudio.load(recorder)
        var inferenceTime = SystemClock.uptimeMillis()
        val output = classifier.classify(tensorAudio) //분류 실행
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime //분류하는데 걸리는 시간 계산
        listener.onResult(output[0].categories, inferenceTime) //분류 결과를 리스너에게 전달

    }

    fun stopAudioClassification() {
        recorder.stop() //오디오 녹음 중지
        executor.shutdownNow() //분류 작업 중지
        soundCheckExecutor.shutdownNow() //데시벨 확인 중지
    }

    companion object {
        const val DELEGATE_CPU = 0 //메인 쓰레드에서 작동
        const val DELEGATE_NNAPI = 1 //NN API 딜리게이트 사용
        const val DISPLAY_THRESHOLD = 0.3f //결과 출력에 영향을 미치는 임계선 값
        const val DEFAULT_NUM_OF_RESULTS = 1 //분류 결과의 최대 개수, default 2개 설정
        const val DEFAULT_OVERLAP_VALUE = 0.5f //반복 실행 간격, default 값은 0.5 설정
        const val YAMNET_MODEL = "yamnet.tflite" //사용하는 모델, default 값은 YAMNET 설정
        var interval by Delegates.notNull<Long>()
    }



}
