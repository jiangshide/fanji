package com.fanji.android.ui.spedit.view

import android.content.Context
import android.text.NoCopySpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.fanji.android.ui.spedit.gif.watcher.GifWatcher

class SpXTextView : AppCompatTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        val watchers = ArrayList<NoCopySpan>()
        watchers.add(GifWatcher(this))
        setSpannableFactory(SpXSpannableFactory(watchers))
    }

    override fun setText(text: CharSequence, type: BufferType) {
        super.setText(text, BufferType.SPANNABLE)
    }

}
