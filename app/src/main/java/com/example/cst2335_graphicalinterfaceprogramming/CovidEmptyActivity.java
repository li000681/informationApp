package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * The class is used to demonstrate Fragment on phone
 *  @author June Li
 * @version 1.0
 */
public class CovidEmptyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_empty);

        Bundle dataToPass = getIntent().getBundleExtra("BUNDLE");
        CovidDetailsFragment dFragment = new CovidDetailsFragment();
        dFragment.setArguments( dataToPass ); //pass data to the the
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, dFragment).commit();
    }
}