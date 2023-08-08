package eu.ldaldx.mobile.zscanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public final class Zebra_DWInterface {
    public static final String DATAWEDGE_SEND_ACTION = "com.symbol.datawedge.api.ACTION";
    public static final String DATAWEDGE_RETURN_ACTION = "com.symbol.datawedge.api.RESULT_ACTION";
    public static final String DATAWEDGE_RETURN_CATEGORY = "android.intent.category.DEFAULT";
    public static final String DATAWEDGE_EXTRA_SEND_RESULT = "SEND_RESULT";
    public static final String DATAWEDGE_EXTRA_RESULT = "RESULT";
    public static final String DATAWEDGE_EXTRA_COMMAND = "COMMAND";
    public static final String DATAWEDGE_EXTRA_RESULT_INFO = "RESULT_INFO";
    public static final String DATAWEDGE_EXTRA_RESULT_CODE = "RESULT_CODE";
    public static final String DATAWEDGE_SCAN_EXTRA_DATA_STRING = "com.symbol.datawedge.data_string";
    public static final String DATAWEDGE_SCAN_EXTRA_LABEL_TYPE = "com.symbol.datawedge.label_type";
    public static final String DATAWEDGE_SEND_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE";
    public static final String DATAWEDGE_SEND_GET_VERSION = "com.symbol.datawedge.api.GET_VERSION_INFO";
    public static final String DATAWEDGE_RETURN_VERSION = "com.symbol.datawedge.api.RESULT_GET_VERSION_INFO";
    public static final String DATAWEDGE_RETURN_VERSION_DATAWEDGE = "DATAWEDGE";
    public static final String DATAWEDGE_SEND_GET_ENUMERATE_SCANNERS = "com.symbol.datawedge.api.ENUMERATE_SCANNERS";
    public static final String DATAWEDGE_RETURN_ENUMERATE_SCANNERS = "com.symbol.datawedge.api.RESULT_ENUMERATE_SCANNERS";
    public static final String DATAWEDGE_SEND_GET_CONFIG = "com.symbol.datawedge.api.GET_CONFIG";
    public static final String DATAWEDGE_RETURN_GET_CONFIG = "com.symbol.datawedge.api.RESULT_GET_CONFIG";
    public static final String DATAWEDGE_SEND_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG";
    public static final String DATAWEDGE_SEND_GET_ACTIVE_PROFILE = "com.symbol.datawedge.api.GET_ACTIVE_PROFILE";
    public static final String DATAWEDGE_RETURN_GET_ACTIVE_PROFILE = "com.symbol.datawedge.api.RESULT_GET_ACTIVE_PROFILE";
    public static final String DATAWEDGE_SEND_SWITCH_SCANNER = "com.symbol.datawedge.api.SWITCH_SCANNER";
    public static final String DATAWEDGE_SEND_SET_SCANNER_INPUT = "com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN";
    public static final String DATAWEDGE_SEND_SET_SCANNER_INPUT_ENABLE = "ENABLE_PLUGIN";
    public static final String DATAWEDGE_SEND_SET_SCANNER_INPUT_DISABLE = "DISABLE_PLUGIN";
    public static final String DATAWEDGE_SEND_SET_SOFT_SCAN = "com.symbol.datawedge.api.SOFT_SCAN_TRIGGER";

    public static final String PROFILE_INTENT_START_ACTIVITY = "0";

    public static final void sendCommandString(Context context, String command, String parameter, boolean sendResult) {
        Intent dwIntent = new Intent();
        dwIntent.setAction(DATAWEDGE_SEND_ACTION);
        dwIntent.putExtra(command, parameter);
        if (sendResult) {
            dwIntent.putExtra(DATAWEDGE_EXTRA_SEND_RESULT, "true");
        }

        context.sendBroadcast(dwIntent);
    }

    public static final void sendCommandBundle(Context context, String command, Bundle parameter) {
        Intent dwIntent = new Intent();
        dwIntent.setAction(DATAWEDGE_SEND_ACTION);
        dwIntent.putExtra(command, parameter);
        context.sendBroadcast(dwIntent);
    }

    public static final void setConfigForDecoder(Context context, String profileName) {
        Bundle profileConfig = new Bundle();
        profileConfig.putString("PROFILE_NAME", profileName);
        profileConfig.putString("PROFILE_ENABLED", "true");
        profileConfig.putString("CONFIG_MODE", "UPDATE");

        Bundle barcodeConfig = new Bundle();
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE");
        barcodeConfig.putString("RESET_CONFIG", "true");

        Bundle barcodeProps = new Bundle();
        barcodeProps.putString("scanner_selection", "auto");
        barcodeProps.putString("decoder_ean8", "true" );
        barcodeProps.putString("decoder_ean13", "true" );
        barcodeProps.putString("decoder_code39", "true" );
        barcodeProps.putString("decoder_code128", "true");

        barcodeConfig.putBundle("PARAM_LIST", barcodeProps);
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig);

        sendCommandBundle(context, DATAWEDGE_SEND_SET_CONFIG, profileConfig);
    }

    public static void createDataWedgeProfile(Context context) {
        // https://github.com/darryncampbell/DataWedgeKotlin/blob/master/app/src/main/java/com/darryncampbell/datawedgekotlin/MainActivity.kt
        // https://4programmers.net/Forum/Mobilne/367583-zebra_datawedge_api_obsluga_intent_do_skonfigurowania_profili?p=1903300
        //  Create and configure the DataWedge profile associated with this application
        //  For readability's sake, I have not defined each of the keys in the DWInterface file
        sendCommandString(context, DATAWEDGE_SEND_CREATE_PROFILE, "ZScanner", false);
        Bundle profileConfig = new Bundle();
        profileConfig.putString("PROFILE_NAME", "ZScanner");
        profileConfig.putString("PROFILE_ENABLED", "true");
        profileConfig.putString("CONFIG_MODE", "UPDATE");

        Bundle barcodeConfig = new Bundle();
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE");
        barcodeConfig.putString("RESET_CONFIG", "true"); //  This is the default but never hurts to specify

        Bundle barcodeProps = new Bundle();
        barcodeProps.putString("scanner_input_enabled", "true");
        barcodeConfig.putBundle("PARAM_LIST", barcodeProps);
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig);

        Bundle appConfig = new Bundle();
        appConfig.putString("PACKAGE_NAME", "eu.ldaldx.mobile.zscanner");      //  Associate the profile with this app
        appConfig.putStringArray("ACTIVITY_LIST", new String[] { "*" });
        profileConfig.putParcelableArray("APP_LIST", new Bundle[] { appConfig });
        sendCommandBundle(context, DATAWEDGE_SEND_SET_CONFIG, profileConfig);

        //  You can only configure one plugin at a time in some versions of DW, now do the intent output
        profileConfig.remove("PLUGIN_CONFIG");

        Bundle intentConfig = new Bundle();
        intentConfig.putString("PLUGIN_NAME", "INTENT");
        intentConfig.putString("RESET_CONFIG", "true");

        Bundle intentProps = new Bundle();
        intentProps.putString("intent_output_enabled", "true");
        intentProps.putString("intent_action", context.getResources().getString(R.string.zebra_activity_intent_filter_action));
        intentProps.putString("intent_delivery", PROFILE_INTENT_START_ACTIVITY);
        intentConfig.putBundle("PARAM_LIST", intentProps);
        profileConfig.putBundle("PLUGIN_CONFIG", intentConfig);
        sendCommandBundle(context, DATAWEDGE_SEND_SET_CONFIG, profileConfig);
        profileConfig.remove("PLUGIN_CONFIG");

        Bundle bundleKSOutConfig = new Bundle();
        bundleKSOutConfig.putString("PLUGIN_NAME", "KEYSTROKE");
        bundleKSOutConfig.putString("RESET_CONFIG", "false");

        Bundle bundleKSParams = new Bundle();
        bundleKSParams.putString("keystroke_output_enabled", "false");
        bundleKSOutConfig.putBundle("PARAM_LIST", bundleKSParams);
        profileConfig.putBundle("PLUGIN_CONFIG", bundleKSOutConfig);
        sendCommandBundle(context, DATAWEDGE_SEND_SET_CONFIG, profileConfig);
    }

}
