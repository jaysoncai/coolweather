package com.sam.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
{
	"HeWeather": [{
		"status": "OK",
		"basic": {},
		"aqi": {},
		"now": {},
		"suggestion": {},
		"daily_forecast": []
	}]
}
 */
public class Weather {

    public String status;

    public Basic basic;

    public Aqi aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
