package com.e.tp3;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class NewCityActivity extends AppCompatActivity {

    private EditText textName, textCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_city);

        textName = (EditText) findViewById(R.id.editNewName);
        textCountry = (EditText) findViewById(R.id.editNewCountry);


        final Button sauvergarderBtn = (Button) findViewById(R.id.button);

        sauvergarderBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                City newCity = new City();
                newCity.setName(textName.getText().toString());
                newCity.setCountry(textCountry.getText().toString());


                Intent mainActivity = new Intent(getBaseContext(),MainActivity.class);
                startActivity(mainActivity);
            }
        });
    }
    public void showMessage(String title , String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


}
