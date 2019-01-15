package com.sam.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sam.coolweather.gson.Forecast;
import com.sam.coolweather.gson.Weather;
import com.sam.coolweather.service.AutoUpdateService;
import com.sam.coolweather.utils.HttpUtil;
import com.sam.coolweather.utils.JsonUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "samuel";
    private static final String BASE_URL = "http://guolin.tech/api/weather";
    private static final String PIC_URL = "http://guolin.tech/api/bing_pic";
    private static final String APP_KEY = "bc0418b57b2d4918819d3974ac1285d9";

    // Title
    private TextView mTitleCity;
    private TextView mUpdateTime;
    // Now
    private TextView mNowTemperature;
    private TextView mNowInfo;
    // Aqi
    private TextView mAqi;
    private TextView mPm25;
    // Suggestion
    private TextView mSuggestionComfort;
    private TextView mSuggestionCarWash;
    private TextView mSuggestionSport;
    // Forecast
    private LinearLayout mForecastLayout;
    private TextView mForecastDate;
    private TextView mForecastInfo;
    private TextView mForecastMax;
    private TextView mForecastMin;

    private ImageView mBackground;
    private ScrollView mScrollView;
    public SwipeRefreshLayout mRefreshLayout;
    private Button mNavHome;
    public DrawerLayout mDrawerLayout;

    private SharedPreferences mPreferences;
    private String mWeatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initView();
        queryPic();
        queryWeatherData();
        refresh();
    }

    private void initView() {
        // 设置Android5.0以上系统的沉浸式状态栏(实现背景图和状态栏融合在一起的效果)
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);   // 设置状态栏颜色为透明
        }
        // 组件初始化
        mTitleCity = findViewById(R.id.tv_title_city);
        mUpdateTime = findViewById(R.id.tv_update_time);
        mNowTemperature = findViewById(R.id.tv_now_temperature);
        mNowInfo = findViewById(R.id.tv_now_info);
        mAqi = findViewById(R.id.tv_aqi);
        mPm25 = findViewById(R.id.tv_pm25);
        mSuggestionComfort = findViewById(R.id.tv_suggest_comfort);
        mSuggestionCarWash = findViewById(R.id.tv_suggest_car_wash);
        mSuggestionSport = findViewById(R.id.tv_suggest_sport);
        mForecastLayout = findViewById(R.id.ll_forecast);
        mBackground = findViewById(R.id.iv_background);
        mScrollView = findViewById(R.id.srl_weather);
        mDrawerLayout = findViewById(R.id.dl_drawer);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // 初始化SwipeRefreshLayout
        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        // 初始化滑动菜单按钮
        mNavHome = findViewById(R.id.btn_home);
        mNavHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void queryPic() {
        String bingPic = mPreferences.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(mBackground);
        } else {
            queryPicFromServer();
        }
    }

    private void queryPicFromServer() {
        HttpUtil.sendOkHttpRequest(PIC_URL, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    final String picUrl = body.string();
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putString("bing_pic", picUrl);
                    editor.apply();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(picUrl).into(mBackground);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void queryWeatherData() {
        // 检查缓存是否有天气Json数据
        String weatherJson = mPreferences.getString("weather", null);
        if (weatherJson != null) {
            // 从缓存中获取
            Weather weather = JsonUtil.handleWeatherResponse(weatherJson);
            if (weather != null) {
                mWeatherId = weather.basic.weatherId;
                showWeatherInfo(weather);
            }
        } else {
            // 没有缓存则从网络上获取
            mWeatherId = getIntent().getStringExtra("weather_id");
            mScrollView.setVisibility(View.INVISIBLE);  // 请求数据时隐藏滚动条
            queryWeatherFromServer(mWeatherId);
        }
    }

    public void queryWeatherFromServer(String weatherId) {
        String url = BASE_URL + "?cityid=" + weatherId + "&key=" + APP_KEY;
        Log.d(TAG, "Request Weather Url=" + url);
        HttpUtil.sendOkHttpRequest(url, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    final String weatherJson = body.string();
                    Log.d(TAG, "onResponse: weatherJson=" + weatherJson);
                    final Weather weather = JsonUtil.handleWeatherResponse(weatherJson);
                    Log.d(TAG, "onResponse: weather=" + weather);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (weather != null && weather.status.equals("ok")) {
                                Log.d(TAG, "Response: status=" + weather.status);
                                // 缓存到本地，方便下次直接从缓存读取
                                SharedPreferences.Editor editor = mPreferences.edit();
                                editor.putString("weather", weatherJson);
                                editor.apply();
                                // 展示天气界面
                                showWeatherInfo(weather);
                            } else {
                                Log.d(TAG, "Response: 获取天气信息失败");
                                Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                            }
                            mRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        queryPic();
    }

    private void refresh() {
        // 从网络上更新最新的天气数据
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryWeatherFromServer(mWeatherId);
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        // 解析Weather实体类的数据，并填充数据到组件中
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String temperature = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        mTitleCity.setText(cityName);
        mUpdateTime.setText(updateTime);
        mNowTemperature.setText(temperature);
        mNowInfo.setText(weatherInfo);

        mForecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            // 加载子布局显示未来几天天气信息，根据需要动态加载
            View view = LayoutInflater.from(this).inflate(R.layout.item_forecast, mForecastLayout, false);
            mForecastDate = view.findViewById(R.id.tv_forecast_date);
            mForecastInfo = view.findViewById(R.id.tv_forecast_info);
            mForecastMax = view.findViewById(R.id.tv_forecast_max);
            mForecastMin = view.findViewById(R.id.tv_forecast_min);
            mForecastDate.setText(forecast.date);
            mForecastInfo.setText(forecast.more.info);
            mForecastMax.setText(forecast.temperature.max);
            mForecastMin.setText(forecast.temperature.min);
            mForecastLayout.addView(view);
        }

        if (weather.aqi != null) {
            String aqi = weather.aqi.city.aqi;
            String pm25 = weather.aqi.city.pm25;
            mAqi.setText(aqi);
            mPm25.setText(pm25);
        }

        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        mSuggestionComfort.setText(comfort);
        mSuggestionCarWash.setText(carWash);
        mSuggestionSport.setText(sport);

        mScrollView.setVisibility(View.VISIBLE);

        // 启动后台自动更新天气数据
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
