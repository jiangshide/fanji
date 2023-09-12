package com.fanji.android.location.data;

import com.fanji.android.location.FJLocation;
import com.fanji.android.location.listener.ILocationListener;
import com.google.gson.Gson;

/**
 * created by jiangshide on 2020/3/23.
 * email:18311271399@163.com
 */
public class PositionData implements ILocationListener {
    public Long uid;
    public Long fromId;
    public int event;
    public String err;
    public String ip;
    public Double latitude;
    public Double longitude;
    public String locationType;
    public String accuracy;
    public String provider;
    public String speed;
    public String bearing;
    public String satellites;
    public String country;
    public String province;
    public String city;
    public String district;
    public String cityCode;
    public String adCode;
    public String address;
    public String poiName;
    public String networkType;
    public String gpsStatus;
    public String gpsSatellites;
    public String timeZone;

    public PositionData() {
        FJLocation.getInstance().startLocation(true, this);
    }

    public String getGson() {
        return new Gson().toJson(this);
    }

    @Override
    public void onGetLocation(PositionData location) {
        PositionData positionData = new PositionData();
        positionData.ip = location.ip;
        positionData.latitude = location.latitude;
        positionData.longitude = location.longitude;
        positionData.locationType = location.locationType + "";
        positionData.accuracy = location.accuracy + "";
        positionData.provider = location.provider;
        positionData.speed = location.speed + "";
        positionData.bearing = location.bearing + "";
        positionData.satellites = location.satellites + "";
        positionData.country = location.country;
        positionData.province = location.province;
        positionData.city = location.city;
        positionData.district = location.district;
        positionData.cityCode = location.cityCode;
        positionData.adCode = location.adCode;
        positionData.address = location.address;
        positionData.poiName = location.poiName;
        positionData.networkType = location.networkType;
        positionData.gpsStatus = location.gpsStatus + "";
        positionData.gpsSatellites = location.gpsSatellites + "";
        positionData.timeZone = location.timeZone;
    }

    @Override
    public void onError(FJLocation.Errors errors) {

    }
}
