
package cn.stj.fplauncher.contacts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import cn.stj.fplauncher.BaseActivity;
import cn.stj.fplauncher.R;
import cn.stj.fplauncher.phone.AddNewContactActivity;
import cn.stj.fplauncher.service.ContactOperationService;
import cn.stj.fplauncher.utils.Constant;

public class ContactOptionsMenuActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener, OnDismissListener, ContactOperationService.Listener {

    private BaseMenuItemAdapter mAdapter;
    private String mName;
    private String mNumber;
    private static boolean HAS_CONTACT = true;
    private AlertDialog mDeleteConfirmDialog;
    private int mSelectePosition;

    private static enum MENU_FUNCTION_NO_CONTACT {
        ADD_NEW_CONTACT
    }

    private static enum MENU_FUNCTION_HAS_CONTACT {
        DIAL, SEND_SMS, LOOKUP, EDIT, DELETE, PHONEBOOK_SETTINGS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_base_listview);
        setBottomKeyClickListener(this);
        ContactOperationService.registerListener(this);

        View listView = mAboveViewStub.inflate();
        TextView emptyTextView = (TextView) listView.findViewById(R.id.empty);
        emptyTextView.setVisibility(View.GONE);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle data = intent.getExtras();
            if (data != null) {
                mName = data.getString(Constant.KEY_CONTACT_NAME);
                mNumber = data.getString(Constant.KEY_CONTACT_NUMBER);
                String[] menuItems = getResources().getStringArray(R.array.contact_menu);

                if (TextUtils.isEmpty(mName) && TextUtils.isEmpty(mNumber)) {
                    menuItems = getResources().getStringArray(R.array.contact_no_contacts_menu);
                    HAS_CONTACT = false;
                } else {
                    menuItems = getResources().getStringArray(R.array.contact_menu);
                    HAS_CONTACT = true;
                }

                mAdapter = new BaseMenuItemAdapter(this, Arrays.asList(menuItems));
                final ListView lv = (ListView) findViewById(R.id.base_list_view);
                lv.setAdapter(mAdapter);
                lv.setOnItemClickListener(new OptionMenuItemClickListener());
                lv.setOnItemSelectedListener(new OptionMenuItemSelectedListener());
            }
        }
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
        onMenuItemClick(mSelectePosition);
    }

    @Override
    public void onMiddleKeyPress() {

    }

    @Override
    public void onRightKeyPress() {
        finish();
    }

    private void startAddNewContactActivity() {
        Intent intent = new Intent(this, AddNewContactActivity.class);
        startActivity(intent);
    }

    private class OptionMenuItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mSelectePosition = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub

        }

    }

    private class OptionMenuItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            onMenuItemClick(position);
        }
    }

    private void onMenuItemClick(int position) {

        if (HAS_CONTACT) {

            if (position == MENU_FUNCTION_HAS_CONTACT.DIAL.ordinal()) {
                dial();
            } else if (position == MENU_FUNCTION_HAS_CONTACT.SEND_SMS.ordinal()) {
                sendSMS();
            } else if (position == MENU_FUNCTION_HAS_CONTACT.LOOKUP.ordinal()) {
                lookupContact();
            } else if (position == MENU_FUNCTION_HAS_CONTACT.EDIT.ordinal()) {
                editContact();
            } else if (position == MENU_FUNCTION_HAS_CONTACT.DELETE.ordinal()) {
                showDeleteConfirmDialog();
            } else if (position == MENU_FUNCTION_HAS_CONTACT.PHONEBOOK_SETTINGS.ordinal()) {

            }

        } else {
            if (position == MENU_FUNCTION_NO_CONTACT.ADD_NEW_CONTACT.ordinal()) {
                addNewContact();
            } 

        }
    }

    private void dial() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mNumber));
        startActivity(intent);
    }

    private void sendSMS() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + mNumber));
        startActivity(intent);
    }

    private void editContact() {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_CONTACT_NAME, mName);
        bundle.putString(Constant.KEY_CONTACT_NUMBER, mNumber);
        Intent intent = new Intent(this, EditContactsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void lookupContact() {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_CONTACT_NAME, mName);
        bundle.putString(Constant.KEY_CONTACT_NUMBER, mNumber);

        Intent intent = new Intent(getApplicationContext(), ContactsDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void deleteContact() {
        Bundle data = new Bundle();
        int rawId = ContactOperationService.getRawContactId(getContentResolver(), mName, mNumber);
        data.putInt(ContactOperationService.KEY_UPDATE_RAW_ID, rawId);

        Intent intent = ContactOperationService.createDeleteContactIntent(this,
                ContactOptionsMenuActivity.class, Constant.ACTION_DELETE_CALLBACK);

        intent.putExtras(data);
        startService(intent);
    }

    private void showDeleteConfirmDialog() {
        mDeleteConfirmDialog = new AlertDialog.Builder(ContactOptionsMenuActivity.this,
                AlertDialog.THEME_HOLO_LIGHT)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setMessage(R.string.title_delete_contact)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteContact();
                            }
                        }
                ).create();
     // modify hhj:add keyevent
        mDeleteConfirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                Button positiveButton = mDeleteConfirmDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton = mDeleteConfirmDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                if (keyCode == KeyEvent.KEYCODE_MENU
                        && event.getRepeatCount() == 0) {
                    if (positiveButton.isFocused()) {
                        deleteContact();
                    }else if(negativeButton.isFocused()){
                        dialog.dismiss();
                    }
                }
                return false;
            }
        });
        mDeleteConfirmDialog.setOnDismissListener(this);
        mDeleteConfirmDialog.show();
        
    }

    private void addNewContact() {
        Intent intent = new Intent(this, AddNewContactActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDeleteConfirmDialog != null) {
            mDeleteConfirmDialog.dismiss();
            mDeleteConfirmDialog = null;
        }
        ContactOperationService.unregisterListener(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mDeleteConfirmDialog = null;
    }

    @Override
    public void onServiceCompleted(Intent callbackIntent) {
        String action = callbackIntent.getAction();
        if (Constant.ACTION_DELETE_CALLBACK.equals(action)) {
            finish();
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
