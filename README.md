# coolweather：完整天气应用

说明：
 - 全国省市县数据API：http://guolin.tech/api/china/
 - 查询某个省的所有城市API：http://guolin.tech/api/china/provinceId
 - 查询某个城市的所有县区API：http://guolin.tech/api/china/provinceId/cityId
 - AppKey：bc0418b57b2d4918819d3974ac1285d9
 - 获取任意城市的天气信息：http://guolin.tech/api/weather?cityid=CityId&key=AppKey
 eg: http://guolin.tech/api/weather?cityid=CN101190401&key=bc0418b57b2d4918819d3974ac1285d9

- 使用第三方库：
	com.squareup.okhttp3:okhttp:3.12.1
	com.github.bumptech.glide:glide:4.8.0
	com.google.code.gson:gson:2.8.5
	org.litepal.android:core:1.3.2


编写思路：
- 准备后台API数据
- 将代码关联到GitHub上，在GitHub上创建CoolWeather仓库，并克隆到本地
- 添加第三方依赖库
- 配置litepal数据库依赖，配置litepal.xml文件，配置LitePalApplication
- 准备数据实体类
