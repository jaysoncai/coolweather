package com.sam.coolweather.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sam.coolweather.db.City;
import com.sam.coolweather.db.County;
import com.sam.coolweather.db.Province;
import com.sam.coolweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Sam on 2019/1/14.
 * 解析服务器返回的 Json数据，并保存到本地数据库
 */
public class JsonUtil {

    private static final String TAG = "samuel";

    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject data = (JSONObject) jsonArray.get(i);
                    Province province = new Province();
                    province.setProvinceName(data.getString("name"));
                    province.setProvinceCode(data.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject data = (JSONObject) jsonArray.get(i);
                    City city = new City();
                    city.setProvinceId(provinceId);
                    city.setCityName(data.getString("name"));
                    city.setCityCode(data.getInt("id"));
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject data = (JSONObject) jsonArray.get(i);
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyName(data.getString("name"));
                    county.setWeatherId(data.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static Weather handleWeatherResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
                String weatherJson = jsonArray.get(0).toString();
                Log.d(TAG, "handleWeatherResponse: weatherJson=" + weatherJson);
                return new Gson().fromJson(weatherJson, Weather.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
