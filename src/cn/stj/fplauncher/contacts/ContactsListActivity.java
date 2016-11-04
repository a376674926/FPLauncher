
package cn.stj.fplauncher.contacts;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import cn.stj.fplauncher.BaseActivity;
import cn.stj.fplauncher.R;
import cn.stj.fplauncher.phone.AddNewContactActivity;
import cn.stj.fplauncher.utils.Cn2Spell;
import cn.stj.fplauncher.utils.Constant;

public class ContactsListActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener, View.OnClickListener {
    private LinearLayout mAddContactLayout;
    private LinearLayout mSearchContactLayout;
    private static final int PHONE_CONTACT_LOADER_ID = 1;
    private final ContactsLoader mContactsLoaderCallbacks = new ContactsLoader();
    private String mCurFilter;
    private SimpleCursorAdapter mAdapter;
    private TextView mEmptyTextView;
    private ListView mListView;
    private String mSelectedContactName;
    private String mSelectedContactNumber;
    private boolean isSearchFouch = false;

    private static final String[] PHONES_PROJECTION = new String[] {
            Contacts._ID, Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID
    };

    private ArrayList<ContactInfo> mContactInfos = new ArrayList<ContactInfo>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.contact_header_add);
        View headerView = mAboveViewStub.inflate();

        mMiddleViewStub.setLayoutResource(R.layout.activity_base_listview);
        View listView = mMiddleViewStub.inflate();
        mEmptyTextView = (TextView) listView.findViewById(R.id.empty);
        mSearchContactLayout = (LinearLayout) headerView.findViewById(R.id.item_contact_header_search);
        mSearchContactLayout.setFocusable(true);
        mSearchContactLayout.setFocusableInTouchMode(true);
        mSearchContactLayout.setOnFocusChangeListener(new MenuItemFocusChangeListener());
        mSearchContactLayout.setOnClickListener(this);
        mAddContactLayout = (LinearLayout) headerView.findViewById(R.id.item_contact_header_add);
        mAddContactLayout.setFocusable(true);
        mAddContactLayout.setFocusableInTouchMode(true);
        mAddContactLayout.setOnFocusChangeListener(new MenuItemFocusChangeListener());

        mAddContactLayout.setOnClickListener(this);

        setBottomKeyClickListener(this);
        setActivityBgResource(0);

        mListView = (ListView) findViewById(R.id.base_list_view);

        mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_type_singleline_text, null,
                new String[] {
                        Phone.DISPLAY_NAME
                }, new int[] {
                        R.id.litst_text
                }, 0);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemSelectedListener(new MenuItemSelectedListener());
        mListView.setOnItemClickListener(new MenuItemClickListener());
        mListView.setOnFocusChangeListener(new MenuItemFocusChangeListener());

        getLoaderManager().initLoader(PHONE_CONTACT_LOADER_ID, null, mContactsLoaderCallbacks);
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
        v.setText(R.string.title_contacts);
        return v;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (R.id.item_contact_header_add == v.getId()) {
            Intent intent = new Intent(this, AddNewContactActivity.class);
            startActivity(intent);
        }else if (R.id.item_contact_header_search == v.getId()) {
            Intent intent = new Intent();
            intent.setClassName("com.android.dialer", "com.android.dialer.DialtactsActivity");
            intent.putExtra("SEARCH_MODE", true);
            startActivity(intent);
        }
    }

    @Override
    public void onLeftKeyPress() {
        openContactOptionsMenuActivity();
    }

    @Override
    public void onMiddleKeyPress() {

    }

    @Override
    public void onRightKeyPress() {
        finish();
    }

    private void updateSelectedContact(String name, String number) {
        mSelectedContactName = name;
        mSelectedContactNumber = number;
    }

    private void openContactOptionsMenuActivity() {
    	if(isSearchFouch){
    		Intent intent = new Intent();
            intent.setClassName("com.android.dialer", "com.android.dialer.DialtactsActivity");
            intent.putExtra("SEARCH_MODE", true);
            startActivity(intent);
            return;
    	}
        Intent intent = new Intent(this, ContactOptionsMenuActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_CONTACT_NAME, mSelectedContactName);
        bundle.putString(Constant.KEY_CONTACT_NUMBER, mSelectedContactNumber);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    public class ContactInfo {
        int id;
        String name;
        String number;
        int photoId;
    }

    private class NameComparator implements Comparator<ContactInfo> {

        @Override
        public int compare(ContactInfo element1, ContactInfo element2) {

            String str1 = Cn2Spell.CN2FirstCharSpell(element1.name);
            String str2 = Cn2Spell.CN2FirstCharSpell(element2.name);
            return str1.compareToIgnoreCase(str2);
        }
    }

    private class MenuItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ContactInfo info = mContactInfos.get(position);
            updateSelectedContact(info.name, info.number);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    private class MenuItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ContactInfo info = mContactInfos.get(position);
            String name = info.name;
            String number = info.number;

            Bundle bundle = new Bundle();
            bundle.putString(Constant.KEY_CONTACT_NAME, name);
            bundle.putString(Constant.KEY_CONTACT_NUMBER, number);

            Intent intent = new Intent(getApplicationContext(), ContactsDetailActivity.class);
            intent.putExtras(bundle);

            startActivity(intent);
        }
    }

    private class MenuItemFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
        	isSearchFouch = false;
            if (v.getId() == R.id.base_list_view && hasFocus) {
                int position = ((ListView) v).getSelectedItemPosition();
                if (position > -1) {
                    ContactInfo info = mContactInfos.get(position);
                    updateSelectedContact(info.name, info.number);
                }
            } else if ((v.getId() == R.id.item_contact_header_add || v.getId() == R.id.item_contact_header_search)&& hasFocus) {
                updateSelectedContact(null, null);
                if(v.getId() == R.id.item_contact_header_search){
                	isSearchFouch = true;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(PHONE_CONTACT_LOADER_ID);
    }

    private class ContactsLoader implements LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri baseUri;

            if (mCurFilter != null) {
                baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI,
                        Uri.encode(mCurFilter));
            } else {
                baseUri = Phone.CONTENT_URI;
            }

            String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                    + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                    + Contacts.DISPLAY_NAME + " != '' ))";

            return new CursorLoader(getApplicationContext(), baseUri,
                    PHONES_PROJECTION, select, null,
                    Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            mEmptyTextView.setVisibility((cursor.getCount() > 0) ? View.GONE : View.VISIBLE);

            if ((mContactInfos != null) && (!mContactInfos.isEmpty())) {
                mContactInfos.clear();
            }

            while (cursor.moveToNext()) {
                ContactInfo info = new ContactInfo();
                info.id = cursor.getInt(cursor.getColumnIndex(Contacts._ID));
                info.name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
                info.number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                info.photoId = cursor.getInt(cursor.getColumnIndex(Photo.PHOTO_ID));
                mContactInfos.add(info);
            }

            Collections.sort(mContactInfos, new NameComparator());
            Cursor newCursor = createCursor(mContactInfos);
            mAdapter.changeCursor(newCursor);
        }

        private Cursor createCursor(ArrayList<ContactInfo> infos) {
            MatrixCursor cursor = new MatrixCursor(new String[] {
                    Contacts._ID, Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID
            });
            for (ContactInfo info : infos) {
                cursor.addRow(new Object[] {
                        info.id, info.name, info.number, info.photoId
                });
            }
            return cursor;
        }

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            mAdapter.swapCursor(null);
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
