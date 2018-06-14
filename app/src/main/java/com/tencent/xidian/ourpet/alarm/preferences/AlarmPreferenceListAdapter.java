package com.tencent.xidian.ourpet.alarm.preferences;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.TimePicker;


import com.tencent.xidian.ourpet.R;
import com.tencent.xidian.ourpet.alarm.Alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by maokelong on 2016/5/2.
 *
 * 闹钟preference中listview的listAadpter
 *
 */
public class AlarmPreferenceListAdapter extends BaseAdapter {
//        implements Serializable {

    private Context context;
    private Alarm alarm;
    private List<AlarmPreference> preferences = new ArrayList<AlarmPreference>();
    private final String[] repeatDays = {"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    public AlarmPreferenceListAdapter(Context context, Alarm alarm) {
        setContext(context);
        setMathAlarm(alarm);
    }

    @Override
    public int getCount() {
        return preferences.size();
    }

    @Override
    public Object getItem(int position) {
        return preferences.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlarmPreference alarmPreference = (AlarmPreference) getItem(position);
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        switch (alarmPreference.getType()) {
            case BOOLEAN:
                if (null == convertView || convertView.getId() != android.R.layout.simple_list_item_checked)
                    convertView = layoutInflater.inflate(android.R.layout.simple_list_item_checked, null);
                /**
                 * 绑定CheckedTextView
                 */
                CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
                checkedTextView.setText(alarmPreference.getTitle());
                checkedTextView.setChecked((Boolean) alarmPreference.getValue());
                break;
            case TIME:
                if (null == convertView || convertView.getId() != R.layout.alarm_preference_time_picker)
                    convertView = layoutInflater.inflate(R.layout.alarm_preference_time_picker, null);
                /**
                 * 绑定TimePicker
                 */
                TimePicker time = (TimePicker) convertView.findViewById(R.id.pref_time_picker);
                time.setIs24HourView(true);
                //加载时间
                Calendar calendar = alarm.getAlarmTime();
                time.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                time.setCurrentMinute(calendar.get(Calendar.MINUTE));
                //监听器
                time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        Calendar newAlarmTime = Calendar.getInstance();
                        newAlarmTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        newAlarmTime.set(Calendar.MINUTE, minute);
                        newAlarmTime.set(Calendar.SECOND, 0);
                        alarm.setAlarmTime(newAlarmTime);
                    }
                });

                break;
            case INTEGER:
            case STRING:
            case MULTIPLE_LIST:
            default:
                if (null == convertView || convertView.getId() != android.R.layout.simple_list_item_2)
                    convertView = layoutInflater.inflate(android.R.layout.simple_list_item_2, null);
                /**
                 * 绑定listView中item的上下两行text
                 */
                //第一行
                TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
                text1.setTextSize(18);
                text1.setText(alarmPreference.getTitle());
                //第二行
                TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
                text2.setText(alarmPreference.getSummary());
                break;
        }

        return convertView;
    }

    public Alarm getMathAlarm() {
        for (AlarmPreference preference : preferences) {
            switch (preference.getKey()) {
                case ALARM_ACTIVE:
                    alarm.setAlarmActive((Boolean) preference.getValue());
                    break;
                case ALARM_NAME:
                    alarm.setAlarmName((String) preference.getValue());
                    break;
                case ALARM_TIME:
                    alarm.setAlarmTime((String) preference.getValue());
                    break;
                case ALARM_REPEAT:
                    alarm.setDays((Alarm.Day[]) preference.getValue());
                    break;
            }
        }

        return alarm;
    }

    /**
     * 加载设置的条目
     * @param alarm
     */
    public void setMathAlarm(Alarm alarm) {
        this.alarm = alarm;
        preferences.clear();
        preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_TIME, "", alarm.getAlarmTimeString(), null, alarm.getAlarmTime(), AlarmPreference.Type.TIME));
        //preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_ACTIVE, "Active", null, null, alarm.getAlarmActive(), Type.BOOLEAN));
        preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_NAME, "标签", alarm.getAlarmName(), null, alarm.getAlarmName(), AlarmPreference.Type.STRING));
        preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_REPEAT, "重复", alarm.getRepeatDaysString(), repeatDays, alarm.getDays(), AlarmPreference.Type.MULTIPLE_LIST));

    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String[] getRepeatDays() {
        return repeatDays;
    }

}
