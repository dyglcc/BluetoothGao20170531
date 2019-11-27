package com.xiaobailong.widget;

/**
 * Created by guorui.he on 2016/6/19.
 */
public class MenuItem {
    public  MenuItem() {

    }

    /**
     *
     * @param position 菜单项名称
     * @param _text 菜单项显示内容
     * @param _style 菜单类型
     * @param _menuItemOnClickListener 菜单点击回调事件
     */
    public MenuItem(int position, String _text, MenuItemStyle _style, MenuItemOnClickListener _menuItemOnClickListener){
        this.position = position;
        this.text = _text;
        this.style = _style;
        this.menuItemOnClickListener = _menuItemOnClickListener;
    }


    private int position;
    private String text;
    private MenuItemStyle style;

    public int getPosition() {
        return position;
    }

    public String getText() {
        return text;
    }

    public MenuItemStyle getStyle() {
        return style;
    }

    public void setPosition(int pos) {
        this.position = pos;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     *
     * @param style 菜单类型
     */
    public void setStyle(MenuItemStyle style) {
        this.style = style;
    }

    public MenuItemOnClickListener getMenuItemOnClickListener() {
        return menuItemOnClickListener;
    }

    public void setMenuItemOnClickListener(MenuItemOnClickListener menuItemOnClickListener) {
        this.menuItemOnClickListener = menuItemOnClickListener;
    }

    private MenuItemOnClickListener menuItemOnClickListener;


    public  enum MenuItemStyle{
        COMMON , STRESS
    }

}
