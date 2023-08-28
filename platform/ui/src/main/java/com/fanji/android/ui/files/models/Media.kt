package com.fanji.android.ui.files.models

import android.net.Uri
import com.fanji.android.ui.files.models.BaseFile
import kotlinx.android.parcel.Parcelize

@Parcelize
class Media @JvmOverloads constructor(
    override var id: Long = 0,
    override var name: String,
    override var path: Uri? = null,
    var mediaType: Int = 0,
    var size: Long = 0,
    var selected: Boolean = false,
    var date: Long = 0,
    var duration: Long = 0,
    var width: Int = 0,
    var high: Int = 0
) : BaseFile(id, name, path) {
    override fun toString(): String {
        return "Media(id=$id, name='$name', path=$path, mediaType=$mediaType, size=$size, selected=$selected, date=$date, duration=$duration, width=$width, high=$high)"
    }
}





