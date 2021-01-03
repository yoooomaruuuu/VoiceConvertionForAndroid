package com.example.voiceconvertion

import android.media.*
import android.util.Log
import kotlin.math.max

//音声録音･再生に関しては反響があるがとりあえず実装
/**
 * AudioRecord クラスのサンプルコード
 */
// 44100 15 64.0fがちょうど良さそう?
class AudioRecordSample {

    // サンプリングレート (Hz)
    // 全デバイスサポート保障は44100のみ
    private val samplingRate = 44100

    // フレームレート (fps)
    // 1秒間に何回音声データを処理したいか
    // 各自好きに決める
    private val frameRate = 15

    // worldの処理フレーム単位
    private val frame_period = 64.0f;

    // 1フレームの音声データ(=Short値)の数
    private val oneFrameDataCount = samplingRate / frameRate
    //private val oneFrameDataCount = getArraySize()

    // 1フレームの音声データのバイト数 (byte)
    // Byte = 8 bit, Short = 16 bit なので, Shortの倍になる
    private val oneFrameSizeInByte = oneFrameDataCount * 2

    // 音声データのバッファサイズ (byte)
    // 要件1:oneFrameSizeInByte より大きくする必要がある
    // 要件2:デバイスの要求する最小値より大きくする必要がある
    private val audioBufferSizeInByte =
        max(oneFrameSizeInByte * 10, // 適当に10フレーム分のバッファを持たせた
            android.media.AudioRecord.getMinBufferSize(samplingRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT))

    var count = 0

    var dataTmp = ByteArray(getTestSize())
    var funcTmp = ByteArray(getTestSize())
    var inputData = DoubleArray(oneFrameDataCount)
    var outputData = DoubleArray(oneFrameDataCount)

    fun startRecording() {

        //音声再生オブジェクト
        initSynthesis(samplingRate, frameRate, frame_period, funcTmp, dataTmp)

        val player = AudioTrack.Builder()
                .setAudioAttributes(AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(samplingRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                .setBufferSizeInBytes(audioBufferSizeInByte)
                .build()

        // インスタンスの作成
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, // 音声のソース
            samplingRate, // サンプリングレート
            AudioFormat.CHANNEL_IN_MONO, // チャネル設定. MONO and STEREO が全デバイスサポート保障
            AudioFormat.ENCODING_PCM_16BIT, // PCM16が全デバイスサポート保障
            audioBufferSizeInByte) // バッファ

        // 音声データを幾つずつ処理するか( = 1フレームのデータの数)
        audioRecord.positionNotificationPeriod = oneFrameDataCount

        // 音声データを格納する配列
        val audioDataArray = ShortArray(oneFrameDataCount)
        player.play()

        // コールバックを指定
        audioRecord.setRecordPositionUpdateListener(object : AudioRecord.OnRecordPositionUpdateListener {

            // フレームごとの処理
            override fun onPeriodicNotification(recorder: AudioRecord) {
                recorder.read(audioDataArray, 0, oneFrameDataCount) // 音声データ読込
                Log.v("AudioRecord", "onPeriodicNotification size=${audioDataArray.size}")
                for(index in audioDataArray.indices)
                {
                    inputData[index] = audioDataArray[index] / 32768.0
                }

                realtimeSynth(2.0, inputData, outputData, funcTmp, dataTmp)

                for(index in outputData.indices)
                {
                    audioDataArray[index] = (outputData[index] * 32768.0).toShort()
                }
                Log.v("AudioRecord", "data[1000]=${audioDataArray[1000]}")
                player.write(audioDataArray, 0, audioDataArray.size)
            }

            // マーカータイミングの処理.
            // notificationMarkerPosition に到達した際に呼ばれる
            override fun onMarkerReached(recorder: AudioRecord) {
            }
        })

        audioRecord.startRecording()
    }

    fun destroyFunc()
    {
        deleteSynthesis(funcTmp, dataTmp)
    }



    fun shortToByteOfArray(shortArray: ShortArray): ByteArray
    {
        var array = ByteArray(shortArray.size * 2)
        for((index, value) in shortArray.withIndex())
        {
            array[index] = (value.toInt() and 0x0F).toByte()
            array[index + 1] = ((value.toInt() and 0xF0) shr 1).toByte()
        }
        return array
    }

    public fun playStart()
    {
    }

    external fun getTestSize(): Int
    external fun initSynthesis(sampling_rate: Int, frame_rate: Int, frame_period: Float, func_pointer: ByteArray, data_pointer: ByteArray)
    external fun realtimeSynth(f0_shift: Double, frame: DoubleArray, output: DoubleArray, func_pointer: ByteArray, data_pointer: ByteArray)
    external fun deleteSynthesis(func_pointer: ByteArray, data_pointe: ByteArray)


}
