package com.timmy.logutils

import android.util.Log


enum class LogType { Verbose, Debug, Info, Warning, Error }

@get:JvmSynthetic
internal val explicitTag = ThreadLocal<String>()

@get:JvmSynthetic
internal  val tag: String?
    get() {
        val tag = explicitTag.get()
        if (tag != null) {
            explicitTag.remove()
        }
        return  explicitTag.get()
    }
//可以全域性控制是否列印log日誌
private const val LOG_MAXLENGTH = 3000

/**印多行文字(例如Json)的時候，可以強迫AndroidStudio全部印出的方法：*/
private fun logMsgMultiLine(msg: String, tagName: String, type: LogType) {
    val strLength = msg.length
    var start = 0
    var end = LOG_MAXLENGTH
    val totalLine = (msg.length / LOG_MAXLENGTH) + 1
    for (i in 0..totalLine) {
        if (strLength > end) {
            printLog(tagName, msg, start, end, type)
            start = end
            end += LOG_MAXLENGTH
        } else {
            printLog(tagName, msg, start, strLength, type)
            break
        }
    }
}

private fun printLog(tagName: String, msg: String, start: Int, end: Int, type: LogType) {
    when (type) {
        LogType.Verbose -> Log.v(tagName, msg.substring(start, end)+"\n")
        LogType.Debug -> Log.d(tagName, msg.substring(start, end)+"\n")
        LogType.Info -> Log.i(tagName, msg.substring(start, end)+"\n")
        LogType.Warning -> Log.w(tagName, msg.substring(start, end)+"\n")
        LogType.Error -> Log.e(tagName, msg.substring(start, end)+"\n")
    }
}

fun logv(msg: String) {
    logv(tag ?:"Log", msg)
}

fun logv(tagName: String, msg: String) {
    logMsgMultiLine(msg, tagName, LogType.Verbose)
}

fun logd(msg: String) {
    logd(tag ?:"Log", msg)
}

fun logd(tagName: String, msg: String) {
    logMsgMultiLine(msg, tagName, LogType.Debug)
}

fun logi(msg: String) {
    logi(tag ?:"Log", msg)
}

fun logi(tagName: String, msg: String) {
    logMsgMultiLine(msg, tagName, LogType.Info)
}

fun logw(msg: String) {
    logw(tag ?:"Log", msg)
}

fun logw(tagName: String, msg: String) {
    logMsgMultiLine(msg, tagName, LogType.Warning)
}

fun loge(msg: String) {
    loge(tag ?:"Log", msg)
}

fun loge(tagName: String, msg: String) {
    logMsgMultiLine(msg, tagName, LogType.Error)
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