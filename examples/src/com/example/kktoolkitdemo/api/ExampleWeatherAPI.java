package com.example.kktoolkitdemo.api;

import com.kkbox.toolkit.api.KKAPIBase;
import com.kkbox.toolkit.api.KKAPIRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExampleWeatherAPI extends KKAPIBase{
    private WeatherData mWeatherData;
    private String mResponseData = null;
    private KKAPIRequest mRequest = null;
    private String mCityName = null;
    @Override
    protected int parse(String data) {
        mResponseData = data;
        if (mWeatherData == null) {
            mWeatherData = new WeatherData();
        }
        try {
            JSONObject jsonObject = new JSONObject(data);
            String name = jsonObject.getString("name");
            JSONArray jobWeather = jsonObject.getJSONArray("weather");
            String main = jobWeather.getJSONObject(0).getString("main");
            String description = jobWeather.getJSONObject(0).getString("description");
            String temp = jsonObject.getJSONObject("main").getString("temp");
            String temp_min = jsonObject.getJSONObject("main").getString("temp_min");
            String temp_max = jsonObject.getJSONObject("main").getString("temp_max");
            mWeatherData.city_name = name;
            mWeatherData.main = main;
            mWeatherData.description = description;
            mWeatherData.temp = (float) (Float.parseFloat(temp) - 273.15);
            mWeatherData.min_temp = (float) (Float.parseFloat(temp_min) - 273.15);
            mWeatherData.max_temp = (float) (Float.parseFloat(temp_max) - 273.15);

        } catch (JSONException e) {
            return ErrorCode.INVALID_API_FORMAT;
        }
        return ErrorCode.NO_ERROR;
    }

    public void start(String cityName){

        String inputURL = "http://api.openweathermap.org/data/2.5/weather?q=" + cityName;
        mRequest = new KKAPIRequest(inputURL, null);

        execute(mRequest);

        mCityName = cityName;
    }

    public String getResponseData(){
        return mResponseData;
    }

    public WeatherData getWeatherData(){
        return mWeatherData;
    }

    public String getCityName(){
        return mCityName;
    }

    public class WeatherData {
        public String city_name = null;
        public String main = null;
        public String description = null;
        public float temp = 0;
        public float min_temp = 0;
        public float max_temp = 0;
    }

}
