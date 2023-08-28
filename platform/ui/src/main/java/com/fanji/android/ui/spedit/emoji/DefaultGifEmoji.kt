package com.fanji.android.ui.spedit.emoji

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.fanji.android.ui.spedit.gif.drawable.ProxyDrawable
import com.fanji.android.ui.R
import pl.droidsonroids.gif.GifDrawable
import java.io.File
import java.io.IOException

class DefaultGifEmoji(private val emojiconFile: File, override val emojiText: String) : Emoji {


    override val res: Any
        get() = emojiconFile


    override val isDeleteIcon: Boolean
        get() = false

    override val defaultResId: Int
        get() = R.mipmap.common_emoj_smile_default

    override val cacheKey: Any
        get() = if (emojiconFile.exists()) {
            emojiconFile.absolutePath
        } else {
            R.mipmap.common_emoj_smile_default
        }


    override fun getDrawable(context: Context): Drawable {
        try {
            return ProxyDrawable(GifDrawable(emojiconFile))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ContextCompat.getDrawable(context, defaultResId)!!
    }


}
