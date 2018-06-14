package com.tencent.xidian.ourpet.alarm.alert;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.tencent.xidian.ourpet.R;
import com.tencent.xidian.ourpet.alarm.Alarm;


/**
 * Created by maokelong on 2016/4/29.
 *
 * 测试用的窗体：接收"alarm"广播后打开窗体并显示广播中的内容。
 *
 */
public class AlarmAlertActivity extends Activity {

	private Alarm alarm;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo);

		Bundle bundle = this.getIntent().getExtras();
		alarm = (Alarm) bundle.getSerializable("alarm");
		textView = (TextView) findViewById(R.id.txt_demo);
		textView.setText(alarm.getAlarmName());
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
}
