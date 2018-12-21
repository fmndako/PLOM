package ng.com.quickinfo.plom.Receivers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.common.util.DataUtils;

import ng.com.quickinfo.plom.ActivitySettings;
import ng.com.quickinfo.plom.DetailActivity;
import ng.com.quickinfo.plom.Service.MyIntentService;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

public class NotificationReceiver extends BroadcastReceiver {

    //shared pref
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;


    long mUserId;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.


        //starts activity
        Utilities.makeToast(context, "On Receive Notification");
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            context.startService(new Intent(context, MyIntentService.class));
            Utilities.makeToast(context, "boot completed");

        }else if(intent.getAction().equals((DetailActivity.offsetUpdateAction))){
            context.startService(new Intent(context, MyIntentService.class));
            Utilities.makeToast(context, "detail screen notification received");

        }



    }

}
