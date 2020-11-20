package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLEngineResult;

public class TicketMasterActivity extends AppCompatActivity {
    private static final int SEARCH = 1;
    private static final int SAVE = 2;
    private List<JSONObject> list = new ArrayList<>();
    String city;
    String radius;
    MyListAdapter myAdapter;
    SQLiteDatabase db;
    SharedPreferences prefs = null;
    int currentButton = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_master);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        ListView myList=findViewById(R.id.the_list);
        myList.setAdapter(myAdapter = new MyListAdapter());

        EditText City = findViewById(R.id.addEditText1);
        EditText Radius = findViewById(R.id.addEditText2);
        Button searchButton = findViewById(R.id.searchButton);
        Button saveButton = findViewById(R.id.saveButton);

        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        String savedCity = prefs.getString("CITY", "");
        String savedRadius = prefs.getString("RADIUS", "");
        City.setText(savedCity);
        Radius.setText(savedRadius);

        loadDataFromDatabase();
        myAdapter.notifyDataSetChanged();

        searchButton.setOnClickListener(click -> {
            currentButton = SEARCH;
            if (( City.getText().toString().length() == 0 ) ||
                    ( Radius.getText().toString().length() == 0 )) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Error!")
                        .setMessage("Please input city and radius")
                        .setPositiveButton("OK", (click_ok, arg) -> {
                        })
                        .create().show();
            } else {
                city = City.getText().toString();
                radius = Radius.getText().toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("CITY", city);
                editor.putString("RADIUS", radius);
                editor.commit();
                ForecastQuery req = new ForecastQuery();
                list.clear();
                req.execute(city, radius);
            }
        });

        saveButton.setOnClickListener(click -> {
            currentButton = SAVE;
            list.clear();
            loadDataFromDatabase();
            myAdapter.notifyDataSetChanged();
        });

        myList.setOnItemClickListener((list1, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString("JSONSTRING", list.get(position).toString());

            Intent nextActivity = new Intent(TicketMasterActivity.this, TicketDetailsActivity.class);
            nextActivity.putExtras(dataToPass); //send data to next activity
            startActivity(nextActivity); //make the transition
        });

        myList.setOnItemLongClickListener((parent, view, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to delete this?")
                    //what the Yes button does:
                    .setPositiveButton("Yes", (click, arg) -> {
                        JSONObject removedItem = null;
                        long database_id = 0;
                        try {
                            removedItem = list.get(pos);
                            database_id = list.get(pos).getLong("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(currentButton == SAVE){
                            db.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "= ?", new String[] {Long.toString(database_id)});
                        }
                        list.remove(pos);
                        myAdapter.notifyDataSetChanged();
                        JSONObject finalRemovedItem = removedItem;
                        Snackbar.make(view,"The item has been deleted!", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                list.add(finalRemovedItem);
                                if(currentButton == SAVE){
                                    ContentValues newRowValues = new ContentValues();
                                    newRowValues.put(MyOpener.COL_MESSAGE, finalRemovedItem.toString());
                                    db.insert(MyOpener.TABLE_NAME, null, newRowValues);
                                }
                                myAdapter.notifyDataSetChanged();
                                Toast.makeText(TicketMasterActivity.this,"The Item has been recovered!",Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    })
                    //What the No button does:
                    .setNegativeButton("No", (click, arg) -> { })
                    //Show the dialog
                    .create().show();
            return true;
        } );
    }

    private void loadDataFromDatabase()
    {
        //get a database connection:
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer

        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {MyOpener.COL_ID, MyOpener.COL_MESSAGE};
        //query all the results from the database:
        Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);
        //printCursor(results, dbOpener.getVersionNum());

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int messageColumnIndex = results.getColumnIndex(MyOpener.COL_MESSAGE);
        int idColIndex = results.getColumnIndex(MyOpener.COL_ID);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            JSONObject event = null;
            try {
                event = new JSONObject(results.getString(messageColumnIndex));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                long id = results.getLong(idColIndex);
                event.put("id", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            list.add(event);
        }
    }

    private String establishConnection(String reqUrl) {
        String result=null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream response = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (MalformedURLException e) {
            Log.e("TicketMasterActivity", "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e("TicketMasterActivity", "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e("TicketMasterActivity", "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e("TicketMasterActivity", "Exception: " + e.getMessage());
        }
        return result;
    } // End establishConnection()

    private boolean downloadImage(String reqUrl, String name){
        Bitmap image = null;
        boolean result = false;
        File file = getBaseContext().getFileStreamPath(name);
        if(file.exists())
            return result;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            if(urlConnection.getResponseCode() == 200){
                image = BitmapFactory.decodeStream(urlConnection.getInputStream());
                FileOutputStream outputStream = openFileOutput(name+".jpg", Context.MODE_PRIVATE);
                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.flush();
                outputStream.close();
                Log.d("FILE", "The icon file is downloaded");
                result = true;
            }
        } catch (MalformedURLException e) {
            Log.e("TicketMasterActivity", "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e("TicketMasterActivity", "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e("TicketMasterActivity", "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e("TicketMasterActivity", "Exception: " + e.getMessage());
        }
        return result;
    }

    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        protected int size = 0;
        protected int totalElement = 0;
        protected int totalPages = 0;

        private JSONObject getPageParameter(String reqUrl) {
            JSONObject page = null;
            try {
                page = new JSONObject(establishConnection(reqUrl));
            } catch (Exception e) {
                Log.e("TicketMasterActivity", "Exception: " + e.getMessage());
            }
            try {
                size = page.getJSONObject("page").getInt("size");
                totalElement = page.getJSONObject("page").getInt("totalElements");
                totalPages = page.getJSONObject("page").getInt("totalPages");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("getPageParameter", "size: "+ size + "totalElement: "+ totalElement +"totalPages: "+totalPages);
            return page;
        }

        //Type3                      Type1
        public String doInBackground(String... args) {
            try {
                int count = 0;
                String nextPage = "";
                String url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=naG2r1v48w2ubfr6icXhrvmlWvBW8dmz&" +
                        "city=" + args[0] + "&radius=" + args[1];
                Log.d("doInBackground",url);
                //url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=naG2r1v48w2ubfr6icXhrvmlWvBW8dmz&city=ottawa&radius=2";
                JSONObject jsonObj = getPageParameter(url);
                JSONArray ticketArray = jsonObj.getJSONObject("_embedded").getJSONArray("events");
                totalElement = (totalElement < size)?totalElement:size;
                for (int k = 0; k < totalElement; k++) {
                    JSONObject price;
                    JSONObject event = ticketArray.getJSONObject(k);
                    JSONObject listItem = new JSONObject();
                    JSONArray imagesArray = event.getJSONArray("images");
                    JSONObject image = imagesArray.getJSONObject(0);
                    for(int i = 0; i < 10; i++) {
                        JSONObject img = imagesArray.getJSONObject(i);
                        if(img.getInt("width") == 1024){
                            image = img;
                            break;
                        }
                    }
                    if(event.has("priceRanges")){
                        price = event.getJSONArray("priceRanges").getJSONObject(0);
                    }
                    else
                    {
                        price = new JSONObject("{min:0, max:0, currency:'CA'}");
                    }
                    listItem.put("name", event.getString("name"));
                    listItem.put("url", event.getString("url"));
                    listItem.put("imgUrl", image.getString("url"));
                    listItem.put("city", args[0]);
                    listItem.put("localDate", event.getJSONObject("dates").getJSONObject("start").getString("localDate"));
                    listItem.put("localTime", event.getJSONObject("dates").getJSONObject("start").getString("localTime"));
                    listItem.put("min", price.getString("min"));
                    listItem.put("max", price.getString("max"));
                    listItem.put("currency", price.getString("currency"));
                    listItem.put("id", 0);
                    listItem.put("imgName", event.getString("id")+".jpg");

                    downloadImage(image.getString("url"), event.getString("id"));

                    list.add(listItem);
                    count++;
                    publishProgress(( count * 100 ) / totalElement, count);
                }
                if(count > 0){
                    publishProgress(100, count);
                }
            } catch (Exception e) {
                Log.e("doInBackground", e.toString());
            }
            return "Done";
        }

        public void onProgressUpdate(Integer... args) {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setProgress(args[0]);
            myAdapter.notifyDataSetChanged();
            Log.d("onProgressUpdate", "Update progress bar to: " + args[0] +"  " +args[1]);
        }

        public void onPostExecute(String fromDoInBackground) {
            Toast toast;
            //Toast toast = Toast.makeText(TicketMasterActivity.this.getApplicationContext(), getResources().getString(R.string.toast), Toast.LENGTH_LONG);
            if(totalElement > 0) {
                toast = Toast.makeText(TicketMasterActivity.this.getApplicationContext(), totalElement+" events has been loaded successfylly!", Toast.LENGTH_LONG);
            }
            else {
                toast = Toast.makeText(TicketMasterActivity.this.getApplicationContext(), " Sorry, No events found!", Toast.LENGTH_LONG);
            }
            toast.show();
        }
    }

    private class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View old, ViewGroup parent) {
            View row = null;
            JSONObject event = getItem(position);
            LayoutInflater inflater= getLayoutInflater();//this loads xml layouts
            if(event != null){
                    row=inflater.inflate(R.layout.row_layout, parent, false);
                    TextView tView = row.findViewById(R.id.name);
                try {
                    position += 1;
                    tView.setText(position+"."+event.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return row;
        }

        @Override
        public long getItemId(int position) {
            long id = 0;
            try {
                id = getItem(position).getLong("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return id;
        }
    }
}