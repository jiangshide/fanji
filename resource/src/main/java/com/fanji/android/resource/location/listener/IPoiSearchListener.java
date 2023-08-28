package com.fanji.android.resource.location.listener;

import com.amap.api.services.poisearch.PoiResult;
import com.fanji.android.resource.location.FJLocation;

/**
 * created by jiangshide on 2019-08-18.
 * email:18311271399@163.com
 */
public interface IPoiSearchListener {
    void onPoiResult(PoiResult poiResult);

    void onError(FJLocation.Errors errors);
}
