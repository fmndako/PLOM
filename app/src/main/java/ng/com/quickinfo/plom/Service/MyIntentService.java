package ng.com.quickinfo.plom.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.List;

import ng.com.quickinfo.plom.ActivitySettings;
import ng.com.quickinfo.plom.DetailActivity;
import ng.com.quickinfo.plom.ListActivity;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.LoanRepo;
import ng.com.quickinfo.plom.R;
import ng.com.quickinfo.plom.Utils.FilterUtils;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static ng.com.quickinfo.plom.Utils.Utilities.log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {

    String CHANNEL_ID;
    int ID = 2323;
    int count;
    public MyIntentService() {
        super("MyIntentService");
    }

    //shared pref
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    long mUserId;
    LoanViewModel loanViewModel;
    NotificationManagerCompat notificationManager;

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            notificationManager = NotificationManagerCompat.from(this);

            log("Service", "service at work");
            //start notification
            CHANNEL_ID = this.getPackageName();
            //get userid from shared pref
            sharedPref = Utilities.MyPref.getSharedPref(this);
            mUserId = sharedPref.getLong(ActivitySettings.Pref_User, 0) ;




            if(mUserId!=0 && sharedPref.getBoolean(ActivitySettings.Pref_Notification, true)) {
                LoanRepo repo = new LoanRepo(this.getApplication());

                List<Loan> dueLoans = FilterUtils.Notifications(
                        repo.getLoans(mUserId));
                if (dueLoans.size() > 0) {
                    count = dueLoans.size();
                }
            }
            else{stopSelf();}



            //then start notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getResources().getString(R.string.channel_name);
            String description = getResources().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);}
        startNotification(notificationManager);



    }}


    private void startNotification(NotificationManagerCompat notificationManager ) {

        String loanString = "";

        // notificationId is a unique int for each notification that you must define
        NotificationCompat.Builder mBuilder = sendNotification(this, "PLOM", "OverDue");
        notificationManager.notify(ID, mBuilder.build());

        //Second time
        if (count == 1){
            loanString = " loan needs";
        } else
        {loanString = " loans need";}
        mBuilder.setContentText(count + loanString + " your attention");

        notificationManager.notify(ID, mBuilder.build());


    }



    public NotificationCompat.Builder sendNotification(Context context, String title, String message ){

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, ListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("loanType", 7);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(alarmsound)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setAutoCancel(false);
        return mBuilder;
    }


}