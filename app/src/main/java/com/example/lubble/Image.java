package com.example.lubble;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Image {

    private String title, url;
    private Long time;
    private String sTime;

    public Image() {
    }

    public void getDate(long milliSeconds)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        Log.d("title",title+"!");
        Log.d("time",sTime+"!");
        this.sTime = formatter.format(calendar.getTime());
        Log.d("title",title+"!");
        Log.d("time",sTime+"!");
    }

    public Image(String title, String url, Long time) {
        this.title = title;
        this.url = url;
        this.time = time;
        Log.d("title",title+"!");
        getDate(time);
        Log.d("time",sTime+"!");
    }


    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
