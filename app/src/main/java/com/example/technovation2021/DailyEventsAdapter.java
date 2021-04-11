package com.example.technovation2021;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class DailyEventsAdapter extends RecyclerView.Adapter<DailyEventsAdapter.CalViewHolder> {

    public static final String LOG_TAG = "EvRecycler";
    ArrayList<Event> evList;
    Context cxt;

    public DailyEventsAdapter(Context ct, ArrayList<Event> eventArrayList) {
        cxt = ct;
        evList = eventArrayList;
    }

    @NonNull
    @Override
    public DailyEventsAdapter.CalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(cxt);
        View vw = li.inflate(R.layout.event_detail_row, parent, false);
        return new CalViewHolder(vw);
    }

    private String durationFormat(Integer duration) {
        String disp = "";
        if ( (duration / 60) > 0 ) {
            disp = Integer.toString(duration / 60) + "h:";
        }
        disp += Integer.toString(duration % 60) + "m";
        return disp;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull DailyEventsAdapter.CalViewHolder holder, int position) {
        //Log.d(LOG_TAG, "Show event number: " + position);
        DateTimeFormatter dateFormat =  DateTimeFormatter.ofPattern("KK:mm a");
        holder.startTime.setText(evList.get(position).startTime.format(dateFormat));
        //holder.duration.setText(evList.get(position).duration.toString());
        holder.duration.setText(durationFormat(evList.get(position).duration));
        holder.evDesc.setText(evList.get(position).eventDesc);
        holder.notes.setText(evList.get(position).notes);
    }

    @Override
    public int getItemCount() {
        //Log.d(LOG_TAG, "evlist size: " + evList.size());
        return evList.size();
    }

    public class CalViewHolder extends RecyclerView.ViewHolder {
        TextView startTime;
        TextView duration;
        TextView evDesc;
        TextView notes;
        public CalViewHolder(@NonNull View itemView) {
            super(itemView);

            startTime = itemView.findViewById(R.id.idEventStartTime);
            duration = itemView.findViewById(R.id.idEventDuration);
            evDesc = itemView.findViewById(R.id.idEventDesc);
            notes = itemView.findViewById(R.id.idEventNotes);
        }
    }
}
