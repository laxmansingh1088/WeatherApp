package demo.weatherapp.models;

import java.util.List;

public class TodayWeather {


    private List<Weather> weatherList;
    private String mainTemperature;
    private String mainPressure;
    private String mainHumidity;
    private String mainTempMin;
    private String mainTempMax;
    private String mainSeaLevel;
    private String mainGroundLevel;
    private String windSpeed;
    private String windDegree;
    private String cloudsAll;
    private Long dateMillis;
    private int visibility;
    private String dateText;
    private double rainPercipitation;
    private String rainHours;


    public String getMainTemperature() {
        return mainTemperature;
    }

    public void setMainTemperature(String mainTemperature) {
        this.mainTemperature = mainTemperature;
    }

    public String getMainPressure() {
        return mainPressure;
    }

    public void setMainPressure(String mainPressure) {
        this.mainPressure = mainPressure;
    }

    public String getMainHumidity() {
        return mainHumidity;
    }

    public void setMainHumidity(String mainHumidity) {
        this.mainHumidity = mainHumidity;
    }

    public String getMainTempMin() {
        return mainTempMin;
    }

    public void setMainTempMin(String mainTempMin) {
        this.mainTempMin = mainTempMin;
    }

    public String getMainTempMax() {
        return mainTempMax;
    }

    public void setMainTempMax(String mainTempMax) {
        this.mainTempMax = mainTempMax;
    }

    public String getMainSeaLevel() {
        return mainSeaLevel;
    }

    public void setMainSeaLevel(String mainSeaLevel) {
        this.mainSeaLevel = mainSeaLevel;
    }

    public String getMainGroundLevel() {
        return mainGroundLevel;
    }

    public void setMainGroundLevel(String mainGroundLevel) {
        this.mainGroundLevel = mainGroundLevel;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDegree() {
        return windDegree;
    }

    public void setWindDegree(String windDegree) {
        this.windDegree = windDegree;
    }

    public String getCloudsAll() {
        return cloudsAll;
    }

    public void setCloudsAll(String cloudsAll) {
        this.cloudsAll = cloudsAll;
    }

    public Long getDateMillis() {
        return dateMillis;
    }

    public void setDateMillis(Long dateMillis) {
        this.dateMillis = dateMillis;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public List<Weather> getWeatherList() {
        return weatherList;
    }

    public void setWeatherList(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
    }

    public double getRainPercipitation() {
        return rainPercipitation;
    }

    public void setRainPercipitation(double rainPercipitation) {
        this.rainPercipitation = rainPercipitation;
    }

    public String getRainHours() {
        return rainHours;
    }

    public void setRainHours(String rainHours) {
        this.rainHours = rainHours;
    }
}
