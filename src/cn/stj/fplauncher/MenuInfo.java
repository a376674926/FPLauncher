
package cn.stj.fplauncher;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

public class MenuInfo {
    private Drawable icon;
    private ComponentName intent;
    private String name;

    public Drawable getIcon() {
        return icon;
    }

    public ComponentName getIntent() {
        return intent;
    }

    public String getName() {
        return name;
    }

    public void setIcon(Drawable drawable) {
        this.icon = drawable;
    }

    public void setIntent(ComponentName componentName) {
        this.intent = componentName;
    }

    public void setName(String name) {
        this.name = name;
    }
}
