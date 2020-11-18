package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
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

public class TicketMasterActivity extends AppCompatActivity {
    String city;
    String radius;
    private List<TicketEvent> list = new ArrayList<>();
    MyListAdapter myAdapter;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_master);

        EditText City = findViewById(R.id.addEditText1);
        EditText Radius = findViewById(R.id.addEditText2);
        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(click -> {
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
                ForecastQuery req = new ForecastQuery();
                req.execute(city, radius);
            }
        });
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while (( line = reader.readLine() ) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    } // End convertStreamToString()

    public String establishConnection(String reqUrl) {

        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(inputStream);
        } catch (MalformedURLException e) {
            Log.e("TicketMasterActivity", "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e("TicketMasterActivity", "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e("TicketMasterActivity", "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e("TicketMasterActivity", "Exception: " + e.getMessage());
        }
        return response;
    } // End establishConnection()


    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        protected String size;
        protected int totalElement;
        protected int totalPages;

        private void getPageParameter(String reqUrl) {
            JSONObject page = null;
            try {
                page = new JSONObject(establishConnection(reqUrl));
            } catch (Exception e) {
                Log.e("TicketMasterActivity", "Exception: " + e.getMessage());
            }
            try {
                size = page.getString("size");
                totalElement = page.getInt("totalElement");
                totalPages = page.getInt("totalPages");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("getPageParameter", "size:"+size+"totalElement:"+totalElement+"totalPages:"+totalPages);
            return;
        }

        //Type3                      Type1
        public String doInBackground(String... args) {
            try {
                int count = 0;
                String url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=naG2r1v48w2ubfr6icXhrvmlWvBW8dmz&" +
                        "city=" + args[0] + "&radius=" + args[1];
                url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=naG2r1v48w2ubfr6icXhrvmlWvBW8dmz&city=ottawa&radius=2";
                getPageParameter(url);

                for (int i = 0; i < totalPages; i++) {
                    url += "&page=" + i + "&size=" + size;
                    JSONObject jsonObj = new JSONObject(establishConnection(url));
                    JSONArray ticketArray = jsonObj.getJSONArray("events");
/*                    String name, String url, String imgUrl, String city, String localDate,
                            String localTime, String min, String max, String currency)*/
                    for (int k = 0; k < ticketArray.length(); k++) {
                        JSONObject event = ticketArray.getJSONObject(i);
                        JSONArray Array = event.getJSONArray("images");
                        JSONObject image = Array.getJSONObject(0);
                        Array = event.getJSONArray("priceRanges");
                        JSONObject price = Array.getJSONObject(0);

                        list.add(new TicketEvent(event.getString("name"),
                                event.getString("url"),
                                event.getString(image.getString("url")),
                                event.getString(args[0]),
                                event.getString("localDate"),
                                event.getString("localTime"),
                                event.getString(price.getString("min")),
                                event.getString(price.getString("max")),
                                event.getString(price.getString("currency"))
                        ));
                        count++;
                        if (count % 10 == 0) {
                            publishProgress(( count * 100 ) / totalElement, count);
                        }
                    }
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
            Log.d("onProgressUpdate", "Update progress bar to: " + args[0]);
        }

        public void onPostExecute(String fromDoInBackground) {
            //Toast toast = Toast.makeText(TicketMasterActivity.this.getApplicationContext(), getResources().getString(R.string.toast), Toast.LENGTH_LONG);
            Toast toast = Toast.makeText(TicketMasterActivity.this.getApplicationContext(), totalElement+" items has been loaded successfylly!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public TicketEvent getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View old, ViewGroup parent) {
            View row = null;
            TicketEvent event = getItem(position);
            LayoutInflater inflater= getLayoutInflater();//this loads xml layouts
            if(event != null){
                    row=inflater.inflate(R.layout.row_layout, parent, false);
                    TextView tView = row.findViewById(R.id.name);
                    tView.setText(event.getName());
            }
            return row;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }
    }
}