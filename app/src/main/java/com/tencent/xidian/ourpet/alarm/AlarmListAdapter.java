package com.tencent.xidian.ourpet.alarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.tencent.xidian.ourpet.R;
import com.tencent.xidian.ourpet.alarm.database.Database;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by maokelong on 2016/5/2.
 *
 * 闹钟列表中listview的listAadpter
 *
 */

public class AlarmListAdapter extends BaseAdapter {

    private AlarmActivity alarmActivity;
    private List<Alarm> alarms = new ArrayList<Alarm>();

    public static final String ALARM_FIELDS[] = { Database.COLUMN_ALARM_ACTIVE,
            Database.COLUMN_ALARM_TIME, Database.COLUMN_ALARM_DAYS };

    public AlarmListAdapter(AlarmActivity alarmActivity) {
        this.alarmActivity = alarmActivity;
//		Database.init(alarmActivity);
//		alarms = Database.getAll();
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (null == view)
            view = LayoutInflater.from(alarmActivity).inflate(
                    R.layout.alarm_list_element, null);

        Alarm alarm = (Alarm) getItem(position);

        ToggleButton toggleButton = (ToggleButton) view.findViewById(R.id.checkBox_alarm_active);
        toggleButton.setChecked(alarm.getAlarmActive());
        toggleButton.setTag(position);
        toggleButton.setOnClickListener(alarmActivity);

        TextView alarmTimeView = (TextView) view
                .findViewById(R.id.textView_alarm_time);
        alarmTimeView.setText(alarm.getAlarmTimeString());


        TextView alarmDaysView = (TextView) view
                .findViewById(R.id.textView_alarm_days);
        alarmDaysView.setText(alarm.getRepeatDaysString());


        return view;
    }

    public List<Alarm> getMathAlarms() {
        return alarms;
    }

    public void setMathAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
    }

}
