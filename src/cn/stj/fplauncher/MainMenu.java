
package cn.stj.fplauncher;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;


import cn.stj.fplauncher.BaseActivity.BottomKeyClickListener;
import cn.stj.fplauncher.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainMenu extends BaseActivity implements BottomKeyClickListener {

    private static final ArrayList<String> APP_FILTER = new ArrayList<String>();

    private List<MenuInfo> mMenus = new ArrayList<MenuInfo>();
    private MainMenuAppAdapter mMenusListAdapter;
    private int mSelectedPosition;
    private int mGridViewItemCount;
    private static final int GRIDVIEW_COLUMN_NUM = 3;
    private GridView mGridview;
    private static Method sClipRevealMethod = null;
    private View mCurrentSelectedView;

    private static final String PKG_NAME_GALLERY = "com.android.gallery3d/com.android.gallery3d.app.Gallery";
    private static final String PKG_NAME_DIALER = "com.android.dialer/com.android.dialer.DialtactsActivity";
    private static final String PKG_NAME_CONTACTS = "cn.stj.fplauncher/.contacts.ContactsListActivity";
    private static final String PKG_NAME_MMS = "com.android.mms/com.android.mms.ui.ConversationList";
    private static final String PKG_NAME_MUSIC = "com.android.music/com.android.music.MusicBrowserActivity";
    private static final String PKG_NAME_VIDEO = "com.android.music/com.android.music.VideoBrowserActivity";
    private static final String PKG_NAME_CAMERA = "com.android.camera2/com.android.camera.CameraLauncher";
    private static final String PKG_NAME_FM = "com.caf.fmradio/com.caf.fmradio.FMRadio";
    private static final String PKG_NAME_SETTINGS = "com.android.settings/com.android.settings.Settings";
    private static final String PKG_NAME_CALENDER = "com.android.calendar/com.android.calendar.AllInOneActivity";

    // row:1
    private static final String FILTER_PKG_NAME_CONTACTS = "cn.stj.fplauncher/.contacts.ContactsListActivity";
    private static final String FILTER_PKG_NAME_CALLLOG = "cn.stj.fplauncher/.callLog.CallLogListActivity";
    private static final String FILTER_PKG_NAME_TOOLS = "cn.stj.fplauncher/.tools.ToolsListActivity";
    // row:2
    private static final String FILTER_PKG_NAME_MULTIMEDIA = "cn.stj.fplauncher/.multimedia.MultiMediaOperationActivity";
    private static final String FILTER_PKG_NAME_MESSAGE = "com.android.mms/.activities.MainActivity";
    private static final String FILTER_PKG_NAME_FILEEXPLORE = "com.stj.fileexplorer/.FileExplorerTabActivity";
    // row:3
    private static final String FILTER_PKG_NAME_FM = "com.android.fmradio/.FmMainActivity";
    private static final String FILTER_PKG_NAME_CAMERA = "com.android.camera2/com.android.camera.CameraLauncher";
    private static final String FILTER_PKG_NAME_SETTINGS = "cn.stj.settings/.activity.SettingsMainActivity";

    static {
        APP_FILTER.add(FILTER_PKG_NAME_CONTACTS);
        APP_FILTER.add(FILTER_PKG_NAME_CALLLOG);
        APP_FILTER.add(FILTER_PKG_NAME_TOOLS);
        APP_FILTER.add(FILTER_PKG_NAME_MULTIMEDIA);
        APP_FILTER.add(FILTER_PKG_NAME_MESSAGE);
        APP_FILTER.add(FILTER_PKG_NAME_FILEEXPLORE);
        APP_FILTER.add(FILTER_PKG_NAME_FM);
        APP_FILTER.add(FILTER_PKG_NAME_CAMERA);
        APP_FILTER.add(FILTER_PKG_NAME_SETTINGS);
    }

    private static final HashMap<String, Integer> SHORTCUT_ICON_MAP = new HashMap<String, Integer>();

    static {
        SHORTCUT_ICON_MAP.put(FILTER_PKG_NAME_CONTACTS, R.drawable.contacts);
        SHORTCUT_ICON_MAP.put(FILTER_PKG_NAME_CALLLOG, R.drawable.calllog);
        SHORTCUT_ICON_MAP.put(FILTER_PKG_NAME_TOOLS, R.drawable.apps);
        SHORTCUT_ICON_MAP.put(FILTER_PKG_NAME_MULTIMEDIA, R.drawable.media);
        SHORTCUT_ICON_MAP.put(FILTER_PKG_NAME_MESSAGE, R.drawable.messages);
        SHORTCUT_ICON_MAP.put(FILTER_PKG_NAME_FILEEXPLORE, R.drawable.my_file);
        SHORTCUT_ICON_MAP.put(FILTER_PKG_NAME_FM, R.drawable.fm);
        SHORTCUT_ICON_MAP.put(FILTER_PKG_NAME_CAMERA, R.drawable.camera);
        SHORTCUT_ICON_MAP.put(FILTER_PKG_NAME_SETTINGS, R.drawable.settings);
    }

    static {
        Class<?> activityOptionsClass = ActivityOptions.class;
        try {
            sClipRevealMethod = activityOptionsClass.getDeclaredMethod("makeClipRevealAnimation",
                    View.class, int.class, int.class, int.class, int.class);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_main_menu);
        View view = mAboveViewStub.inflate();

        setBottomKeyClickListener(this);
        setBottomButtonsResource(0);
        setTopTitleBgResource(0);

        mGridview = (GridView) view.findViewById(R.id.main_menu_apps);
        mGridview.setSelector(getResources().getDrawable(R.drawable.ic_selector));

        mMenus = loadMainMenuInfo(this);
        mMenusListAdapter = new MainMenuAppAdapter(this, mMenus);
        mGridview.setAdapter(this.mMenusListAdapter);

        mGridview.setOnItemClickListener(new MenuItemClickListener());
        mGridview.setOnItemSelectedListener(new MenuItemSelectedListener());
        mGridview.setOnFocusChangeListener(new MenuItemFocusChangeListener());
        mGridViewItemCount = mGridview.getCount();

    }

    private List<MenuInfo> loadMainMenuInfo(Context context) {

        ArrayList<MenuInfo> menuList = new ArrayList<MenuInfo>();
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        Collections.sort(infos, new ResolveInfo.DisplayNameComparator(pm));
        if (infos != null) {
            menuList.clear();
            for (int i = 0; i < infos.size(); i++) {
                ResolveInfo info = (ResolveInfo) infos.get(i);
                if (isInFilter(info)) {
                    MenuInfo menu = new MenuInfo();
                    menu.setName(info.loadLabel(pm).toString());

                    Drawable icon = getMenuIcon(info);
                    if (icon != null) {
                        menu.setIcon(icon);
                    } else {
                        menu.setIcon(info.loadIcon(pm));
                    }
                    menu.setIntent(new ComponentName(
                            info.activityInfo.packageName,
                            info.activityInfo.name));
                    menuList.add(menu);
                }
            }
        }
        return menuList;
    }

    private Drawable getMenuIcon(ResolveInfo info) {
        if (info == null) {
            return null;
        }

        ComponentName componentName = new ComponentName(info.activityInfo.packageName,
                info.activityInfo.name);

        String componentStr = componentName.flattenToShortString();
        Drawable drawable = null;
        Integer id = SHORTCUT_ICON_MAP.get(componentStr);
        if (id != null) {
            drawable = getResources().getDrawable(id);
        }
        return drawable;
    }

    private boolean isInFilter(ResolveInfo info) {
        if (info == null) {
            return false;
        }

        if (TextUtils.isEmpty(info.activityInfo.packageName)
                || TextUtils.isEmpty(info.activityInfo.name)) {
            return false;
        }

        ComponentName componentName = new ComponentName(info.activityInfo.packageName,
                info.activityInfo.name);
        String componentStr = componentName.flattenToShortString();
        return APP_FILTER.contains(componentStr);
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
        return v;
    }

    @Override
    public void onLeftKeyPress() {
        launchApplication(mMenus.get(mSelectedPosition).getIntent());
    }

    @Override
    public void onMiddleKeyPress() {

    }

    @Override
    public void onRightKeyPress() {
        finish();
    }

    private class MenuItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCurrentSelectedView = view;
            mSelectedPosition = position;
            String appName = mMenus.get(position).getName();
            setTopTitleText(appName);
            launchApplication(mMenus.get(position).getIntent());
        }
    }

    private void launchApplication(ComponentName component) {
        launchWithAnimation(true, mCurrentSelectedView, component);
    }

    private class MenuItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mCurrentSelectedView = view;
            mSelectedPosition = position;
            String appName = mMenus.get(position).getName();
            setTopTitleText(appName);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class MenuItemFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == R.id.main_menu_apps && hasFocus) {
                int position = ((GridView) v).getSelectedItemPosition();
                if (position > -1) {
                    mCurrentSelectedView = v;
                    String appName = mMenus.get(position).getName();
                    setTopTitleText(appName);
                }
            }
        }
    }

    @Override
    public void onDirectLeftKeyPress() {
        int nextPos = 0;
        nextPos = mSelectedPosition - 1;
        if (mSelectedPosition <= 0) {
            nextPos = mGridViewItemCount - 1;
        }
        mGridview.setSelection(nextPos);
    }

    @Override
    public void onDirectRightKeyPress() {
        int nextPos = 0;
        nextPos = mSelectedPosition + 1;
        if ((mSelectedPosition + 1) >= mGridViewItemCount) {
            nextPos = 0;
        }
        mGridview.setSelection(nextPos);
    }

    @Override
    public void onDirectUpKeyPress() {
        int temp = (mGridViewItemCount - GRIDVIEW_COLUMN_NUM) / GRIDVIEW_COLUMN_NUM;
        int nextPos = mSelectedPosition + (temp * GRIDVIEW_COLUMN_NUM);
        mGridview.setSelection(nextPos);
    }

    @Override
    public void onDirectDownKeyPress() {
        int temp = mSelectedPosition / GRIDVIEW_COLUMN_NUM;
        int nextPos = mSelectedPosition - (temp * GRIDVIEW_COLUMN_NUM);
        mGridview.setSelection(nextPos);
    }

    private void launchWithAnimation(boolean useLaunchAnimation, View v, ComponentName component) {
        Bundle optsBundle = null;
        ActivityOptions opts = null;
        if (useLaunchAnimation) {
            if (sClipRevealMethod != null) {
                int left = 0, top = 0;
                int width = v.getMeasuredWidth();
                int height = v.getMeasuredHeight();
                try {
                    opts = (ActivityOptions) sClipRevealMethod.invoke(null, v,
                            left, top, width, height);
                } catch (IllegalAccessException e) {
                    Log.d("debug", "Could not call makeClipRevealAnimation: " + e);
                    sClipRevealMethod = null;
                } catch (InvocationTargetException e) {
                    Log.d("debug", "Could not call makeClipRevealAnimation: " + e);
                    sClipRevealMethod = null;
                }
            }

            if (opts == null && !Utils.isLmpOrAbove()) {
                opts = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getMeasuredWidth(),
                        v.getMeasuredHeight());
            }
            optsBundle = opts != null ? opts.toBundle() : null;
        }

        Intent intent = new Intent();
        intent.setComponent(component);
        startActivity(intent, optsBundle);
    }
}
