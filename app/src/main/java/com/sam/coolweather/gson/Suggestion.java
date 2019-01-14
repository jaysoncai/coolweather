package com.sam.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/*
{
	"suggestion": {
		"comf": {
			"txt": "白天天气较热会让你感到不舒服。"
		},
		"cw": {
			"txt": "不宜洗车。"
		},
		"sport": {
			"txt": "支持户外运动。"
		}
	}
}
 */
public class Suggestion {

    @SerializedName("cmf")
    private Comfort comfort;

    @SerializedName("cw")
    private CarWash carWash;

    private Sport sport;

    public class Comfort {

        @SerializedName("txt")
        public String info;
    }

    public class CarWash {

        @SerializedName("txt")
        public String info;
    }

    public class Sport {

        @SerializedName("txt")
        public String info;
    }
}
