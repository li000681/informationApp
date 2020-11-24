package com.example.cst2335_graphicalinterfaceprogramming;


import android.content.Context;
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




public class RecipeSearchDetailsFragment extends Fragment {

    private Bundle dataFromActivity;
    private long id;
    private AppCompatActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        String title=dataFromActivity.getString(ListOfRecipes.ITEM_ID );
        String url=dataFromActivity.getString(ListOfRecipes.ITEM_URL );
        String ingredients=dataFromActivity.getString(ListOfRecipes.ITEM_INGREDIENTS );
        id = dataFromActivity.getLong(ListOfRecipes.ITEM_ID );

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_recipe_search_details, container, false);

        //show the title
        TextView message = (TextView)result.findViewById(R.id.recipeTitle);
        message.setText("Title: " + title);

        //show the URL:
        TextView linkView = (TextView)result.findViewById(R.id.recipeURL);
        linkView.setText("URL: " + url);

        //show the ingrdients
        TextView ingredientsView = (TextView)result.findViewById(R.id.recipeIngredients);
        linkView.setText("Ingredients: " + ingredients);

        // get the delete button, and add a click listener:
        Button finishButton = (Button)result.findViewById(R.id.finishButton);
        finishButton.setOnClickListener( clk -> {

            //Tell the parent activity to remove
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();

            if (parentActivity instanceof RecipeSearchEmptyActivity){
                parentActivity.finish();
            }
        });
        Button savehButton = (Button)result.findViewById(R.id.finishButton);
        savehButton.setOnClickListener( clk -> {

            //Tell the parent activity to remove
            Recipes r= new Recipes(title,url,ingredients);
            ((ListOfRecipes)getActivity()).updateMessage(r);
            Toast.makeText(getActivity(), getResources().getString(R.string.Recipe_save_toast_message), Toast.LENGTH_LONG).show();
        });

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }
}
