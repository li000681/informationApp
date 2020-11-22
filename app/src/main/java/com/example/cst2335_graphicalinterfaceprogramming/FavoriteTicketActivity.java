package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoriteTicketActivity extends AppCompatActivity {
    private List<JSONObject> list = new ArrayList<>();
    private FavoriteTicketActivity.MyListAdapter myAdapter;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_ticket);

        ListView myList=findViewById(R.id.favorite_list);
        myList.setAdapter(myAdapter = new FavoriteTicketActivity.MyListAdapter());

        list.clear();
        loadDataFromDatabase();
        myAdapter.notifyDataSetChanged();

        Button help3 = findViewById(R.id.Help);
        help3.setOnClickListener(v ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.TicketFavoriteHelp) + ": ")
                    .setMessage(getResources().getString(R.string.TicketFavoriteDelete))
                    .setNeutralButton(getResources().getString(R.string.ticketFavoriteAlertNB), (click, b) -> { })
                    .create().show();});

        myList.setOnItemClickListener((list1, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString("JSONSTRING", list.get(position).toString());
            dataToPass.putString("FAVORITE", "1");

            Intent nextActivity = new Intent(FavoriteTicketActivity.this, TicketDetailsActivity.class);
            nextActivity.putExtras(dataToPass); //send data to next activity
            startActivity(nextActivity); //make the transition
        });

        myList.setOnItemLongClickListener((parent, view, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.ticketDelete))
                    //what the Yes button does:
                    .setPositiveButton(getResources().getString(R.string.ticketYes), (click, arg) -> {
                        JSONObject removedItem = null;
                        long database_id = 0;
                        try {
                            removedItem = list.get(pos);
                            database_id = list.get(pos).getLong("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        db.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "= ?", new String[] {Long.toString(database_id)});
                        list.remove(pos);
                        myAdapter.notifyDataSetChanged();
                        JSONObject finalRemovedItem = removedItem;
                        Snackbar.make(view,getResources().getString(R.string.ticketDeleted), Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.ticketUndo), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                list.add(finalRemovedItem);
                                ContentValues newRowValues = new ContentValues();
                                newRowValues.put(MyOpener.COL_MESSAGE, finalRemovedItem.toString());
                                db.insert(MyOpener.TABLE_NAME, null, newRowValues);
                                myAdapter.notifyDataSetChanged();
                                Toast.makeText(FavoriteTicketActivity.this,getResources().getString(R.string.ticketRecover),Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    })
                    //What the No button does:
                    .setNegativeButton(getResources().getString(R.string.ticketNo), (click, arg) -> { })
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