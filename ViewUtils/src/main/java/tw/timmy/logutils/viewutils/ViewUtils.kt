package tw.timmy.logutils.viewutils

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children

fun View.setTextSize(sp: Int) {
    val displayMetrics = this.context.resources.displayMetrics
    val realSpSize = ((sp * displayMetrics.widthPixels).toFloat() / displayMetrics.density / 360f).toInt()
    (this as? TextView)?.setTextSize(TypedValue.COMPLEX_UNIT_SP, realSpSize.toFloat())
}

fun ViewGroup.resetLayoutTextSize() {
    val scale = this.context.resources.displayMetrics.scaledDensity
    this.children.forEach {
        (it as? TextView)?.setTextSize((it.textSize / scale + 0.5f).toInt())?.apply {
            return@forEach
        }
        (it as? ViewGroup)?.resetLayoutTextSize()
    }
}