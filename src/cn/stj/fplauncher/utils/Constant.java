
package cn.stj.fplauncher.utils;

public class Constant {

    public static final String KEY_CONTACT_NAME = "key_contact_name";
    public static final String KEY_CONTACT_NUMBER = "key_contact_number";
    public static final String ACTION_DELETE_CALLBACK = "action_delete_callback";
    public static final String ACTION_UPDATE_CALLBACK = "action_update_callback";
    public static final String ACTION_SAVE_NEW_CONTACT = "cn.stj.action.SAVE_NEW_CONTACT";

    public static final int CALL_LOG_OPTIONS_REQUEST_CODE = 1;
    public static final int RESULT_OK = -1;
    public static final String KEY_CALL_LOG_OPTIONS = "key_call_log_options";

    public static enum CALL_LOG_OPERATIONS {
        LOOK_UP_OPTION, DIAL_OPTION, DELETE_OPTION, SEND_SMS_OPTION, SAVE_CONTACT_OPTION
    }
}
