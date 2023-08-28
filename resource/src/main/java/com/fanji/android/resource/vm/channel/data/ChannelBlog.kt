package com.fanji.android.resource.vm.channel.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.fanji.android.resource.vm.feed.data.Feed

/**
 * created by jiangshide on 2020/4/7.
 * email:18311271399@163.com
 */
class ChannelBlog() : Channel(), Parcelable {
  var blog: Feed? = null

  constructor(parcel: Parcel) : this() {
  }

  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {

  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Creator<ChannelBlog> {
    override fun createFromParcel(parcel: Parcel): ChannelBlog {
      return ChannelBlog(parcel)
    }

    override fun newArray(size: Int): Array<ChannelBlog?> {
      return arrayOfNulls(size)
    }
  }
}