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

/**
 * Class shows a list of favorite events, allows user to delete a event from the list
 * @author Wei Li
 * @version 1.0
 */
public class FavoriteTicketActivity extends AppCompatActivity {
    /**
     * list with user's favorite events
     */
    private List<JSONObject> list = new ArrayList<>();
    /**
     * adapter for the ListView
     */
    private FavoriteTicketActivity.MyListAdapter myAdapter;
    /**
     * database used to store data
     */
    SQLiteDatabase db;

    /**
     * Method loads a list of favorite events from database, allows to delete events from the list
     * @param savedInstanceState reference to a Bundle object that is passed into the onCreate method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_ticket);

        ListView myList=findViewById(R.id.favorite_list);
        myList.setAdapter(myAdapter = new FavoriteTicketActivity.MyListAdapter());

        list.clear();
        loadDataFromDatabase();
        myAdapter.notifyDataSetChanged();
        //HELP button in favorite page
        Button help3 = findViewById(R.id.Help);
        help3.setOnClickListener(v ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.TicketFavoriteHelp) + ": ")
                    .setMessage(getResources().getString(R.string.TicketFavoriteDelete))
                    .setNeutralButton(getResources().getString(R.string.ticketFavoriteAlertNB), (click, b) -> { })
                    .create().show();});
        //click on the specific favorite event then go to event detail page
        myList.setOnItemClickListener((list1, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString("JSONSTRING", list.get(position).toString());
            dataToPass.putString("FAVORITE", "1");

            Intent nextActivity = new Intent(FavoriteTicketActivity.this, TicketDetailsActivity.class);
            nextActivity.putExtras(dataToPass); //send data to next activity
            startActivity(nextActivity); //make the transition
        });
       //long click on the specific favorite event, shows "Do you want to delete this?"
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
                        //what UNDO do
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
    /**
     * Method loads a list of favorite events from database
     */
    private void loadDataFromDatabase()
    {
        //get a database connection:
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer

        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {MyOpener.COL_ID, MyOpener.COL_MESSAGE};
        //query all the results from the database:
        Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

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