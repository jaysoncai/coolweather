package com.sam.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Sam on 2019/1/14.
 */
public class County extends DataSupport {

    private int id;
    private String countyName;
    private int weatherId;
    private int cityId; // 记录当前县区所在的城市

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
