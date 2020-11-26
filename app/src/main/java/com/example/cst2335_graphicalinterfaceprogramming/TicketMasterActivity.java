package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.File;
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

/**
 * The class is used to search events data according to user's input city and radius
 *  @author Wei Li
 * @version 1.0
 */
public class TicketMasterActivity extends AppCompatActivity {
    /**
     * list of JSONObject
     */
    private List<JSONObject> list = new ArrayList<>();
    /**
     * name of city
     */
    String city;
    /**
     * number of radius
     */
    String radius;
    /**
     * adapter for the ListView
     */
    MyListAdapter myAdapter;
    /**
     * database used to store data
     */
    SQLiteDatabase db;
    /**
     * shared preferences instance
     */
    SharedPreferences prefs = null;

    /**
     * The method is the entry of execute,it equivalent to main method
     * @param savedInstanceState savedInstanceState reference to a Bundle object that is passed into the onCreate method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_master);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        ListView myList=findViewById(R.id.the_list);
        myList.setAdapter(myAdapter = new MyListAdapter());
        boolean isTablet = findViewById(R.id.frameLayout) != null;

        EditText City = findViewById(R.id.addEditText1);
        EditText Radius = findViewById(R.id.addEditText2);
        Button searchButton = findViewById(R.id.searchButton);
        Button saveButton = findViewById(R.id.saveButton);

        //SharedPreferences to save "CITY" and "RADIUS" about the application for use the next time the application is launched.
        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        String savedCity = prefs.getString("CITY", "");
        String savedRadius = prefs.getString("RADIUS", "");
        City.setText(savedCity);
        Radius.setText(savedRadius);

        //press SEARCH button to show events
        searchButton.setOnClickListener(click -> {
            if (( City.getText().toString().length() == 0 ) ||
                    ( Radius.getText().toString().length() == 0 )) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.ticketError))
                        .setMessage(getResources().getString(R.string.ticketPlease))
                        .setPositiveButton(getResources().getString(R.string.ticketok), (click_ok, arg) -> {
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

        //press FAVORITE button go to FavoriteTicketActivity page
        Intent nextPage = new Intent(TicketMasterActivity.this, FavoriteTicketActivity.class);
        saveButton.setOnClickListener(click -> {
            startActivity(nextPage);
        });

        //press TOOLBAR button go to TOOLBAR and NAVIGATION page
        Button toolbarButton = findViewById(R.id.my_toolbar);
        Intent nextToolbarPage = new Intent(TicketMasterActivity.this, MenuExample.class);
        toolbarButton.setOnClickListener( click ->
        {
            startActivity( nextToolbarPage );
        });

        //click on events, if tablet shows fragment, if phone shows next TicketDetailsActivity page
        myList.setOnItemClickListener((list1, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString("JSONSTRING", list.get(position).toString());
            dataToPass.putString("SEARCH", "1");

            if(isTablet)
            {
                TicketDetailsFragment dFragment = new TicketDetailsFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment.
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(TicketMasterActivity.this, TicketDetailsActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

        //click HELP button on TicketMasterActivity page will show help AlertDialog message
        Button help1 = findViewById(R.id.helpButton);
        help1.setOnClickListener(v ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.TicketHelp) + ": ")
                    .setMessage(getResources().getString(R.string.WelcomeTicketSearch)+getResources().getString(R.string.ticketFavorite)+getResources().getString(R.string.ticketToolBar))
                    .setNeutralButton(getResources().getString(R.string.ticketAlertNB), (click, b) -> { })
                    .create().show();});
    }

    /**
     * This method is used to establish connection with required http server
     * @param reqUrl The parameters of the required connection URL
     * @return A String of all JASON data
     */
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

    /**
     * This method is used to check whether the image download successfully, if it downloaded successfully return true, otherwise return false
     * @param reqUrl URL that used to connect
     * @param name the name of the image that needs to be downloaded
     * @return A boolean result, whether the image been downloaded
     */
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

    /**
     * The inner class is used to read data from website using JSON
     * @return A object of JSONObject
     */
    //AsynTask to retrieve data from http server
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

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This will normally run on a background thread. But to better
         * support testing frameworks, it is recommended that this also tolerates
         * direct execution on the foreground thread, as part of the {@link #execute} call.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         * @param args The parameters of the task
         * @return A result, defined by the subclass of this task
         */
        @Override
        public String doInBackground(String... args) {
            try {
                int count = 0;
                String nextPage = "";
                String url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=naG2r1v48w2ubfr6icXhrvmlWvBW8dmz&" +
                        "city=" + args[0] + "&radius=" + args[1];
                Log.d("doInBackground",url);
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

        /**
         * Runs on the UI thread after {@link #publishProgress} is invoked.
         * The specified values are the values passed to {@link #publishProgress}.
         * The default version does nothing.
         * @param args The values indicating progress.
         */
        @Override
        public void onProgressUpdate(Integer... args) {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setProgress(args[0]);
            myAdapter.notifyDataSetChanged();
            Log.d("onProgressUpdate", "Update progress bar to: " + args[0] +"  " +args[1]);
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.
         * <p>If searching events that been found greater than 0, it shows the number of events has been loaded.
         * Otherwise no events found.
         * @param fromDoInBackground The result of the operation computed by {@link #doInBackground}.
         */
        @Override
        public void onPostExecute(String fromDoInBackground) {
            Toast toast;
            if(totalElement > 0) {
                toast = Toast.makeText(TicketMasterActivity.this.getApplicationContext(), totalElement+getResources().getString(R.string.ticketLoad), Toast.LENGTH_LONG);
            }
            else {
                toast = Toast.makeText(TicketMasterActivity.this.getApplicationContext(), getResources().getString(R.string.ticketNoFound), Toast.LENGTH_LONG);
            }
            toast.show();
        }
    }

    /**
     * The inner class is an adapter for ListView
     */
    private class MyListAdapter extends BaseAdapter {
        /**
         * Method counts how many events are in a list
         * @return number of events in a list
         */
        @Override
        public int getCount() {
            return list.size();
        }
          /**
            * Method gets a event from a list
         * @param position position of a event in a list
         * @return JSONObject event
         */
        @Override
        public JSONObject getItem(int position) {
            return list.get(position);
        }
        /**
         * Method returns a view for a event
         * @param position position of a event in a list
         * @param old recycled view
         * @param parent view that can contain other views
         * @return view of a event (row to the ListView)
         */
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
        /**
         * Method gets a event's id from a database
         * @param position position of a event in a list
         * @return id of a event
         */
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