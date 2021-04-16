package com.example.technovation2021;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class DailyEventsAdapter extends RecyclerView.Adapter<DailyEventsAdapter.CalViewHolder> {

    public static final String LOG_TAG = "EvRecycler";
    ArrayList<Event> evList;
    Context cxt;
    LocalDate displayDate;

    public DailyEventsAdapter(Context ct, LocalDate dateShowing, ArrayList<Event> eventArrayList) {
        cxt = ct;
        evList = eventArrayList;
        displayDate = dateShowing;
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
            disp = Integer.toString(duration / 60) + "h";
            if ((duration % 60) > 0) {
                disp += ":";
            }
        }
        if ((duration % 60) > 0) {
            disp += Integer.toString(duration % 60) + "m";
        }
        return disp;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull DailyEventsAdapter.CalViewHolder holder, final int position) {
        //Log.d(LOG_TAG, "Show event number: " + position);
        DateTimeFormatter dateFormat =  DateTimeFormatter.ofPattern("KK:mm a");
        holder.startTime.setText(evList.get(position).startTime.format(dateFormat));
        //holder.duration.setText(evList.get(position).duration.toString());
        holder.duration.setText(durationFormat(evList.get(position).duration));
        holder.evDesc.setText(evList.get(position).eventDesc);
        holder.notes.setText(evList.get(position).notes);
        if ( evList.get(position).type == 1 ){
            holder.taskDone.setVisibility(View.GONE);
        }
        else {
            if ( displayDate.isBefore(LocalDate.now()) == false ) {
                holder.taskDone.setImageResource(R.drawable.checkbox_unclicked);
            }
            else holder.taskDone.setVisibility(View.GONE);
        }

        holder.taskDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TASK DONE", "task " + evList.get(position).eventDesc + " is done");
                new AlertDialog.Builder(cxt)
                        .setTitle("MissionTech2021")
                        .setMessage("Do you want to mark the task as done? This will remove remaining scheduled events for this task from your calendar.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Log.d("TASKDONE", "have user input " + whichButton);
                                FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
                                frd.clearEvents(evList.get(position));
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
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
        ImageView taskDone;

        public CalViewHolder(@NonNull View itemView) {
            super(itemView);

            startTime = itemView.findViewById(R.id.idEventStartTime);
            duration = itemView.findViewById(R.id.idEventDuration);
            evDesc = itemView.findViewById(R.id.idEventDesc);
            notes = itemView.findViewById(R.id.idEventNotes);
            taskDone = itemView.findViewById(R.id.logo);
        }
    }
}
