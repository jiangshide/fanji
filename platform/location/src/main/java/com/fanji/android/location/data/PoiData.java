package com.fanji.android.location.data;

/**
 * @author: jiangshide
 * @date: 2023/9/11
 * @email: 18311271399@163.com
 * @description:
 */
public class PoiData {

    public String title;
    public String snippet;
    public String cityName;

    public String cityCode;

    public PoiData() {
    }

    public PoiData(String title, String snippet, String cityName, String cityCode) {
        this.title = title;
        this.snippet = snippet;
        this.cityName = cityName;
        this.cityCode = cityCode;
    }
}
