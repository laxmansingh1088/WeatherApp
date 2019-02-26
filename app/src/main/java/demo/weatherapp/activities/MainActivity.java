package demo.weatherapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import demo.weatherapp.R;
import demo.weatherapp.adapters.DayWeatherDataAdapter;
import demo.weatherapp.models.TodayWeather;
import demo.weatherapp.mvvm.viewmodels.WeatherForecastViewModel;
import demo.weatherapp.utils.Utils;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Toolbar toolbar;
    private RequestQueue mRequestQueue;
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private TextView temp_now, temp_min_max, percipation_probability_val, percipation_val, wind_val, perceived_temp_val, humidity_val, visibility_val, uv_val, pressure_val;
    private NestedScrollView scrollView;
    private WeatherForecastViewModel viewModel;
    private RelativeLayout topLyt;
    private RecyclerView recylerview;
    private DayWeatherDataAdapter adapter;
    private LineChartView lineChartView;
    private BroadcastReceiver networkChangeReceiver;

    ProgressDialog progressdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        topLyt = findViewById(R.id.topLyt);
        scrollView = findViewById(R.id.scrollView);
        temp_now = findViewById(R.id.temp_now);
        temp_min_max = findViewById(R.id.temp_min_max);
        toolbar.setNavigationIcon(R.drawable.place);

        percipation_probability_val = findViewById(R.id.percipation_probability_val);
        percipation_val = findViewById(R.id.percipation_val);
        wind_val = findViewById(R.id.wind_val);
        perceived_temp_val = findViewById(R.id.perceived_temp_val);
        humidity_val = findViewById(R.id.humidity_val);
        visibility_val = findViewById(R.id.visibility_val);
        uv_val = findViewById(R.id.uv_val);
        pressure_val = findViewById(R.id.pressure_val);

        recylerview = findViewById(R.id.recylerview);
        recylerview.setHasFixedSize(true);
        recylerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new DayWeatherDataAdapter(MainActivity.this);
        recylerview.setAdapter(adapter);


        lineChartView = (LineChartView) findViewById(R.id.graph);
        setLineChartSettings();
        setUpGClient();


        if (!Utils.isNetworkAvailable(this)) {
            networkChangeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (Utils.isNetworkAvailable(context))
                        setUpGClient();
                    checkPermissions();
                }
            };
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(networkChangeReceiver, intentFilter);
        }


        viewModel = ViewModelProviders.of(this).get(WeatherForecastViewModel.class);
        try {
            viewModel.fetchOldData(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        viewModel.getWeatherForecastModelLiveData().observe(this, WeatherForecastModel -> {
            cancelProgressDialog();
            if (viewModel.getWeatherForecastModelLiveData().getValue() == null) {
                Toast.makeText(MainActivity.this, "null data", Toast.LENGTH_LONG).show();
            } else {
                setUiData();
                //   if (viewModel.getWeatherForecastModelLiveData().getValue().getCity() != null)
                //      Toast.makeText(MainActivity.this, viewModel.getWeatherForecastModelLiveData().getValue().getCity().getName().toString(), Toast.LENGTH_LONG).show();
            }
        });

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                float alpha = (float) scrollView.getScrollY() / topLyt.getHeight();
                toolbar.setBackgroundColor(Utils.getColorWithAlpha(alpha, getResources().getColor(R.color.blue)));
            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuitems, menu);
        return true;
    }

    public void setUiData() {


        if (viewModel.getWeatherForecastModelLiveData().getValue().getListWeatherData() != null && viewModel.getWeatherForecastModelLiveData().getValue().getListWeatherData().size() > 0) {
            TodayWeather todayWeather = viewModel.getWeatherForecastModelLiveData().getValue().getListWeatherData().get(0);
            setGraph(viewModel.getWeatherForecastModelLiveData().getValue().getListWeatherData());
            temp_now.setText(todayWeather.getMainTemperature() + "\u00B0");
            temp_min_max.setText(todayWeather.getWeatherList().get(0).getWeatherMain() + "  " + todayWeather.getMainTempMax() + " / " + todayWeather.getMainTempMin() + " \u2103");
            humidity_val.setText(todayWeather.getMainHumidity() + "%");
            pressure_val.setText(String.valueOf(Math.round(Double.parseDouble(todayWeather.getMainPressure()))) + "hPa");
            wind_val.setText(String.valueOf(Math.round(Double.parseDouble(todayWeather.getWindSpeed()))) + "km/h");
            perceived_temp_val.setText(todayWeather.getMainTempMax() + "\u00B0");
            if (todayWeather.getRainHours() != null) {
                percipation_val.setText(String.valueOf(todayWeather.getRainPercipitation()) + "mm");
            }

            lineChartView.setVisibility(View.VISIBLE);
            adapter.setList(viewModel.getWeatherForecastModelLiveData().getValue().getListWeatherData());
            adapter.notifyDataSetChanged();

        }


        if (viewModel.getWeatherForecastModelLiveData().getValue().getCity() != null) {
            toolbar.setTitle(viewModel.getWeatherForecastModelLiveData().getValue().getCity().getName());
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            toolbar.setSubtitle("just Updated");
            toolbar.setSubtitleTextColor(getResources().getColor(R.color.white_50));
        }
    }


    private void setLineChartSettings() {
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.setValueTouchEnabled(false);
        lineChartView.setZoomEnabled(true);
        lineChartView.setScrollEnabled(true);
        lineChartView.setInteractive(true);
        lineChartView.setZoomType(ZoomType.HORIZONTAL);
        lineChartView.setMaxZoom((float) 8);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

    }

    private synchronized void setUpGClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, 0, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            if (!googleApiClient.isConnected())
                googleApiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            Toast.makeText(MainActivity.this, mylocation.getLatitude() + " \n " + mylocation.getLongitude(), Toast.LENGTH_LONG).show();
            try {
                if (Utils.isNetworkAvailable(MainActivity.this)) {
                    if (Utils.getWeatherData(MainActivity.this) == null) {
                        showProgress();
                    }
                    viewModel.fetchWeatherInformation(MainActivity.this, mylocation.getLatitude(), mylocation.getLongitude());

                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.pls_check_internet), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Or Do whatever you want withṇ your location
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Do whatever you need
        //You can display a message here
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    // Acquire a reference to the system Location Manager
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(MainActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(MainActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }


    public void setGraph(List<TodayWeather> list) {
        List yMaxAxisValues = new ArrayList();
        List yMinAxisValues = new ArrayList();
        List axisValues = new ArrayList();


        Line line = new Line(yMaxAxisValues).setColor(Color.parseColor("#861E9F")).setHasLabels(true);
        Line line1 = new Line(yMinAxisValues).setColor(Color.parseColor("#861E9F")).setHasLabels(true);
        line.setFilled(true);
        line1.setFilled(true);

        for (int i = 0; i < list.size(); i++) {
            axisValues.add(i, new AxisValue(i).setLabel(i + ""));
            yMaxAxisValues.add(new PointValue(i, (int) Math.round(Double.parseDouble(list.get(i).getMainTempMax()))));
            yMinAxisValues.add(new PointValue(i, (int) Math.round(Double.parseDouble(list.get(i).getMainTempMin())) - 10));
        }


        List lines = new ArrayList();
        lines.add(line);
        lines.add(line1);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#03A9F4"));
//        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName("Sales in millions");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
//        data.setAxisYLeft(yAxis);

        lineChartView.setLineChartData(data);
        // Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        // lineChartView.setMaximumViewport(viewport);
        // lineChartView.setCurrentViewport(viewport);
        lineChartView.setZoomLevel(8, 0, 6);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (networkChangeReceiver != null)
            unregisterReceiver(networkChangeReceiver);
    }


    private void showProgress() {
        if (progressdialog == null) {
            progressdialog = new ProgressDialog(this);
            progressdialog.setMessage("Please Wait....");
            progressdialog.setCancelable(false);
            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.show();
        }
    }

    private void cancelProgressDialog() {
        if (progressdialog != null) {
            progressdialog.cancel();
            progressdialog = null;
        }
    }

}
