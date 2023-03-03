package com.timmy.logutils

import android.os.Build
import android.util.Log
import java.util.regex.Pattern


enum class LogType { Verbose, Debug, Info, Warning, Error }

@get:JvmSynthetic
internal val tag: String?
    get() = Throwable().stackTrace.getOrNull(2)?.let(::createStackElementTag)

object LogOption {
    //控制列印log日誌的每行字數
    var LOG_MAX_LENGTH = 3000
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


fun Throwable.trace(TAG: String = "TAG") {
    try {
        throw this
    } catch (th: Throwable) {
        loge(TAG, "=======${th.localizedMessage}=======")
        th.stackTrace.forEach {
            loge(TAG, it.toString())
        }
    }

}