package demo.weatherapp.mvvm.repositories;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import demo.weatherapp.R;
import demo.weatherapp.models.City;
import demo.weatherapp.models.TodayWeather;
import demo.weatherapp.models.Weather;
import demo.weatherapp.models.WeatherForecastModel;
import demo.weatherapp.utils.Utils;

public class WeatherForecastRepository {
    private RequestQueue mRequestQueue;
    private MutableLiveData<WeatherForecastModel> forecastModelMutableLiveData = new MutableLiveData<>();
    private WeatherForecastModel weatherForecastModel = new WeatherForecastModel();


    @Inject
    public WeatherForecastRepository() {
    }

    public MutableLiveData<WeatherForecastModel> getForecastModelMutableLiveData() {
        return forecastModelMutableLiveData;
    }


    public MutableLiveData<WeatherForecastModel> fetchWeatherInfo(Context context, double latitude, double longitude) {
        String url = Utils.getBaseUrl() + "forecast?" + "lat=" + latitude + "&lon=" + longitude + "&APPID=" + context.getResources().getString(R.string.weather_api_key) + "&units=metric";
        Log.d("URL", url);
        mRequestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.saveWeatherData(context, response.toString());
                        parseData(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });

// Access the RequestQueue through your singleton class.
        mRequestQueue.add(jsonObjectRequest);

        return forecastModelMutableLiveData;
    }


    public void parseData(JSONObject response) {
        JSONObject cityObject = response.optJSONObject("city");
        City city = new City();
        city.setName(cityObject.optString("name"));
        city.setCountry(cityObject.optString("country"));
        city.setPopulation(cityObject.optLong("population"));
        city.setLatitude(cityObject.optJSONObject("coord").optDouble("lat"));
        city.setLongitude(cityObject.optJSONObject("coord").optDouble("lon"));

        JSONArray arrayJson = response.optJSONArray("list");
        List<TodayWeather> todayWeatherList = new ArrayList<>();
        if (arrayJson != null) {
            for (int i = 0; i < arrayJson.length(); i++) {
                TodayWeather todayWeather = new TodayWeather();
                JSONObject jsonObject = arrayJson.optJSONObject(i);

                todayWeather.setDateText(jsonObject.optString("dt_txt"));
                todayWeather.setCloudsAll(jsonObject.optJSONObject("clouds").optString("all"));
                todayWeather.setDateMillis(jsonObject.optLong("dt"));
                todayWeather.setVisibility(jsonObject.optInt("visibility"));

                todayWeather.setWindDegree(jsonObject.optJSONObject("wind").optString("deg"));
                todayWeather.setWindSpeed(jsonObject.optJSONObject("wind").optString("speed"));

                JSONObject rainObject=jsonObject.optJSONObject("rain");
                if(rainObject!=null){
                    Iterator<String> keys = rainObject.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                      todayWeather.setRainHours(key);
                      todayWeather.setRainPercipitation(rainObject.optDouble(key));
                    }
                }


                JSONObject mainObject = jsonObject.optJSONObject("main");

                todayWeather.setMainGroundLevel(mainObject.optString("grnd_level"));
                todayWeather.setMainHumidity(mainObject.optString("humidity"));
                todayWeather.setMainPressure(mainObject.optString("pressure"));
                todayWeather.setMainSeaLevel(mainObject.optString("sea_level"));
                todayWeather.setMainTemperature(String.valueOf(Math.round(Double.parseDouble(mainObject.optString("temp")))));
                todayWeather.setMainTempMax(String.valueOf(Math.round(Double.parseDouble(mainObject.optString("temp_max")))));
                todayWeather.setMainTempMin(String.valueOf(Math.round(Double.parseDouble(mainObject.optString("temp_min")))));


                JSONArray weatherArray = jsonObject.optJSONArray("weather");
                List<Weather> weatherList = new ArrayList<>();
                if (weatherArray != null && weatherArray.length() > 0) {
                    for (int j = 0; j < weatherArray.length(); j++) {
                        JSONObject jo = weatherArray.optJSONObject(0);
                        Weather weather = new Weather();
                        weather.setWeatherDescription(jo.optString("description"));
                        weather.setWeatherIcon(jo.optString("icon"));
                        weather.setWeatherID(jo.optString("id"));
                        weather.setWeatherMain(jo.optString("main"));
                        weatherList.add(weather);
                    }
                }
                todayWeather.setWeatherList(weatherList);
                todayWeatherList.add(todayWeather);
            }
        }
        weatherForecastModel.setCity(city);
        weatherForecastModel.setListWeatherData(todayWeatherList);
        forecastModelMutableLiveData.setValue(weatherForecastModel);
    }


    public MutableLiveData<WeatherForecastModel> parseAndShowOldData(Context context) throws JSONException {
        parseData(Utils.getWeatherData(context));
        return forecastModelMutableLiveData;
    }
}
