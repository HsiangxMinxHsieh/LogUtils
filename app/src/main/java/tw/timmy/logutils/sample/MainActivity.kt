package tw.timmy.logutils.sample

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.timmy.logutils.*
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logSample()
    }

    private fun logSample() {

        // 計時方法內容型使用範例：
//        sampleForCalculateTimeInterval()

        // 計時方法階段型使用範例：
        sampleForCalculateTimeStep()

        // Log寫入檔案範例
        sampleForLogWriteToFile()

        // 讀取檔案寫入多行Log範例：
//        sampleForLogMultipleLine()


    }

    private fun sampleForLogWriteToFile() {
        (1..10000).forEach {
            // 寫入Log檔案範例：
            logWtf(File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath + "/LOG_FOLDER/" + "loga.txt"), "msgTest in ${+it} times")
        }
    }

    // 計時方法階段型使用範例：
    private fun sampleForCalculateTimeStep() = runBlocking {

        val timeList = mutableListOf(System.currentTimeMillis())  // 開始計時

        delay(1000) // 實際處理了一些事情

        timeList.add(calculateTimeStep(timeList[0], "第一步驟"))
//        loge("開始階段性計時2，此時的內容是=>${timeList[1]}")
        delay(2000) // 實際又處理了一些事情

        timeList.add(calculateTimeStep(timeList[1], "第二步驟"))

        delay(3000) // 實際上再處理了一些事情

        timeList.add(calculateTimeStep(timeList[2], "第三步驟"))

    }

    // 計時方法內容型使用範例：
    private fun sampleForCalculateTimeInterval() {
        calculateTimeInterval("某件事的計時") {
            loge("我做了一件事")
            delay(1000L)
            loge("這件事已經完成了")
        }
    }

    private fun sampleForLogMultipleLine() {
        CoroutineScope(Dispatchers.IO).launch {
            // 讀取檔案(一大串Json)後印出範例。
            kotlin.runCatching {
                assets.open("test_to_print.json").bufferedReader().use { it.readText() }
                    .apply { loge(this) }
            }.onFailure { loge("讀取錯誤！原因：", it) }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}