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
        sampleForCalculateTimeInterval()

        // 計時方法階段型使用範例：
        sampleForCalculateTimeStep()

        // Log寫入檔案範例
        sampleForLogWriteToFile()

        // 讀取檔案寫入多行Log範例：
        sampleForLogMultipleLine()

    }

    private fun sampleForLogWriteToFile() = CoroutineScope(Dispatchers.Default).launch {

//        LogOption.COLLECT_LOG_SIZE = 5000 // 透過此方法去設定每次收集這麼多次的訊息以後再寫入
        getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath?.let { environmentPath ->
            logWtf(File("$environmentPath/ERROR_LOG_FOLDER/error_log.txt"), "msgTest in first times", WriteType.Single)

            logWtf(File("$environmentPath/ERROR_LOG_FOLDER/error_log.txt"), "msgTest in second times")
            (1..10300).forEach {
                // 寫入Log檔案範例：
                logWtf(File("$environmentPath/LOG_FOLDER/log.txt"), "msgTest in ${+it} times", WriteType.Collect)
            }

            logWtf(File("$environmentPath/LOG_FOLDER/log.txt"), "msgTest in penultimate times")

            // 以下範例將造成Exception // 因為檔名相同，寫入的type必須一樣。 //所有的Collect Type 共用一組 collectTimes
//        logWtf(File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath + "/LOG_FOLDER/" + "log.txt"), "msgTest in last times", WriteType.Single)

        }
        loge("sampleForLogWriteToFile", "檔案範例寫入完成")
    }

    // 計時方法階段型使用範例：
    private fun sampleForCalculateTimeStep() = CoroutineScope(Dispatchers.Default).launch {

        val timeList = mutableListOf(System.currentTimeMillis())  // 開始計時

        delay(1000) // 實際上處理了一些事情

        timeList.add(calculateTimeStep(timeList[0], "第一步驟"))

        delay(2000) // 實際上又處理了一些事情

        timeList.add(calculateTimeStep(timeList[1], "第二步驟"))

        delay(3000) // 實際上再處理了一些事情

        timeList.add(calculateTimeStep(timeList[2], "第三步驟"))

        loge("sampleForCalculateTimeStep", "階段型計時方法示範完成")
    }

    // 計時方法內容型使用範例：
    private fun sampleForCalculateTimeInterval() {
        calculateTimeInterval("某件事的計時") {
            loge("我做了一件事")
            delay(1000L)
            loge("這件事已經完成了")
        }

        loge("sampleForCalculateTimeInterval", "內容型計時方法示範完成")
    }

    private fun sampleForLogMultipleLine() {
        CoroutineScope(Dispatchers.IO).launch {
            // 讀取檔案(一大串Json)後印出範例。
            kotlin.runCatching {
                assets.open("test_to_print.json").bufferedReader().use { it.readText() }
                    .apply { loge(this) }
            }.onFailure { loge("讀取錯誤！原因：", it) }
        }
        loge("sampleForLogMultipleLine", "多行Log方法示範完成")
    }


    override fun onDestroy() {
        super.onDestroy()
        writeRemainingLogOnExit()
    }
}