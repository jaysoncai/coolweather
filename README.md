# coolweather：完整天气应用

说明：
 - 全国省市县数据API：http://guolin.tech/api/china/
 - 查询某个省的所有城市API：http://guolin.tech/api/china/provinceId
 - 查询某个城市的所有县区API：http://guolin.tech/api/china/provinceId/cityId
 - AppKey：bc0418b57b2d4918819d3974ac1285d9
 - 获取任意城市的天气信息：http://guolin.tech/api/weather?cityid=CityId&key=AppKey
 eg: http://guolin.tech/api/weather?cityid=CN101190401&key=bc0418b57b2d4918819d3974ac1285d9
 - 获取必应每日一图的API：http://guolin.tech/api/bing_pic

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
- 准备数据实体类Province、City、County
- 编写HttpUtil工具类用于执行OkHttp请求
- 编写JsonUtil工具类用于解析服务器返回的Json数据，并保存到本地数据库
- 编写ChooseAreaFragment实现省市区县三级列表的查看功能
- 对照Json数据，编写天气GSON实体类
- 编写天气界面activity_weather：
	1. weather_title：头部标题
	2. weather_now：中部当前天气信息
	3. weather_forecast：底部未来几天天气信息
	4. item_forecast：底部未来几天天气信息的item布局
	5. weather_aqi：空气质量信息布局
	6. weather_suggestion：生活建议信息布局
	7. activity_weather：把以上子布局组合到主界面中
- 在JsonUtil工具类中添加解析天气Json数据的方法，将Json数据转化为GSON实体对象Weather
- 编写WeatherActivity逻辑，主要功能是获取天气数据并填充到相应的组件界面上
- 在ChooseAreaFragment中的点击事件中添加逻辑：判断区县的点击事件并携带weatherId跳转到WeatherActivity中
- 添加随机背景图片，访问必应每日一图API
- 简单设置沉浸式状态栏，只适配Android5.0以上系统(实现背景图和状态栏融合在一起的效果)
	```
	if (Build.VERSION.SDK_INT >= 21) {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);   // 设置状态栏颜色为透明
	}
	```
- 简单设置沉浸式状态栏问题：标题几乎与状态栏贴合在一起造成不美观
	`
	android:fitsSystemWindows="true"
	`
- 实现下拉刷新更新实时天气信息的功能：在ScrollView外层嵌套SwipeRefreshLayout
- 实现切换城市查询天气的功能：
1. 把城市界面ChooseAreaFragment作为滑动菜单嵌套在天气界面WeatherActivity中
2. 首先在标题栏添加一个导航按钮用于打开侧滑菜单
3. 在SwipeRefreshLayout外层嵌套DrawerLayout布局，该布局允许有两个子控件：第一个子控件SwipeRefreshLayout表示主屏幕显示的界面；第二个子控件Fragment表示表示滑动菜单显示的内容。注意第二个子控件要指定android:layout_gravity属性
4. 在导航按钮的点击事件中打开滑动菜单：mDrawerLayout.openDrawer(GravityCompat.START);
5. 需要在ChooseAreaFragment的点击事件中重新判断选择县区后的执行逻辑：如果是在MainActivity中则元逻辑不变；如果是在WeatherActivity中则关闭侧滑菜单、显示下拉刷新进度条、请求最新的天气数据



