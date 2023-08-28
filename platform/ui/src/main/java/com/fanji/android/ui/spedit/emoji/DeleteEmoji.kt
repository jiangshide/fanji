package com.fanji.android.ui.spedit.emoji

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.fanji.android.ui.R

class DeleteEmoji : Emoji {

    override val emojiText: CharSequence
        get() = ""

    override val res: Any
        get() = R.mipmap.common_emoj_delete_expression


    override val isDeleteIcon: Boolean
        get() = true

    override val defaultResId: Int
        get() = R.mipmap.common_emoj_delete_expression

    override val cacheKey: Any
        get() = R.mipmap.common_emoj_delete_expression


    override fun getDrawable(context: Context): Drawable {
        return ContextCompat.getDrawable(context, defaultResId)!!
    }


}
