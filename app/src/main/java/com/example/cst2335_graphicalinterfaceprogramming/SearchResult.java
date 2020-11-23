package com.example.cst2335_graphicalinterfaceprogramming;


/**
 * The class is used to contain the the search result
 *  @author June Li
 * @version 1.0
 */
public class SearchResult {

    private String country;
    private String province;
    private int caseNumber;
    private String date;
    private long id;
    /**
     * The default constructor
     */
    public SearchResult(){}
    /**
     * The four parameter constructor
     */
    public SearchResult( String c,String p,int ca,String d)
    {
        country=c;
        province=p;
        caseNumber=ca;
        date=d;
    }
    public SearchResult( String p,int ca)
    {
        province=p;
        caseNumber=ca;
    }
    /**
     * The getters method
     */
    public String getCountry() {
        return country;
    }
    public String getProvince() {
        return province;
    }
    public int getCase() {
        return caseNumber;
    }
    public String getDate() {
        return date;
    }
    public long getId() {return id; }
}
