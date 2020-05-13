package com.example.pubcrawl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Event event = events.get(position);
        // bind data from event into viewholder
        holder.bind(event);

        holder.buttonViewOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create popup menu for each item in RecyclerView
                PopupMenu popup = new PopupMenu(context, holder.buttonViewOptions);
                // inflate menu from XML resource
                popup.inflate(R.menu.menu_event);
                // add onclick listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.edit:
                                // TODO: redirect to edit activity
                                break;
                            case R.id.delete:
                                // TODO: delete item
                                break;
                        }
                        return false;
                    }
                });
                // display the popup
                popup.show();
            }
        });
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
        private TextView buttonViewOptions;

        // constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStart = itemView.findViewById(R.id.tvStart);
            tvEnd = itemView.findViewById(R.id.tvEnd);
            buttonViewOptions = itemView.findViewById(R.id.textViewOptions);
        }

        // bind the event data to the view elements of one item
        public void bind(Event event) {
            tvLocation.setText(event.getLocation());
            tvStart.setText(event.getStartTime().toString());
            tvEnd.setText(event.getEndTime().toString());
        }

    }
}
