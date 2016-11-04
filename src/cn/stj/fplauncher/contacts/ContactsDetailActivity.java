
package cn.stj.fplauncher.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.stj.fplauncher.BaseActivity;
import cn.stj.fplauncher.R;
import cn.stj.fplauncher.utils.Constant;

public class ContactsDetailActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {
    private View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.contact_detail_activity);
        mRootView = mAboveViewStub.inflate();
        setActivityBgResource(0);
        setBottomKeyClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle data = intent.getExtras();
            String name = data.getString(Constant.KEY_CONTACT_NAME);
            String number = data.getString(Constant.KEY_CONTACT_NUMBER);

            TextView nameTextView = (TextView) mRootView.findViewById(R.id.contact_item_name);
            TextView numberTextView = (TextView) mRootView.findViewById(R.id.contact_item_number);

            nameTextView.setText(name);
            numberTextView.setText(number);

        }

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
        v.setText(R.string.title_lookup_contact);
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
