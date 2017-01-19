package poc.cbt.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import poc.cbt.activities.R;

/**
 * Created by taro on 1/19/17.
 */
public class WindowUtil
{
    static private DisplayMetrics displaymetrics = null;


    static public DisplayMetrics getDisplayMetrics(Context context) {
        if (displaymetrics == null)
        {
            displaymetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displaymetrics);
        }

        return displaymetrics;
    }

    static public int getDeviceScreenHeightPixel(Context context) {
        getDisplayMetrics(context);

        return displaymetrics.heightPixels;
    }

    static public int getDeviceScreenWidthPixel(Context context) {
        getDisplayMetrics(context);

        return displaymetrics.widthPixels;
    }

    static public int convertDPToPixel(Context context, int dp) {
        getDisplayMetrics(context);

        return (int)(displaymetrics.density * dp);
    }

    static public int convertPixelToDP(Context context, int pixel) {
        getDisplayMetrics(context);

        return (int)(pixel / displaymetrics.density);
    }
//
//    static public void showAlertMessageBoxDefault(Context context) {
//        String title = context.getResources().getString(R.string.default_alert_error_title);
//        String message = context.getResources().getString(R.string.default_alert_error_message);
//
//        showAlertMessageBoxDefault(context, title, message, "");
//    }
//
//    static public void showAlertMessageBoxDefault(Context context, String title, String message, String lablePositiveButton) {
//        DialogInterface.OnClickListener positiveHandler = new DialogInterface.OnClickListener()
//        {
//            public void onClick(DialogInterface dialog, int id)
//            {
//                //  Action for 'NO' Button
//                dialog.cancel();
//            }
//        };
//
//        if (lablePositiveButton.equals(""))
//            lablePositiveButton = context.getString(R.string.default_alert_default_positivebutton_title);
//
//        showAlertMessageBoxWithButton(context, title, message, lablePositiveButton, "", positiveHandler, null);
//    }

    static public void showAlertMessageBoxWithButton(Context context, String title, String message,
                                                     String lablePositiveButton, String labelNegativeButton,
                                                     DialogInterface.OnClickListener positiveHandler,
                                                     DialogInterface.OnClickListener negaitveHandler) {

        if(((Activity) context).isFinishing())
            return;

        AlertDialog.Builder alertPopup = new AlertDialog.Builder(context);

        if (!title.equals("")) alertPopup.setTitle(title);
        if (!message.equals("")) alertPopup.setMessage(message);
        alertPopup.setCancelable(false);

        if (!lablePositiveButton.equals("")) alertPopup.setPositiveButton(lablePositiveButton, positiveHandler);
        if (!labelNegativeButton.equals("")) alertPopup.setNegativeButton(labelNegativeButton, negaitveHandler);

        alertPopup.show();
    }
//
//    static public void showAlertMessageTimeoutWithButton(Context context, DialogInterface.OnClickListener positiveHandler) {
//        String title = context.getResources().getString(R.string.popup_interopt_get_profile_timeout_title);
//        String message = context.getResources().getString(R.string.popup_interopt_get_profile_timeout_message);
//        String okLabel = context.getResources().getString(R.string.default_alert_default_positivebutton_title);
//        WindowUtil.showAlertMessageBoxWithButton(context, title, message, okLabel, "", positiveHandler, null);
//    }

}
