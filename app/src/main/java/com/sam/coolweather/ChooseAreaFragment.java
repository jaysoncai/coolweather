package com.sam.coolweather;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.coolweather.db.City;
import com.sam.coolweather.db.County;
import com.sam.coolweather.db.Province;
import com.sam.coolweather.utils.HttpUtil;
import com.sam.coolweather.utils.JsonUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChooseAreaFragment extends Fragment {

    private static final String BASE_URL = "http://guolin.tech/api/china/";
    private static final String PROVINCE_TYPE = "province";
    private static final String CITY_TYPE = "city";
    private static final String COUNTY_TYPE = "county";

    private ListView mListView;
    private Button mBack;
    private TextView mTitle;
    private ProgressDialog mProgressDialog;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private List<String> dataList = new ArrayList<>();

    private Province selectedProvince;
    private City selectedCity;
    private String mCurrentPage = PROVINCE_TYPE;
    private ArrayAdapter mAdapter;

    public ChooseAreaFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        mListView = view.findViewById(R.id.lv_list);
        mBack = view.findViewById(R.id.btn_back);
        mTitle = view.findViewById(R.id.tv_title);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListView();
        initBackPress();
    }

    private void initListView() {
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentPage.equals(PROVINCE_TYPE)) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (mCurrentPage.equals(CITY_TYPE)) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (mCurrentPage.equals(COUNTY_TYPE)) {
                    // 携带weatherId跳转到天气展示界面，并关闭本身
                    String weatherId = countyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.mDrawerLayout.closeDrawers();
                        activity.mRefreshLayout.setRefreshing(true);
                        activity.queryWeatherFromServer(weatherId);
                    }
                }
            }
        });
        queryProvinces();
    }

    private void initBackPress() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage.equals(COUNTY_TYPE)) {
                    queryCities();
                } else if (mCurrentPage.equals(CITY_TYPE)) {
                    queryProvinces();
                }
            }
        });
    }

    public void queryProvinces() {
        mTitle.setText("中国");
        mBack.setVisibility(View.GONE);
        // 优先从数据库查找，否则从服务器查找
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentPage = PROVINCE_TYPE;
        } else {
            queryFromServer(BASE_URL, PROVINCE_TYPE);
        }
    }

    private void queryCities() {
        mTitle.setText(selectedProvince.getProvinceName());
        mBack.setVisibility(View.VISIBLE);
        int provinceId = selectedProvince.getId();

        cityList = DataSupport.where("provinceid = ?", String.valueOf(provinceId)).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentPage = CITY_TYPE;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            queryFromServer(BASE_URL + provinceCode, CITY_TYPE);
        }
    }

    private void queryCounties() {
        mTitle.setText(selectedCity.getCityName());
        mBack.setVisibility(View.VISIBLE);
        int cityId = selectedCity.getId();

        countyList = DataSupport.where("cityid = ?", String.valueOf(cityId)).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentPage = COUNTY_TYPE;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String url = BASE_URL + provinceCode + "/" + cityCode;
            queryFromServer(url, COUNTY_TYPE);
        }
    }

    private void queryFromServer(String url, final String type) {
        showProgressDialog();

        HttpUtil.sendOkHttpRequest(url, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    String json = body.string();
                    boolean result = false;
                    // 根据不同省份、城市、县区解析并存储到本地
                    switch (type) {
                        case PROVINCE_TYPE:
                            result = JsonUtil.handleProvinceResponse(json);
                            break;
                        case CITY_TYPE:
                            result = JsonUtil.handleCityResponse(json, selectedProvince.getId());
                            break;
                        case COUNTY_TYPE:
                            result = JsonUtil.handleCountyResponse(json, selectedCity.getId());
                            break;
                        default:
                    }
                    // 重新读取数据，记得切换回UI线程
                    if (result) {
                        // TODO: 关闭loading
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                if (PROVINCE_TYPE.equals(type)) {
                                    queryProvinces();
                                } else if (CITY_TYPE.equals(type)) {
                                    queryCities();
                                } else if (COUNTY_TYPE.equals(type)) {
                                    queryCounties();
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("loading...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
