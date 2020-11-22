package com.example.cst2335_graphicalinterfaceprogramming;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TicketDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TicketDetailsFragment extends Fragment {
    private Bundle dataFromActivity;
    private AppCompatActivity parentActivity;
    SQLiteDatabase db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_ticket_details, container, false);
        dataFromActivity = getArguments();
        JSONObject jsonObject = null;
        Bitmap image = null;
        super.onCreate(savedInstanceState);
        MyOpener dbOpener = new MyOpener(getContext());
        db = dbOpener.getWritableDatabase();
        try {
            jsonObject = new JSONObject(dataFromActivity.getString("JSONSTRING"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ImageView imageView=(ImageView)result.findViewById(R.id.imageView1);
        TextView textView1=(TextView)result.findViewById(R.id.starting_date1);
        TextView textView2=(TextView)result.findViewById(R.id.min1);
        TextView textView3=(TextView)result.findViewById(R.id.max1);
        TextView textView4=(TextView)result.findViewById(R.id.URL1);
        FileInputStream fis = null;
        try {
            fis = getContext().openFileInput(jsonObject.getString("imgName"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("FILE", "The icon file exists!");
        image = BitmapFactory.decodeStream(fis);
        imageView.setImageBitmap(image);
        try {
            textView1.setText("Starting date: "+jsonObject.getString("localDate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            textView2.setText("Min price: $"+jsonObject.getString("min"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            textView3.setText("Max price: $"+jsonObject.getString("max"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            textView4.setText("URL: "+jsonObject.getString("url"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Button favoriteButton = result.findViewById(R.id.favorite_button1);
        Button help2 = result.findViewById(R.id.help_button1);

        favoriteButton.setOnClickListener( click -> {
            ContentValues newRowValues = new ContentValues();
            //Log.d("sssssssss", getIntent().getStringExtra("JSONSTRING"));
            newRowValues.put(MyOpener.COL_MESSAGE, dataFromActivity.getString("JSONSTRING"));
            db.insert(MyOpener.TABLE_NAME, null, newRowValues);
        });

        help2.setOnClickListener(v ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.TicketFavoriteHelp) + ": ")
                    .setMessage(getResources().getString(R.string.TicketFavoriteSaveAs))
                    .setNeutralButton(getResources().getString(R.string.ticketAlertNB1), (click, b) -> { })
                    .create().show();});
        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }
}