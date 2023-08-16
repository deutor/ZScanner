package eu.ldaldx.mobile.zscanner;

public interface IBrowserListener {
        void onBrowserItemClick(int position, String data);
        void onBrowserItemClickUp(int position);
        void onBrowserItemClickDown(int position);

        void onBrowserEnterClicked();
}
