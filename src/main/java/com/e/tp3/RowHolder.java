package com.e.tp3;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class RowHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    City city;
    TextView cName ;
    TextView cCountry ;
    TextView temperature ;
    ImageView imageViewRow ;
    ConstraintLayout constraintLayout;

    public RowHolder(View row) {
        super(row);

        cName = (TextView)row.findViewById(R.id.cName);
        cCountry = (TextView)row.findViewById(R.id.cCountry);
        temperature = (TextView)row.findViewById(R.id.temperature);
        imageViewRow = (ImageView)row.findViewById(R.id.imageViewRow);
        constraintLayout = (ConstraintLayout)row.findViewById(R.id.rowrv);
    }


    @Override
    public void onClick(View v) {

    }

    void bindModel(City city){

    }
}
