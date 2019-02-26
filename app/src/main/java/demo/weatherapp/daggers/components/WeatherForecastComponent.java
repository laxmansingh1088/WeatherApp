package demo.weatherapp.daggers.components;

import dagger.Component;
import demo.weatherapp.mvvm.repositories.WeatherForecastRepository;

@Component
public interface WeatherForecastComponent {
    WeatherForecastRepository getWeatherForecastRepository();
}
