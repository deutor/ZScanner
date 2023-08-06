package eu.ldaldx.mobile.zscanner;

public interface IMenuListener {
        void onMenuItemClick(int position, String data);
        void onMenuItemClickUp(int position);
        void onMenuItemClickDown(int position);
        void onNumericPressed(int numeric);
}
