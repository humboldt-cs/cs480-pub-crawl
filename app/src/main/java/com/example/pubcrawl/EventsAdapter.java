package com.example.pubcrawl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private Context context;
    private List<Event> events;

    public EventsAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        // wrap inflated layout in view holder
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        // bind data from event into viewholder
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    // clear out all elements of the recycler
    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }

    // add a list of events
    public void addAll(List<Event> list) {
        events.addAll(list);
        notifyDataSetChanged();
    }

    // holds the view for one item in recyclerview
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvLocation;
        private TextView tvStart;
        private TextView tvEnd;

        // constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStart = itemView.findViewById(R.id.tvStart);
            tvEnd = itemView.findViewById(R.id.tvEnd);
        }

        // bind the event data to the view elements of one item
        public void bind(Event event) {
            tvLocation.setText(event.getLocation());
            tvStart.setText(event.getStartTime().toString());
            tvEnd.setText(event.getEndTime().toString());
        }

    }
}
