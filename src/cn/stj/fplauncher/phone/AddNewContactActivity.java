
package cn.stj.fplauncher.phone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.stj.fplauncher.BaseActivity;
import cn.stj.fplauncher.R;
import cn.stj.fplauncher.service.ContactOperationService;
import cn.stj.fplauncher.utils.Constant;

public class AddNewContactActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {
    private View mRootView;
    private EditText mNameEditText;
    private EditText mNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.phone_add_new_contact);
        mRootView = mAboveViewStub.inflate();
        mNameEditText = (EditText) mRootView.findViewById(R.id.contact_name);
        mNumberEditText = (EditText) mRootView.findViewById(R.id.contact_item_number);

        setBottomKeyClickListener(this);
        setActivityBgResource(0);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle data = intent.getExtras();
            String name = null;
            String number = null;
            if (data != null) {
                name = data.getString(Constant.KEY_CONTACT_NAME);
                number = data.getString(Constant.KEY_CONTACT_NUMBER);
            }
            mNameEditText.setText(name);
            mNumberEditText.setText(number);
        }
    }

    @Override
    public Button BuildLeftBtn(Button v) {
        v.setText(R.string.save);
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
        return null;
    }

    @Override
    public void onLeftKeyPress() {
        boolean result = saveContact();
        if (result) {
            finish();
        }
    }

    @Override
    public void onMiddleKeyPress() {

    }

    @Override
    public void onRightKeyPress() {
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private boolean saveContact() {
        String name = mNameEditText.getText().toString();
        String number = mNumberEditText.getText().toString();

        if (TextUtils.isEmpty(name.trim())) {
            showToast(getString(R.string.toast_name_null));
            return false;
        }
        if (TextUtils.isEmpty(number.trim())) {
            showToast(getString(R.string.toast_number_null));
            return false;
        }

        Bundle data = new Bundle();
        data.putString(ContactOperationService.KEY_NAME, name);
        data.putString(ContactOperationService.KEY_NUMBER, number);

        Intent intent = ContactOperationService.createSaveContactIntent(this, null, null);
        intent.putExtras(data);
        startService(intent);
        return true;
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
