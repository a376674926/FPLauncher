
package cn.stj.fplauncher.callLog;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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
import cn.stj.fplauncher.utils.Constant;

public class CallLogOptionsMenuActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {
    private BaseMenuItemAdapter mAdapter;
    private int mCurrentIndexPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_base_listview);
        setBottomKeyClickListener(this);

        View listView = mAboveViewStub.inflate();
        TextView emptyTextView = (TextView) listView.findViewById(R.id.empty);
        emptyTextView.setVisibility(View.GONE);

        String[] menuItems = getResources().getStringArray(R.array.contact_menu);

        boolean isInContacts = getIntent().getBooleanExtra("key_number_in_contact", false);
        if (isInContacts) {
            menuItems = getResources().getStringArray(R.array.callLog_menu_contact);
        } else {
            menuItems = getResources().getStringArray(R.array.callLog_menu_unKnow);
        }

        mAdapter = new BaseMenuItemAdapter(this, Arrays.asList(menuItems));

        final ListView lv = (ListView) findViewById(R.id.base_list_view);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new OptionMenuItemClickListener());
        lv.setOnItemSelectedListener(mItemSelectedListener);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            onLeftKeyPress();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public Button BuildLeftBtn(Button v) {
        v.setText(R.string.select);
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
        v.setText(R.string.option);
        return v;
    }

    @Override
    public void onLeftKeyPress() {
        onMenuItemClick(mCurrentIndexPos);
    }

    @Override
    public void onMiddleKeyPress() {

    }

    @Override
    public void onRightKeyPress() {
        finish();
    }

    private class OptionMenuItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onMenuItemClick(position);
        }
    }

    private OnItemSelectedListener mItemSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mCurrentIndexPos = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }

    };

    private void onMenuItemClick(int position) {
        Intent intent = new Intent();
        intent.putExtra(Constant.KEY_CALL_LOG_OPTIONS, position);
        setResult(Constant.RESULT_OK, intent);
        finish();
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
