package com.sam.coolweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * # 天气应用：完整应用实例
 说明：
 - 全国省市县数据API：http://guolin.tech/api/china/
 - 查询某个省的所有城市API：http://guolin.tech/api/china/provinceId
 - 查询某个城市的所有县区API：http://guolin.tech/api/china/provinceId/cityId
 - AppKey：bc0418b57b2d4918819d3974ac1285d9
 - 获取任意城市的天气信息：http://guolin.tech/api/weather?cityid=CityId&key=AppKey
 eg: http://guolin.tech/api/weather?cityid=CN101190401&key=bc0418b57b2d4918819d3974ac1285d9
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
