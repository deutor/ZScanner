package eu.ldaldx.mobile.zscanner.menu;

public interface IMenuListener {
        void onMenuItemClick(int position, String action, String actionArgs);
        void onMenuItemClickUp(int position);
        void onMenuItemClickDown(int position);
        void onNumericPressed(int numeric);

}
