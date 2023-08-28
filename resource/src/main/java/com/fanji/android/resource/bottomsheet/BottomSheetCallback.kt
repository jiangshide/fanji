package com.fanji.android.resource.bottomsheet

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * created by jiangshide on 5/22/21.
 * email:18311271399@163.com
 */
interface BottomSheetCallback {
    fun onDismiss()
    fun onCancel()

    fun onBehaviorStateChange(view: View, @BottomSheetBehavior.State state: Int) = Unit
}