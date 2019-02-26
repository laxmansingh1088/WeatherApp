package demo.weatherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import demo.weatherapp.R;
import demo.weatherapp.models.TodayWeather;
import demo.weatherapp.models.Weather;
import demo.weatherapp.models.WeatherForecastModel;

public class DayWeatherDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<TodayWeather> mList;


    public DayWeatherDataAdapter(Context context) {
        this.mContext = context;
    }

    public void setList(List<TodayWeather> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.temperature_data_list_item, parent, false);
        TemperatureDataViewHolder viewHolder = new TemperatureDataViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof TemperatureDataViewHolder) {
            TodayWeather todayWeather = mList.get(position);

            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
            Date date1 = null;
            try {
                date1 = format1.parse(todayWeather.getDateText());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String time = format2.format(date1);

            ((TemperatureDataViewHolder) holder).txt_time.setText(time);
            ((TemperatureDataViewHolder) holder).txt_temperature.setText(todayWeather.getMainTemperature() + "\u00B0");

            if (todayWeather.getWeatherList() != null && todayWeather.getWeatherList().size() > 0) {
                Weather weather = todayWeather.getWeatherList().get(0);
                Glide.with(mContext)
                        .load("http://openweathermap.org/img/w/" + weather.getWeatherIcon() + ".png")
                        .apply(new RequestOptions().downsample(DownsampleStrategy.AT_MOST).diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(((TemperatureDataViewHolder) holder).img_icon);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    public class TemperatureDataViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_time, txt_temperature;
        private ImageView img_icon;

        public TemperatureDataViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_time = itemView.findViewById(R.id.txt_time);
            txt_temperature = itemView.findViewById(R.id.txt_temperature);
            img_icon = itemView.findViewById(R.id.img_icon);
        }
    }


}
