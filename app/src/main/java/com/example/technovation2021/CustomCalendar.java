package com.example.technovation2021;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

interface EventsFetchedListener {
    void onEventsFetchDone(ArrayList<Event> eventsScheduled);
    void onEventsFetchError(String error);
}

@RequiresApi(api = Build.VERSION_CODES.O)
public class CustomCalendar extends LinearLayout implements EventsFetchedListener
{
    private static final String LOG_TAG = "MonthView";
    // 31 days of a month can end up in 6 weeks. hence show 42 days in calendar.
    private static final int DAYS_COUNT = 42;
    private static final String DATE_FORMAT = "MMM yyyy";

    private ArrayList<LocalDate> cells;
    Context calContext;

    LocalDate curDate = LocalDate.now();
    int displayMonth = 0;

    //event handling
    private EventHandler eventHandler = null;

    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;


    public void onEventsFetchDone(ArrayList<Event> evList) {
        //updateCalendar(evList);
        Log.d(LOG_TAG, "time to render events " + evList.size() + " datesArraysize: " + cells.size());
        // Appears to be sorted by date already! TODO: Double check this
        // If not we need to sort this in CalendarAdapter::getView()!!!
        for (int i = 0; i < evList.size(); i++ ) {
            //Log.d(LOG_TAG, "event " + (i+1) + " " + evList.get(i).print());
        }
        grid.setAdapter(new CalendarAdapter(getContext(), cells, evList));
    }

    public void onEventsFetchError(String error) {

    }

    public CustomCalendar(Context context)
    {
        super(context);
    }

    public CustomCalendar(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_my_calendar, this);
        header = (LinearLayout)findViewById(R.id.calendar_header);
        btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView)findViewById(R.id.calendar_next_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);

        // show events of next month
        btnNext.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                curDate = curDate.plusMonths(1);
                Log.d(LOG_TAG, "show me events for next mo:" + curDate.toString());
                updateCalendar(curDate);
            }
        });

        // show events of previous month
        btnPrev.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                curDate = curDate.plusMonths(-1);
                Log.d(LOG_TAG, "show me events for prev mo:" + curDate.toString());
                updateCalendar(curDate);
            }
        });

        // long tap on day to display expanded list of events for that day
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> view, View cell, int position, long id)
            {
                // handle long-press
                if (eventHandler == null)
                    return false;

                eventHandler.onDayLongPress((LocalDate)view.getItemAtPosition(position));
                return true;
            }
        });
        Log.d(LOG_TAG, "Have caller context");
        calContext = context;
    }

    public void updateCalendar(LocalDate now)
    {
        //LocalDate fromDate = now;
        cells = new ArrayList<>();

        displayMonth = now.getMonth().getValue();
        LocalDate fromDay = now;
        fromDay = fromDay.with(TemporalAdjusters.firstDayOfMonth());
        Log.d(LOG_TAG, "for " + now.toString() + " day of week: " + fromDay.getDayOfWeek());
        int displayBeginsAtDate = fromDay.getDayOfWeek().getValue();
        if (displayBeginsAtDate != 7) {
            fromDay = fromDay.plusDays(-displayBeginsAtDate);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL yyyy");
        txtDate.setText(now.format(formatter));

        while (cells.size() < DAYS_COUNT)
        {
            cells.add(fromDay);
            //Log.d(LOG_TAG, "updateCal: show day: " + fromDay.toString());
            fromDay = fromDay.plusDays(1);
        }

        header.setBackgroundColor(Color.WHITE);

        FirebaseRealtimeDatabase frb = new FirebaseRealtimeDatabase();
        //Log.d(LOG_TAG, "updatecal " + fromDate.toString() + " TO " + fromDate.plusDays(DAYS_COUNT-1).toString());
        // TODO: do we really need to fill and use 'cells'?
        frb.getEvents(fromDay.plusDays(-DAYS_COUNT), fromDay,this);
    }

    // For each day, we need to show the date and up to 3 events for that day. Users can long
    // press on a day to see all events for that day.
    private static class ViewHolder {
        public TextView day;
        public LocalDate date;
        ArrayList<Event> eventsForDay;
        public TextView[] events;
    }

    private class CalendarAdapter extends ArrayAdapter<LocalDate> {
        private ArrayList<Event> eventList;
        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<LocalDate> days, ArrayList<Event> events) {
            super(context, R.layout.control_calendar_day, days);
            this.eventList = events;
            inflater = LayoutInflater.from(context);
        }

        private int getColor(Event e) {
            if ( e.eventDesc.contains("Math") ) {
                //Log.d(LOG_TAG, "go into math" + e.eventDesc);
                return Color.parseColor("#FFD8E8");
            }
            if ( e.eventDesc.contains("History") )
                return Color.parseColor("#F8E791");
            if ( e.eventDesc.contains("Science") )
                return Color.parseColor("#97FF94");
            if ( e.eventDesc.contains("English") )
                return Color.parseColor("#FDA366");
            if ( e.eventDesc.contains("Physics") )
                return Color.parseColor("#4DFFC6");
            if ( e.eventDesc.contains("Language") )
                return Color.parseColor("#FF7272");
            if ( e.eventDesc.contains("Econ") )
                return Color.parseColor("#FFCEFF72");
            return Color.parseColor("#B0ADFF");
        }

        private void setCalEvent(int evNr, Event e, View view) {
            TextView tv = null;
            switch ( evNr ) {
                case 1:
                    tv = view.findViewById(R.id.calEvent1);
                    break;
                case 2:
                    tv = view.findViewById(R.id.calEvent2);
                    break;
                case 3:
                    tv = view.findViewById(R.id.calEvent3);
                    break;
            }
            if ( tv != null ) {
                tv.setBackgroundColor(getColor(e));
                String d = e.eventDesc;
                if ( d.length() > 6 )
                    d = d.substring(0,6);
                tv.setText(d);
            }
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            LocalDate day = getItem(position);
            ViewHolder holder = null;

            //Log.d("cal render", "create new view: " + position);
            // inflate item if it does not exist yet
            if (view == null) {
                holder = new ViewHolder();
                //holder.day = day;
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.control_calendar_day, parent, false);
                holder.date = day;
                holder.eventsForDay = new ArrayList<Event>();
                view.setTag(holder);
            }
            else {
                holder = (ViewHolder) view.getTag();
                holder.date = day;
                holder.eventsForDay = new ArrayList<Event>();
                Log.d(LOG_TAG, "view not null");
            }

            // if this day has an event
            view.setBackgroundResource(0);

            view.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ViewHolder v = (ViewHolder) view.getTag();
                    if (v != null && v.eventsForDay.size() > 0) {
                        Log.d(LOG_TAG, "on long press. show:" + v.date.toString() + " eventList Size:" + v.eventsForDay.size());
                        Intent intent = new Intent(calContext, DailyEvents.class);
                        intent.putExtra("EventsForDay", v.date.toString());
                        // Log.d("v.date description", "v.date= "+v.date.toString());

                        //Bundle args = new Bundle();
                        //args.putParcelableArrayList("EventList", v.eventsForDay);
                        //intent.putExtra("BUNDLE",args);

                        calContext.startActivity(intent);
                    }
                    return false;
                }
            });

            //Log.d(LOG_TAG, "getView show date: " + day.toString() + " showingMo:" + showingMonth);
            TextView tvDate = view.findViewById(R.id.calDate);
            tvDate.setText(String.valueOf(day.getDayOfMonth()));
            if ( displayMonth == day.getMonthValue() ) {
                tvDate.setTextColor(Color.BLUE);
                tvDate.setTypeface(null, Typeface.BOLD);
            }
            // This is current day.
            if (LocalDate.now().getDayOfMonth() == day.getDayOfMonth() &&
                LocalDate.now().getMonth() == day.getMonth() &&
                LocalDate.now().getYear() == day.getYear()) {
                tvDate.setTextColor(Color.GREEN);
            }

            // TODO: If the firebase returned event list is already sorted, then we can just
            // TODO: traverse eventList with an index to pick all events for a certain day.
            if (eventList != null)
            {
                for (Event eventDate : eventList)
                {
                    if ( eventDate.date.equals(day) ) {

                        //TextView ev1 = view.findViewById(R.id.calEvent1);
                        //ev1.setText("Event1");
                        //Log.d(LOG_TAG, "Have events for: " + day.toString());
                        //daysEvents.add(eventDate);
                        holder.eventsForDay.add(eventDate);
                    }
                }
                // Done with picking all events for the date in cell
                int k = 0;
                TextView ev1 = view.findViewById(R.id.calEvent1);
                TextView ev2 = view.findViewById(R.id.calEvent2);
                TextView ev3 = view.findViewById(R.id.calEvent3);
                ev1.setText("");
                ev2.setText("");
                ev3.setText("");
                for (Event ev : holder.eventsForDay) {
                    //Log.d("CALDISPLAY", "ev type:" + ev.type + " desc:" + ev.eventDesc);
                    if ((ev.type == CalEvent.CAL_EVENT_EXTRACURRICULAR.ordinal()) ||
                            (ev.type == CalEvent.CAL_EVENT_HOMEWORK.ordinal())) {
                        k++;
                        //Log.d("CALDISPLAY22", "ev type:" + ev.type + " desc:" + ev.eventDesc);
                        setCalEvent(k, ev, view);
                    }
                }
            }
            return view;
        }
    }

    public void setEventHandler(EventHandler eventHandler)
    {
        this.eventHandler = eventHandler;
    }

    public interface EventHandler
    {
        void onDayLongPress(LocalDate date);
    }
}