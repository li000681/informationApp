package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ListOfRecipes extends AppCompatActivity {
    private ListView lv;
    private ProgressBar pb;
    ArrayList<Recipes> elements = new ArrayList<>();
    private MyListAdapter myAdapter;
    ListView recipeList;

    //    Intent fromMain = getIntent();
//    String recipe= fromMain.getStringExtra("Recipe");
//    String ingredients = fromMain.getStringExtra("Ingredients");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_recipes);
        Intent fromMain = getIntent();
        String recipe = fromMain.getStringExtra("Recipe");
        String ingredients = fromMain.getStringExtra("Ingredients");
        pb = findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);
        ForecastQuery req = new ForecastQuery();
        req.execute("http://www.recipepuppy.com/api/","?i=", ingredients, "&q=", recipe, "&format=xml");
        recipeList = (ListView) findViewById(R.id.recipeList);


        recipeList.setOnItemLongClickListener((parent, view, pos, id) -> {

            showMessage(pos);

            return true;
        });

    }


    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        //string variables for the UV, min, max, and current temperature
        String title;
        String href;
        String ingredients;


        @Override
        public String doInBackground(String... args) {
            try {
                String a = URLEncoder.encode(args[1], "UTF-8");
                String b = URLEncoder.encode(args[2], "UTF-8");
                String c = URLEncoder.encode(args[3], "UTF-8");
                String d = URLEncoder.encode(args[4], "UTF-8");
                String e = URLEncoder.encode(args[5], "UTF-8");
               // String f = URLEncoder.encode(args[6], "UTF-8");
                //create a URL object of what server to contact:
                URL url = new URL(args[0] + a + b + c + d + e);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();


                //From part 3: slide 19
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8"); //response is data from the server


                //From part 3, slide 20
                //String parameter = null;

                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {
                        //If you get here, then you are pointing at a start tag
                        if (xpp.getName().equals("recipe")) {
                            //If you get here, then you are pointing to a <Weather> start tag
                            title = xpp.getAttributeValue(null, "title");

                            publishProgress(25);
                            href = xpp.getAttributeValue(null, "href");

                            publishProgress(50);

                            ingredients = xpp.getAttributeValue(null, "ingredients");
                            publishProgress(75);

                        }

                    }
                    Recipes recipe = new Recipes(title, href, ingredients);
                    elements.add(recipe);
                    publishProgress(100);
                    eventType = xpp.next(); //move to the next xml event and store it in a variable
                }
            } catch (Exception e) {
                Log.i(String.valueOf(e), "not connected");
            }

            return "done";
        }


        //Type 2
        public void onProgressUpdate(Integer... value) {
            pb.setVisibility(View.VISIBLE);
            pb.setProgress(value[0]);

        }

        //Type3
        public void onPostExecute(String fromDoInBackground) {
            recipeList.setAdapter(myAdapter = new MyListAdapter());
            pb.setVisibility(View.INVISIBLE);
        }


    }

    private class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return elements.size();
        }

        @Override
        public Object getItem(int position) {
            return elements.get(position);
        }

        @Override
        public long getItemId(int position) {
            return elements.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.recipe_list_view, parent, false);


            TextView tView = newView.findViewById(R.id.recipeTitle);
            tView.setText(elements.get(position).getTitle());

            return newView;

        }

    }
    protected void showMessage(int position)
    {
        Recipes selectedRecipe = elements.get(position);

        //View contact_view = getLayoutInflater().inflate(R.layout.message_edit, null);
        //get the TextViews
//        EditText rowName = contact_view.findViewById(R.id.row_name);
//        EditText rowEmail = contact_view.findViewById(R.id.row_email);
//        TextView rowId = contact_view.findViewById(R.id.row_id);

        //set the fields for the alert dialog
//        rowName.setText(selectedMessage.getMsg());
//        rowEmail.setText(selectedMessage.booleanToString(selectedMessage.getSendButtonIsClicked()));
//        rowId.setText("id:" + selectedMessage.getId());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(selectedRecipe.getTitle())
                .setMessage(selectedRecipe.getHref()+"/n"+selectedRecipe.getIngredients())
//                .setView(contact_view) //add the 3 edit texts showing the contact information
//                .setPositiveButton(getResources().getString(R.string.alertUB), (click, b) -> {
//                    selectedMessage.update(rowName.getText().toString(), selectedMessage.stringToBoolean(rowEmail.getText().toString()));
//                    updateMessage(selectedMessage);
//                    myAdapter.notifyDataSetChanged(); //the email and name have changed so rebuild the list
//                })
//                .setNegativeButton(getResources().getString(R.string.alertPB), (click, b) -> {
//                    deleteMessage(selectedMessage); //remove the contact from database
//                    elements.remove(position);
//                    if(isTablet){
//                        getSupportFragmentManager().beginTransaction().remove(dFragment).commit();}//remove the contact from contact list
//                    myAdapter.notifyDataSetChanged(); //there is one less item so update the list
//                })
                .setNeutralButton(getResources().getString(R.string.recipeAlertNB), (click, b) -> { })
                .create().show();
    }
}