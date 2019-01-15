package com.sam.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.sam.coolweather.gson.Weather;
import com.sam.coolweather.utils.HttpUtil;
import com.sam.coolweather.utils.JsonUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AutoUpdateService extends Service {

    private static final String BASE_URL = "http://guolin.tech/api/weather";
    private static final String APP_KEY = "bc0418b57b2d4918819d3974ac1285d9";
    private static final String PIC_URL = "http://guolin.tech/api/bing_pic";
    private SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updateWeather();
        updatePic();
        autoUpdateTask();

        return super.onStartCommand(intent, flags, startId);
    }

    private void autoUpdateTask() {
        // 此Service一直运行在后台，每隔8小时自动更新天气数据
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 8 * 60 * 60 * 1000;  // 8小时的毫秒数
        long triggerTime = SystemClock.elapsedRealtime() + time;
        Intent intent = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
        if (manager != null) {
            manager.cancel(pi);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        }
    }


    private void updateWeather() {
        String weatherJson = mPreferences.getString("weather", null);
        if (weatherJson != null) {
            Weather weather = JsonUtil.handleWeatherResponse(weatherJson);
            if (weather != null) {
                final String weatherId = weather.basic.weatherId;
                String url = BASE_URL + "?cityid=" + weatherId + "&key=" + APP_KEY;
                // 请求网络更新数据
                HttpUtil.sendOkHttpRequest(url, new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ResponseBody body = response.body();
                        if (body != null) {
                            String weatherJson = body.string();
                            Weather weather = JsonUtil.handleWeatherResponse(weatherJson);
                            if (weather != null && "ok".equals(weather.status)) {
                                // 缓存天气数据
                                mPreferences.edit().putString("weather", weatherJson).apply();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
                });
            }

        }
    }

    private void updatePic() {
        HttpUtil.sendOkHttpRequest(PIC_URL, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    String picUrl = body.string();
                    mPreferences.edit().putString("bing_pic", picUrl).apply();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
