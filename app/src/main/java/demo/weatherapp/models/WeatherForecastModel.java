package demo.weatherapp.models;

import java.util.List;

public class WeatherForecastModel {

    private City city;
    private List<TodayWeather> listWeatherData;



    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<TodayWeather> getListWeatherData() {
        return listWeatherData;
    }

    public void setListWeatherData(List<TodayWeather> listWeatherData) {
        this.listWeatherData = listWeatherData;
    }
}
