package com.fanji.android.location.listener;

import com.fanji.android.location.FJLocation;
import com.fanji.android.location.data.PositionData;

/**
 * created by jiangshide on 2019-08-18.
 * email:18311271399@163.com
 */
public interface ILocationListener {
    void onGetLocation(PositionData location);

    void onError(FJLocation.Errors errors);
}
