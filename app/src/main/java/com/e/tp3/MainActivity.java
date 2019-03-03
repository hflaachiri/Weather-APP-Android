package com.e.tp3;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView cities = findViewById(R.id.cityList);

        WeatherDbHelper dbHelper = new WeatherDbHelper(this);
        dbHelper.populate();
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
                 android.R.layout.simple_list_item_2,
                 dbHelper.fetchAllCities () ,
                 new String[] { WeatherDbHelper.COLUMN_CITY_NAME, WeatherDbHelper.COLUMN_COUNTRY },
                 new int[] { android.R.id.text1, android.R.id.text2}
                 );

        cities.setAdapter(cursorAdapter);

        cities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursorCity = (Cursor) parent.getItemAtPosition(position);
                City city = WeatherDbHelper.cursorToCity(cursorCity);
                Intent cityActivity = new Intent(getBaseContext(), CityActivity.class);
                cityActivity.putExtra("City", city);
                startActivity(cityActivity);
                finish();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newCityActivity = new Intent(getBaseContext(), NewCityActivity.class);
                startActivity(newCityActivity);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
