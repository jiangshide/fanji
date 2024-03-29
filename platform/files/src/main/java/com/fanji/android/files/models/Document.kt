package com.fanji.android.files.models

import android.net.Uri
import kotlinx.android.parcel.Parcelize

@Parcelize
class Document @JvmOverloads constructor(override var id: Long = 0,
                                         override var name: String,
                                         override var path: Uri?=null,
                                         var mimeType: String? = null,
                                         var size: String? = null,
                                         var fileType: FileType? = null
) : BaseFile(id, name, path){
    override fun toString(): String {
        return "Document(id=$id, name='$name', path=$path, mimeType=$mimeType, size=$size, fileType=$fileType)"
    }
}