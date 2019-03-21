package com.e.tp3;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class CityActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = CityActivity.class.getSimpleName();
    private TextView textCityName, textCountry, textTemperature, textHumdity, textWind, textCloudiness, textLastUpdate;
    private ImageView imageWeatherCondition;
    private City city;
    WeatherDbHelper dbHelper;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        Intent in= getIntent();
        dbHelper=new WeatherDbHelper(this);
        Bundle extras = getIntent().getExtras();
        city = (City)extras.get("CITY");

        textCityName = (TextView) findViewById(R.id.nameCity);
        textCountry = (TextView) findViewById(R.id.country);
        textTemperature = (TextView) findViewById(R.id.editTemperature);
        textHumdity = (TextView) findViewById(R.id.editHumidity);
        textWind = (TextView) findViewById(R.id.editWind);
        textCloudiness = (TextView) findViewById(R.id.editCloudiness);
        textLastUpdate = (TextView) findViewById(R.id.editLastUpdate);
        imageWeatherCondition = (ImageView) findViewById(R.id.imageView);



        updateView();

        final Button mettreAJour = (Button) findViewById(R.id.button);

        mettreAJour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MainActivity.isConnected(CityActivity.this)) {
                    swipeRefreshLayout.setRefreshing(true);
                    new UpdateCityWheather().execute();

                }

                Intent mainActivity = new Intent(getBaseContext(), MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        });

        swipeRefreshLayout = findViewById(R.id.refreshCity);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onBackPressed() {
        //TODO : prepare result for the main activity
        Intent mainActivity = new Intent();
        mainActivity.putExtra(MainActivity.messageCity, city);
        setResult(Activity.RESULT_OK, mainActivity);
        finish();

        super.onBackPressed();
    }

    private void updateView() {

        textCityName.setText(city.getName());
        textCountry.setText(city.getCountry());
        textTemperature.setText(city.getTemperature()+" °C");
        textHumdity.setText(city.getHumidity()+" %");
        textWind.setText(city.getFullWind());
        textCloudiness.setText(city.getHumidity()+" %");
        textLastUpdate.setText(city.getLastUpdate());

        if (city.getIcon()!=null && !city.getIcon().isEmpty()) {
            Log.d(TAG,"icon="+"icon_" + city.getIcon());
            imageWeatherCondition.setImageDrawable(getResources().getDrawable(getResources()
                    .getIdentifier("@drawable/"+"icon_" + city.getIcon(), null, getPackageName())));
            imageWeatherCondition.setContentDescription(city.getDescription());
        }

    }

    @Override
    public void onRefresh() {
        if (MainActivity.isConnected(CityActivity.this)) {
            new UpdateCityWheather().execute();
        }

    }



    class UpdateCityWheather extends AsyncTask<Object, Integer, City> {
        @Override
        protected City doInBackground(Object... objects) {

            HttpURLConnection urlConnection = null;
            try {
                // prepare url
                URL urlToRequest = WebServiceUrl.build(city.getName(), city.getCountry());
                // send a GET request to the serve
                urlConnection = (HttpURLConnection) urlToRequest.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // read data
                InputStream inputStream = urlConnection.getInputStream();
                JSONResponseHandler jsonHandler = new JSONResponseHandler(city);
                jsonHandler.readJsonStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return city;
        }

        @Override
        protected void onPostExecute(City city) {
            updateView();
            dbHelper.updateCity(city);
            swipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(city);

        }
    }


}
