package tw.timmy.logutils.sample

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
//import com.timmy.logutils.logWtf
import com.timmy.logutils.loge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
    }

    private fun initData() {


        CoroutineScope(Dispatchers.IO).launch {
            // 寫入Log檔案範例：
            (1..10000).forEach {
//                logWtf(File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath + "/LOG_FOLDER/" + "loga.txt"), "msgTest in ${+it} times")
            }

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