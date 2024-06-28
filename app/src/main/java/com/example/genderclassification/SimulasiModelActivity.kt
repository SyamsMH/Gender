package com.example.genderclassification

import android.annotation.SuppressLint
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiModelActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "gender.tflite"

    private lateinit var resultText: TextView
    private lateinit var longhair: EditText
    private lateinit var foreheadwidthcm: EditText
    private lateinit var foreheadheightcm: EditText
    private lateinit var nosewide: EditText
    private lateinit var noselong: EditText
    private lateinit var lipsthin: EditText
    private lateinit var distancenosetoliplong: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi_model)

        resultText = findViewById(R.id.txtResult)
        longhair = findViewById(R.id.longhair)
        foreheadwidthcm = findViewById(R.id.foreheadwidthcm)
        foreheadheightcm = findViewById(R.id.foreheadheightcm)
        nosewide = findViewById(R.id.nosewide)
        noselong = findViewById(R.id.noselong)
        lipsthin = findViewById(R.id.lipsthin)
        distancenosetoliplong = findViewById(R.id.distancenosetoliplong)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                longhair.text.toString(),
                foreheadwidthcm.text.toString(),
                foreheadheightcm.text.toString(),
                nosewide.text.toString(),
                noselong.text.toString(),
                lipsthin.text.toString(),
                distancenosetoliplong.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Perempuan"
                }else if (result == 1){
                    resultText.text = "Laki-Laki"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(8)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String): Int{
        val inputVal = FloatArray(7)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}