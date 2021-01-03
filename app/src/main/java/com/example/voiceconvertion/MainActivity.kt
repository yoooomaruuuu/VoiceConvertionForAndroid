package com.example.voiceconvertion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var ars = AudioRecordSample()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        findViewById<TextView>(R.id.sample_text).text = stringFromJNI()

        findViewById<Button>(R.id.record_button).setOnClickListener{
            ars.startRecording()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    external fun stringFromJNI(): String
    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}