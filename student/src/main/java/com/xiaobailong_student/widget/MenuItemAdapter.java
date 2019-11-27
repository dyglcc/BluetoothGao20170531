package com.xiaobailong_student.widget;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaobailong_student.bluetoothfaultboardcontrol.R;

import java.util.List;


/**
 * Created by guorui.he on 2016/6/19.
 */
public class MenuItemAdapter extends BaseAdapter {


    private Context context;//运行上下文

    private LayoutInflater listContainer;  //视图容器

    private List<MenuItem> menuItems;

    public MenuItemAdapter(Context _context, List<MenuItem> _menuItems) {
        this.context = _context;
        this.listContainer = LayoutInflater.from(_context);
        this.menuItems = _menuItems;
    }

    @Override
    public int getCount() {
        return this.menuItems == null ? 0 :this.menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= menuItems.size() || position < 0) {
            return null;
        } else {
            return menuItems.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (convertView == null) {
            view = listContainer.inflate(R.layout.menu_item, null);
        }

        MenuItem menuItem = menuItems.get(position);

        TextView textView = (TextView) view.findViewById(R.id.menu_item);
        textView.setText(menuItem.getText());
        if (menuItems.size() == 1) {
            textView.setBackgroundResource(R.drawable.bottom_menu_btn_selector);
        } else if (position == 0) {
            textView.setBackgroundResource(R.drawable.bottom_menu_top_btn_selector);
        } else if (position < menuItems.size() - 1) {
            textView.setBackgroundResource(R.drawable.bottom_menu_mid_btn_selector);
        } else {
            textView.setBackgroundResource(R.drawable.bottom_menu_bottom_btn_selector);
        }
        if (position == menuItems.size() - 1) {
            view.findViewById(R.id.view_line).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.view_line).setVisibility(View.VISIBLE);
        }
//        if(menuItem.getStyle() == MenuItem.MenuItemStyle.COMMON) {
//            textView.setTextColomr(ContextCompat.getColor(context, R.color.bottom_menu_btn_text_commom_color));
//        } else {
//            textView.setTextColor(ContextCompat.getColor(context, R.color.bottom_menu_btn_text_stress_color));
//        }
        if (menuItem.getStyle() == MenuItem.MenuItemStyle.COMMON) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.change_team_text_color));
        } else {
            textView.setTextColor(ContextCompat.getColor(context, R.color.change_team_text_color));
        }
        MenuItemOnClickListener _menuItemOnClickListener = menuItem.getMenuItemOnClickListener();
        if (_menuItemOnClickListener != null) {
            textView.setOnClickListener(_menuItemOnClickListener);
        }
        return view;
    }
}
