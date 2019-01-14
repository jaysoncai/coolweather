package com.sam.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/*
{
	"daily_forecast": [{
		"date": "2016-08-09",
		"cond": {
			"txt_d": "阵雨"
		},
		"tmp": {
			"max": "34",
			"min": "29"
		}
	}]
}
由于daily_forecast是一个数组，所以只需要编写数组里面的对象GSON实体即可
 */
public class Forecast {

    public String date;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperature temperature;

    public class More {

        @SerializedName("txt_d")
        public String info;
    }

    public class Temperature {

        public String max;

        public String min;
    }
}
