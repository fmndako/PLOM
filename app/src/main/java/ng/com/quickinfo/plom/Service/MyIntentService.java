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

import java.util.Date;
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

import static ng.com.quickinfo.plom.Utils.FilterUtils.Notifications;
import static ng.com.quickinfo.plom.Utils.FilterUtils.activeLoans;
import static ng.com.quickinfo.plom.Utils.FilterUtils.dateFilterList;
import static ng.com.quickinfo.plom.Utils.FilterUtils.getItemCount;
import static ng.com.quickinfo.plom.Utils.FilterUtils.getTotalSum;
import static ng.com.quickinfo.plom.Utils.FilterUtils.isDueSoon;
import static ng.com.quickinfo.plom.Utils.FilterUtils.isOverDue;
import static ng.com.quickinfo.plom.Utils.FilterUtils.isToday;
import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    String TAG = getClass().getSimpleName();
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
    List<Loan> dueLoans;
    NotificationManagerCompat notificationManager;

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            notificationManager = NotificationManagerCompat.from(this);

            log(TAG, "service at work");
            //start notification
            CHANNEL_ID = this.getPackageName();
            //get userid from shared pref
            sharedPref = Utilities.MyPref.getSharedPref(this);
            mUserId = sharedPref.getLong(ActivitySettings.Pref_User, 0) ;

            //get currency and also reminder days from pref
            String currency = sharedPref.getString(ActivitySettings.Pref_Currency, "N");
            int reminderDays = sharedPref.getInt(ActivitySettings.Pref_ReminderDays, 7);

            log(TAG, "intent not null");
            //makeToast(this, "PLOM service");
            //for higher versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getResources().getString(R.string.channel_name);
                String description = getResources().getString(R.string.channel_description);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
                }

            log(TAG, mUserId + "" + sharedPref.getBoolean(ActivitySettings.Pref_Notification, true));
            //if user is not logout and notification is on then notify else stop service
            if(mUserId!=0 && sharedPref.getBoolean(ActivitySettings.Pref_Notification, true)) {
                LoanRepo repo = new LoanRepo(this.getApplication());
                log(TAG, "Should notify");
                //TODO this also works
                //get loans that are due
//                dueLoans = FilterUtils.Notifications(
//                        repo.getLoans(mUserId), reminderDays);
//                dueLoans = repo.getLoans(mUserId);
//                log(TAG, getTotalSum(dueLoans) + ":" + reminderDays );
//                if (dueLoans.size() > 0) {
//                   log(TAG, "count is less than 1");
//                    count = dueLoans.size();
//                    int amount = getTotalSum(dueLoans);
////                    String message = String.valueOf(count) +
//                             getResources().getQuantityString(
//                                     R.plurals.numberOfLoansAvailable, count) +":"+ currency
//                            + String.valueOf(amount) + " needs your attention";
//
//                    //startNotification(notificationManager);
//                    NotificationCompat.Builder mBuilder = sendNotification(
//                            this, "PLOM", message );
//                    // notificationId is a unique int for each notification that you must define
//                    notificationManager.notify(ID, mBuilder.build());
//

//                }



                List<Loan> loans = Notifications(activeLoans(repo.getLoans(mUserId)), reminderDays);

                for (Loan loan : loans) {
                    Date date = loan.getDateToRepay();
                    String name = loan.getName();
                    String amount = String.valueOf(loan.getAmount());
                    String message = "";
                    if (isToday(date)){
                        message = "Due Today: ";
                    } else if (isDueSoon(date, reminderDays)){
                        message = "Due Soon: ";
                    }else{
                        message = "Over Due: ";
                    }

                    message = message + name+ ", " + currency + amount;
                    NotificationCompat.Builder mBuilder = sendNotification(
                            this, getString(R.string.notificatin_title_soon), message, loan.getId() );
                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(Integer.valueOf(loan.getId() + ""), mBuilder.build());



                    }
                }


            }



//            for (int pos=0; pos<loans.size(); pos++) {
//                List <Loan> mLoans = loans.get(pos);
//                if (mLoans.size() != 0) {
//                   int size = getItemCount(mLoans);
//                   int amount = getTotalSum(mLoans);
//                   String type = "";
//                   String loanPlural = loanPlural(size);
//                   if(pos == 0){type = "Due soon: ";}
//                   else if (pos == 1){type = "Due today: ";}
//                   else{type = "Over due: ";}
//                   message = message + type + String.valueOf(size)
//                           + currency + String.valueOf(amount) + "\n";
//
//
//                }
//            }




        }






    public NotificationCompat.Builder sendNotification(Context context, String title, String message , long id){

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, DetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(DetailActivity.EXTRA_PARAM_ID, id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.lending)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(alarmsound)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setAutoCancel(true);
        return mBuilder;
    }


}