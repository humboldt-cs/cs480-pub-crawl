package com.example.pubcrawl.fragments;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pubcrawl.Event;
import com.example.pubcrawl.EventsAdapter;
import com.example.pubcrawl.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {

    public static final String TAG = "EventsFragment";
    private RecyclerView rvEvents;
    protected EventsAdapter adapter;
    protected List<Event> allEvents;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvEvents = view.findViewById(R.id.rvEvents);
        allEvents = new ArrayList<>();
        adapter = new EventsAdapter(getContext(), allEvents);

        // set the adapter on the recyclerview
        rvEvents.setAdapter(adapter);
        // set the layout manager on the recyclerview
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        queryEvents();
    }

    protected void queryEvents() {
        // Specify which class to query
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        // TODO: change according to which itinerary items we want displayed.
        //  could also restrict query by date using query.whereEqualTo("startTime", [today's date at any time])
        query.setLimit(20);
        query.addDescendingOrder(Event.KEY_START_TIME);
        query.findInBackground((new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting events", e);
                    return;
                }
                // if e is null, we have a list of events to show
                // clear adapter first
                adapter.clear();
                // then load new events into adapter
                for (Event event : events) {
                    Log.i(TAG, "Location: " + event.getLocation() + ", Start time: " + event.getStartTime().toString() + ", End time: " + event.getEndTime().toString());
                }
                allEvents.addAll(events);
                adapter.notifyDataSetChanged();
            }
        }));
    }
}
