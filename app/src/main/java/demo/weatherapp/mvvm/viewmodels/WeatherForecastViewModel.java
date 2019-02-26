package demo.weatherapp.mvvm.viewmodels;

import android.content.Context;

import org.json.JSONException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import demo.weatherapp.daggers.components.DaggerWeatherForecastComponent;
import demo.weatherapp.daggers.components.WeatherForecastComponent;
import demo.weatherapp.models.WeatherForecastModel;
import demo.weatherapp.mvvm.repositories.WeatherForecastRepository;
import demo.weatherapp.utils.Utils;

public class WeatherForecastViewModel extends ViewModel {

    private MutableLiveData<WeatherForecastModel> mPiListMutableLiveData;
    private WeatherForecastRepository mWeatherForecastRepository;

    public WeatherForecastViewModel() {
        if (mWeatherForecastRepository == null) {
            WeatherForecastComponent weatherForecastComponent = DaggerWeatherForecastComponent.create();
            mWeatherForecastRepository = weatherForecastComponent.getWeatherForecastRepository();
        }
    }

    public LiveData<WeatherForecastModel> getWeatherForecastModelLiveData() {
        if (mPiListMutableLiveData == null) {
            mPiListMutableLiveData = mWeatherForecastRepository.getForecastModelMutableLiveData();
        }
        return mPiListMutableLiveData;
    }

    public void fetchWeatherInformation(Context context, double latitude, double longitude) throws JSONException {
        mPiListMutableLiveData = mWeatherForecastRepository.fetchWeatherInfo(context, latitude, longitude);
    }

    public void fetchOldData(Context context) throws JSONException {
        if(Utils.getWeatherData(context)!=null){
            mPiListMutableLiveData=mWeatherForecastRepository.parseAndShowOldData(context);
        }
    }
}
