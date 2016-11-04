
package cn.stj.fplauncher.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.CopyOnWriteArrayList;

public class ContactOperationService extends IntentService {
    private static final String TAG = ContactOperationService.class.getSimpleName();;
    public static final String ACTION_SAVE_CONTACT = "saveContact";
    public static final String ACTION_DELETE_CONTACT = "delete";
    public static final String ACTION_UPDATE_CONTACT = "update";

    public static final String EXTRA_CONTACT_URI = "contactUri";
    public static final String EXTRA_CALLBACK_INTENT = "callbackIntent";
    public static final String ACTION_SAVE_COMPLETED = "saveCompleted";
    public static final String KEY_NAME = "key_name";
    public static final String KEY_NUMBER = "key_number";
    public static final String KEY_UPDATE_RAW_ID = "key_update_raw_id";

    private Handler mMainHandler;

    private static final CopyOnWriteArrayList<Listener> sListeners =
            new CopyOnWriteArrayList<Listener>();

    public ContactOperationService() {
        super(TAG);
        setIntentRedelivery(true);
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Bundle data = intent.getExtras();
        if (ACTION_SAVE_CONTACT.equals(action)) {
            if (data != null) {
                String name = data.getString(KEY_NAME);
                String number = data.getString(KEY_NUMBER);
                saveContact(name, number, intent);
            }
        } else if (ACTION_UPDATE_CONTACT.equals(action)) {
            if (data != null) {
                String name = data.getString(KEY_NAME);
                String number = data.getString(KEY_NUMBER);
                int rawId = data.getInt(KEY_UPDATE_RAW_ID);
                updateContact(rawId, name, number, intent);
            }

        } else if (ACTION_DELETE_CONTACT.equals(action)) {
            if (data != null) {
                int rawId = data.getInt(KEY_UPDATE_RAW_ID);
                deleteContact(rawId, intent);
            }
        }
    }

    private void showToast(final int message) {
        mMainHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ContactOperationService.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static Intent createDeleteContactIntent(Context context,
            Class<? extends Activity> callbackActivity, String callbackAction) {
        Intent serviceIntent = new Intent(context, ContactOperationService.class);
        serviceIntent.setAction(ContactOperationService.ACTION_DELETE_CONTACT);

        if (callbackActivity != null) {
            Intent callbackIntent = new Intent(context, callbackActivity);
            callbackIntent.setAction(callbackAction);
            serviceIntent.putExtra(ContactOperationService.EXTRA_CALLBACK_INTENT, callbackIntent);
        }
        return serviceIntent;
    }

    public static Intent createSaveContactIntent(Context context,
            Class<? extends Activity> callbackActivity, String callbackAction) {
        Intent serviceIntent = new Intent(context, ContactOperationService.class);
        serviceIntent.setAction(ContactOperationService.ACTION_SAVE_CONTACT);

        if (callbackActivity != null) {
            Intent callbackIntent = new Intent(context, callbackActivity);
            callbackIntent.setAction(callbackAction);
            serviceIntent.putExtra(ContactOperationService.EXTRA_CALLBACK_INTENT, callbackIntent);
        }
        return serviceIntent;
    }

    public static Intent createUpdateContactIntent(Context context,
            Class<? extends Activity> callbackActivity, String callbackAction) {
        Intent serviceIntent = new Intent(context, ContactOperationService.class);
        serviceIntent.setAction(ContactOperationService.ACTION_UPDATE_CONTACT);

        if (callbackActivity != null) {
            Intent callbackIntent = new Intent(context, callbackActivity);
            callbackIntent.setAction(callbackAction);
            serviceIntent.putExtra(ContactOperationService.EXTRA_CALLBACK_INTENT, callbackIntent);
        }
        return serviceIntent;
    }

    private void saveContact(String name, String phoneNum, Intent intent) {
        ContentValues values = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(
                RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);

        if (!TextUtils.isEmpty(name)) {
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
            values.put(StructuredName.GIVEN_NAME, name);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
                    values);
        }

        if (!TextUtils.isEmpty(phoneNum)) {
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
            values.put(Phone.NUMBER, phoneNum);
            values.put(Phone.TYPE, Phone.TYPE_MOBILE);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
                    values);
        }

        Intent callbackIntent = intent.getParcelableExtra(EXTRA_CALLBACK_INTENT);
        if (callbackIntent != null) {
            deliverCallback(callbackIntent);
        }
    }

    public static int getRawContactId(ContentResolver resolver, String name, String number) {
        String selection = Phone.DISPLAY_NAME + "=?" + " AND " + Phone.TYPE + "=?" + " AND "
                + Phone.NUMBER + "=?";

        Cursor cursor = resolver.query(Data.CONTENT_URI, new String[] {
                Data.RAW_CONTACT_ID
        }, selection, new String[] {
                name, String.valueOf(Phone.TYPE_MOBILE), number
        }, null);

        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex(Data.RAW_CONTACT_ID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return -1;
    }

    private void updateContact(int rawId, String newName, String newNumber, Intent intent) {
        if (rawId == -1) {
            Log.i(TAG, "updateContact fail radId=" + rawId);
            return;
        }

        // phone number
        ContentValues values = new ContentValues();
        if ((newNumber != null) && (!TextUtils.isEmpty(newNumber.trim()))) {

            values.put(Data.RAW_CONTACT_ID, rawId);
            values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
            values.put(Phone.NUMBER, newNumber);
            values.put(Phone.TYPE, Phone.TYPE_MOBILE);

            String where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND "
                    + ContactsContract.Data.MIMETYPE + " = ? AND " + Phone.TYPE + " = ?";

            getContentResolver().update(
                    ContactsContract.Data.CONTENT_URI,
                    values,
                    where,
                    new String[] {
                            String.valueOf(rawId), Phone.CONTENT_ITEM_TYPE,
                            String.valueOf(Phone.TYPE_MOBILE)
                    });
        }

        // name
        if ((newName != null) && (!TextUtils.isEmpty(newName.trim()))) {
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawId);
            values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
            values.put(StructuredName.GIVEN_NAME, newName);

            String where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND "
                    + ContactsContract.Data.MIMETYPE + " = ? ";

            getContentResolver().update(
                    ContactsContract.Data.CONTENT_URI,
                    values,
                    where,
                    new String[] {
                            String.valueOf(rawId), StructuredName.CONTENT_ITEM_TYPE
                    });
        }

        Intent callbackIntent = intent.getParcelableExtra(EXTRA_CALLBACK_INTENT);
        if (callbackIntent != null) {
            deliverCallback(callbackIntent);
        }

    }

    private void deleteContact(int rawId, Intent intent) {
        getContentResolver().delete(
                ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawId), null, null);

        Intent callbackIntent = intent.getParcelableExtra(EXTRA_CALLBACK_INTENT);
        if (callbackIntent != null) {
            deliverCallback(callbackIntent);
        }
    }

    public interface Listener {
        public void onServiceCompleted(Intent callbackIntent);
    }

    public static void registerListener(Listener listener) {
        if (!(listener instanceof Activity)) {
            throw new ClassCastException("Only activities can be registered to"
                    + " receive callback from " + ContactOperationService.class.getName());
        }
        sListeners.add(0, listener);
    }

    public static void unregisterListener(Listener listener) {
        sListeners.remove(listener);
    }

    private void deliverCallback(final Intent callbackIntent) {
        mMainHandler.post(new Runnable() {

            @Override
            public void run() {
                deliverCallbackOnUiThread(callbackIntent);
            }
        });
    }

    private void deliverCallbackOnUiThread(final Intent callbackIntent) {
        for (Listener listener : sListeners) {
            if (callbackIntent.getComponent().equals(
                    ((Activity) listener).getIntent().getComponent())) {
                listener.onServiceCompleted(callbackIntent);
                return;
            }
        }
    }
}
