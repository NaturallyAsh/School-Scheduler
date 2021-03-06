package com.example.ashleighwilson.schoolscheduler.timetable;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ashleighwilson.schoolscheduler.R;
import static com.example.ashleighwilson.schoolscheduler.timetable.WeekViewUtil.isSameDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class CalendarAdapter extends BaseAdapter
{
    private static final String TAG = CalendarAdapter.class.getSimpleName();

    static final int FIRST_DAY_OF_WEEK =0;
    Context context;
    Calendar cal;
    public String[] days;
    public Event currentDay;
    private Calendar currentCal;
    List<EventRect> mEventRects;

    ArrayList<Event> dayList = new ArrayList<Event>();

    public CalendarAdapter(Context context, Calendar cal, List<EventRect> eventRects){
        this.cal = cal;
        this.context = context;
        cal.set(Calendar.DAY_OF_MONTH, 1);
        refreshDays(eventRects);
        Log.i(TAG, "created");
    }

    @Override
    public int getCount() {
        return days.length;
    }

    @Override
    public Object getItem(int position) {
        return dayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public int getPrevMonth(){
        if(cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)){
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR-1));
        }else{

        }
        int month = cal.get(Calendar.MONTH);
        if(month == 0){
            return month = 11;
        }

        return month-1;
    }

    public int getMonth(){
        return cal.get(Calendar.MONTH);
    }

    public Event getCalDate() {
        /*Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, currentDay.getDay());
        calendar.set(Calendar.MONTH, currentDay.getMonth());
        calendar.set(Calendar.YEAR, currentDay.getYear());

        calendar.getTimeInMillis();
        if (currentDay != null) {
            return calendar;
        }
        return null;*/
        return currentDay;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(position >= 0 && position < 7){
            v = vi.inflate(R.layout.day_of_week, null);
            TextView day = v.findViewById(R.id.textView3);

            if(position == 0){
                day.setText(R.string.sunday);
            }else if(position == 1){
                day.setText(R.string.monday);
            }else if(position == 2){
                day.setText(R.string.tuesday);
            }else if(position == 3){
                day.setText(R.string.wednesday);
            }else if(position == 4){
                day.setText(R.string.thursday);
            }else if(position == 5){
                day.setText(R.string.friday);
            }else if(position == 6){
                day.setText(R.string.saturday);
            }

        }else{

            v = vi.inflate(R.layout.day_view, null);
            FrameLayout today = v.findViewById(R.id.today_frame);
            Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
            Event day = dayList.get(position);
            currentCal = (Calendar) cal.clone();
            currentCal.clear();
            currentCal.set(Calendar.MONTH, day.getMonth());
            currentCal.set(Calendar.YEAR, day.getYear());
            currentCal.set(Calendar.DAY_OF_MONTH, day.getDay());

            TextView dayTV = v.findViewById(R.id.textView1);

            RelativeLayout rl = v.findViewById(R.id.rl);
            ImageView iv = v.findViewById(R.id.imageView1);

            iv.setVisibility(View.INVISIBLE);
            dayTV.setVisibility(View.VISIBLE);
            rl.setVisibility(View.VISIBLE);

            if (day.events == null) {
                day.events = new ArrayList<>();
            }
            day.events.clear();

            for (EventRect rect : mEventRects) {
                if (isSameDay(rect.event.getStartTime(), currentCal)) {
                    iv.setVisibility(View.VISIBLE);
                    day.addDayEvent(rect.event);
                }
            }

            if(day.getYear() == cal.get(Calendar.YEAR) && day.getMonth() == cal.get(Calendar.MONTH) && day.getDay() == cal.get(Calendar.DAY_OF_MONTH)){
                currentDay = day;
                today.setVisibility(View.VISIBLE);
            }else{
                today.setVisibility(View.GONE);
            }

            if(day.getDay() == 0){
                rl.setVisibility(View.GONE);
            }else{
                dayTV.setVisibility(View.VISIBLE);
                dayTV.setText(String.valueOf(day.getDay()));
            }
        }

        return v;
    }

    public void refreshDays(List<EventRect> eventRects) {
        // clear items
        dayList.clear();

        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)+7;
        int firstDay = cal.get(Calendar.DAY_OF_WEEK);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        TimeZone tz = TimeZone.getDefault();

        // figure size of the array
        if(firstDay==1){
            days = new String[lastDay+(FIRST_DAY_OF_WEEK*6)];
        }
        else {
            days = new String[lastDay+firstDay-(FIRST_DAY_OF_WEEK+1)];
        }

        int j=FIRST_DAY_OF_WEEK;

        // populate empty days before first real day
        if(firstDay>1) {
            for(j=0;j<(firstDay-FIRST_DAY_OF_WEEK)+7;j++) {
                days[j] = "";
                Event d = new Event(context,0,0,0);
                dayList.add(d);
            }
        }
        else {
            for(j=0;j<(FIRST_DAY_OF_WEEK*6)+7;j++) {
                days[j] = "";
                Event d = new Event(context,0,0,0);
                dayList.add(d);
            }
            j=FIRST_DAY_OF_WEEK*6+1; // sunday => 1, monday => 7
        }

        // populate days
        int dayNumber = 1;

        if(j>0 && dayList.size() > 0 && j != 1){
            dayList.remove(j-1);
        }

        for(int i=j-1;i<days.length;i++) {
            Event d = new Event(context,dayNumber,year,month);

            Calendar cTemp = Calendar.getInstance();
            cTemp.set(year, month, dayNumber);
            int startDay = Time.getJulianDay(cTemp.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cTemp.getTimeInMillis())));

            d.setAdapter(this);
            d.setCurrentDay(dayNumber);
            d.setStartDay(startDay);

            days[i] = ""+dayNumber;
            dayNumber++;
            dayList.add(d);
        }

        if (this.mEventRects == null) {
            this.mEventRects = new ArrayList<>();
        }
        this.mEventRects.clear();
        if (eventRects != null) {
            mEventRects.addAll(eventRects);
        }
    }
}
