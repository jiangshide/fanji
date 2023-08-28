package com.fanji.android.ui.jsbridge;

import com.fanji.android.ui.jsbridge.interfaces.BridgeHandler;
import com.fanji.android.ui.jsbridge.interfaces.CallBackFunction;

public class DefaultHandler implements BridgeHandler {

	@Override
	public void handler(String data, CallBackFunction function) {
		if(function != null){
			function.onCallBack("DefaultHandler response data");
		}
	}

}
