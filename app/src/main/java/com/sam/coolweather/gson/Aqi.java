package com.sam.coolweather.gson;

/*
{
	"aqi": {
		"city": {
			"aqi": "44",
			"pm25": "13"
		}
	}
}
*/
public class Aqi {

    public AqiCity city;

    public class AqiCity {

        public String aqi;

        public String pm25;
    }
}
