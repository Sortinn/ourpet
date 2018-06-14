package com.tencent.xidian.ourpet.alarm.preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.xidian.ourpet.R;
import com.tencent.xidian.ourpet.alarm.Alarm;
import com.tencent.xidian.ourpet.alarm.BaseActivity;
import com.tencent.xidian.ourpet.alarm.database.Database;


/**
 * Created by maokelong on 2016/4/29.
 *
 * 对闹钟进行设置的activity
 *
 */
public class AlarmPreferencesActivity extends BaseActivity {
    Button button;
    ImageButton deleteButton;
    TextView okButton;
    TextView cancelButton;
    private Alarm alarm;
    private ListAdapter listAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.alarm_preferences);

        /* 进入选中（新建）闹钟 */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("alarm")) {
            setMathAlarm((Alarm) bundle.getSerializable("alarm"));
        } else {
            setMathAlarm(new Alarm());
        }
        if (bundle != null && bundle.containsKey("adapter")) {
            setListAdapter((AlarmPreferenceListAdapter) bundle.getSerializable("adapter"));
        } else {
            setListAdapter(new AlarmPreferenceListAdapter(this, getMathAlarm()));
        }
        /**
         * 绑定listView中item的点击事件
         */
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                final AlarmPreferenceListAdapter alarmPreferenceListAdapter = (AlarmPreferenceListAdapter) getListAdapter();
                final AlarmPreference alarmPreference = (AlarmPreference) alarmPreferenceListAdapter.getItem(position);

                AlertDialog.Builder alert;
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                switch (alarmPreference.getType()) {
                    case BOOLEAN:
                        /**
                         * 对于checkBox的点击
                         */
                        CheckedTextView checkedTextView = (CheckedTextView) v;
                        boolean checked = !checkedTextView.isChecked();
                        ((CheckedTextView) v).setChecked(checked);
                        switch (alarmPreference.getKey()) {
                            case ALARM_ACTIVE:
                                alarm.setAlarmActive(checked);
                                break;
                        }
                        alarmPreference.setValue(checked);
                        break;
                    case STRING:
                        /**
                         * 对于label的录入
                         */
                        alert = new AlertDialog.Builder(AlarmPreferencesActivity.this);

                        alert.setTitle(alarmPreference.getTitle());
                        // alert.setMessage(message);

                        // Set an EditText view to get user input
                        final EditText input = new EditText(AlarmPreferencesActivity.this);

                        input.setText(alarmPreference.getValue().toString());

                        alert.setView(input);
                        alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                alarmPreference.setValue(input.getText().toString());

                                if (alarmPreference.getKey() == AlarmPreference.Key.ALARM_NAME) {
                                    alarm.setAlarmName(alarmPreference.getValue().toString());
                                }

                                alarmPreferenceListAdapter.setMathAlarm(getMathAlarm());
                                alarmPreferenceListAdapter.notifyDataSetChanged();
                            }
                        });
                        alert.show();
                        break;
                    case MULTIPLE_LIST:
                        /**
                         * 对于复选列表的点击
                         */
                        alert = new AlertDialog.Builder(AlarmPreferencesActivity.this);

                        alert.setTitle(alarmPreference.getTitle());
                        // alert.setMessage(message);

                        CharSequence[] multiListItems = new CharSequence[alarmPreference.getOptions().length];
                        for (int i = 0; i < multiListItems.length; i++)
                            multiListItems[i] = alarmPreference.getOptions()[i];

                        boolean[] checkedItems = new boolean[multiListItems.length];
                        for (Alarm.Day day : getMathAlarm().getDays()) {
                            checkedItems[day.ordinal()] = true;
                        }
                        alert.setMultiChoiceItems(multiListItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog, int which, boolean isChecked) {

                                Alarm.Day thisDay = Alarm.Day.values()[which];

                                if (isChecked) {
                                    alarm.addDay(thisDay);
                                } else {
                                    // Only remove the day if there are more than 1
                                    // selected
                                    if (alarm.getDays().length > 1) {
                                        alarm.removeDay(thisDay);
                                    } else {
                                        // If the last day was unchecked, re-check
                                        // it
                                        ((AlertDialog) dialog).getListView().setItemChecked(which, true);
                                    }
                                }

                            }
                        });
                        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                alarmPreferenceListAdapter.setMathAlarm(getMathAlarm());
                                alarmPreferenceListAdapter.notifyDataSetChanged();

                            }
                        });
                        alert.show();
                        break;
                    case TIME:
                        /**
                         * timePicker不作点击处理，相关监听器写在 AlarmPreferenceListAdapter中
                         */
//                        TimePickerDialog timePickerDialog = new TimePickerDialog(AlarmPreferencesActivity.this, new TimePickerDialog.OnTimeSetListener() {
//
//                            @Override
//                            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
//                                Calendar newAlarmTime = Calendar.getInstance();
//                                newAlarmTime.set(Calendar.HOUR_OF_DAY, hours);
//                                newAlarmTime.set(Calendar.MINUTE, minutes);
//                                newAlarmTime.set(Calendar.SECOND, 0);
//                                alarm.setAlarmTime(newAlarmTime);
//                                alarmPreferenceListAdapter.setMathAlarm(getMathAlarm());
//                                alarmPreferenceListAdapter.notifyDataSetChanged();
//                            }
//                        }, alarm.getAlarmTime().get(Calendar.HOUR_OF_DAY), alarm.getAlarmTime().get(Calendar.MINUTE), true);
//                        timePickerDialog.setTitle(alarmPreference.getTitle());
//                        timePickerDialog.show();
                        break;
                    default:
                        break;
                }
            }

        });

        /**
         * 删除按钮
         * 新建闹钟时隐藏，反之绑定
         */
        button = (Button) findViewById(R.id.listButton);
        if (getMathAlarm().getId() < 1) {
            button.setVisibility(View.GONE);
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AlarmPreferencesActivity.this);
                    dialog.setTitle("删除");
                    dialog.setMessage("是否删除此闹钟？");
                    dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Database.init(getApplicationContext());
                            Database.deleteEntry(alarm);
                            callMathAlarmScheduleService();
                            finish();
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

        }

    }

    /**
     * 建立时显示菜单相
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.menu_item_new).setVisible(false);
        return result;
    }

    /**
     * 选择菜单项的处理逻辑
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save:
                Database.init(getApplicationContext());
                if (getMathAlarm().getId() < 1) {
                    Database.create(getMathAlarm());
                } else {
                    Database.update(getMathAlarm());
                }
                callMathAlarmScheduleService();
                Toast.makeText(AlarmPreferencesActivity.this, getMathAlarm().getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private CountDownTimer alarmToneTimer;

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putSerializable("alarm", getMathAlarm());
//        outState.putSerializable("adapter", (AlarmPreferenceListAdapter) getListAdapter());
//    };

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public Alarm getMathAlarm() {
        return alarm;
    }

    public void setMathAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public ListAdapter getListAdapter() {
        return listAdapter;
    }

    public void setListAdapter(ListAdapter listAdapter) {
        this.listAdapter = listAdapter;
        getListView().setAdapter(listAdapter);

    }

    public ListView getListView() {
        if (listView == null)
            listView = (ListView) findViewById(android.R.id.list);
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    @Override
    public void onClick(View v) {
        // super.onClick(v);

    }


}
