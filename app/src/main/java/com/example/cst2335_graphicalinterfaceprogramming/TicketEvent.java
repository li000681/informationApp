package com.example.cst2335_graphicalinterfaceprogramming;

public class TicketEvent {
    protected String name;
    protected String url;
    protected String imgUrl;
    protected String city;
    protected String localDate;
    protected String localTime;
    protected String min;
    protected String max;
    protected String currency;
    protected long id;


    public TicketEvent(
            String name, String url, String imgUrl, String city, String localDate,
            String localTime, String min, String max, String currency){
        this.name=name;
        this.url=url;
        this.imgUrl=imgUrl;
        this.city=city;
        this.localDate=localDate;
        this.localTime=localTime;
        this.min=min;
        this.max=max;
        this.currency=currency;
    }

    public String getName(){
        return name;
    }
    public String getUrl(){
        return url;
    }
    public String getImageUrl(){
        return imgUrl;
    }
    public String getCity(){
        return city;
    }
    public String getLocalDate(){
        return localDate;
    }
    public String getLocalTime(){
        return localTime;
    }
    public String getMin(){
        return min;
    }
    public String getMax(){
        return max;
    }
    public String getCurrency(){
        return currency;
    }
    public long getId() { return id; }
}
