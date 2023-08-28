package com.fanji.android.ui.files.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by jiangshide on 29/07/16.
 */
@Parcelize
open class BaseFile(open var id: Long = 0,
                    open var name: String="",
                    open var path: Uri?=null
) : Parcelable