package com.timmy.logutils

import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.Log
import kotlinx.coroutines.runBlocking
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


enum class LogType { Verbose, Debug, Info, Warning, Error }

@get:JvmSynthetic
internal val tag: String?
    get() = Throwable().stackTrace.getOrNull(2)?.let(::createStackElementTag)

object LogOption {
    // 控制列印log日誌的每行字數
    var LOG_MAX_LENGTH = 3000

    // 每一個Log檔案可以容許的最大大小(KB)
    var MAX_LOG_FILE_SIZE = 1024

    // 裝置剩餘空間小於這個容量以後，不寫入檔案(MB)。
    var WRITE_LOG_FREE_SPACE = 20
}

private const val MAX_TAG_LENGTH = 23

private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

private fun createStackElementTag(element: StackTraceElement): String {
    var tag = element.className.substringAfterLast('.')
    val m = ANONYMOUS_CLASS.matcher(tag)
    if (m.find()) {
        tag = m.replaceAll("")
    }
    // Tag length limit was removed in API 26.
    return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= 26) {
        tag
    } else {
        tag.substring(0, MAX_TAG_LENGTH)
    }
}


/**印多行文字(例如Json)的時候，可以強迫AndroidStudio全部印出的方法：*/
private fun logMsgMultiLine(msg: String, tagName: String, type: LogType) {
    val strLength = msg.length
    var start = 0
    var end = LogOption.LOG_MAX_LENGTH
    val totalLine = (msg.length / LogOption.LOG_MAX_LENGTH) + 1
    for (i in 0..totalLine) {
        if (strLength > end) {
            printLog(tagName, msg, start, end, type)
            start = end
            end += LogOption.LOG_MAX_LENGTH
        } else {
            printLog(tagName, msg, start, strLength, type)
            break
        }
    }
}

private fun printLog(tagName: String, msg: String, start: Int, end: Int, type: LogType) {
    when (type) {
        LogType.Verbose -> Log.v(tagName, msg.substring(start, end))
        LogType.Debug -> Log.d(tagName, msg.substring(start, end))
        LogType.Info -> Log.i(tagName, msg.substring(start, end))
        LogType.Warning -> Log.w(tagName, msg.substring(start, end))
        LogType.Error -> Log.e(tagName, msg.substring(start, end))
    }
}

// 一般過多字元換行方法

fun logv(msg: String) {
    logv(tag ?: "Log", msg)
}

fun logv(tagName: String, msg: String) {
    logMsgMultiLine(msg, tagName, LogType.Verbose)
}

fun logd(msg: String) {
    logd(tag ?: "Log", msg)
}

fun logd(tagName: String, msg: String) {
    logMsgMultiLine(msg, tagName, LogType.Debug)
}

fun logi(msg: String) {
    logi(tag ?: "Log", msg)
}

fun logi(tagName: String, msg: String) {
    logMsgMultiLine(msg, tagName, LogType.Info)
}

fun logw(msg: String) {
    logw(tag ?: "Log", msg)
}

fun logw(tagName: String, msg: String) {
    logMsgMultiLine(msg, tagName, LogType.Warning)
}

fun loge(msg: String) {
    loge(tag ?: "Log", msg)
}

fun loge(tagName: String, msg: String) {
    logMsgMultiLine(msg, tagName, LogType.Error)
}

fun loge(msg: String, throwable: Throwable) {
    Log.e(tag ?: "Log", msg, throwable)
}

// 寫入檔案的Log方法

private const val END_LINE = "\n"

lateinit var storeFile: File

enum class WriteType(var collectTimes:Int){
    Collect(1000),
    Single(1)
}

private data class WrapFile(
    val writeType:WriteType = WriteType.Single,
    val storeFile:File,
    var catchMsg:String = ""
){


}
fun logWtf(filePath: File, msg: String) {
    logWtf(filePath, tag ?: "Log", msg)
}

fun logWtf(filePath: File, tagName: String, msg: String) {
    writeToFileFolder(filePath, "${getNowTimeFormat()} $tag <Thread ID:${Thread.currentThread().id}>: $msg $END_LINE")
}


private fun getNowTimeFormat(): String = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(Date())

private fun getInternalFreeSpace(showMB: Boolean = true): Long {
    val stat = StatFs(Environment.getDataDirectory().absolutePath)
    val bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
    return bytesAvailable / (if (showMB) (1024 * 1024) else 1)
}

private fun writeToFileFolder(filePath: File, msg: String) {
    if (getInternalFreeSpace(true) < LogOption.WRITE_LOG_FREE_SPACE) return

    try {
        if (!::storeFile.isInitialized || !storeFile.exists() || storeFile.length() + msg.length > LogOption.MAX_LOG_FILE_SIZE * 1024) {
            storeFile = File(filePath.parentFile, getStoreFileName(filePath)).apply {
                parentFile?.mkdirs()
                createNewFile()
            }
        }


        storeFile.appendText(msg)

    } catch (e: IOException) {
        loge("Exception", "File write failed: ${e.message}")
        e.printStackTrace()
    }
}


/**由於要自動分檔名去儲存，因此需要加上時間戳記，那傳進來的檔案名稱比如說是
 * log.txt
 * 要回傳 「log_2023-03-09 11:15:15.txt」
 * */
private fun getStoreFileName(filePath: File) = filePath.name.let {
    it.substring(0, it.lastIndexOf(".")) + // 檔案名稱
            "_${getNowTimeFormat()}" + // 時間戳記
            it.substring(it.lastIndexOf("."), it.length) // 副檔名
}

/**找到是哪裡呼叫到這裡的(呼叫路徑追蹤方法)
 * 使用方法：
 * Exception("標題TAG").trace("到底是哪裡去Call的")
 * */
fun Throwable.trace(TAG: String = tag ?: "TRACE LOG") {
    try {
        throw this
    } catch (th: Throwable) {
        loge(TAG, "=======${th.localizedMessage}=======")
        th.stackTrace.forEach {
            loge(TAG, it.toString())
        }
    }

}
/**執行多久計時工具(階段型)
 * */
fun calculateTimeStep(stepTime:Long,tagName: String = tag?:"CalculateTime LOG"):Long{
    return System.currentTimeMillis().apply{
        loge(tagName,"於[${tagName}]，時間是${this-stepTime}毫秒")
    }
}


/**執行多久計時工具(內容型)
 * */
fun calculateTimeInterval( tagName: String = tag?:"CalculateTime LOG",function: suspend() -> Unit) = runBlocking {
    val startTime = System.currentTimeMillis()
    loge(tagName,"計時開始。")
    function.invoke()
    loge(tagName,"花費時間共計${System.currentTimeMillis()-startTime}毫秒。")
}
