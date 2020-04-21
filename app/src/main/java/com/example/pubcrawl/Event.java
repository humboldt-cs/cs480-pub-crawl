package com.example.pubcrawl;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Event")
public class Event extends ParseObject {

    public static final String KEY_LOCATION = "location";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_END_TIME = "endTime";

    public String getLocation() {
        return getString(KEY_LOCATION);
    }

    public Date getStartTime() {
        return getDate(KEY_START_TIME);
    }

    public Date getEndTime() {
        return getDate(KEY_END_TIME);
    }

    public void setLocation(String location) {
        put(KEY_LOCATION, location);
    }

    public void setStartTime(Date startTime) {
        put(KEY_START_TIME, startTime);
    }

    public void setEndTime(Date endTime) {
        put(KEY_END_TIME, endTime);
    }
}
