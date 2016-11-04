
package cn.stj.fplauncher.callLog;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteFullException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.stj.fplauncher.BaseActivity;
import cn.stj.fplauncher.R;
import cn.stj.fplauncher.utils.Constant;
import cn.stj.fplauncher.utils.Constant.CALL_LOG_OPERATIONS;

public class CallLogListActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {

    private TextView mEmptyTextView;
    private ListView mListView;
    private static final int CALL_LOG_LOADER_ID = 1;
    private final CallLogLoader mCallLogLoaderCallbacks = new CallLogLoader();
    private CallLogAdapter mAdapter;
    private int mCurrentPosition = -1;
    private Handler mHandler = new Handler();
    private final ContentObserver mContactsObserver = new CustomContentObserver();
    private boolean isNeedupdateCache = true;

    private static final String[] PROJECTION = new String[] {
            Calls._ID, Calls.CACHED_NAME, Calls.NUMBER, Calls.TYPE, Calls.DATE, Calls.DURATION,
            Calls.NEW
    };

    private static final String[] _PROJECTION = new String[] {
            PhoneLookup._ID,
            PhoneLookup.DISPLAY_NAME,
            PhoneLookup.TYPE,
            PhoneLookup.LABEL,
            PhoneLookup.NUMBER,
            PhoneLookup.PHOTO_ID,
            PhoneLookup.LOOKUP_KEY,
            PhoneLookup.PHOTO_URI
    };

    private static final int PERSON_ID = 0;
    private static final int NAME = 1;
    private static final int PHONE_TYPE = 2;
    private static final int LABEL = 3;
    private static final int MATCHED_NUMBER = 4;
    private static final int PHOTO_ID = 5;
    private static final int LOOKUP_KEY = 6;
    private static final int PHOTO_URI = 7;

    public enum Tasks {
        QUERY_FROM_CALL_LOG,
        REMOVE_FROM_CALL_LOG
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMiddleViewStub.setLayoutResource(R.layout.activity_base_listview);

        View view = mMiddleViewStub.inflate();
        mEmptyTextView = (TextView) view.findViewById(R.id.empty);

        setBottomKeyClickListener(this);
        setActivityBgResource(0);

        mListView = (ListView) findViewById(R.id.base_list_view);
        mAdapter = new CallLogAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mMenuItemClickListener);
        mListView.setOnItemSelectedListener(mMenuItemSelectedListener);

        getLoaderManager().initLoader(CALL_LOG_LOADER_ID, null, mCallLogLoaderCallbacks);
        getContentResolver().registerContentObserver(Contacts.CONTENT_URI, true, mContactsObserver);
    }

    private class CallLogAdapter extends CursorAdapter {
        private LayoutInflater mInflater;

        public CallLogAdapter(Context context) {
            super(context, null);
            this.mInflater = ((LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            String name = cursor.getString(cursor.getColumnIndex(Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));
            int  callType = cursor.getInt(cursor.getColumnIndex(Calls.TYPE));
            if (!TextUtils.isEmpty(name)) {
                holder.itemText.setText(name);
            } else {
                holder.itemText.setText(number);
            }
             if(callType == Calls.OUTGOING_TYPE){
            	holder.itemType.setImageResource(R.drawable.ic_call_outgoing_holo_dark_ex);
            	holder.itemTypeStr.setText(R.string.call_type_outgoing);
            }else if(callType == Calls.MISSED_TYPE){
            	holder.itemType.setImageResource(R.drawable.ic_call_missed_holo_dark_ex);
            	holder.itemTypeStr.setText(R.string.call_type_missing);
            }else{
            	holder.itemType.setImageResource(R.drawable.ic_call_incoming_holo_dark_ex);
            	holder.itemTypeStr.setText(R.string.call_type_incoming);
            }
            String timeStr = cursor.getString(cursor.getColumnIndex(Calls.DATE));
            holder.itemTime.setText(timeFormat(timeStr));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.list_item_type_calllog_singleline2_text, null);
            ViewHolder holder = new ViewHolder();
            holder.itemText = (TextView) view.findViewById(R.id.litst_text);
            holder.itemTime = (TextView) view.findViewById(R.id.litst_text2);
            holder.itemType = (ImageView) view.findViewById(R.id.call_type);
            holder.itemTypeStr = (TextView) view.findViewById(R.id.call_type_str);
            view.setTag(holder);
            return view;
        }

        private class ViewHolder {
            TextView itemTime;
            TextView itemText;
            ImageView itemType;
            TextView itemTypeStr;
        }
    }

    @Override
    public Button BuildLeftBtn(Button v) {
        v.setText(R.string.option);
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
        v.setText(R.string.title_calllog);
        return v;
    }

    @Override
    public void onLeftKeyPress() {
        openCallLogOptionsMenuActivity();
    }

    @Override
    public void onMiddleKeyPress() {

    }

    @Override
    public void onRightKeyPress() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(CALL_LOG_LOADER_ID);
        getContentResolver().unregisterContentObserver(mContactsObserver);
    }

    private void openCallLogOptionsMenuActivity() {
        CallLogDetailInfo info = getCallLogDetailInfo();
        try{
	        if(info == null && mAdapter.getCursor() != null && mAdapter.getCursor().getCount()>0){
	        	mListView.setSelection(0);
	        	mCurrentPosition = 0;
	        	info = getCallLogDetailInfo();
	        }
        }catch(Exception e){}
        if(info == null){
        	return;
        }
        String number = info.getNumber();
        int id = getRawContactId(number);
        boolean isInContacts = ((id == -1) ? false : true);

        Intent intent = new Intent(this, CallLogOptionsMenuActivity.class);
        intent.putExtra("key_number_in_contact", isInContacts);

        startActivityForResult(intent, Constant.CALL_LOG_OPTIONS_REQUEST_CODE);
    }

    private void openCallLogDetailActivity(CallLogDetailInfo info) {
        Intent intent = new Intent(getApplicationContext(), CallLogDetailActivity.class);
        intent.putExtra("key_call_log_detail_info", info);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.CALL_LOG_OPTIONS_REQUEST_CODE
                && resultCode == Constant.RESULT_OK) {
            int optionId = data.getIntExtra(Constant.KEY_CALL_LOG_OPTIONS, -1);
            String number = null;
            Cursor cursor;
            if (mCurrentPosition != -1) {
                cursor = (Cursor) (mAdapter.getItem(mCurrentPosition));
                number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));
            }

            if (optionId == CALL_LOG_OPERATIONS.LOOK_UP_OPTION.ordinal()) {
                doLookupDetail();

            } else if (optionId == CALL_LOG_OPERATIONS.DELETE_OPTION.ordinal()) {
                removeFromCallLog();
            } else if (optionId == CALL_LOG_OPERATIONS.DIAL_OPTION.ordinal()) {
                dial(number);
            } else if (optionId == CALL_LOG_OPERATIONS.SAVE_CONTACT_OPTION.ordinal()) {
                saveContact();
            } else if (optionId == CALL_LOG_OPERATIONS.SEND_SMS_OPTION.ordinal()) {
                sendSMS(number);
            }
        }
    }

    private OnItemClickListener mMenuItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCurrentPosition = position;
            doLookupDetail();
        }
    };

    private OnItemSelectedListener mMenuItemSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mCurrentPosition = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            onLeftKeyPress();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static class CallLogDetailInfo implements Parcelable {

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public static Parcelable.Creator<CallLogDetailInfo> getCreator() {
            return CREATOR;
        }

        private String name;
        private String number;
        private int type;
        private String date;
        private String duration;

        public static final Parcelable.Creator<CallLogDetailInfo> CREATOR = new Parcelable.Creator<CallLogDetailInfo>() {

            @Override
            public CallLogDetailInfo createFromParcel(Parcel source) {
                CallLogDetailInfo info = new CallLogDetailInfo();
                info.setName(source.readString());
                info.setNumber(source.readString());
                info.setType(source.readInt());
                info.setDate(source.readString());
                info.setDuration(source.readString());
                return info;
            }

            @Override
            public CallLogDetailInfo[] newArray(int size) {
                return new CallLogDetailInfo[size];
            }

        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(number);
            dest.writeInt(type);
            dest.writeString(date);
            dest.writeString(duration);
        }

    }

    private class CallLogLoader implements LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri baseUri;
            baseUri = Calls.CONTENT_URI;
            return new CursorLoader(getApplicationContext(), baseUri,
                    PROJECTION, null, null, Calls.DATE + " desc ");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

            if (isNeedupdateCache) {
                while (cursor.moveToNext()) {
                    String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));
                    String name = cursor.getString(cursor.getColumnIndex(Calls.CACHED_NAME));
                    ContactInfo callLogInfo = new ContactInfo();
                    callLogInfo.number = number;
                    callLogInfo.name = name;

                    ContactInfo updateContactInfo = queryContactInfoForNumber(number);
                    updateCallLogContactInfoCache(number, updateContactInfo, callLogInfo);
                }
                isNeedupdateCache = false;
            }

            mEmptyTextView.setVisibility((cursor.getCount() > 0) ? View.GONE : View.VISIBLE);
            setLeftBtnVisible((cursor.getCount() > 0) ? View.VISIBLE : View.GONE);
            mAdapter.changeCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            mAdapter.swapCursor(null);
        }
    }

    private class ContactInfo {
        int id;
        String name;
        String number;
        int photoId;
    }

    private String timeFormat(String time) {
        Date date = new Date(Long.parseLong(time));
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    private void doLookupDetail() {
        CallLogDetailInfo info = getCallLogDetailInfo();
        if (info != null) {
            openCallLogDetailActivity(info);
        }
    }

    private void dial(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void sendSMS(String number) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + number));
        startActivity(intent);
    }

    private void removeFromCallLog() {
        CallLogDetailInfo info = getCallLogDetailInfo();
        final long id = queryCallLogId(info);
        String selection = Calls._ID + "=?";
        getContentResolver().delete(Calls.CONTENT_URI, selection, new String[] {
                String.valueOf(id)

        });
    }

    private void saveContact() {
        CallLogDetailInfo info = getCallLogDetailInfo();
        String name = info.getName();
        String number = info.getNumber();

        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_CONTACT_NAME, name);
        bundle.putString(Constant.KEY_CONTACT_NUMBER, number);

        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_SAVE_NEW_CONTACT);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private long queryCallLogId(CallLogDetailInfo info) {
        long id = -1;
        if (info != null) {
            String selection = Calls.NUMBER + "=?" + " AND " + Calls.DATE + "=?" + " AND "
                    + Calls.DURATION + "=?";

            String number = info.getNumber();
            String date = info.getDate();
            String duration = info.getDuration();

            String[] selectionArgs = new String[] {
                    number, date, duration
            };

            Cursor cursor = getContentResolver().query(Calls.CONTENT_URI, PROJECTION, selection,
                    selectionArgs,
                    Calls.DATE + " desc ");
            if (cursor.moveToFirst()) {
                id = cursor.getLong(cursor.getColumnIndex(Calls._ID));
            }
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return id;
    }

    private CallLogDetailInfo getCallLogDetailInfo() {
        if (mCurrentPosition != -1) {
            Cursor cursor = (Cursor) (mAdapter.getItem(mCurrentPosition));
            String name = cursor.getString(cursor.getColumnIndex(Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));
            int type = cursor.getInt(cursor.getColumnIndex(Calls.TYPE));
            String date = cursor.getString(cursor.getColumnIndex(Calls.DATE));
            String duration = cursor.getString(cursor.getColumnIndex(Calls.DURATION));

            CallLogDetailInfo info = new CallLogDetailInfo();
            info.setName(name);
            info.setNumber(number);
            info.setType(type);
            info.setDate(date);
            info.setDuration(duration);
            return info;
        }
        return null;
    }

    private int getRawContactId(String number) {
        String selection = Phone.NUMBER + "=?";

        Cursor cursor = getContentResolver().query(Data.CONTENT_URI, new String[] {
                Data.RAW_CONTACT_ID
        }, selection, new String[] {
                number
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

    private class CustomContentObserver extends ContentObserver {
        public CustomContentObserver() {
            super(mHandler);
        }

        @Override
        public void onChange(boolean selfChange) {
            isNeedupdateCache = true;
            getLoaderManager().restartLoader(CALL_LOG_LOADER_ID, null,
                    mCallLogLoaderCallbacks);

        }
    }

    private ContactInfo queryContactInfoForNumber(String num) {
        final ContactInfo info;
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(num));
        Cursor phonesCursor = getContentResolver().query(uri, _PROJECTION, null, null, null);

        try {
            if (phonesCursor != null) {
                if (phonesCursor.moveToFirst()) {
                    info = new ContactInfo();
                    String name = phonesCursor.getString(NAME);
                    String number = phonesCursor.getString(MATCHED_NUMBER);

                    info.name = name;
                    info.number = number;
                } else {
                    info = new ContactInfo();
                }
            } else {
                info = new ContactInfo();
            }
        } finally {
            if (phonesCursor != null) {
                phonesCursor.close();
                phonesCursor = null;
            }
        }

        return info;
    }

    private void updateCallLogContactInfoCache(String number, ContactInfo updatedInfo,
            ContactInfo callLogInfo) {
        final ContentValues values = new ContentValues();
        boolean needsUpdate = false;

        if (!TextUtils.equals(updatedInfo.number, callLogInfo.number)) {
            updatedInfo.number = callLogInfo.number;
        }

        if (callLogInfo != null) {

            if (!TextUtils.equals(updatedInfo.name, callLogInfo.name)) {
                values.put(Calls.CACHED_NAME, updatedInfo.name);
                needsUpdate = true;
            }

            if (!TextUtils.equals(updatedInfo.number, callLogInfo.number)) {
                values.put(Calls.NUMBER, updatedInfo.number);
                needsUpdate = true;
            }
        }

        if (!needsUpdate) {
            return;
        }

        try {
            getContentResolver().update(Calls.CONTENT_URI, values,
                    Calls.NUMBER + " = ? ",
                    new String[] {
                        number
                    });
        } catch (SQLiteFullException e) {
            e.printStackTrace();
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
