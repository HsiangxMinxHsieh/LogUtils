package tw.timmy.logutils.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.timmy.logutils.loge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(1000)
                val inputStream: InputStream = assets.open("test_to_print.json")
                inputStream.bufferedReader().use { it.readText() }.run {
                    loge(this)
                    Timber.e(this)
                }

            } catch (e: Exception) {
                loge("讀取錯誤！", "錯誤內容：${e.message}")
                e.printStackTrace()
            }
        }
    }
}