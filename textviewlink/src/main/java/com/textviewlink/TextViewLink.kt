package com.textviewlink

import android.content.Context
import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import java.util.regex.Pattern


class TextViewLink : AppCompatTextView {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.textViewStyle)

    init {
        this.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(SpannableString(text).removeLinkUnderLineText(), type)
    }

    private fun getLinksFromTheText(text: Spannable?): List<String> {
        return text?.let {
            val links: ArrayList<String> = ArrayList()
            val p = Pattern.compile(Patterns.WEB_URL.pattern(), Pattern.CASE_INSENSITIVE)
            val m = p.matcher(text)
            while (m.find()) {
                links.add(m.group())
            }
            links
        } ?: emptyList()
    }

    private fun Spannable.removeLinkUnderLineText(): Spannable {
        val spannable = SpannableString(this)
        val listTextNeedRemoveUnderline = getLinksFromTheText(this)
        var lastIndexReplace = 0;
        for (link in listTextNeedRemoveUnderline) {
            val start = this.indexOf(link, lastIndexReplace)
            val end = start + link.length
            val nspan = URLSpanNoUnderline(link, this@TextViewLink.linkTextColors)
            spannable.setSpan(nspan, start, end, 0)
            lastIndexReplace = end
        }
        return spannable
    }

    inner class URLSpanNoUnderline(url: String?, private val colorState: ColorStateList) : URLSpan(url) {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.linkColor = colorState.defaultColor
            ds.isUnderlineText = false
        }
    }
}
