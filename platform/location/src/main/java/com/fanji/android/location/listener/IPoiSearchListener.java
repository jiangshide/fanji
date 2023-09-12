package com.fanji.android.location.listener;

import com.fanji.android.location.FJLocation;
import com.fanji.android.location.data.PoiData;

import java.util.List;

/**
 * created by jiangshide on 2019-08-18.
 * email:18311271399@163.com
 */
public interface IPoiSearchListener {
    void onPoiResult(List<PoiData> poiData);

    void onError(FJLocation.Errors errors);
}
