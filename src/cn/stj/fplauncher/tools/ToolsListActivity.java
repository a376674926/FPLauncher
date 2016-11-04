
package cn.stj.fplauncher.tools;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.Arrays;

import cn.stj.fplauncher.BaseActivity;
import cn.stj.fplauncher.R;

public class ToolsListActivity extends BaseActivity implements BaseActivity.BottomKeyClickListener {

    private BaseMenuItemAdapter mAdapter;

    private static enum MENU_FUNCTION_TOOLS {
        ALARM, CALCULATOR, CALENDER, FLASHLIGHT
    }

    private static final String COMPONENT_START_ALARMCLOCK = "com.android.deskclock/.DeskClock";
    private static final String COMPONENT_START_CALCULATOR = "cn.stj.calculator/.activity.CalculatorActivity";
    private static final String COMPONENT_START_FLASHLIGHT = "com.stj.light/.LightActivity";
    private static final String COMPONENT_START_CALENDER = "com.android.calendar/com.android.calendar.AllInOneActivity";
    private int mSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMiddleViewStub.setLayoutResource(R.layout.activity_base_listview);
        View listView = mMiddleViewStub.inflate();
        TextView emptyTextView = (TextView) listView.findViewById(R.id.empty);
        emptyTextView.setVisibility(View.GONE);

        setBottomKeyClickListener(this);
        setActivityBgResource(0);

        String[] menuItems = getResources().getStringArray(R.array.menu_tools);

        mAdapter = new BaseMenuItemAdapter(this, Arrays.asList(menuItems));
        final ListView lv = (ListView) findViewById(R.id.base_list_view);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(mOptionMenuItemClickListener);
        lv.setOnItemSelectedListener(mOptionMenuItemSelectedListener);

    }

    @Override
    public Button BuildLeftBtn(Button v) {
        v.setText(R.string.option_ok);
        return v;
    }

    @Override
    public Button BuildMiddleBtn(Button v) {
        return null;
    }

    @Override
    public Button BuildRightBtn(Button v) {
        v.setText(R.string.back);
        return v;
    }

    @Override
    public TextView BuildTopTitle(TextView v) {
        v.setText(R.string.application_menumain_title);
        return v;
    }

    @Override
    public void onLeftKeyPress() {
        onFunctionChoose(mSelectedPosition);
    }

    @Override
    public void onMiddleKeyPress() {

    }

    @Override
    public void onRightKeyPress() {
        finish();
    }

    private OnItemClickListener mOptionMenuItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onFunctionChoose(position);
        }
    };

    private OnItemSelectedListener mOptionMenuItemSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mSelectedPosition = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void onFunctionChoose(int position) {

        if (position == MENU_FUNCTION_TOOLS.ALARM.ordinal()) {
            openAlarmFunction();
        } else if (position == MENU_FUNCTION_TOOLS.CALCULATOR.ordinal()) {
            openCalculatorFunction();
        } else if (position == MENU_FUNCTION_TOOLS.CALENDER.ordinal()) {
            openCalenderFunction();
        } else if (position == MENU_FUNCTION_TOOLS.FLASHLIGHT.ordinal()) {
            openFlashLightFunction();
        }

    }

    private void openAlarmFunction() {
        launchApplication(COMPONENT_START_ALARMCLOCK);
    }

    private void openCalculatorFunction() {
        launchApplication(COMPONENT_START_CALCULATOR);
    }

    private void openCalenderFunction() {
        launchApplication(COMPONENT_START_CALENDER) ;
    }

    private void openFlashLightFunction() {
        launchApplication(COMPONENT_START_FLASHLIGHT);
    }
    
    private void launchApplication(String componentStr) {
        Intent intent = new Intent();
        ComponentName component = ComponentName.unflattenFromString(componentStr);
        intent.setComponent(component);
        startActivity(intent);
    }

    @Override
    public void onDirectLeftKeyPress() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onDirectRightKeyPress() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onDirectUpKeyPress() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onDirectDownKeyPress() {
        // TODO Auto-generated method stub
        
    }

}
