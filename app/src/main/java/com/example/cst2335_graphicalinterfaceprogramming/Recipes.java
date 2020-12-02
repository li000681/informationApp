package com.example.cst2335_graphicalinterfaceprogramming;
/**
 * The class is the basic message about Recipe
 *  @author Jianchuan Li
 * @version 1.0
 */
public class Recipes {
    protected  String title;
    protected String href;
    protected String ingredients;
    protected Long id;
    public Recipes(Long id,String title, String href, String ingredients){
        this.title=title;
        this.href=href;
        this.ingredients=ingredients;
        this.id=id;
    }
    public void update(String t, String h, String i)
    {
        title=t;
        href=h;
        ingredients=i;
    }

    /**Chaining constructor: */
    public Recipes(String t, String h, String i) { this( 0L, t, h,i);}



    public long getId() {
        return id;
    }

    public String getHref(){

        return href;
    }
    public String getIngredients(){

        return ingredients;
    }


    public String getTitle() {
        return title;
    }


}