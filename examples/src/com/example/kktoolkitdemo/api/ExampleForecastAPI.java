package com.example.kktoolkitdemo.api;

import com.kkbox.toolkit.api.KKAPIBase;
import com.kkbox.toolkit.api.KKAPIRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gigichien on 13/10/24.
 */
public class ExampleForecastAPI extends KKAPIBase {
    ArrayList<Float> mForecast;
    private KKAPIRequest mRequest = null;
    @Override
    protected int parse(String data) {
        if(mForecast == null){
            mForecast = new ArrayList<Float>();
        } else {
            mForecast.clear();
        }
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jobWeather = jsonObject.getJSONArray("list");
            for(int i = 0; i < jobWeather.length(); i++)
            {
                JSONObject jsonForecast = jobWeather.getJSONObject(i).getJSONObject("temp");
                String temp = jsonForecast.getString("day");
                mForecast.add((float)(Float.parseFloat(temp) - 273.15));
            }
        } catch (JSONException e) {
            return ErrorCode.INVALID_API_FORMAT;
        }
        return ErrorCode.NO_ERROR;
    }

    public void start(){
        String inputURL = "http://api.openweathermap.org/data/2.5/forecast/daily?id=2643743";
        mRequest = new KKAPIRequest(inputURL, null);
        if(mRequest != null) {
            execute(mRequest);
        }
    }

    public ArrayList<Float> getForecastData(){
        return mForecast;
    }

}
