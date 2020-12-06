package com.example.cst2335_graphicalinterfaceprogramming;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * The class is used to demonstrate Fragment
 *  @author Jianchuan` Li
 * @version 1.0
 */


public class RecipeSearchDetailsFragment extends Fragment{
    //private SQLiteDatabase db;
    private Bundle dataFromActivity;
    private long id;
    SQLiteDatabase db;
    private AppCompatActivity parentActivity;
    /**
     * Method used to retrieve required data(title, ingredients and URL) from previous activity and set to text view.
     * @param inflater reference of LayoutInflater that loads xml layouts
     * @param container reference of ViewGroup
     * @param savedInstanceState reference to a Bundle object that is passed into the onCreate method
     * @return view of ticket details
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        String title=dataFromActivity.getString(ListOfRecipes.ITEM_TITLE );
        String url=dataFromActivity.getString(ListOfRecipes.ITEM_URL )+"\n";
        String ingredients=dataFromActivity.getString(ListOfRecipes.ITEM_INGREDIENTS );
        id = dataFromActivity.getLong(ListOfRecipes.ITEM_ID );
        RecipeSearchMyOpener dbOpener = new RecipeSearchMyOpener(getContext());
        db = dbOpener.getWritableDatabase();
        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_recipe_search_details, container, false);

        //show the title
        TextView message = (TextView)result.findViewById(R.id.recipeTitle);
        message.setText(getResources().getString(R.string.inputRecipe) + title);

        //show the URL:
        TextView linkView = (TextView)result.findViewById(R.id.recipeURL);
        linkView.setText(getResources().getString(R.string.recipeURL) + url);

        //show the ingredients
        TextView ingredientsView = (TextView)result.findViewById(R.id.recipeIngredients);
        ingredientsView.setText(getResources().getString(R.string.inputIngredients) + ingredients);
        Button urlButton = result.findViewById(R.id.recipeURL);
        /**press button to open url in browser*/
        urlButton.setOnClickListener(click->{
            Intent nextActivity = new Intent(Intent.ACTION_VIEW);
            nextActivity.setData(Uri.parse(url));
            startActivity(nextActivity);
        });

        Button finishButton = (Button)result.findViewById(R.id.finishButton);
        finishButton.setOnClickListener( clk -> {

            //Tell the parent activity to remove
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();

            if (parentActivity instanceof RecipeSearchEmptyActivity){
                parentActivity.finish();
            }
        });
        Button saveButton = (Button)result.findViewById(R.id.saveButton);
        saveButton.setOnClickListener( clk -> {

            //Tell the parent activity to remove
            Recipes r= new Recipes(title,url,ingredients);
                updateMessage(r);
            Toast.makeText(getActivity(), getResources().getString(R.string.Recipe_save_toast_message), Toast.LENGTH_LONG).show();
        });

        return result;
    }
    /**
     * Call back function, when the fragment has been added to the Activity which has the FrameLayout
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }
    protected void updateMessage(Recipes c)
        {
            //get a database connection:

            //Create a ContentValues object to represent a database row:
            ContentValues updatedValues = new ContentValues();
            updatedValues.put(RecipeSearchMyOpener.COL_TITLE, c.getTitle());
            updatedValues.put(RecipeSearchMyOpener.COL_URL, c.getHref());
            updatedValues.put(RecipeSearchMyOpener.COL_INGREDIENTS, c.getIngredients());
           // updatedValues.put(RecipeSearchMyOpener.COL_ID, c.getId());



            //Now insert in the database:
            long newId = db.insert(RecipeSearchMyOpener.TABLE_NAME, null, updatedValues);
             }
}
