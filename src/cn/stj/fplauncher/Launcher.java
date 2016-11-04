
package cn.stj.fplauncher;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import cn.stj.fplauncher.BaseActivity.BottomKeyClickListener;
import cn.stj.fplauncher.utils.LunarCalendar;

;

public class Launcher extends BaseActivity implements BottomKeyClickListener {
    TimeChangeReceiver mTimeChangeReceiver;

    private ImageView mHour1;
    private ImageView mHour2;
    private ImageView mMinute1;
    private ImageView mMinute2;
    private TextView mAmPm;
    private TextView mDate;
    private TextView mLunarCalendar;
    private TextView mMobileOperators;
    private SimpleDateFormat mDateFormat;
    private static final String ACTION_START_CONTACTS = "cn.stj.action.START_CONTACTS";

    private static final HashMap<Integer, Integer> TIME_IMG_MAP = new HashMap<Integer, Integer>();

    private static final int KEY_DPAD_UP = 19;
    private static final int KEY_DPAD_DOWN = KEY_DPAD_UP + 1;
    private static final int KEY_DPAD_LEFT = KEY_DPAD_UP + 2;
    private static final int KEY_DPAD_RIGHT = KEY_DPAD_UP + 3;
    private static final String INTENT = "intent";

    private static final String TAG = "Launcher";
    static {
        TIME_IMG_MAP.put(0, R.drawable.time0);
        TIME_IMG_MAP.put(1, R.drawable.time1);
        TIME_IMG_MAP.put(2, R.drawable.time2);
        TIME_IMG_MAP.put(3, R.drawable.time3);
        TIME_IMG_MAP.put(4, R.drawable.time4);
        TIME_IMG_MAP.put(5, R.drawable.time5);
        TIME_IMG_MAP.put(6, R.drawable.time6);
        TIME_IMG_MAP.put(7, R.drawable.time7);
        TIME_IMG_MAP.put(8, R.drawable.time8);
        TIME_IMG_MAP.put(9, R.drawable.time9);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_launcher);
        View view = mAboveViewStub.inflate();

        setBottomKeyClickListener(this);
        setTopTitleBgResource(0);
        setBottomButtonsResource(0);

        mTimeChangeReceiver = new TimeChangeReceiver();
        IntentFilter timeIntentFilter = new IntentFilter();
        timeIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        timeIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        timeIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(mTimeChangeReceiver, timeIntentFilter);

        mDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        mHour1 = (ImageView) view.findViewById(R.id.hour1);
        mHour2 = (ImageView) view.findViewById(R.id.hour2);
        mMinute1 = (ImageView) view.findViewById(R.id.minute1);
        mMinute2 = (ImageView) view.findViewById(R.id.minute2);
        mAmPm = (TextView) view.findViewById(R.id.am_tv);
        mDate = (TextView) view.findViewById(R.id.date_tv);
        mLunarCalendar = (TextView) view.findViewById(R.id.lunar_calendar_tv);
        mMobileOperators = (TextView) view.findViewById(R.id.mobile_operators_tv);

        mHour1.setImageResource(R.drawable.time1);
        mHour2.setImageResource(R.drawable.time2);
        mMinute1.setImageResource(R.drawable.time3);
        mMinute2.setImageResource(R.drawable.time4);

        updateTimeDateTemplate();

    }

    private Intent getEnterMainMenuIntent() {
        Intent intent = new Intent();
        ComponentName component = new ComponentName("cn.stj.fplauncher",
                "cn.stj.fplauncher.MainMenu");
        intent.setComponent(component);
        return intent;
    }

    private Intent getMiddleBtnIntent() {
        return null;
    }

    private Intent getRightBtnIntent() {
        Intent intent = new Intent();
        intent.setAction(ACTION_START_CONTACTS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        return intent;
    }

    private class TimeChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String action = intent.getAction();
                if (Intent.ACTION_TIME_TICK.equals(action)
                        || Intent.ACTION_TIME_CHANGED.equals(action)
                        || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
                    updateTimeDateTemplate();
                }
            }
        }
    }

    private void updateTimeDateTemplate() {
        updateTime();
        updateDate();
        updateLunarCalendar();
    }

    private void updateDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String dateStr = mDateFormat.format(date);
        mDate.setText(dateStr);

    }

    private void updateLunarCalendar() {
        try {
            Calendar calendar = Calendar.getInstance();
            LunarCalendar lunarCalendar = new LunarCalendar(calendar);
            String lunar = lunarCalendar.toStringWithoutYear();
            String week = getResources().getString(getWeekRes());

            mLunarCalendar.setText(lunar + " " + week);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = 0;
        int minute = 0;
        int am_pm = 0;
        boolean is24Hformate = DateFormat.is24HourFormat(this);
        if (is24Hformate) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            mAmPm.setVisibility(View.GONE);
        } else {
            hour = calendar.get(Calendar.HOUR);
            am_pm = calendar.get(Calendar.AM_PM);
            mAmPm.setVisibility(View.VISIBLE);
            mAmPm.setText((am_pm == Calendar.AM) ? R.string.am_title : R.string.pm_title);
        }
        minute = calendar.get(Calendar.MINUTE);
        formateTime(mHour1, mHour2, hour);
        formateTime(mMinute1, mMinute2, minute);
    }

    private void formateTime(ImageView view1, ImageView view2, int t) {
        String time = String.valueOf(t);
        String timeStr1 = "0";
        String timeStr2 = "0";
        int timeLen = time.length();
        if (timeLen < 2) {
            timeStr2 = time;
        } else {
            timeStr1 = String.valueOf(time.charAt(0));
            timeStr2 = String.valueOf(time.charAt(1));
        }
        int time1 = Integer.parseInt(timeStr1);
        int time2 = Integer.parseInt(timeStr2);

        view1.setImageResource(TIME_IMG_MAP.get(time1));
        view2.setImageResource(TIME_IMG_MAP.get(time2));
    }

    private int getWeekRes() {
        Calendar cal = Calendar.getInstance();
        int week = cal.get(Calendar.DAY_OF_WEEK);
        switch (week) {
            case Calendar.SUNDAY:
                return R.string.sunday;
            case Calendar.MONDAY:
                return R.string.monday;
            case Calendar.TUESDAY:
                return R.string.tuesday;
            case Calendar.WEDNESDAY:
                return R.string.wednesday;
            case Calendar.THURSDAY:
                return R.string.thursday;
            case Calendar.FRIDAY:
                return R.string.friday;
            case Calendar.SATURDAY:
                return R.string.saturday;
            default:
                return 0;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_POUND) {
            if (keyCode == KeyEvent.KEYCODE_STAR) {
                starDialpad("*");
            } else if (keyCode == KeyEvent.KEYCODE_POUND) {
                starDialpad("#");
            } else {
                starDialpad(String.valueOf(keyCode - KeyEvent.KEYCODE_0));
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimeChangeReceiver != null) {
            unregisterReceiver(mTimeChangeReceiver);
        }
    }

    @Override
    public Button BuildLeftBtn(Button v) {
        v.setText(R.string.menu);
        return v;
    }

    @Override
    public Button BuildMiddleBtn(Button v) {
        return null;
    }

    @Override
    public Button BuildRightBtn(Button v) {
        v.setText(R.string.contacts_menumain_title);
        return v;
    }

    @Override
    public TextView BuildTopTitle(TextView v) {
        return null;
    }

    @Override
    public void onLeftKeyPress() {
        Log.d(TAG, "onLeftKeyPress");
        Intent intent = getEnterMainMenuIntent();
        startActivity(intent);
    }

    @Override
    public void onMiddleKeyPress() {
        Intent intent = getEnterMainMenuIntent();
        startActivity(intent);
    }

    @Override
    public void onRightKeyPress() {
        Log.d(TAG, "onRightKeyPress");
        Intent intent = getRightBtnIntent();
        startActivity(intent);
    }

    private void starDialpad(String number) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    @Override
    public void onDirectLeftKeyPress() {
        startDirectKeyFunction(KEY_DPAD_LEFT);
    }

    @Override
    public void onDirectRightKeyPress() {
        startDirectKeyFunction(KEY_DPAD_RIGHT);
    }

    @Override
    public void onDirectUpKeyPress() {
        startDirectKeyFunction(KEY_DPAD_UP);
    }

    @Override
    public void onDirectDownKeyPress() {
        startDirectKeyFunction(KEY_DPAD_DOWN);
    }

    private void startDirectKeyFunction(int key) {
        ComponentName component = null;
        String componentStr = null;
        String directFunKey = String.valueOf(key) + "_" + INTENT;
        componentStr = Settings.Global.getString(getContentResolver(), directFunKey);

        if (componentStr != null) {
            component = ComponentName.unflattenFromString(componentStr);
            Intent intent = new Intent();
            intent.setComponent(component);
            startActivity(intent);
        }
    }
}
