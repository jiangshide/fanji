package com.fanji.android.ui.spedit.view

import android.text.Editable
import android.view.KeyEvent

interface KeyEventProxy {

    fun onKeyEvent(keyEvent: KeyEvent, text: Editable): Boolean
}
