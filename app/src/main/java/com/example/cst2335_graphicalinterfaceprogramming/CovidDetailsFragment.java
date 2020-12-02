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
 * The class is used to demonstrate Fragment
 *  @author June Li
 * @version 1.0
 */
public class CovidDetailsFragment  extends Fragment {
    /**
     * The varialbe is used to pass data
     */
    private Bundle dataFromActivity;
    /**
     * The varialbe is used define the context
     */
    private AppCompatActivity parentActivity;
    /**
     * The varialbe is used to show fragment listview
     */
    private FragmentAdapter fragmentAdapter=new FragmentAdapter();
    /**
     * The varialbe is used to store detailed data
     */
    ArrayList<String> fragmentList=new ArrayList();

    public CovidDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        //Inflate the layout for this fragment
        fragmentList = (ArrayList<String>) dataFromActivity.getSerializable("ARRAYLIST");
        View result = inflater.inflate(R.layout.activity_covid_details, container, false);
        ListView detailView = result.findViewById(R.id.details);
        detailView.setAdapter(fragmentAdapter);

        Button hideButton = (Button)result.findViewById(R.id.hide);
        hideButton.setOnClickListener( clk -> parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit());
        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (AppCompatActivity)context;
    }

    protected class FragmentAdapter extends BaseAdapter {
        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return fragmentList.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public String getItem(int position){
            return fragmentList.get(position);
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param old         The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, View old, ViewGroup parent) {
            String sr = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.activity_search_result, parent, false);
            if (sr != null) {
                TextView savedView = view.findViewById(R.id.searchResult);
                savedView.setText(sr);
            }
            return view;
        }
        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }
    }

}