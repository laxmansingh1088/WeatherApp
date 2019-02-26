package demo.weatherapp.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

    public static String getBaseUrl() {
        return "http://api.openweathermap.org/data/2.5/";
    }

    public static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }


    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("WeatherInfo", Context.MODE_PRIVATE);
    }

    public static void saveWeatherData(Context context, String data) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("data", data);
        editor.commit();
    }

    public static JSONObject getWeatherData(Context context) throws JSONException {
        SharedPreferences sharedpreferences = getSharedPreferences(context);
        return sharedpreferences.getString("data", null) == null ? null : new JSONObject(sharedpreferences.getString("data", null));
    }


    public static String getWindDirection(double degree) {
        if (degree > 337.5) return "Northerly";
        if (degree > 292.5) return "North Westerly";
        if (degree > 247.5) return "Westerly";
        if (degree > 202.5) return "South Westerly";
        if (degree > 157.5) return "Southerly";
        if (degree > 122.5) return "South Easterly";
        if (degree > 67.5) return "Easterly";
        if (degree > 22.5) return "North Easterly";
        return "Northerly";
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
