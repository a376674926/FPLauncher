
package cn.stj.fplauncher.multimedia;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import java.util.Arrays;

import cn.stj.fplauncher.BaseActivity;
import cn.stj.fplauncher.R;

public class MultiMediaOperationActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {
    private BaseMenuItemAdapter mAdapter;
    private int mSelectedPosition;

    private static enum MENU_FUNCTION_MULTI_MEDIA {
        FM, MUSICPLAYER, RECORDER, CAMERA, GALLERY, FILEMANAGER
    }

//    private static final String ACTION_START_RECORDER = "android.provider.MediaStore.RECORD_SOUND";//系统录音机
    private static final String COMPONENT_START_RECORDER = "com.stj.soundrecorder/.SoundRecorder";//录音机
    private static final String COMPONENT_START_GALLERY = "com.android.gallery3d/.app.GalleryActivity";//相册
    private static final String COMPONENT_START_CAMERA = "com.android.camera2/com.android.camera.CameraLauncher";//相机
    private static final String COMPONENT_START_FILEMANAGER = "com.stj.fileexplorer/.FileExplorerTabActivity";//文件管理器
    private static final String COMPONENT_START_MUSICPLAYER = "com.android.music/.MusicBrowserActivity";//音乐×
    private static final String COMPONENT_START_FM = "com.android.fmradio/.FmMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMiddleViewStub.setLayoutResource(R.layout.activity_base_listview);
        View listView = mMiddleViewStub.inflate();
        TextView emptyTextView = (TextView) listView.findViewById(R.id.empty);
        emptyTextView.setVisibility(View.GONE);

        setBottomKeyClickListener(this);
        setActivityBgResource(0);
        Log.e("DD","");
        String[] menuItems = getResources().getStringArray(R.array.menu_multi_media);

        mAdapter = new BaseMenuItemAdapter(this, Arrays.asList(menuItems));
        final ListView lv = (ListView) findViewById(R.id.base_list_view);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(mOptionMenuItemClickListener);
        lv.setOnItemSelectedListener(mOptionMenuItemSelectedListener);
    }

    private OnItemClickListener mOptionMenuItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onFunctionChoose(position);
        }
    };

    private OnItemSelectedListener mOptionMenuItemSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mSelectedPosition = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void onFunctionChoose(int position) {

        if (position == MENU_FUNCTION_MULTI_MEDIA.CAMERA.ordinal()) {
            openCameraFunction();
        } else if (position == MENU_FUNCTION_MULTI_MEDIA.FILEMANAGER.ordinal()) {
            openFileManagerFunction();
        } else if (position == MENU_FUNCTION_MULTI_MEDIA.FM.ordinal()) {
            openFmFunction();
        } else if (position == MENU_FUNCTION_MULTI_MEDIA.GALLERY.ordinal()) {
            openGalleryFunction();
        } else if (position == MENU_FUNCTION_MULTI_MEDIA.MUSICPLAYER.ordinal()) {
            openMusicPlayerFunction();
        } else if (position == MENU_FUNCTION_MULTI_MEDIA.RECORDER.ordinal()) {
            openRecorderFunction();
        }
    }

    private void openCameraFunction() {
        Intent intent = new Intent();
        ComponentName component = ComponentName.unflattenFromString(COMPONENT_START_CAMERA);
        intent.setComponent(component);
        startActivity(intent);
    }

    private void openFileManagerFunction() {
        Intent intent = new Intent();
        ComponentName component = ComponentName.unflattenFromString(COMPONENT_START_FILEMANAGER);
        intent.setComponent(component);
        startActivity(intent);
    }

    private void openFmFunction() {
        Intent intent = new Intent();
        ComponentName component = ComponentName.unflattenFromString(COMPONENT_START_FM);
        intent.setComponent(component);
        startActivity(intent);
    }

    private void openGalleryFunction() {
        Intent intent = new Intent();
        ComponentName component = ComponentName.unflattenFromString(COMPONENT_START_GALLERY);
        intent.setComponent(component);
        startActivity(intent);
    }

    private void openMusicPlayerFunction() {
        Intent intent = new Intent();
        ComponentName component = ComponentName.unflattenFromString(COMPONENT_START_MUSICPLAYER);
        intent.setComponent(component);
        startActivity(intent);
    }

    private void openRecorderFunction() {
        Intent intent = new Intent();
        ComponentName component = ComponentName.unflattenFromString(COMPONENT_START_RECORDER);
        intent.setComponent(component);
        startActivity(intent);
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
        v.setText(R.string.multi_media_title);
        return v;
    }

    @Override
    public void onLeftKeyPress() {
        onFunctionChoose(mSelectedPosition);
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
