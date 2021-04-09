package com.example.technovation2021;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
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

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

import androidx.annotation.RequiresApi;

interface EventsFetchedListener {
    void onEventsFetchDone(ArrayList<Event> eventsScheduled);
    void onEventsFetchError(String error);
}

@RequiresApi(api = Build.VERSION_CODES.O)
public class CustomCalendar extends LinearLayout implements EventsFetchedListener
{
    private static final String LOG_TAG = "Calendar View";
    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    private static final String DATE_FORMAT = "MMM yyyy";

    // date format
    private String dateFormat;
    private ArrayList<LocalDate> cells;
    Context calContext;

    // current displayed month
    //private Calendar currentDate = Calendar.getInstance();
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
            Log.d(LOG_TAG, "event " + (i+1) + " " + evList.get(i).print());
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
        initControl(context, attrs);
        Log.d(LOG_TAG, "Have caller context");
        calContext = context;
    }

    /*
    public CustomCalendar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }
     */

    /**
     * Load control xml layout
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initControl(Context context, AttributeSet attrs)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_my_calendar, this);

        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();

        //updateCalendar(curDate);
    }

    private void loadDateFormat(AttributeSet attrs)
    {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try
        {
            // try to load provided date format, and fallback to default otherwise
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        }
        finally
        {
            ta.recycle();
        }
    }
    private void assignUiElements()
    {
        // layout is inflated, assign local variables to components
        header = (LinearLayout)findViewById(R.id.calendar_header);
        btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView)findViewById(R.id.calendar_next_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);
    }

    private void assignClickHandlers()
    {
        // show events of next month
        btnNext.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //currentDate.add(Calendar.MONTH, 1);
                //updateCalendar(LocalDate.now());
                curDate = curDate.plusMonths(1);
                updateCalendar(curDate);
            }
        });

        // show events of previous month
        btnPrev.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //currentDate.add(Calendar.MONTH, -1);
                //updateCalendar(LocalDate.now());
                curDate = curDate.plusMonths(-1);
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
    }

    /*
    public void updateCalendar()
    {
        LocalDate fromDay = curDate;
        fromDay = fromDay.with(TemporalAdjusters.firstDayOfMonth());
        Log.d(LOG_TAG, "day of week: " + fromDay.getDayOfWeek());
        int displayBeginsAtDate = fromDay.getDayOfWeek().getValue();
        fromDay = fromDay.plusDays(-displayBeginsAtDate);
        Log.d(LOG_TAG, "Show next 42 day events from " + fromDay.toString());
        updateCalendar(fromDay);
    }

     */

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

        // update title
        //SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        //txtDate.setText(sdf.format(now));//.getTime()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL yyyy");
        txtDate.setText(now.format(formatter));


/*
        Calendar calendar = (Calendar)currentDate.clone();

        Log.d("customcalendarxx", calendar.get(Calendar.DAY_OF_MONTH) + " " +
                calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.YEAR));
        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        Log.d("CALXX", "month begin " + monthBeginningCell);

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);
        Log.d("CalXXY", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        now = now.plusDays(-monthBeginningCell);
*/
        // fill cells
        while (cells.size() < DAYS_COUNT)
        {
            //Log.d("CalXXY", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            //EventDetail ed = new EventDetail(calendar.get(Calendar.DAY_OF_MONTH),
            //                    calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR));
            cells.add(fromDay);
            //Log.d(LOG_TAG, "updateCal: show day: " + fromDay.toString());
            //calendar.add(Calendar.DAY_OF_MONTH, 1);
            fromDay = fromDay.plusDays(1);
        }

        // update grid
        // grid.setAdapter(new CalendarAdapter(getContext(), cells, events));


        // set header color according to current season
        //int month = currentDate.get(Calendar.MONTH);

        header.setBackgroundColor(Color.WHITE);

        FirebaseRealtimeDatabase frb = new FirebaseRealtimeDatabase();
        //Log.d(LOG_TAG, "updatecal " + fromDate.toString() + " TO " + fromDate.plusDays(DAYS_COUNT-1).toString());
        // TODO: do we really need to fill and use 'cells'?
        frb.getEvents(fromDay.plusDays(-DAYS_COUNT), fromDay,this);
    }

    private static class ViewHolder {
        public TextView day;
        public LocalDate date;
        ArrayList<Event> eventsForDay;
        public TextView[] events;
    }

    private class CalendarAdapter extends ArrayAdapter<LocalDate>
    {
        // days with events
        private ArrayList<Event> eventList;

        // for view inflation
        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<LocalDate> days, ArrayList<Event> events)
        {
            super(context, R.layout.control_calendar_day, days);
            this.eventList = events;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            LocalDate day = getItem(position);
            //ArrayList<Event> daysEvents = new ArrayList<Event>();

            //Log.d(LOG_TAG, "show events for: " + day.toString());
            // today
            //Calendar today = Calendar.getInstance();
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
                tvDate.setTextColor(Color.RED);
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
                TextView ev1 = view.findViewById(R.id.calEvent1);
                if ( holder != null && holder.eventsForDay.size() > 0 ) {
                    ev1.setText(holder.eventsForDay.get(0).eventDesc);
                }
                else {
                    ev1.setText("NONE");
                    ev1.setTypeface(null, Typeface.ITALIC);
                    ev1.setTextColor(Color.LTGRAY);
                }
            }
/*
            // clear styling
            ((TextView)view).setTypeface(null, Typeface.NORMAL);
            ((TextView)view).setTextColor(Color.BLACK);

            // TODO: grey out all days except the days of month displayed
            if (month != today.get(Calendar.MONTH) || year != today.get(Calendar.YEAR))
            {
                ((TextView)view).setTextColor(Color.GRAY);
            }
            else if (day == today.get(Calendar.DAY_OF_MONTH))
            {
                // if it is today, set it to blue/bold
                ((TextView)view).setTypeface(null, Typeface.BOLD);
                ((TextView)view).setTextColor(BLUE);
            }

            // set text
            ((TextView)view).setText(String.valueOf(ed.getDay()));
            Log.d(" calendarXX", String.valueOf(ed.getDay()));
*/
            /*
            TextView tvDate = view.findViewById(R.id.calDate);
            TextView ev1 = view.findViewById(R.id.calEvent1);
            tvDate.setText(String.valueOf(day.getDayOfMonth()));
            ev1.setText("Event1");
             */
            return view;
        }
    }

    /**
     * Assign event handler to be passed needed events
     */
    public void setEventHandler(EventHandler eventHandler)
    {
        this.eventHandler = eventHandler;
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    public interface EventHandler
    {
        void onDayLongPress(LocalDate date);
    }
}