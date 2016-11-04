
package cn.stj.fplauncher.utils;

import android.os.Build;

public class Utils {
    public static final boolean isLmpOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static final boolean isLmpMR1() {
        return Build.VERSION.SDK_INT == 22;
    }
}
