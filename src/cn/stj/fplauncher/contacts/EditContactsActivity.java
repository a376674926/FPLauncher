
package cn.stj.fplauncher.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.stj.fplauncher.BaseActivity;
import cn.stj.fplauncher.R;
import cn.stj.fplauncher.service.ContactOperationService;
import cn.stj.fplauncher.utils.Constant;

public class EditContactsActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener, ContactOperationService.Listener {
    private View mRootView;
    private EditText mNameEditText;
    private EditText mNumberEditText;
    private int mRawId = -1;
    private static final String TAG = EditContactsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.contact_edit_activity);
        mRootView = mAboveViewStub.inflate();

        setBottomKeyClickListener(this);
        setActivityBgResource(0);
        ContactOperationService.registerListener(this);

        mNameEditText = (EditText) mRootView.findViewById(R.id.contact_item_name);
        mNumberEditText = (EditText) mRootView.findViewById(R.id.contact_item_number);

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
            mRawId = ContactOperationService.getRawContactId(getContentResolver(), name, number);
        }
    }

    @Override
    public Button BuildLeftBtn(Button v) {
        v.setText(R.string.mms_save);
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
        v.setText(R.string.title_edit_contact);
        return v;
    }

    @Override
    public void onLeftKeyPress() {
        if (mRawId == -1) {
            Log.i(TAG, "onLeftKey press mRawId=" + mRawId);
        }

        updateContact();
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

    private boolean updateContact() {
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
        data.putInt(ContactOperationService.KEY_UPDATE_RAW_ID, mRawId);

        Intent intent = ContactOperationService.createUpdateContactIntent(this,
                EditContactsActivity.class, Constant.ACTION_UPDATE_CALLBACK);

        intent.putExtras(data);
        startService(intent);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ContactOperationService.unregisterListener(this);
    }

    @Override
    public void onServiceCompleted(Intent callbackIntent) {
        String action = callbackIntent.getAction();
        if (Constant.ACTION_UPDATE_CALLBACK.equals(action)) {
            Intent intent = new Intent();
            intent.setClass(EditContactsActivity.this, ContactsListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
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
