
package cn.stj.fplauncher.callLog;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.stj.fplauncher.BaseActivity;
import cn.stj.fplauncher.R;
import cn.stj.fplauncher.callLog.CallLogListActivity.CallLogDetailInfo;

public class CallLogDetailActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {
    private TextView mNameTv;
    private TextView mNumberTv;
    private TextView mCallTypeTv;
    private TextView mCallDateTv;
    private TextView mCallDurationTv;
    private Resources mResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMiddleViewStub.setLayoutResource(R.layout.calllog_detail_activity);
        View view = mMiddleViewStub.inflate();

        mResources = getResources();

        mNameTv = (TextView) findViewById(R.id.detail_contact_name);
        mNumberTv = (TextView) findViewById(R.id.detail_contact_number);
        mCallTypeTv = (TextView) findViewById(R.id.detail_contact_type);
        mCallDateTv = (TextView) findViewById(R.id.detail_contact_date);
        mCallDurationTv = (TextView) findViewById(R.id.detail_contact_duration);

        CallLogDetailInfo info = getIntent().getParcelableExtra("key_call_log_detail_info");
        String name = info.getName();
        String number = info.getNumber();
        int type = info.getType();
        String date = info.getDate();
        String duration = info.getDuration();

        if (!TextUtils.isEmpty(name)) {
            mNameTv.setText(name);
        }
        mNumberTv.setText(number);

        int typeResId = getCallTypeTextId(type);
        if (typeResId != -1) {
            mCallTypeTv.setText(mResources.getString(typeResId));
        }

        mCallDateTv.setText(timeFormat(date));
        mCallDurationTv.setText(formatDuration(this, Long.valueOf(duration)));
        setBottomKeyClickListener(this);
        setActivityBgResource(0);
    }

    @Override
    public Button BuildLeftBtn(Button v) {
        return null;
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
        v.setText(R.string.title_calllog);
        return v;
    }

    @Override
    public void onLeftKeyPress() {

    }

    @Override
    public void onMiddleKeyPress() {

    }

    @Override
    public void onRightKeyPress() {
        finish();
    }

    private String timeFormat(String time) {
        Date date = new Date(Long.parseLong(time));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(date);
    }

    private int getCallTypeTextId(int type) {
        int resId = -1;
        switch (type) {
            case Calls.INCOMING_TYPE:
                resId = R.string.call_type_incoming;
                break;
            case Calls.MISSED_TYPE:
                resId = R.string.call_type_missing;
                break;
            case Calls.OUTGOING_TYPE:
                resId = R.string.call_type_outgoing;
                break;
            default:
                break;
        }
        return resId;
    }

    private static String formatDuration(Context context, long seconds) {
        long h = seconds / 3600;
        long m = (seconds - h * 3600) / 60;
        long s = seconds - (h * 3600 + m * 60);
        String durationValue = String.format(
                context.getString(R.string.call_log_duration_format_ms), m, s);
        return durationValue;
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
