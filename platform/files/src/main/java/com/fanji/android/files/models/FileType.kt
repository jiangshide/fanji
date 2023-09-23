package com.fanji.android.files.models

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

/**
 * Created by droidNinja on 29/07/16.
 */
@Parcelize
class FileType constructor(
        var title: String,
        var extensions : Array<String>,
        @DrawableRes
        var drawable: Int
) : Parcelable{
        override fun toString(): String {
                return "FileType(title='$title', extensions=${extensions.contentToString()}, drawable=$drawable)"
        }
}