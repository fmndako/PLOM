package ng.com.quickinfo.plom.Receivers;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.common.util.DataUtils;

import ng.com.quickinfo.plom.ActivitySettings;
import ng.com.quickinfo.plom.DetailActivity;
import ng.com.quickinfo.plom.Service.MyIntentService;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static ng.com.quickinfo.plom.Utils.Utilities.log;

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
        //Utilities.makeToast(context, "On Receive Notification");
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            schedule(context);
            Log.d("NOTIFICATIONRECEIVER", "action boot completed");
        }else if(intent.getAction().equals((DetailActivity.offsetUpdateAction))){
            //TODO remove in production
            schedule(context);
            Log.d("NOTIFICATIONRECEIVER", "action from detail activity");

//            context.startService(new Intent(context, MyIntentService.class));
//            Utilities.makeToast(context, "detail screen notification received");

        }





    }

    private void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(
                Context.ALARM_SERVICE);
        long startTime = System.currentTimeMillis();
        long intervalTime =8*60* 60 *1000;
        log("PLOM", "timer");
        //create intent and set alarm

        Intent intent = new Intent(context, MyIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Utilities.makeToast(context, "detail screen notification received");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                startTime, intervalTime,pendingIntent );


    }

}
