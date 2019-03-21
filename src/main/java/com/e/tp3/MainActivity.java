package com.e.tp3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    List<City> cities;
    WeatherDbHelper dbHelper;
    RecycleViewAdapter recycleViewAdapter;
    public static final String messageCity = "CITY";
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv = findViewById(R.id.my_recycler_view);

        dbHelper = new WeatherDbHelper(this);
        dbHelper.populate();
        cities = dbHelper.getAllCities();

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recycleViewAdapter= new RecycleViewAdapter(cities);
        TouchInItemHelper();
        rv.setAdapter(recycleViewAdapter);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newCityActivity = new Intent(getBaseContext(), NewCityActivity.class);
                startActivityForResult(newCityActivity, 2);
            }
        });


        swipeRefreshLayout = findViewById(R.id.refreshHome);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (MainActivity.isConnected(MainActivity.this)) {
                    new WeatherTask().execute();
                }
                cities = dbHelper.getAllCities();
                recycleViewAdapter.mycities = cities;
                TouchInItemHelper();
                rv.setAdapter(recycleViewAdapter);
            }
        });

    }
    private void TouchInItemHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                City city=cities.get(position);
                int id =(int) city.getId();
                dbHelper.deleteCity(id);
                recycleViewAdapter.remove(position);
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
    }


    class RecycleViewAdapter extends RecyclerView.Adapter<RowHolder> {

        private List<City> mycities;
        City city;

        public RecycleViewAdapter(List<City> cts) {
            mycities = cts;
        }

        @Override
        public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return (new RowHolder(getLayoutInflater().inflate(R.layout.row, parent, false)));
        }

        @Override
        public void onBindViewHolder(@NonNull final RowHolder holder, int position) {
            //mycities=dbHelper.getAllCities();
            city = mycities.get(position);
            holder.bindModel(cities.get(position));
            holder.cName.setText(city.getName());
            holder.cCountry.setText(city.getCountry());

            holder.imageViewRow.setImageResource(R.drawable.icon_01d);
            holder.temperature.setText(city.getTemperature() + " °C");
            holder.city = city;
            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cityActivity = new Intent(v.getContext(), CityActivity.class);
                    Bundle bundle = new Bundle();
                    cityActivity .putExtra("CITY", holder.city);
                    ((Activity) v.getContext()).startActivityForResult(cityActivity, 1);
                    //startActivity(cityActivity);
                }
            });
            if (city.getIcon() != null && !city.getIcon().isEmpty()) {
                holder.imageViewRow.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/" + "icon_" + city.getIcon(), null, getPackageName())));
                holder.imageViewRow.setContentDescription(city.getDescription());
            }

        }

        @Override
        public int getItemCount() {
            return (cities.size());
        }
        public void remove(int position) {
            if (position < 0 || position >= mycities.size()) {
                return;
            }
            mycities.remove(position);
            notifyDataSetChanged();
        }

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
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netWorkInfo = connectivityManager.getActiveNetworkInfo();
        return netWorkInfo != null && netWorkInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1 == requestCode && RESULT_OK == resultCode) {
            cities=dbHelper.getAllCities();
            recycleViewAdapter.mycities = cities;
            recycleViewAdapter.notifyDataSetChanged();
            //recy.setAdapter(new MyRecyAdapter(cities));
            rv.setAdapter(recycleViewAdapter);
        }
        if (2 == requestCode && RESULT_OK == resultCode) {
            cities = dbHelper.getAllCities();
            recycleViewAdapter.mycities = cities;
            recycleViewAdapter.notifyDataSetChanged();
            //recy.setAdapter(new MyRecyAdapter(cities));
            rv.setAdapter(recycleViewAdapter);

        }

    }
    class WeatherTask extends AsyncTask<Object, Integer, List<City>> {
        private List<City> listOfCities;

        @Override
        protected void onPreExecute() {

            listOfCities = dbHelper.getAllCities();
            recycleViewAdapter.mycities = listOfCities;
            recycleViewAdapter.notifyDataSetChanged();
            rv.setAdapter(recycleViewAdapter);

            super.onPreExecute();
        }

        @Override
        protected List<City> doInBackground(Object... objects) {
            HttpURLConnection urlConnection = null;
            listOfCities = dbHelper.getAllCities();
            for (City city : listOfCities) {

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
                dbHelper.updateCity(city);
            }
            return listOfCities;
        }


        @Override
        protected void onPostExecute(List<City> cities) {
            super.onPostExecute(cities);

            swipeRefreshLayout.setRefreshing(false);


        }


    }

}

