
package cn.stj.fplauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.util.List;

public class MainMenuAppAdapter extends BaseAdapter {

    private List<MenuInfo> menus;
    private LayoutInflater inflater;

    public MainMenuAppAdapter(Context context, List<MenuInfo> list) {
        this.menus = list;
        this.inflater = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    }

    @Override
    public int getCount() {
        return menus.size();
    }

    @Override
    public Object getItem(int position) {
        return Integer.valueOf(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.main_menu_app_item, null);
            holder.icon = ((ImageView) view.findViewById(R.id.menu_icon));
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.icon.setImageDrawable(menus.get(position).getIcon());
        return view;
    }

    private class ViewHolder {
        ImageView icon;
    }
}
